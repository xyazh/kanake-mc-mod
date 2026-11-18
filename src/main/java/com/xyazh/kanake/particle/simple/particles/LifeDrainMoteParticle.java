package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityLifeDrain;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * One small billboard mote orbiting the life-drain focus. The mote keeps its
 * own random spherical direction and angular speed, so the whole sphere looks
 * like it is built from fast, chaotic green sparks instead of a static shell.
 */
@SideOnly(Side.CLIENT)
public class LifeDrainMoteParticle extends SimpleParticle {
    private final int sourceId;
    private final double radius;
    private final double wobble;
    private final float baseScale;
    private double angleA;
    private double angleB;
    private double angularSpeedA;
    private double angularSpeedB;

    public LifeDrainMoteParticle(EntityLifeDrain source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.particleMaxAge = 14 + this.rand.nextInt(22);
        this.baseScale = 0.55F + this.rand.nextFloat() * 0.42F;
        this.particleScale = this.baseScale * 0.12F;
        this.radius = 0.42D + this.rand.nextDouble() * 0.18D;
        this.angleA = this.rand.nextDouble() * Math.PI * 2.0D;
        this.angleB = this.rand.nextDouble() * Math.PI * 2.0D;
        this.angularSpeedA = (this.rand.nextBoolean() ? 1.0D : -1.0D)
                * (0.18D + this.rand.nextDouble() * 0.38D);
        this.angularSpeedB = (this.rand.nextBoolean() ? 1.0D : -1.0D)
                * (0.18D + this.rand.nextDouble() * 0.38D);
        this.wobble = this.rand.nextDouble() * Math.PI * 2.0D;
        this.setRBGColorF(
                0.03F + this.rand.nextFloat() * 0.22F,
                0.78F + this.rand.nextFloat() * 0.22F,
                0.18F + this.rand.nextFloat() * 0.22F
        );
        // The bloom pass compares the normal-scene framebuffer with the
        // particle-only framebuffer. A particle with alpha < 1 blends against
        // different backgrounds in those two buffers and is rejected by the
        // bloom shader, so the motes are opaque and fade through size/motion.
        this.alpha = 1.0F;
        this.updatePosition(source, 0);
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
        if (!(sourceEntity instanceof EntityLifeDrain) || sourceEntity.isDead) {
            this.setExpired();
            return;
        }
        EntityLifeDrain source = (EntityLifeDrain) sourceEntity;

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.angleA += this.angularSpeedA;
        this.angleB += this.angularSpeedB;
        if (this.rand.nextInt(7) == 0) {
            this.angularSpeedA += (this.rand.nextDouble() - 0.5D) * 0.18D;
            this.angularSpeedB += (this.rand.nextDouble() - 0.5D) * 0.18D;
            this.angularSpeedA = Math.max(-0.7D, Math.min(0.7D, this.angularSpeedA));
            this.angularSpeedB = Math.max(-0.7D, Math.min(0.7D, this.angularSpeedB));
        }

        this.updatePosition(source, this.particleAge);
        double birth = Math.min(1.0D, this.particleAge / 4.0D);
        double death = Math.min(1.0D, (this.particleMaxAge - this.particleAge) / 6.0D);
        double envelope = smoothStep(birth) * smoothStep(death);
        this.particleScale = this.baseScale * (float) (0.08D + 0.92D * envelope)
                * (float) endFade(source);
    }

    private void updatePosition(EntityLifeDrain source, int age) {
        double birth = Math.min(1.0D, age / 4.0D);
        double death = Math.min(1.0D, (this.particleMaxAge - age) / 6.0D);
        double envelope = smoothStep(birth) * smoothStep(death);
        double currentRadius = (this.radius * (0.10D + 0.90D * envelope)
                + Math.sin(age * 0.45D + this.wobble) * 0.055D * envelope) * endFade(source);
        double sinA = Math.sin(this.angleA);
        this.posX = source.posX + currentRadius * sinA * Math.cos(this.angleB);
        this.posY = source.posY + currentRadius * Math.cos(this.angleA);
        this.posZ = source.posZ + currentRadius * sinA * Math.sin(this.angleB);
    }

    private static double endFade(EntityLifeDrain source) {
        int remaining = EntityLifeDrain.DURATION_TICKS - source.ticksExisted;
        if (remaining >= 20) {
            return 1.0D;
        }
        return Math.max(0.0D, remaining / 20.0D);
    }

    private static double smoothStep(double value) {
        double x = Math.max(0.0D, Math.min(1.0D, value));
        return x * x * (3.0D - 2.0D * x);
    }
}
