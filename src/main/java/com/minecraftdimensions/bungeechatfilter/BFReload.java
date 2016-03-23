package com.minecraftdimensions.bungeechatfilter;

import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Bloodsplat
 * Date: 21/10/13
 */
public class BFReload extends Command {


    public BFReload( String name, String permission, String... aliases ) {
        super( name, permission, aliases);
    }

    @Override
    public void execute( CommandSender sender, String[] args ) {
        MainConfig.c = new Config( MainConfig.configpath );
        Main.RULES.clear();
        Main.c = MainConfig.c;
        Main.COMMANDS = Main.c.getBoolean( "Monitor Commands", true );
        List<String> defaultList = new ArrayList<>();
        defaultList.add( "message" );
        defaultList.add( "msg" );
        Main.COMLIST = Main.c.getListString( "Commands", defaultList );
        Main.NOSPAM = Main.c.getBoolean( "AntiSpam", true );
        Main.loadRules();
        sender.sendMessage(new TextComponent(  "BungeeFilter Reloaded" ));
    }
}
