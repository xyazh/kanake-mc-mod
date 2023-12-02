package com.xyazh.kanake.common;

import com.xyazh.kanake.Kanake;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

public class ConfigLoader
{
    private static Configuration config;

    private static Logger logger= Kanake.logger;

    public static int diamondBurnTime;

    public ConfigLoader(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        load();
    }

    public static void load()
    {
        logger.info("Started loading config. ");
        String comment;
        comment = "How many seconds can acceleration diamond burn in acceleration furnace. ";
        diamondBurnTime = config.get("pedestal_recipes", "diamondBurnTime", 640, comment).getInt();
        config.save();
        logger.info("Finished loading config. ");
    }

    public static Logger logger()
    {
        return logger;
    }
}