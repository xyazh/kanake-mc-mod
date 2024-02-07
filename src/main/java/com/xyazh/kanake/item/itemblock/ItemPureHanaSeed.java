package com.xyazh.kanake.item.itemblock;

import com.xyazh.kanake.item.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ItemPureHanaSeed extends ItemBlockBase {
    public ItemPureHanaSeed(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack)
    {
        return this.getUnlocalizedName();
    }

    @Nonnull
    @Override
    public String getUnlocalizedName()
    {
        ResourceLocation location = this.getRegistryName();
        if(location==null){
            return super.getUnlocalizedName();
        }
        return "item." + location.getResourcePath();
    }
}
