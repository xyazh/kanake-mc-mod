package com.xyazh.kanake.gui.test;

import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ContainerTest extends Container
{
    public final String[] strArr = new String[78];
    public ContainerTest()
    {
        super();
        Arrays.fill(strArr, "");
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModItems.TEST_ITEM || playerIn.getHeldItem(EnumHand.OFF_HAND).getItem() == ModItems.TEST_ITEM;
    }
}