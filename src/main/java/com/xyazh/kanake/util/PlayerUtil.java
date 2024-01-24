package com.xyazh.kanake.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class PlayerUtil {
    public static int FindItemInIvtrGeneralized(EntityPlayer player, Class<? extends Item> itemClass)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            {
                //itemClass.getClass();
                if (itemClass.isAssignableFrom(itemstack.getItem().getClass()))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public static ItemStack FindStackInIvtrGeneralized(EntityPlayer player, Class<? extends Item> itemClass)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            {
                //itemClass.getClass();
                if (itemClass.isAssignableFrom(itemstack.getItem().getClass()))
                {
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static int FindItemInIvtr(EntityPlayer player, Item item)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            {
                if (itemstack.getItem() == item)
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public static ItemStack FindStackInIvtr(EntityPlayer player, Item item)
    {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            {
                if (itemstack.getItem() == item)
                {
                    return itemstack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isCreative(EntityPlayer player)
    {
        return player.capabilities.isCreativeMode;
    }

    public static boolean giveToPlayer(EntityPlayer player, ItemStack stack)
    {
        boolean result = player.addItemStackToInventory(stack);
        if (!result)
        {
            player.dropItem(stack, false);
        }
        return result;
    }

    public static boolean isMainHandEmpty(EntityPlayer player){
        return player.getHeldItem(EnumHand.MAIN_HAND).isEmpty();
    }

    public static ItemStack countMainHandItemStack(EntityPlayer player,int n){
        if(isMainHandEmpty(player)){
            return ItemStack.EMPTY;
        }
        ItemStack handItemStack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        int hasN = handItemStack.getCount();
        int c = Math.min(n,hasN);
        int lastN = hasN - c;
        if(lastN <= 0){
            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,ItemStack.EMPTY);
            return handItemStack;
        }else{
            handItemStack.setCount(lastN);
            ItemStack itemStack = handItemStack.copy();
            itemStack.setCount(c);
            return itemStack;
        }
    }
}
