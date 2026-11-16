package com.xyazh.kanake.particle.particles;

import com.xyazh.kanake.particle.ParticleTempData;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MagicParticle extends BaseParticle {

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
        this.particleMaxAge = 10+this.rand.nextInt(6);
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
        this.setAlphaF(dAge);
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

    @Override
    public int getBrightnessForRender(float partialTicks) {
        // 返回一个常数值，以禁用光照计算
        if(disLight){
            return 0xF000F0;
        }
        return super.getBrightnessForRender(partialTicks); // 这里使用一个常数值，可以根据需要进行调整
    }

    public boolean shouldDisableDepth()
    {
        return false;
    }

    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = (float)this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float)this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            f = this.particleTexture.getMinU();
            f1 = this.particleTexture.getMaxU();
            f2 = this.particleTexture.getMinV();
            f3 = this.particleTexture.getMaxV();
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3d[] avec3d = ParticleTempData.AVE_3D;
        avec3d[0].x = -rotationX * f4 - rotationXY * f4;
        avec3d[0].y = -rotationZ * f4;
        avec3d[0].z = -rotationYZ * f4 - rotationXZ * f4;
        avec3d[1].x = -rotationX * f4 + rotationXY * f4;
        avec3d[1].y = rotationZ * f4;
        avec3d[1].z = -rotationYZ * f4 + rotationXZ * f4;
        avec3d[2].x = rotationX * f4 + rotationXY * f4;
        avec3d[2].y = rotationZ * f4;
        avec3d[2].z = rotationYZ * f4 + rotationXZ * f4;
        avec3d[3].x = rotationX * f4 - rotationXY * f4;
        avec3d[3].y = -rotationZ * f4;
        avec3d[3].z = rotationYZ * f4 - rotationXZ * f4;
        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new MagicParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}