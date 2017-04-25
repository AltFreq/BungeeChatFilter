/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.minecraftdimensions.bungeechatfilter;

import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;

/**
 *
 * @author zedwick
 */
public class util {

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static TextComponent MessageFromStringList(List<String> StringList, ChatEvent event) {
        TextComponent message = new TextComponent();
        
        for (Iterator<String> it = StringList.iterator(); it.hasNext();) {
            String string = it.next();
            string = ParseVariables(string, event);
            String newLine = "";
            if ( it.hasNext() ) {
                newLine = "\n";
            }
            message.addExtra(new TextComponent(StringToBaseComponent(string+newLine)));
        }
        
        return message;
    }
    
    public static BaseComponent[] StringToBaseComponent(String string) {
        return TextComponent.fromLegacyText(color(string));
    }
    
    public static String ParseVariables(String string, ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        String arguments = "";
        
        if(message.split( " ", 2 ).length>1) {
            arguments =  message.split( " ", 2 )[1];
        }
        
        string = string
                .replace("{player}", player.getDisplayName())
                .replace("{message}", message)
                .replace("{arguments}", arguments);
        
        
        
        return string;
    }
}
