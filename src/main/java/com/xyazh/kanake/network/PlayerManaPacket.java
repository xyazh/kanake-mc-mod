package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class PlayerManaPacket implements IMessage {
    public double mana = 0;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.mana = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.mana);
    }
}
