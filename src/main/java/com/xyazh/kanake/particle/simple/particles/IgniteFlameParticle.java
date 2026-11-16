package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IgniteFlameParticle extends SimpleParticle {

    public IgniteFlameParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                               float red, float green, float blue, float size) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.setRBGColorF(red, green, blue);
        this.particleMaxAge = 10 + this.rand.nextInt(10);
        this.particleScale = size;
        this.motionX = (this.rand.nextDouble() - 0.5) * 0.04;
        this.motionY = 0.03 + this.rand.nextDouble() * 0.05;
        this.motionZ = (this.rand.nextDouble() - 0.5) * 0.04;
        this.setPosition(
                xCoordIn + (this.rand.nextDouble() - 0.5) * 0.6,
                yCoordIn + (this.rand.nextDouble() - 0.5) * 0.4,
                zCoordIn + (this.rand.nextDouble() - 0.5) * 0.6
        );
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.particleScale *= 0.95f;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.002;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
