package com.minecraftdimensions.bungeechatfilter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void playerChat( ChatEvent e ) {
        if ( e.getSender() instanceof ProxiedPlayer ) {
            ProxiedPlayer player = ( ProxiedPlayer ) e.getSender();
            if ( !player.hasPermission( "bungeefilter.bypass" ) ) {
                if ( !Main.COMMANDS && isChatCommand( e.getMessage() ) ) {
                    return;
                } else if( Main.COMMANDS && isChatCommand( e.getMessage() ) && !isMonitoredCommand( e.getMessage() )){
                    return;
                }
                if ( Main.NOSPAM && !player.hasPermission( "bungeefilter.bypass.spam" ) ) {
                    if ( spamCheck( player, e.getMessage(), System.currentTimeMillis()) ) {
                        e.setCancelled( true );
                        player.sendMessage( new TextComponent( ChatColor.RED + Main.c.getString("AntiSpamMessage", "Please do not spam") ) );
                        return;
                    }
                }
                if(Main.NOREPEAT && !player.hasPermission( "bungeefilter.bypass.repeat" )){
                    if(repeatCheck(player.getName(), e.getMessage(),System.currentTimeMillis())){
                        e.setCancelled( true );
                        player.sendMessage( new TextComponent( ChatColor.RED + Main.c.getString("AntiSpamMessage", "Please do not spam") ) );
                        return;
                    }else{
                        Main.ANTIREPEAT.put( player.getName(), e.getMessage() );
                    }

                }
                Main.ANTISPAM.put( player.getName(),System.currentTimeMillis());
                for ( Rule r : Main.RULES ) {
                    if(r.hasPermission()){
                        if(!r.needsPerm && player.hasPermission( r.getPermission() )){
                            continue;
                        }
                        if(r.needsPerm && !player.hasPermission( r.getPermission() )){
                            continue;
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
        if ( Main.ANTISPAM.containsKey( player.getName() ) ) {
            Long diff = time-Main.ANTISPAM.get( player.getName() );
            return diff<Main.SPAMTIMER;
        }
        return false;
    }
    
    private boolean repeatCheck( String name, String message, long time ) {
        if ( Main.ANTISPAM.containsKey( name) && Main.ANTIREPEAT.containsKey( name ) ) {
            Long diff = time-Main.ANTISPAM.get( name );
            if ( diff<Main.REPEATTIMER ) {
                return Main.ANTIREPEAT.get( name ).equals( message );
            } else {
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void playerLogOut( PlayerDisconnectEvent e ) {
          if(Main.ANTISPAM.containsKey( e.getPlayer().getName() )){
              Main.ANTISPAM.remove( e.getPlayer().getName() );
              Main.ANTIREPEAT.remove( e.getPlayer().getName() );
          }
    }

    public boolean isChatCommand( String message ) {
        return message.startsWith( "/" );
    }

    public boolean isMonitoredCommand(String command){
        return  Main.COMLIST.contains( command.substring( 1, command.length() ).split( " " )[0]);
    }

}
