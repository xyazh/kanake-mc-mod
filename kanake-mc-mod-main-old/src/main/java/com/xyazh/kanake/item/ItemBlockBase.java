package com.xyazh.kanake.item;

import com.xyazh.kanake.item.items.ItemBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockBase extends ItemBlock {
    public String[] oreDictName = {};

    public ItemBlockBase(Block block) {
        super(block);
    }

    public ItemBlockBase setOreDict(String[] oreDictName){
        this.oreDictName = oreDictName;
        return this;
    }
}
