package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Small fallout ash mote. It is dark on purpose so it does not dominate the
 * bloom pass; the radiation ring remains the bright readable element.
 */
@SideOnly(Side.CLIENT)
public class NuclearAshParticle extends SimpleParticle {
    public NuclearAshParticle(World worldIn, double x, double y, double z,
                              double motionX, double motionY, double motionZ,
                              float size, int lifetime) {
        super(worldIn, x, y, z, motionX, motionY, motionZ);
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = Math.max(20, lifetime);
        this.particleScale = Math.max(0.2F, size);
        this.setRBGColorF(0.36F, 0.33F, 0.30F);
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
        this.motionX *= 0.995D;
        this.motionY -= 0.0008D;
        this.motionZ *= 0.995D;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
