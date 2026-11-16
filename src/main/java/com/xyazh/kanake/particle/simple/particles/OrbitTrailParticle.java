package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OrbitTrailParticle extends SimpleParticle {

    public OrbitTrailParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                              float red, float green, float blue, float size) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.setRBGColorF(red, green, blue);
        this.particleMaxAge = 10 + this.rand.nextInt(8);
        this.particleScale = size;
        this.motionX = (this.rand.nextDouble() - 0.5) * 0.02;
        this.motionY = (this.rand.nextDouble() - 0.5) * 0.02;
        this.motionZ = (this.rand.nextDouble() - 0.5) * 0.02;
        this.setPosition(xCoordIn, yCoordIn, zCoordIn);
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.particleScale *= 0.92f;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
