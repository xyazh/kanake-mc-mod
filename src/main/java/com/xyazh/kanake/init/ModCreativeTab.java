package com.xyazh.kanake.init;


import com.xyazh.kanake.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTab {
    public static final CreativeTabs SBW_CREATIVE = new CreativeTabs(CreativeTabs.getNextID(), "knkcreative")
    {
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(ModItems.TEST_ITEM);
        }
    };
}
