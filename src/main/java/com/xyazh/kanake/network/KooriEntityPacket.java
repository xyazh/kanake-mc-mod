package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class KooriEntityPacket implements IMessage {
    public UUID id;
    public boolean type = false;

    @Override
    public void fromBytes(ByteBuf buf) {
        long highBits = buf.readLong();
        long lowBits = buf.readLong();
        this.id = new UUID(highBits, lowBits);
        this.type = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
        buf.writeBoolean(this.type);
    }
}
