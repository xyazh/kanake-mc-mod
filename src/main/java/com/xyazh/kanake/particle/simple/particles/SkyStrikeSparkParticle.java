package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A small opaque spark used by the Sky Strike projectile, arrays and cannon.
 * It has no texture: the particle renderer draws a billboard quad, and the
 * opaque color is picked up by the bloom pass.
 */
@SideOnly(Side.CLIENT)
public class SkyStrikeSparkParticle extends SimpleParticle {
    private final float baseScale;

    public SkyStrikeSparkParticle(World worldIn, double x, double y, double z,
                                  double motionX, double motionY, double motionZ,
                                  float red, float green, float blue,
                                  float size, int lifetime) {
        super(worldIn, x, y, z, motionX, motionY, motionZ);
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = Math.max(2, lifetime);
        this.baseScale = Math.max(0.1F, size) * 2.2F;
        this.particleScale = this.baseScale * 0.15F;
        this.setRBGColorF(red, green, blue);
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX *= 0.96D;
        this.motionY *= 0.96D;
        this.motionZ *= 0.96D;
        this.move(this.motionX, this.motionY, this.motionZ);

        double birth = Math.min(1.0D, this.particleAge / 2.0D);
        double death = Math.min(1.0D, (this.particleMaxAge - this.particleAge) / 5.0D);
        double envelope = smoothStep(birth) * smoothStep(death);
        this.particleScale = this.baseScale * (float) (0.12D + 0.88D * envelope);
    }

    private static double smoothStep(double value) {
        double x = Math.max(0.0D, Math.min(1.0D, value));
        return x * x * (3.0D - 2.0D * x);
    }
}
