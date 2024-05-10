package com.xyazh.kanake.network;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SpawnParticlesHandler implements IMessageHandler<SpawnParticlesPacket, IMessage> {
    @Override
    public IMessage onMessage(SpawnParticlesPacket message, MessageContext ctx) {
        //判断是否为客户端（接收端）
        if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    //获取客户端（接收端）玩家对象
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    World world = player.world;
                    EnumParticleTypes particleTypes = ModParticles.myParticlesString.get(message.particleName);
                    if(particleTypes == null){
                        Kanake.logger.warn("particle name not found: " + message.particleName);
                        return;
                    }
                    for (int i = 0; i < message.count; i++){
                        world.spawnParticle(particleTypes, message.x, message.y, message.z, message.xSpeed, message.ySpeed, message.zSpeed, message.parameters);
                    }
                }
            });
        }
        return null;
    }
}
