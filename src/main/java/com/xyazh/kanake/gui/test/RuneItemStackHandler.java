package com.xyazh.kanake.gui.test;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class RuneItemStackHandler extends ItemStackHandler {
    protected ItemStack itemStack;
    protected String key;

    public RuneItemStackHandler(ItemStack itemStack)
    {
        this(itemStack, "rune_order", 54);
    }

    public RuneItemStackHandler(ItemStack itemStack, String key, int size)
    {
        super(size);
        this.itemStack = itemStack;
        this.key = key;
        this.loadFromItem();
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        super.setStackInSlot(slot, stack);
        this.saveToItem();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        ItemStack ret = super.insertItem(slot, stack, simulate);
        this.saveToItem();
        return ret;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        ItemStack ret = super.extractItem(slot, amount, simulate);
        this.saveToItem();
        return ret;
    }

    public void saveToItem()
    {
        NBTTagCompound compound = this.itemStack.getTagCompound();
        if(compound==null){
            compound = new NBTTagCompound();
        }
        compound.setTag(this.key, this.serializeNBT());
        this.itemStack.setTagCompound(compound);
    }

    public void loadFromItem()
    {
        NBTTagCompound compound = this.itemStack.getTagCompound();
        if(compound!=null){
            if(!compound.hasKey(this.key)){
                return;
            }
            this.deserializeNBT(compound.getCompoundTag(this.key));
        }
    }
}
