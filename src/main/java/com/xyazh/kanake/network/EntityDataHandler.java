package com.xyazh.kanake.network;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.particles.WanaParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class EntityDataHandler implements IMessageHandler<EntityDataPacket, IMessage> {
    @Override
    public IMessage onMessage(EntityDataPacket message, MessageContext ctx) {
        //判断是否为客户端（接收端）
        if (ctx.side == Side.CLIENT) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
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
