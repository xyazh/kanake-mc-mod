package com.xyazh.kanake.particle.particles;

import com.xyazh.kanake.Kanake;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleHeart;
import net.minecraft.client.particle.ParticleSuspendedTown;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class HealParticles extends  ParticleSuspendedTown {
    protected HealParticles(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double speedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, speedIn);
        double posXIn,posYIn,posZIn;
        posXIn = xCoordIn + this.rand.nextGaussian()/2;
        posYIn = yCoordIn + 1 + this.rand.nextGaussian();
        posZIn = zCoordIn + this.rand.nextGaussian()/2;
        this.setPosition(posXIn, posYIn, posZIn);
        this.prevPosX = posXIn;
        this.prevPosY = posYIn;
        this.prevPosZ = posZIn;

        this.motionX = this.rand.nextGaussian() * 0.01D;
        this.motionY = this.rand.nextGaussian() * 0.01D;
        this.motionZ = this.rand.nextGaussian() * 0.01D;
    }

    @SideOnly(Side.CLIENT)
    public static class HealParticlesFactory implements IParticleFactory
    {
        public Particle createParticle(int particleID, @Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            Particle particle = new HealParticles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            particle.setParticleTextureIndex(82);
            particle.setRBGColorF(1.0F, 1.0F, 1.0F);
            return particle;
        }
    }
}
