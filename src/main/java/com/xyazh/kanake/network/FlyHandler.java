package com.xyazh.kanake.network;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.events.FlyEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class FlyHandler implements IMessageHandler<FlyPacket, IMessage> {
    @Override
    public IMessage onMessage(FlyPacket message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                FlyEvent.PLAYER_MAP.put(serverPlayer,message.keyDown);
            });
        }
        return null;
    }
}
