package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;

public class Rule {

    Pattern regex;
    Pattern ignore;
    HashMap<String, Object> actions;
    String permission = null;
    boolean needsPerm;

    public Rule( String regex, HashMap<String, Object> actions, String permission, String ignores ) {
        this.regex = Pattern.compile( regex , Pattern.UNICODE_CHARACTER_CLASS);
        if ( ignores == null ) {
            ignore = null;
        }    else{
        this.ignore = Pattern.compile( ignores , Pattern.UNICODE_CHARACTER_CLASS);
        }
        this.actions = actions;
        if(permission!=null && permission.startsWith( "!" )){
            permission = permission.substring( 1,permission.length() );
            needsPerm = true;
        }
        this.permission = permission;
    }

    public Pattern getRegex() {
        return regex;
    }

    public String getStringRegex() {
        return regex.pattern();
    }

    public Matcher getMatcher( String message ) {
        return regex.matcher( message );
    }

    public boolean doesMessageContainRegex( String message ) {
        return getMatcher( message ).find();
    }

    public void performActions( ChatEvent event, ProxiedPlayer player ) {
        String message = event.getMessage();
        if(ignore!=null){
            Matcher ig =Pattern.compile(ignore.pattern()).matcher(message);
                while(ig.find()){
                return;
            }
        }
        for ( String action : actions.keySet() ) {
            if ( action.equals( "deny" ) && (Boolean) actions.get( action ) ) {
                event.setCancelled( true );
            } else if ( action.equals( "message" ) ) {
                player.sendMessage( util.MessageFromStringList( (List<String>) actions.get( action ), event ) );
            } else if ( action.equals( "kick" ) ) {
                player.disconnect( util.MessageFromStringList( (List<String>) actions.get( action ), event ) );
            } else if ( action.equals( "alert" ) ) {
                ProxyServer.getInstance().broadcast( util.MessageFromStringList( (List<String>) actions.get( action ), event ) );
            } else if ( action.equals( "scommand" ) ) {
                for ( String scommand : (List<String>) actions.get( action ) ) {
                    player.chat( util.ParseVariables( scommand, event ) );
                }
            } else if ( action.equals( "pcommand" ) ) {
                for ( String pcommand : (List<String>) actions.get( action ) ) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, util.ParseVariables(pcommand, event) );
                }
            } else if( action.equals( "ccommand" ) ) {
                for ( String ccommand : (List<String>) actions.get( action ) ) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand( ProxyServer.getInstance().getConsole(), util.ParseVariables(ccommand, event) );
                }
            } else if ( action.equals( "remove" ) && (Boolean) actions.get( action ) ) {
                message = message.replaceAll( regex.pattern(), "" );
            } else if ( action.equals( "replace" ) ) {
                Random rand = new Random();
                Matcher m = getMatcher( message );
                StringBuilder sb = new StringBuilder();
                int last = 0;
                while ( m.find() ) {
                        int n = rand.nextInt( ((String[]) actions.get( action )).length );
                        sb.append( message.substring( last, m.start() ) );
                        sb.append( util.ParseVariables(((String[]) actions.get( action ))[n], event) );
                        last = m.end();
                }
                sb.append( message.substring( last ) );
                message = sb.toString();
            } else if ( action.equals( "lower" ) && (Boolean) actions.get( action ) ) {
                Matcher m = getMatcher( message );
                StringBuilder sb = new StringBuilder();
                int last = 0;
                while ( m.find() ) {
                        sb.append( message.substring( last, m.start() ) );
                        sb.append( m.group( 0 ).toLowerCase() );
                        last = m.end();
                }
                sb.append( message.substring( last ) );
                message = sb.toString();
            }
        }
        event.setMessage( message );
    }

    public boolean hasPermission() {
        return permission != null;
    }

    public boolean needsPermission(){
        return needsPerm;
    }

    public String getPermission() {
        return permission;
    }
}
