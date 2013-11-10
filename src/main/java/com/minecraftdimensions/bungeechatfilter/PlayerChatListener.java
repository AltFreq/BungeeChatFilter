package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.SQLException;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat( ChatEvent e ) {
        if ( e.getSender() instanceof ProxiedPlayer ) {
            ProxiedPlayer player = ( ProxiedPlayer ) e.getSender();
            if ( !player.hasPermission( "bungeefilter.bypass" ) ) {
                if ( !Main.COMMANDS && isChatCommand( e.getMessage() ) ) {
                    return;
                }
                if ( Main.NOSPAM ) {
                    if ( spamCheck( player, e.getMessage(), System.currentTimeMillis()) ) {
                        e.setCancelled( true );
                        player.sendMessage( ChatColor.RED + "Please do not spam" );
                        return;
                    } else {
                        Main.ANTISPAM.put( player,System.currentTimeMillis());
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

    private boolean spamCheck( ProxiedPlayer player,String message, long time ) {
        if(isChatCommand( message ) && !isMonitoredCommand( message )){
            return false;
        }
        if ( Main.ANTISPAM.containsKey( player ) ) {
            System.out.println(time);
            System.out.println(time-Main.ANTISPAM.get( player ));
            System.out.println(Main.SPAMTIMER);
            Long diff = time-Main.ANTISPAM.get( player );
            return diff<Main.SPAMTIMER;
        }
        return false;
    }

    @EventHandler
    public void playerLogOut( PlayerDisconnectEvent e ) {
          if(Main.ANTISPAM.containsKey( e.getPlayer() )){
              Main.ANTISPAM.remove( e.getPlayer() );
              System.out.println("removed");
          }
    }

    public boolean isChatCommand( String message ) {
        return message.startsWith( "/" );
    }

    public boolean isMonitoredCommand(String command){
        return  Main.COMLIST.contains( command.substring( 1, command.length() ).split( " " )[0]);
    }

}
