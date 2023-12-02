package com.xyazh.kanake.network;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ManaHandler implements IMessageHandler<ManaPacket, IMessage> {
    @Override
    public IMessage onMessage(ManaPacket message, MessageContext ctx) {
        //判断是否为客户端（接收端）
        if (ctx.side == Side.CLIENT) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
                    if(player!=null){
                        NBTTagCompound compound = player.getEntityData();
                        compound.setDouble(Kanake.MODID+":mana",message.mana);
                    }
                }
            });
        }
        return null;
    }
}
