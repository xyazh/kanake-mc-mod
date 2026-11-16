package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.ParticleTempData;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;


@SideOnly(Side.CLIENT)
public class TestParticle extends SimpleParticle {
    private double f = 0;
    float angle = 0;
    public TestParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        double radius = 1 + (xSpeedIn+ySpeedIn+zSpeedIn)/3 + rand.nextGaussian()/2;
        double theta = 2 * Math.PI * rand.nextDouble();
        double phi = Math.PI * rand.nextDouble();
        this.posX = xCoordIn + radius * Math.sin(phi) * Math.cos(theta);
        this.posY = yCoordIn + radius * Math.sin(phi) * Math.sin(theta);
        this.posZ = zCoordIn + radius * Math.cos(phi);
        this.particleMaxAge = (int) (30*(rand.nextFloat()+1));
        this.setPosition(this.posX,this.posY,this.posZ);
        this.prevPosX = xCoordIn;
        this.prevPosY = yCoordIn;
        this.prevPosZ = zCoordIn;
        this.motionX = (this.posX-xCoordIn)/radius/10;
        this.motionY = (this.posY-yCoordIn)/radius/10+0.05;
        this.motionZ = (this.posZ-zCoordIn)/radius/10;
        switch (this.rand.nextInt(4)){
            case 0:
                this.setRBGColorF(1.0f, 0.6f, 0.0f);
                this.disLight = true;
                break;
            case 1:
                this.setRBGColorF(0.1f, 0.1f, 0.1f);
                f = 1;
                break;
            case 2:
                this.setRBGColorF(1.0f, 1.0f, 1.0f);
                f = -1;
                break;
            case 3:
                this.setRBGColorF(1.0f, 0.4f, 0.0f);
                this.disLight = true;
                break;
        }
    }

    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        this.motionX *= 0.8D;
        this.motionY *= 0.8D;
        this.motionZ *= 0.8D;
        this.motionY -= this.rand.nextDouble() * 0.008 * this.f;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
