package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat( ChatEvent e ) throws SQLException {
        if ( e.getSender() instanceof ProxiedPlayer ) {
            ProxiedPlayer player = ( ProxiedPlayer ) e.getSender();
            if ( !player.hasPermission( "bungeefilter.bypass" ) ) {
                if ( !Main.COMMANDS && isChatCommand( e.getMessage() ) ) {
                    return;
                }
                if ( Main.NOSPAM ) {
                    if ( spamCheck( player, e.getMessage() ) ) {
                        e.setCancelled( true );
                        player.sendMessage( ChatColor.RED + "Please do not spam" );
                        return;
                    } else {
                        Main.ANTISPAM.put( player, e.getMessage() );
                    }
                }
                for ( Rule r : Main.RULES ) {
                    if(r.hasPermission()){
                        if(!r.needsPerm && player.hasPermission( r.getPermission() )){
                            return;
                        }
                        if(r.needsPerm && !player.hasPermission( r.getPermission() )){
                            return;
                        }
                    }
                    if ( r.doesMessageContainRegex( e.getMessage() ) ) {
                        r.performActions( e, player );
                    }
                }
            }
        }

    }

    private boolean spamCheck( ProxiedPlayer player, String message ) {
        if(isChatCommand( message ) && !isMonitoredCommand( message )){
            return false;
        }
        if ( Main.ANTISPAM.containsKey( player ) ) {
            return Main.ANTISPAM.get( player ).equals( message );
        }
        return false;
    }

    public boolean isChatCommand( String message ) {
        return message.startsWith( "/" );
    }

    public boolean isMonitoredCommand(String command){
        return  Main.COMLIST.contains( command.substring( 1, command.length() ).split( " " )[0]);
    }

}
