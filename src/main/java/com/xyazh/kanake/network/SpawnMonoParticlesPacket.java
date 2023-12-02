package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SpawnMonoParticlesPacket implements IMessage {
    public boolean isEnd = false;
    public double x = 0;
    public double y = 0;
    public double z = 0;
    public double speedX = 0;
    public double speedY = 0;
    public double speedZ = 0;

    @Override
    public void fromBytes(ByteBuf buf) {
        isEnd = buf.readBoolean();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        speedX = buf.readDouble();
        speedY = buf.readDouble();
        speedZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isEnd);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(speedX);
        buf.writeDouble(speedY);
        buf.writeDouble(speedZ);
    }
}
