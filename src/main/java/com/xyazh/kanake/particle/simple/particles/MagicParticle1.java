package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.ParticleTempData;
import com.xyazh.kanake.particle.particles.BaseParticle;
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
public class MagicParticle1 extends SimpleParticle {

    public MagicParticle1(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.disLight = true;
        switch (this.rand.nextInt(6)){
            case 0:
                this.setRBGColorF(0.8f, 0.6f, 0.8f);
                break;
            case 1:
                this.setRBGColorF(0.8f, 0.4f, 0.8f);
                break;
            case 2:
                this.setRBGColorF(0.8f, 0.2f, 0.6f);
                break;
            case 3:
                this.setRBGColorF(0.8f, 0.1f,0.6f);
                break;
            case 4:
                this.setRBGColorF(0.8f, 0.0f, 0.8f);
                break;
            case 5:
                this.setRBGColorF(1f, 0.0f, 1f);
                break;
        }
        this.particleMaxAge = 30+this.rand.nextInt(8);
        Vec3d motion = new Vec3d(this.rand.nextFloat()-0.5,this.rand.nextFloat()-0.5,this.rand.nextFloat()-0.5);
        motion.normalize();
        this.motionX = motion.x/20;
        this.motionY = motion.y/20;
        this.motionZ = motion.z/20;
        this.setPosition(xCoordIn,yCoordIn,zCoordIn);
    }

    public void onUpdate()
    {
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        float dAge = (float) (this.particleMaxAge-this.particleAge)/(float) this.particleMaxAge;
        this.particleScale *= dAge;
        this.setSize(dAge*this.width,dAge*this.height);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX *= 0.6;
        this.motionY *= 0.6;
        this.motionZ *= 0.6;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}