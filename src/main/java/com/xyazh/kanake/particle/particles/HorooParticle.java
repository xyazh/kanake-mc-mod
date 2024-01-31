package com.xyazh.kanake.particle.particles;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class HorooParticle extends BaseParticle {
    float reddustParticleScale;

    protected HorooParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,  double red, double green, double blue)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, 1.0F, red,green,blue);
    }

    protected HorooParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double scale, double red, double green, double blue)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX *= 0.10000000149011612D;
        this.motionY = 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
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
        this.particleScale *= 0.75F;
        this.particleScale *= scale;
        this.reddustParticleScale = this.particleScale;
        this.particleMaxAge = (int)(2.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int)((double)this.particleMaxAge * scale);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        // 返回一个常数值，以禁用光照计算
        if(disLight){
            return 0xF000F0;
        }
        return super.getBrightnessForRender(partialTicks); // 这里使用一个常数值，可以根据需要进行调整
    }

    public void renderParticle(@Nonnull BufferBuilder buffer, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = (this.particleAge + partialTicks) / this.particleMaxAge * 32.0F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        this.particleScale = this.reddustParticleScale * f;
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
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

        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.1D;
            this.motionZ *= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }


    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, @Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, @Nonnull int... p_178902_15_) {
            return new HorooParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
