package com.xyazh.kanake.init;


import com.xyazh.kanake.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ModCreativeTab {
    public static final CreativeTabs KNK_CREATIVE = new CreativeTabs(CreativeTabs.getNextID(), "knkcreative")
    {
        @Nonnull
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.TEST_ITEM);
        }
    };

    public static final CreativeTabs KNK_EMBLEM = new CreativeTabs(CreativeTabs.getNextID(), "emblemcreative")
    {
        @Nonnull
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.EMBLEM_SPAWN);
        }
    };

}
