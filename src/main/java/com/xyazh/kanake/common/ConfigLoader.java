package com.xyazh.kanake.common;

import com.xyazh.kanake.Kanake;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigLoader
{
    private static Configuration config;
    public static int diamondBurnTime;

    public static void init(FMLPreInitializationEvent event){
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        load();
    }

    public static void load()
    {
        Kanake.logger.info("Started loading config. ");
        String comment;
        comment = "How many seconds can acceleration diamond burn in acceleration furnace. ";
        diamondBurnTime = config.get("pedestal_recipes", "diamondBurnTime", 640, comment).getInt();
        config.save();
        Kanake.logger.info("Finished loading config. ");
    }
}