package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class FlyPacket implements IMessage {
    public boolean keyDown = false;
    @Override
    public void fromBytes(ByteBuf buf) {
        this.keyDown = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.keyDown);
    }
}
