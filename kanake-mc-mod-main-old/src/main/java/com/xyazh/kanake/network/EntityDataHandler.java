package com.xyazh.kanake.network;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EntityDataHandler implements IMessageHandler<EntityDataPacket, IMessage> {
    @Override
    public IMessage onMessage(EntityDataPacket message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                net.minecraft.client.multiplayer.WorldClient world = net.minecraft.client.Minecraft.getMinecraft().world;
                if (world == null) {
                    Kanake.logger.warn("EntityPacket too early: {}", message.id);
                    return;
                }
                Entity entity = world.getEntityByID(message.id);
                if (entity == null) {
                    Kanake.logger.warn("Entity Not Found: {}", message.id);
                    return;
                }
                if(!(entity instanceof IEntityDataParameter)){
                    Kanake.logger.warn("Entity Not IEntityDataParameter: {}", message.id);
                    return;
                }
                IEntityDataParameter parameter = (IEntityDataParameter) entity;
                parameter.readData(message.buffer);
            });
        }
        return null;
    }
}
