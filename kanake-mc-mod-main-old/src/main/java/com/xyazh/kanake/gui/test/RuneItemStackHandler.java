package com.xyazh.kanake.gui.test;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class RuneItemStackHandler extends ItemStackHandler {
    protected ItemStack itemStack;

    public RuneItemStackHandler(ItemStack itemStack)
    {
        super(54);
        this.itemStack = itemStack;
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
        compound.setTag("rune_command",this.serializeNBT());
        this.itemStack.setTagCompound(compound);
    }

    public void loadFromItem()
    {
        NBTTagCompound compound = this.itemStack.getTagCompound();
        if(compound!=null){
            if(!compound.hasKey("rune_command")){
                return;
            }
            this.deserializeNBT(compound.getCompoundTag("rune_command"));
        }
    }
}
