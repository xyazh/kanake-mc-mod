package com.xyazh.kanake.entity;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityFireBallLookAt extends EntityFireBall{
    protected EntityLivingBase target = null;
    protected int lockAge = 20;

    public EntityFireBallLookAt(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    public void findTarget() {
        AxisAlignedBB aabb = new AxisAlignedBB(
                this.posX + 8, this.posY + 8, this.posZ + 8,
                this.posX - 8, this.posY - 8, this.posZ - 8);
        EntityLivingBase target = null;
        for (EntityLivingBase entity:
                this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (e) -> !e.equals(this.shootingEntity))) {
            if(target == null){
                target = entity;
                continue;
            }
            double distance1 = this.getDistance(entity);
            double distance2 = this.getDistance(target);
            if(distance1 < distance2){
                target = entity;
            }
        }
        this.target = target;
    }

    public boolean hasNoGravity() {
        return this.target != null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.lockAge > 0) {
            this.lockAge -= 1;
        } else if (this.target != null) {
            Vec3d targetPos = new Vec3d(this.target.posX, this.target.posY, this.target.posZ);
            Vec3d thisPos = new Vec3d(this.posX, this.posY, this.posZ);
            this.speed += 0.002;
            Vec3d motion = new Vec3d();
            motion.sub(targetPos, thisPos);
            motion.normalize();
            this.forward.set(motion);
            motion.mul(this.speed * 1.5);
            this.motionX = motion.x;
            this.motionY = motion.y;
            this.motionZ = motion.z;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            if (!this.target.isEntityAlive()) {
                this.target = null;
            }
        } else {
            this.findTarget();
        }
    }

    protected BlockPos getBlockPosition() {
        if (this.blockPos == null) {
            this.blockPos = this.getPosition();
        }
        return this.blockPos;
    }

    protected boolean onHurt() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(posX + 0.4, posY + 0.4, posZ + 0.4, posX - 0.4, posY - 0.4, posZ - 0.4);
        List<EntityLivingBase> entityLivingBases = world.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB);
        boolean flag = false;
        for (EntityLivingBase entityLivingBase : entityLivingBases) {
            if (entityLivingBase.equals(this.shootingEntity)) {
                continue;
            }
            flag = true;
            DamageSource damageSource = DamageSource.causeIndirectDamage(this, this.shootingEntity);
            damageSource.setFireDamage();
            entityLivingBase.attackEntityFrom(damageSource, 3);
            entityLivingBase.setFire(3);
        }
        return flag;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }
}
