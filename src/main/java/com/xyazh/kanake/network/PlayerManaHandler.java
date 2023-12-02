package com.xyazh.kanake.network;

import com.xyazh.kanake.data.ManaData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerManaHandler implements IMessageHandler<PlayerManaPacket, IMessage> {
    @Override
    public IMessage onMessage(PlayerManaPacket message, MessageContext ctx) {
        //判断是否为客户端（接收端）
        if (ctx.side == Side.CLIENT) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
                    double mana = message.mana;
                    ManaData.set(player,mana);
                }
            });
        }
        return null;
    }
}
