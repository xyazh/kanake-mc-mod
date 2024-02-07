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

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class BiimuParticle extends BaseParticle {
    private final Vec3d p1;
    protected double sy = 1;

    public BiimuParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn + 0.1, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.setRBGColorF(0.12f, 1.0f, 1.0f);
        this.particleMaxAge = 600 + this.rand.nextInt(100);
        this.motionX = 0;
        this.motionY = ySpeedIn;
        this.motionZ = 0;
        this.setPosition(xCoordIn, yCoordIn + 0.01, zCoordIn);
        this.setAlphaF(0.6f);
        this.particleScale = 4;
        this.sy = 80;
        this.p1 = new Vec3d(rand.nextDouble(), 0, rand.nextDouble());
        this.p1.normalize();
        this.canCollide = false;
        this.disLight = true;
    }

    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        // 返回一个常数值，以禁用光照计算
        if (disLight) {
            return 0xF000F0;
        }
        return super.getBrightnessForRender(partialTicks); // 这里使用一个常数值，可以根据需要进行调整
    }

    public boolean shouldDisableDepth() {
        return false;
    }

    public void renderParticle(@Nonnull BufferBuilder buffer, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float iu = (float) this.particleTextureIndexX / 16.0F;
        float xu = iu + 0.0624375F;
        float iv = (float) this.particleTextureIndexY / 16.0F;
        float xv = iv + 0.0624375F;
        float f4 = 0.1f * this.particleScale;

        if (this.particleTexture != null) {
            iu = this.particleTexture.getMinU();
            xu = this.particleTexture.getMaxU();
            iv = this.particleTexture.getMinV();
            xv = this.particleTexture.getMaxV();
        }
        float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3d[] avec3d = ParticleTempData.AVE_3D;
        avec3d[0].x = p1.x * f4;
        avec3d[0].y = sy * f4;
        avec3d[0].z = p1.z * f4;
        avec3d[1].x = -p1.x * f4;
        avec3d[1].y = sy * f4;
        avec3d[1].z = -p1.z * f4;
        avec3d[2].x = -p1.x * f4;
        avec3d[2].y = -sy * f4;
        avec3d[2].z = -p1.z * f4;
        avec3d[3].x = p1.x * f4;
        avec3d[3].y = -sy * f4;
        avec3d[3].z = p1.z * f4;
        buffer.pos((double) f5 + avec3d[0].x, (double) f6 + avec3d[0].y, (double) f7 + avec3d[0].z).tex(xu, xv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double) f5 + avec3d[1].x, (double) f6 + avec3d[1].y, (double) f7 + avec3d[1].z).tex(xu, iv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double) f5 + avec3d[2].x, (double) f6 + avec3d[2].y, (double) f7 + avec3d[2].z).tex(iu, iv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double) f5 + avec3d[3].x, (double) f6 + avec3d[3].y, (double) f7 + avec3d[3].z).tex(iu, xv).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
       }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, @Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, @Nonnull int... p_178902_15_) {
            return new BiimuParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
