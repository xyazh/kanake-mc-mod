package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SpawnParticlesPacket implements IMessage {
    public int id = 0;
    public double x = 0;
    public double y = 0;
    public double z = 0;
    public double speedX = 0;
    public double speedY = 0;
    public double speedZ = 0;
    public int n = 0;
    public int txId = -1;

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        speedX = buf.readDouble();
        speedY = buf.readDouble();
        speedZ = buf.readDouble();
        n = buf.readInt();
        txId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(speedX);
        buf.writeDouble(speedY);
        buf.writeDouble(speedZ);
        buf.writeInt(n);
        buf.writeInt(txId);
    }
}
