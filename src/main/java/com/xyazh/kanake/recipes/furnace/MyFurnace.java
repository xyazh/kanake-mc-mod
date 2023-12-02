package com.xyazh.kanake.recipes.furnace;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class MyFurnace {
    @SubscribeEvent
    public static void getVanillaFurnaceFuelValue(FurnaceFuelBurnTimeEvent event) {
    }

    public static void addFurnaceRecipes(){
        GameRegistry.addSmelting(ModItems.WS_POLLUTE_SALT, new ItemStack(ModItems.DUST_MAGIC), 0);
    }
}
