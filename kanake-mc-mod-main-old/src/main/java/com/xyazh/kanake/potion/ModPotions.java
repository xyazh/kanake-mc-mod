package com.xyazh.kanake.potion;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.potion.buff.PotionKoori;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class ModPotions {
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> evt)
    {
        evt.getRegistry().register(PotionKoori.POTION_KOORI);
    }
}
