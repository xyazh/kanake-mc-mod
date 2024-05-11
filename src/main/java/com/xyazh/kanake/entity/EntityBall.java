package com.xyazh.kanake.entity;

import com.xyazh.kanake.util.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;


public abstract class EntityBall extends EntityShoot {
    public float radius;
    public int color;
    public int alpha;
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
    }

    @Override
    protected void entityInit() {

    }

    public void setRadius(float radius) {
        this.radius = radius;
        float length = 2 * this.radius;
        this.setSize(length, length);
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
}
