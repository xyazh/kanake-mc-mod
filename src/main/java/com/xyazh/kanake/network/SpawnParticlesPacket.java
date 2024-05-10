package com.xyazh.kanake.network;

import com.xyazh.kanake.util.ByteBufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SpawnParticlesPacket implements IMessage {
    public int count = 0;
    public String particleName = "";
    public float x = 0f;
    public float y = 0f;
    public float z = 0f;
    public float xSpeed = 0f;
    public float ySpeed = 0f;
    public float zSpeed = 0f;
    public int[] parameters = new int[0];

    public SpawnParticlesPacket(){}
    

    public SpawnParticlesPacket(int count,String particleName, float x, float y, float z, float xSpeed, float ySpeed, float zSpeed, int... parameters){
        this.count = count;
        this.particleName = particleName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;
        this.parameters = parameters;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.count = buf.readInt();
        this.particleName = ByteBufferUtil.readString(buf);
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.xSpeed = buf.readFloat();
        this.ySpeed = buf.readFloat();
        this.zSpeed = buf.readFloat();
        this.parameters = ByteBufferUtil.readIntArray(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.count);
        ByteBufferUtil.writeString(buf,this.particleName);
        buf.writeFloat(this.x);
        buf.writeFloat(this.y);
        buf.writeFloat(this.z);
        buf.writeFloat(this.xSpeed);
        buf.writeFloat(this.ySpeed);
        buf.writeFloat(this.zSpeed);
        ByteBufferUtil.writeIntArray(buf,this.parameters);
    }
}
