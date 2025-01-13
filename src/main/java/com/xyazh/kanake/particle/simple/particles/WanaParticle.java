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

@SideOnly(Side.CLIENT)
public class WanaParticle extends SimpleParticle {
    private int begin;

    public WanaParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn){
        super(worldIn, xCoordIn, yCoordIn+0.1, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        switch (this.rand.nextInt(3)){
            case 0:
                this.setRBGColorF(1.0f, 0.6f, 0.0f);
                break;
            case 1:
                this.setRBGColorF(1.0f, 0.4f, 0.0f);
                break;
            case 2:
                this.setRBGColorF(1.0f, 0.8f, 0.4f);
                break;
        }
        this.disLight = true;
        this.begin = 20+this.rand.nextInt(5);
        this.particleMaxAge = 25+this.rand.nextInt(5);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.setPosition(xCoordIn,yCoordIn+0.1,zCoordIn);
        this.setAlphaF(0);
        this.canCollide = false;
    }

    public void onUpdate()
    {
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        float dAge;
        if(this.particleAge<this.begin){
            dAge = 1-(float)(this.begin-this.particleAge)/(float) this.begin;
            this.particleScale *= dAge;
            this.setSize(dAge*this.width,dAge*this.height);
            return;
        }else {
            dAge = (float) (this.particleMaxAge-this.particleAge)/(float) this.particleMaxAge;
            this.particleScale *= dAge;
            this.setSize(dAge*this.width,dAge*this.height);
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX = (this.rand.nextFloat()-0.5)/10;
        this.motionY = (this.particleAge-this.begin)/100.0;
        this.motionZ = (this.rand.nextFloat()-0.5)/10;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
