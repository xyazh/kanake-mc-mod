package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.util.Vec3d;
import com.xyazh.kanake.util.WSHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class EntityWSSoulBullet extends EntityLiving implements IProjectile {
    public EntityLivingBase owner;
    public Entity target;
    public float speed = 0.6f;
    public float damage = 4.0f;
    public UUID ownerUniqueId;
    public UUID targetUniqueId;
    public BlockPos ownerBlockPos = BlockPos.ORIGIN;
    public BlockPos targetBlockPos = BlockPos.ORIGIN;
    public int livingTickTime = 0;
    public Vec3d a = new Vec3d(0, 0, 0);
    public Vec3d v = new Vec3d(0, 0, 0);

    public EntityWSSoulBullet(World worldIn) {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
        this.noClip = true;
    }

    @Nonnull
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }


    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("LivingTickTime", this.livingTickTime);
        compound.setDouble("acceleration.x", this.a.x);
        compound.setDouble("acceleration.args", this.a.y);
        compound.setDouble("acceleration.z", this.a.z);
        if (this.owner != null) {
            BlockPos blockpos = new BlockPos(this.owner);
            NBTTagCompound nbttagcompound = NBTUtil.createUUIDTag(this.owner.getUniqueID());
            nbttagcompound.setInteger("X", blockpos.getX());
            nbttagcompound.setInteger("Y", blockpos.getY());
            nbttagcompound.setInteger("Z", blockpos.getZ());
            compound.setTag("Owner", nbttagcompound);
        }

        if (this.target != null) {
            BlockPos blockpos1 = new BlockPos(this.target);
            NBTTagCompound nbttagcompound1 = NBTUtil.createUUIDTag(this.target.getUniqueID());
            nbttagcompound1.setInteger("X", blockpos1.getX());
            nbttagcompound1.setInteger("Y", blockpos1.getY());
            nbttagcompound1.setInteger("Z", blockpos1.getZ());
            compound.setTag("Target", nbttagcompound1);
        }
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        this.livingTickTime = compound.getInteger("LivingTickTime");
        this.a = new Vec3d(
                compound.getDouble("acceleration.x"),
                compound.getDouble("acceleration.args"),
                compound.getDouble("acceleration.z")
        );
        if (compound.hasKey("Owner", 10)) {
            NBTTagCompound nbttagcompound = compound.getCompoundTag("Owner");
            this.ownerUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound);
            this.ownerBlockPos = new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z"));
        }

        if (compound.hasKey("Target", 10)) {
            NBTTagCompound nbttagcompound1 = compound.getCompoundTag("Target");
            this.targetUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound1);
            this.targetBlockPos = new BlockPos(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"));
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if(!this.world.isRemote && this.livingTickTime > 800){
            this.setDead();
        }
        livingTickTime += 1;
        BlockPos blockPos =  new BlockPos(this.posX, this.posY, this.posZ);
        if(!this.world.isRemote && !this.world.getBlockState(blockPos).getBlock().isPassable(world,blockPos)){
            this.setDead();
        }
        if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }

        if (this.world.isRemote)
        {
            this.world.spawnParticle(EnumParticleTypes.END_ROD, this.posX - this.motionX, this.posY - this.motionY + 0.15D, this.posZ - this.motionZ, 0.0D, 0.0D, 0.0D);
        }
        if (!this.world.isRemote) {
            if (this.target == null && this.targetUniqueId != null) {
                for (EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.targetBlockPos.add(-2, -2, -2), this.targetBlockPos.add(2, 2, 2)))) {
                    if (entitylivingbase.getUniqueID().equals(this.targetUniqueId)) {
                        this.target = entitylivingbase;
                        break;
                    }
                }

                this.targetUniqueId = null;
            }

            if (this.owner == null && this.ownerUniqueId != null) {
                for (EntityLivingBase entitylivingbase1 : this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.ownerBlockPos.add(-2, -2, -2), this.ownerBlockPos.add(2, 2, 2)))) {
                    if (entitylivingbase1.getUniqueID().equals(this.ownerUniqueId)) {
                        this.owner = entitylivingbase1;
                        break;
                    }
                }

                this.ownerUniqueId = null;
            }
            if (this.target == null || !this.target.isEntityAlive() || this.target instanceof EntityPlayer && ((EntityPlayer) this.target).isSpectator()) {
                if (!this.hasNoGravity()) {
                    this.motionY -= 0.04D;
                }
            } else {
                double dx, dy, dz;
                dx = this.target.posX - this.posX;
                dy = this.target.posY - this.posY;
                dz = this.target.posZ - this.posZ;
                this.a.x = dx;
                this.a.y = dy;
                this.a.z = dz;
                this.a.normalize();
                this.a.mul(0.04);
                this.v.add(this.a);
                double l = this.v.length();
                if (l > this.speed) {
                    this.v.mul(this.speed / l);
                }
                this.motionX = this.v.x;
                this.motionY = this.v.y;
                this.motionZ = this.v.z;
                WSHelper.rotateTowardsMovement(this, 0.5f);
            }
            if(this.collided){
                this.setDead();
            }
            if(this.onHurt()){
                this.setDead();
            }
        }
    }

    protected boolean onHurt(){
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(posX+0.4,posY+0.4,posZ+0.4,posX-0.4,posY-0.4,posZ-0.4);
        List<EntityLivingBase> entityLivingBases = world.getEntitiesWithinAABB(EntityLivingBase.class,axisAlignedBB);
        boolean flag = false;
        for(EntityLivingBase entityLivingBase:entityLivingBases){
            if(entityLivingBase.equals(this.owner)||entityLivingBase == this){
                continue;
            }
            Kanake.logger.debug(entityLivingBase);
            flag = true;
            DamageSource damageSource = DamageSource.causeIndirectDamage(this, this.owner);
            entityLivingBase.attackEntityFrom(damageSource, 4);
        }
        return flag;
    }

    public boolean isBurning() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return true;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        this.setPosition(x, y, z);
        this.speed = velocity;
    }

    public void shoot(Entity target, EntityLiving owner, double x, double y, double z, float velocity) {
        this.target = target;
        this.owner = owner;
        this.shoot(x, y, z, velocity, 0);
    }

    public void shoot(Entity target, EntityLiving owner, double x, double y, double z) {
        this.shoot(target, owner, x, y, z, this.speed);
    }

    public void shoot(Entity target, EntityLiving owner) {
        this.shoot(target, owner, owner.posX, owner.posY, owner.posZ);
    }
}