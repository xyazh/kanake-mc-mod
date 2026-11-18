package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityLifeDrain;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A single flowing mote on a life-drain stream. The mote travels from the
 * victim towards the caster, with a small curved offset, instead of forming a
 * static line.
 */
@SideOnly(Side.CLIENT)
public class LifeDrainStreamParticle extends SimpleParticle {
    private final int sourceId;
    private final int targetId;
    private final double speed;
    private final double seed;
    private final float baseScale;
    private double progress;

    public LifeDrainStreamParticle(EntityLifeDrain source, EntityLivingBase target) {
        super(source.world, target.posX, target.posY, target.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.targetId = target.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.progress = this.rand.nextDouble() * 0.8D;
        this.speed = 0.045D + this.rand.nextDouble() * 0.035D;
        this.particleMaxAge = 200;
        this.baseScale = 0.62F + this.rand.nextFloat() * 0.30F;
        this.particleScale = this.baseScale * 0.18F;
        this.seed = this.rand.nextDouble() * Math.PI * 2.0D;
        this.setRBGColorF(
                0.06F + this.rand.nextFloat() * 0.14F,
                0.82F + this.rand.nextFloat() * 0.18F,
                0.20F + this.rand.nextFloat() * 0.20F
        );
        this.alpha = 1.0F;
        this.updatePosition(source, target);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        Entity sourceEntity = this.world.getEntityByID(this.sourceId);
        Entity targetEntity = this.world.getEntityByID(this.targetId);
        if (!(sourceEntity instanceof EntityLifeDrain) || sourceEntity.isDead
                || !(targetEntity instanceof EntityLivingBase) || !targetEntity.isEntityAlive()) {
            this.setExpired();
            return;
        }
        EntityLifeDrain source = (EntityLifeDrain) sourceEntity;
        EntityLivingBase owner = source.getOwner();
        if (owner == null || !owner.isEntityAlive()) {
            this.setExpired();
            return;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.progress += this.speed;
        if (this.progress >= 1.0D) {
            this.setExpired();
            return;
        }

        this.updatePosition(source, (EntityLivingBase) targetEntity);
        double birth = Math.min(1.0D, this.particleAge / 3.0D);
        double death = Math.min(1.0D, (1.0D - this.progress) / 0.12D);
        double envelope = smoothStep(birth) * smoothStep(death);
        this.particleScale = this.baseScale * (float) (0.08D + 0.92D * envelope)
                * (float) endFade(source);
    }

    private static double endFade(EntityLifeDrain source) {
        int remaining = EntityLifeDrain.DURATION_TICKS - source.ticksExisted;
        if (remaining >= 20) {
            return 1.0D;
        }
        return Math.max(0.0D, remaining / 20.0D);
    }

    private void updatePosition(EntityLifeDrain source, EntityLivingBase target) {
        double targetX = target.posX;
        double targetY = target.posY + target.height * 0.5D;
        double targetZ = target.posZ;
        // The stream now ends at the orb centre above the caster's head,
        // not at the caster's body centre.
        double orbX = source.posX;
        double orbY = source.posY;
        double orbZ = source.posZ;

        double t = this.progress;
        double x = targetX + (orbX - targetX) * t;
        double y = targetY + (orbY - targetY) * t;
        double z = targetZ + (orbZ - targetZ) * t;

        double arc = Math.sin(t * Math.PI) * 0.16D;
        x += Math.sin(this.seed + t * 13.0D) * 0.055D;
        y += arc + Math.cos(this.seed * 1.7D + t * 9.0D) * 0.045D;
        z += Math.cos(this.seed + t * 11.0D) * 0.055D;

        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    private static double smoothStep(double value) {
        double x = Math.max(0.0D, Math.min(1.0D, value));
        return x * x * (3.0D - 2.0D * x);
    }
}
