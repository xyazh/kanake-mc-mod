package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.entity.EntityForeverItem;
import net.minecraft.item.ItemStack;

public interface ITileForeverEntity {
    boolean checkItem(ItemStack itemStack);
    double getFEY();
    void setFeEntity();
}
