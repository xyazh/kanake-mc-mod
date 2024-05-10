package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.network.IEntityDataParameter;
import com.xyazh.kanake.util.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public abstract class EntityShoot extends Entity implements IProjectile, IEntityAdditionalSpawnData, IEntityDataParameter {
    protected int livingMaxAge = 600;
    protected int livingAge = 0;
    protected Vec3d forward = new Vec3d(0,1,0);
    public double speed = 1.0;
    public EntityLivingBase shootingEntity = null;
    protected BlockPos blockPos = null;
    protected double dy = 0;
    protected int explosionKeepAge = 0;
    protected boolean shouldSync = false;
    public Explosion lastExplosion = null;

    public EntityShoot(World worldIn) {
        super(worldIn);
        this.setSize(0.01f, 0.01f);
    }

    protected boolean isHurt(){
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(posX+0.4,posY+0.4,posZ+0.4,posX-0.4,posY-0.4,posZ-0.4);
        List<EntityLivingBase> entityLivingBases = world.getEntitiesWithinAABB(EntityLivingBase.class,axisAlignedBB);
        boolean flag = false;
        for(EntityLivingBase entityLivingBase:entityLivingBases){
            if(entityLivingBase.equals(this.shootingEntity)){
                continue;
            }
            flag = true;
        }
        return flag;
    }

    public boolean hasNoGravity()
    {
        return false;
    }

    protected boolean customMotion(){
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.shouldSync){
            this.trySyncSpeed();
        }
        if(this.livingAge++ > this.livingMaxAge){
            if(!this.world.isRemote){
                this.setDead();
            }
        }
        if(this.customMotion()){
            return;
        }
        this.motionX = this.speed * forward.x;
        this.motionZ = this.speed * forward.z;
        this.blockPos = this.getPosition();
        this.explosionKeepAge -= 1;
        if(this.hasNoGravity()){
            this.motionY = this.speed * forward.y;
        }else{
            dy -= 0.0054;
            this.motionY = this.speed * forward.y + dy;
        }
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    public void setForwardUnlimited(Vec3d forward) {
        this.forward.set(forward);
    }

    public void setForward(Vec3d forward) {
        this.forward.set(forward);
        this.forward.normalize();
    }

    public void setForward(net.minecraft.util.math.Vec3d forward) {
        this.forward.set(forward);
        this.forward.normalize();
    }

    public void setLivingMaxAge(int livingMaxAge) {
        this.livingMaxAge = livingMaxAge;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        this.setPosition(x,y,z);
        this.speed = velocity;
    }

    public void shoot(double x, double y, double z) {
        this.setPosition(x,y,z);
    }

    public void entityShoot(EntityLivingBase owner, Vec3d direction,float velocity, float inaccuracy){
        double x,y,z;
        x = owner.posX + 0.5*direction.x;
        y = owner.posY + 1 + 0.5*direction.y;
        z = owner.posZ + 0.5*direction.z;
        this.setForward(direction);
        this.shoot(x,y,z,velocity,inaccuracy);
        this.shootingEntity = owner;
    }

    public void entityShoot(EntityLivingBase owner, Vec3d direction){
        double x,y,z;
        x = owner.posX + 0.5*direction.x;
        y = owner.posY + 1 + 0.5*direction.y;
        z = owner.posZ + 0.5*direction.z;
        this.setForward(direction);
        this.shoot(x,y,z);
        this.shootingEntity = owner;
    }

    public void entityShoot(EntityLivingBase owner, net.minecraft.util.math.Vec3d direction, float velocity, float inaccuracy){
        double x,y,z;
        x = owner.posX + 0.5*direction.x;
        y = owner.posY + 1 + 0.5*direction.y;
        z = owner.posZ + 0.5*direction.z;
        this.setForward(direction);
        this.shoot(x,y,z,velocity,inaccuracy);
        this.shootingEntity = owner;
    }

    public void entityShoot(EntityLivingBase owner, net.minecraft.util.math.Vec3d direction){
        double x,y,z;
        x = owner.posX + 0.5*direction.x;
        y = owner.posY + 1 + 0.5*direction.y;
        z = owner.posZ + 0.5*direction.z;
        this.setForward(direction);
        this.shoot(x,y,z);
        this.shootingEntity = owner;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.livingMaxAge = compound.getInteger("livingMaxAge");
        this.livingAge = compound.getInteger("livingAge");
        this.forward = new Vec3d(
                compound.getDouble("forward.x"),
                compound.getDouble("forward.args"),
                compound.getDouble("forward.z")
        );
        this.speed = compound.getDouble("speed");
        this.dy = compound.getDouble("dy");
        UUID id = compound.getUniqueId("shootingEntity");
        if(id != null){
            this.shootingEntity = this.world.getPlayerEntityByUUID(id);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("livingMaxAge",this.livingMaxAge);
        compound.setInteger("livingAge",this.livingAge);
        compound.setDouble("forward.x",this.forward.x);
        compound.setDouble("forward.args",this.forward.y);
        compound.setDouble("forward.z",this.forward.z);
        compound.setDouble("speed",this.speed);
        compound.setDouble("dy",this.dy);
        if(this.shootingEntity!=null){
            compound.setUniqueId("shootingEntity",this.shootingEntity.getUniqueID());
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.livingMaxAge);
        buffer.writeInt(this.livingAge);
        buffer.writeDouble(this.forward.x);
        buffer.writeDouble(this.forward.y);
        buffer.writeDouble(this.forward.z);
        buffer.writeDouble(this.speed);
        buffer.writeDouble(this.dy);
        boolean hasPlayerShooter = this.shootingEntity instanceof EntityPlayer;
        buffer.writeBoolean(hasPlayerShooter);
        if(hasPlayerShooter){
            UUID id = this.shootingEntity.getUniqueID();
            buffer.writeLong(id.getMostSignificantBits());
            buffer.writeLong(id.getLeastSignificantBits());
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.livingMaxAge = buffer.readInt();
        this.livingAge = buffer.readInt();
        this.forward = new Vec3d(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
        this.speed = buffer.readDouble();
        this.dy = buffer.readDouble();
        boolean hasPlayerShooter = buffer.readBoolean();
        if(hasPlayerShooter){
            long highBits = buffer.readLong();
            long lowBits = buffer.readLong();
            UUID id = new UUID(highBits, lowBits);
            this.shootingEntity = this.world.getPlayerEntityByUUID(id);
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if(source.isExplosion()){
            if(amount>25){
                this.isDead = true;
            }else if(this.lastExplosion != null){
                Vec3d vec3d = new Vec3d();
                vec3d.set(this.lastExplosion.getPosition());
                Vec3d thisPos = new Vec3d(this.posX, this.posY, this.posZ);
                thisPos.sub(vec3d);
                thisPos.normalize();
                thisPos.mul(this.speed * amount / 20);
                Vec3d froward = new Vec3d();
                froward.set(this.forward);
                froward.mul(this.speed);
                froward.add(thisPos);
                this.speed = froward.normalizeAndLength();
                this.forward = froward;
                this.shouldSync = true;
            }
        }
        return true;
    }

    public void trySyncSpeed(){
        if(!this.world.isRemote){
            this.shouldSync = false;
            EntityDataPacket packet = EntityDataPacket.getPacket(this);
            if (packet == null) {
                return;
            }
            packet.buffer.writeInt(1);
            packet.buffer.writeDouble(this.speed);
            packet.buffer.writeDouble(this.forward.x);
            packet.buffer.writeDouble(this.forward.y);
            packet.buffer.writeDouble(this.forward.z);
            Kanake.network.sendToAll(packet);
        }
   }

    @Override
    public int readData(ByteBuf buf) {
        int type = buf.readInt();
        if(type == 1){
            this.speed = buf.readDouble();
            this.forward = new Vec3d(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        }
        return type;
    }
}
