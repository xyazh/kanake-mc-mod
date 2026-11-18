package com.xyazh.kanake.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * A straight-flying ender bolt. It behaves like an ender pearl for its caster:
 * on impact the caster is moved to the impact position and takes the normal
 * five points of ender-pearl fall damage.
 */
public class EntityEnderBolt extends EntityShoot {
    public EntityEnderBolt(World worldIn) {
        super(worldIn);
        this.setSize(0.2F, 0.2F);
        this.setInvisible(true);
        this.livingMaxAge = 100;
        this.speed = 1.1D;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead) {
            return;
        }
        if (this.world.isRemote) {
            if (this.collided || this.isInWater() || this.isInLava()) {
                this.setDead();
            }
            return;
        }
        if (this.collided || this.isInWater() || this.isInLava()) {
            this.impact();
            return;
        }
        EntityLivingBase target = this.findTarget();
        if (target != null) {
            this.impact();
        }
    }

    private EntityLivingBase findTarget() {
        AxisAlignedBB box = this.getEntityBoundingBox().grow(0.75D);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        for (EntityLivingBase entity : entities) {
            if (entity == this.shootingEntity || !entity.isEntityAlive()) {
                continue;
            }
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) {
                continue;
            }
            return entity;
        }
        return null;
    }

    private void impact() {
        if (this.isDead) {
            return;
        }
        if (this.world.isRemote) {
            this.setDead();
            return;
        }

        EntityLivingBase owner = this.shootingEntity;
        if (owner != null && owner.isEntityAlive() && owner.world == this.world) {
            if (owner.isRiding()) {
                owner.dismountRidingEntity();
            }
            owner.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            owner.motionX = 0.0D;
            owner.motionY = 0.0D;
            owner.motionZ = 0.0D;
            owner.fallDistance = 0.0F;
            owner.attackEntityFrom(DamageSource.FALL, 5.0F);
        }

        for (int i = 0; i < 64; ++i) {
            this.world.spawnParticle(
                    EnumParticleTypes.PORTAL,
                    this.posX + (this.rand.nextDouble() - 0.5D) * 1.2D,
                    this.posY + (this.rand.nextDouble() - 0.5D) * 1.2D,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * 1.2D,
                    (this.rand.nextDouble() - 0.5D) * 0.2D,
                    (this.rand.nextDouble() - 0.5D) * 0.2D,
                    (this.rand.nextDouble() - 0.5D) * 0.2D
            );
        }
        this.setDead();
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        int count = this.consumeParticleBudget(18.0D, deltaTicks);
        if (count <= 0) {
            return;
        }
        double x = this.getParticleX(partialTicks);
        double y = this.getParticleY(partialTicks);
        double z = this.getParticleZ(partialTicks);
        for (int i = 0; i < count; ++i) {
            this.world.spawnParticle(
                    EnumParticleTypes.PORTAL,
                    x + (this.rand.nextDouble() - 0.5D) * 0.28D,
                    y + (this.rand.nextDouble() - 0.5D) * 0.28D,
                    z + (this.rand.nextDouble() - 0.5D) * 0.28D,
                    (this.rand.nextDouble() - 0.5D) * 0.08D,
                    (this.rand.nextDouble() - 0.5D) * 0.08D,
                    (this.rand.nextDouble() - 0.5D) * 0.08D
            );
        }
    }
}
