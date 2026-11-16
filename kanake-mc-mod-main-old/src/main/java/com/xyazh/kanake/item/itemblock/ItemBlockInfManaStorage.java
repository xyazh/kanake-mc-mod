package com.xyazh.kanake.item.itemblock;

import com.xyazh.kanake.item.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBlockInfManaStorage extends ItemBlockBase {
    public ItemBlockInfManaStorage(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true;
    }
}
