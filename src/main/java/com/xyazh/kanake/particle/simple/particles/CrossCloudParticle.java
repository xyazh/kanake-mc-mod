package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CrossCloudParticle extends SimpleParticle {

    public CrossCloudParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                              double dirX, double dirY, double dirZ,
                              float red, float green, float blue, float size) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.setRBGColorF(red, green, blue);
        this.particleMaxAge = 30 + this.rand.nextInt(21);
        this.particleScale = size;
        double speed = 0.4 + this.rand.nextDouble() * 0.3;
        this.motionX = dirX * speed + (this.rand.nextDouble() - 0.5) * 0.05;
        this.motionY = dirY * speed + (this.rand.nextDouble() - 0.5) * 0.05;
        this.motionZ = dirZ * speed + (this.rand.nextDouble() - 0.5) * 0.05;
        this.setPosition(xCoordIn, yCoordIn, zCoordIn);
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.particleScale *= 0.97f;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX *= 0.94;
        this.motionY *= 0.94;
        this.motionZ *= 0.94;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
