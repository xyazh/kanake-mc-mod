package com.xyazh.kanake.api;

import net.minecraft.item.ItemStack;

public interface IItemManaStorage {
    int getMaxMana(ItemStack itemStack);
    int getMana(ItemStack itemStack);
    void setMana(ItemStack itemStack, int energy);
    int addMana(ItemStack itemStack, int n);
    int subMana(ItemStack itemStack, int n);
    int remain(ItemStack itemStack);
    void setFull(ItemStack itemStack);
}
