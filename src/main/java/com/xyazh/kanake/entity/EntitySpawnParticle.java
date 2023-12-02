package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.ModParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySpawnParticle extends Entity implements IEntityAdditionalSpawnData {
    protected double x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
    protected int age = 0;
    protected int itemId = 0;

    public EntitySpawnParticle(World worldIn) {
        super(worldIn);
        this.setSize(0,0);
    }

    public EntitySpawnParticle(World worldIn, int maxAge,int itemId) {
        this(worldIn);
        this.age = maxAge;
        this.itemId = itemId;
    }

    public EntitySpawnParticle(World worldIn, int maxAge, int itemId, Vec3d begin, Vec3d end) {
        this(worldIn, maxAge, itemId, begin.x, begin.y, begin.z, end.x, end.y, end.z);
    }

    public EntitySpawnParticle(World worldIn, int maxAge, int itemId, double x1, double y1, double z1, double x2, double y2, double z2) {
        this(worldIn, maxAge, itemId);
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.setPosition(x1,y1,z1);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        if (this.age-- < 0) {
            this.setDead();
            return;
        }
        if (!this.world.isRemote) {
            return;
        }
        this.workParticle();
    }

    public void workParticle(){
        double rx,ry,rz,rx1,ry1,rz1,dn;
        dn = 0.1;
        rx = Kanake.rand.nextGaussian()*dn;
        ry = Kanake.rand.nextGaussian()*dn;
        rz = Kanake.rand.nextGaussian()*dn;
        rx1 = Kanake.rand.nextGaussian()*dn;
        ry1 = Kanake.rand.nextGaussian()*dn;
        rz1 = Kanake.rand.nextGaussian()*dn;
        for(int i=0;i<10;i++){
            this.world.spawnParticle(ModParticles.MANA_PARTICLES,
                    this.x1+rx,this.y1+ry,this.z1+rz,
                    this.x2+rx1,this.y2+ry1,this.z2+rz1,
                    this.itemId);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeDouble(this.x1);
        buffer.writeDouble(this.y1);
        buffer.writeDouble(this.z1);
        buffer.writeDouble(this.x2);
        buffer.writeDouble(this.y2);
        buffer.writeDouble(this.z2);
        buffer.writeInt(this.age);
        buffer.writeInt(this.itemId);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.x1 = buffer.readDouble();
        this.y1 = buffer.readDouble();
        this.z1 = buffer.readDouble();
        this.x2 = buffer.readDouble();
        this.y2 = buffer.readDouble();
        this.z2 = buffer.readDouble();
        this.age = buffer.readInt();
        this.itemId = buffer.readInt();
    }
}
