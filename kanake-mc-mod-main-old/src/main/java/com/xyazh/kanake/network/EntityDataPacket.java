package com.xyazh.kanake.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class EntityDataPacket implements IMessage {
    public int id = -1;
    public Entity entity;
    public IEntityDataParameter parameter;
    public final ByteBuf buffer = new PacketBuffer(Unpooled.buffer());

    @Nullable
    public static EntityDataPacket getPacket(Entity entity) {
        if (entity instanceof IEntityDataParameter) {
            return new EntityDataPacket(entity, (IEntityDataParameter) entity);
        }
        return null;
    }

    public EntityDataPacket() {
    }

    public EntityDataPacket(Entity entity, IEntityDataParameter parameter) {
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
        buf.writeBytes(this.buffer);
    }

}
