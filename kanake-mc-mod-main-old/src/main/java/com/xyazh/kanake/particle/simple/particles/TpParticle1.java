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
public class TpParticle1 extends SimpleParticle {
    public TpParticle1(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn){
        super(worldIn, xCoordIn, yCoordIn+0.01, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.setRBGColorF(0.12f, 1.0f, 1.0f);
        this.particleMaxAge = 600 + this.rand.nextInt(100);
        this.motionX = 0;
        this.motionY = this.rand.nextDouble()/50;
        this.motionZ = 0;
        this.setPosition(xCoordIn,yCoordIn+0.01,zCoordIn);
        this.setAlphaF(1);
    }

    public void onUpdate()
    {
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
