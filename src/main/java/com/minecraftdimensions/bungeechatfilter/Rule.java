package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule {

    Pattern regex;
    Pattern ignore;
    HashMap<String, String[]> actions;
    String permission = null;

    public Rule( String regex, HashMap<String, String[]> actions, String permission, String ignores ) {
        this.regex = Pattern.compile( regex );
        if ( ignores == null ) {
            ignores = "";
        }
        this.ignore = Pattern.compile( ignores );
        this.actions = actions;
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
        for ( String action : actions.keySet() ) {
            if ( action.equals( "deny" ) ) {
                event.setCancelled( true );
            } else if ( action.equals( "message" ) ) {
                player.sendMessage( color( actions.get( action )[0] ) );
            } else if ( action.equals( "kick" ) ) {
                player.disconnect( color( actions.get( action )[0] ) );
            } else if ( action.equals( "alert" ) ) {
                ProxyServer.getInstance().broadcast( color( actions.get( action )[0] ).replace( "{player}", player.getDisplayName() ) );
            } else if ( action.equals( "command" ) ) {
                player.chat( "/" + actions.get( action )[0] );
            } else if ( action.equals( "remove" ) ) {
                message = message.replaceAll( regex.pattern(), "" );
            } else if ( action.equals( "replace" ) ) {
                Random rand = new Random();
                Matcher m = getMatcher( message );
                StringBuilder sb = new StringBuilder();
                int last = 0;
                while ( m.find() ) {
                    if ( !m.group().matches( ignore.pattern() ) ) {
                        int n = rand.nextInt( actions.get( action ).length );
                        sb.append( message.substring( last, m.start() ) );
                        sb.append( actions.get( action )[n] );
                        last = m.end();
                    }
                }
                sb.append( message.substring( last ) );
                message = sb.toString();
            } else if ( action.equals( "lower" ) ) {
                Matcher m = getMatcher( message );
                StringBuilder sb = new StringBuilder();
                int last = 0;
                while ( m.find() ) {
                    if ( !m.group().matches( ignore.pattern() ) ) {
                        sb.append( message.substring( last, m.start() ) );
                        sb.append( m.group( 0 ).toLowerCase() );
                        last = m.end();
                    }
                }
                sb.append( message.substring( last ) );
                message = sb.toString();
            }
        }
        event.setMessage( message );
    }

    public String color( String s ) {
        return ChatColor.translateAlternateColorCodes( '&', s );
    }

    public boolean hasPermission() {
        return permission != null;
    }

    public String getPermission() {
        return permission;
    }
}
