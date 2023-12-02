package com.xyazh.kanake.network;

import com.xyazh.kanake.events.KooriEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class KooriEntityHandler implements IMessageHandler<KooriEntityPacket, IMessage> {
    @Override
    public IMessage onMessage(KooriEntityPacket message, MessageContext ctx) {
        //判断是否为客户端（接收端）
        if (ctx.side == Side.CLIENT) {
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if(message.type){
                        KooriEvent.clientEntitySet.add(message.id);
                    }else{
                        KooriEvent.clientEntitySet.remove(message.id);
                    }
                }
            });
        }
        return null;
    }
}
