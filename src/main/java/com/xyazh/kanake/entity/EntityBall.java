package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.EntityDataPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


public abstract class EntityBall extends EntityShoot {
    public float radius;
    public int color;
    public int alpha;
    protected boolean shouldSyncRadius = false;
    public boolean hasNoGravity()
    {
        return true;
    }

    public EntityBall(World worldIn) {
        super(worldIn);
        this.setRadius(0.5f);
        this.speed = 0.0f;
        this.color = 0X000000;
        this.alpha = 255;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.shouldSyncRadius) {
            this.trySyncRadius();
        }
    }

    @Override
    protected void entityInit() {

    }

    public void setRadius(float radius) {
        this.radius = radius;
        float length = 2 * this.radius;
        this.setSize(length, length);
        if(!this.world.isRemote){
            this.shouldSyncRadius = true;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setRadius(compound.getFloat("radius"));
        this.color = compound.getInteger("color");
        this.alpha = compound.getInteger("alpha");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setFloat("radius", this.radius);
        compound.setInteger("color", this.color);
        compound.setInteger("alpha", this.alpha);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(this.radius);
        buffer.writeInt(this.color);
        buffer.writeInt(this.alpha);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        this.setRadius(buffer.readFloat());
        this.color = buffer.readInt();
        this.alpha = buffer.readInt();
    }

    public void trySyncRadius() {
        if (!this.world.isRemote) {
            this.shouldSyncSpeed = false;
            EntityDataPacket packet = EntityDataPacket.getPacket(this);
            if (packet == null) {
                return;
            }
            packet.buffer.writeInt(2);
            packet.buffer.writeFloat(this.radius);
            Kanake.network.sendToAll(packet);
        }
    }

    @Override
    public int readData(ByteBuf buf) {
        int type = super.readData(buf);
        if(type==2){
            this.setRadius(buf.readFloat());
        }
        return type;
    }
}
