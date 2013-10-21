package com.minecraftdimensions.bungeechatfilter;


import com.minecraftdimensions.bungeechatfilter.configlibrary.Config;

import java.io.File;

public class MainConfig {

    public static String configpath = File.separator + "plugins" + File.separator + "BungeeChatFilter" + File.separator + "config.yml";
    public static Config c = new Config( configpath );

}
