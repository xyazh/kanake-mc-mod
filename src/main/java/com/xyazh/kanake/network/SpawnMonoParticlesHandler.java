package com.xyazh.kanake.network;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SpawnMonoParticlesHandler implements IMessageHandler<SpawnParticlesPacket, IMessage> {
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
                    if(!ModParticles.myParticles.containsKey(message.id)){
                        Kanake.logger.warn("Particles id:{} -Not Found",message.id);
                        return;
                    }
                    for(int i = 0;i<message.n;i++){
                        if(message.txId>0){
                            world.spawnParticle(ModParticles.myParticles.get(message.id), message.x, message.y, message.z, message.speedX, message.speedY, message.speedZ, message.txId);
                        }else{
                            world.spawnParticle(ModParticles.myParticles.get(message.id), message.x, message.y, message.z, message.speedX, message.speedY, message.speedZ);
                        }

                    }
                }
            });
        }
        return null;
    }
}
