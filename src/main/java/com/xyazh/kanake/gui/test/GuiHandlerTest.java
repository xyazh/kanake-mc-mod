package com.xyazh.kanake.gui.test;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class GuiHandlerTest implements IGuiHandler {
    public static final int GUI_ID = 1;

    public GuiHandlerTest()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(Kanake.instance, this);
    }

    @Override
    @Nullable
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == GUI_ID) {
            ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (itemStack.isEmpty()){
                itemStack = player.getHeldItem(EnumHand.OFF_HAND);
            }
            if(itemStack.isEmpty()){
                return null;
            }
            return new ContainerTest(player,itemStack);
        }
        return null;
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == GUI_ID) {
            ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (itemStack.isEmpty()){
                itemStack = player.getHeldItem(EnumHand.OFF_HAND);
            }
            if(itemStack.isEmpty()){
                return null;
            }
            return new GuiContainerTest(new ContainerTest(player,itemStack));
        }
        return null;
    }
}
