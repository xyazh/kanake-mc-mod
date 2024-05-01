package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class EntityDataPacket implements IMessage {
    protected int id = -1;
    protected Entity entity;
    protected IEntityDataParameter parameter;
    public final ByteBuf buffer = new PacketBuffer(Unpooled.buffer());

    @Nullable
    public static EntityDataPacket getPacket(Entity entity) {
        if (entity instanceof IEntityDataParameter) {
            return new EntityDataPacket(entity, (IEntityDataParameter) entity);
        }
        return null;
    }

    protected EntityDataPacket() {
    }

    protected EntityDataPacket(Entity entity, IEntityDataParameter parameter) {
        this.id = entity.getEntityId();
        this.entity = entity;
        this.parameter = parameter;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
        this.buffer.clear();
        this.buffer.writeBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.id);
        this.parameter.writeData(this.buffer);
        buf.writeBytes(this.buffer);
    }

}
