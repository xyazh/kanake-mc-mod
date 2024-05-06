package com.xyazh.kanake.gui.test;

import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ContainerTest extends Container
{
    public final RuneItemStackHandler magicInv;

    public ContainerTest(EntityPlayer player,ItemStack itemStack)
    {
        int numRows = 6;
        int i = (numRows - 4) * 18;
        this.magicInv = new RuneItemStackHandler(itemStack);
        for (int j = 0; j < numRows; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new SlotItemHandler(this.magicInv, k + j * 9, 8 + k * 18, 18 + j * 18){
                    @Override
                    public boolean isItemValid(@Nonnull ItemStack stack)
                    {
                        return true;
                    }

                    @Override
                    public int getItemStackLimit(@Nonnull ItemStack stack)
                    {
                        return 1;
                    }
                });
            }
        }

        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(player.inventory, j1 + l * 9 + 9, 8 + j1 * 18, 104 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(player.inventory, i1, 8 + i1 * 18, 162 + i));
        }
    }



    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index)
    {
        return ItemStack.EMPTY;
    }



    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.RUNE_EMPTY_MAGIC || playerIn.getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.RUNE_EMPTY_MAGIC;
    }
}