package com.minecraftdimensions.bungeechatfilter;

import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends Plugin {

    public static long SPAMTIMER = 0;
    public static long REPEATTIMER = 0;
    public static Boolean COMMANDS;
    public static List<String> COMLIST;
    public static ArrayList<Rule> RULES;
    public static HashMap<String, Long> ANTISPAM = new HashMap<>();
    public static HashMap<String, String> ANTIREPEAT = new HashMap<>(  );
    public static boolean NOSPAM;
    public static boolean NOREPEAT;
    public static Config c;

    public void onEnable() {
        initialiseConfig();
        this.getProxy().getPluginManager().registerListener( this, new PlayerChatListener() );
        this.getProxy().getPluginManager().registerCommand( this, new BFReload("bungeefilterreload", "bungeefilter.reload", "bfreload", "reloadbf" ) );
    }

    private void initialiseConfig() {
        File file = new File( this.getDataFolder().getAbsoluteFile() + File.separator + "config.yml" );
        if ( !file.exists() ) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch ( IOException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            InputStream is = this.getClass().getClassLoader().getResourceAsStream( "config.yml" );

            try {

                OutputStream os = null;

                os = new FileOutputStream( file );

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ( ( bytesRead = is.read( buffer ) ) != -1 ) {
                    try {
                        os.write( buffer, 0, bytesRead );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
                is.close();
                os.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        c = MainConfig.c;
        COMMANDS = c.getBoolean( "Monitor Commands", true );
        List<String> defaultList = new ArrayList<>();
        defaultList.add( "message" );
        defaultList.add( "msg" );
        COMLIST = c.getListString( "Commands", defaultList );
        NOSPAM = c.getBoolean( "AntiSpam", true );
        NOREPEAT = c.getBoolean( "AntiRepeat", true );
        SPAMTIMER = c.getInt( "Minimum-Chat-Delay", 1500 ) ;
        REPEATTIMER = c.getInt( "Minimum-Repeat-Delay", 60000 ) ;
        loadRules();
    }

    public static void loadRules() {
        RULES = new ArrayList<>();
        List<String> nodes = c.getSubNodes( "rules" );
        for ( String node : nodes ) {
            String regex = "";
                List<String> strList = c.getListString( "rules."+node+".regex" ) ;
                for(String str:strList){
                   regex+=str+"|";
                }
            if(regex.length()==0){
             regex = c.getString( "rules." + node + ".regex" );
            }     else{
                regex = regex.substring( 0,regex.length()-1 );
            }
            String perm = c.getString( "rules." + node + ".permission" );
            String ignore = c.getString( "rules." + node + ".ignores" );
            HashMap<String, String[]> actions = new HashMap<>();
            for ( String action : c.getSubNodes( "rules." + node + ".actions" ) ) {
                if ( action.equals( "replace" ) ) {
                    List<String> strlist = c.getListString( "rules." + node + ".actions.replace" );
                    actions.put( action, strlist.toArray( new String[strlist.size()] ) );
                } else {
                    actions.put( action, new String[] { c.getString( "rules." + node + ".actions." + action ) } );
                }
            }
            RULES.add( new Rule( regex, actions, perm, ignore ) );
        }
        System.out.println( RULES.size() + " filter rules loaded!" );
    }


    public static String color( String s ) {
        return ChatColor.translateAlternateColorCodes( '&', s );
    }
}
