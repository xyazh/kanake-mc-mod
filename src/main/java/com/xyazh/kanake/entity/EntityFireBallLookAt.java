package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
                this.posX + 16, this.posY + 16, this.posZ + 16,
                this.posX - 16, this.posY - 16, this.posZ - 16);
        for (EntityLivingBase entityLivingBase :
                this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (e) -> !e.equals(this.shootingEntity))) {
            this.target = entityLivingBase;
            break;
        }
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
            Vec3d motion = targetPos.subtract(thisPos).normalize();
            motion = motion.scale(this.speed * 1.5);
            this.motionX = motion.x;
            this.motionY = motion.y;
            this.motionZ = motion.z;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            if (this.target.isDead) {
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
}
