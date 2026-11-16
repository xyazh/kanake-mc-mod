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
public class MagicParticle extends SimpleParticle {

    public MagicParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        switch (this.rand.nextInt(4)){
            case 0:
                this.setRBGColorF(0.8f, 0.6f, 0.8f);
                break;
            case 1:
                this.setRBGColorF(0.8f, 0.4f, 0.8f);
                break;
            case 2:
                this.setRBGColorF(0.8f, 0.2f, 0.8f);
                break;
            case 3:
                this.setRBGColorF(0.8f, 0.0f, 0.8f);
                break;
        }
        this.disLight = true;
        this.particleMaxAge = 20+this.rand.nextInt(6);
        Vec3d motion = new Vec3d(this.rand.nextFloat()-0.5,this.rand.nextFloat()-0.5,this.rand.nextFloat()-0.5);
        motion.normalize();
        this.motionX = motion.x/8;
        this.motionY = motion.y/8;
        this.motionZ = motion.z/8;
        this.setPosition(motion.x/8+xCoordIn,motion.y/8+yCoordIn,motion.z/8+zCoordIn);
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
        this.motionX *= 0.9;
        this.motionY *= 0.9;
        this.motionZ *= 0.9;
        this.move(
                this.motionX+this.rand.nextFloat()/10-0.05,
                this.motionY+this.rand.nextFloat()/10-0.05,
                this.motionZ+this.rand.nextFloat()/10-0.05
        );
    }
}