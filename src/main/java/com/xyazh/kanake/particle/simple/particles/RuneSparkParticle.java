package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RuneSparkParticle extends SimpleParticle {

    public RuneSparkParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                             float red, float green, float blue, float size) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.setRBGColorF(red, green, blue);
        this.particleMaxAge = 8 + this.rand.nextInt(9);
        this.particleScale = size;
        this.motionX = (this.rand.nextDouble() - 0.5) * 0.18;
        this.motionY = (this.rand.nextDouble() - 0.2) * 0.18;
        this.motionZ = (this.rand.nextDouble() - 0.5) * 0.18;
        this.setPosition(
                xCoordIn + (this.rand.nextDouble() - 0.5) * 0.3,
                yCoordIn + (this.rand.nextDouble() - 0.5) * 0.3,
                zCoordIn + (this.rand.nextDouble() - 0.5) * 0.3
        );
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.particleScale *= 0.93f;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX *= 0.9;
        this.motionY = this.motionY * 0.9 - 0.003;
        this.motionZ *= 0.9;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
