package com.minecraftdimensions.bungeechatfilter;

import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends Plugin {

    public static Boolean COMMANDS;
    public static List<String> COMLIST;
    public static ArrayList<Rule> RULES;
    public static HashMap<ProxiedPlayer, String> ANTISPAM = new HashMap<>();
    public static boolean NOSPAM;
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
        loadRules();
    }

    public static void loadRules() {
        RULES = new ArrayList<>();
        List<String> nodes = c.getSubNodes( "rules" );
        for ( String node : nodes ) {
            String regex = c.getString( "rules." + node + ".regex" );
            String perm = c.getString( "rules." + node + ".permission" );
            String ignore = c.getString( "rules." + node + ".ignore" );
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


    public String color( String s ) {
        return ChatColor.translateAlternateColorCodes( '&', s );
    }
}
