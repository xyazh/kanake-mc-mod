package com.xyazh.kanake.particle.particle;

import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class ManaParticle1 extends BaseParticle {
    private final Vec3d inPos;
    private final Vec3d targetPos;
    private final Vec3d inPoint1;
    private final Vec3d inPoint2;
    public ManaParticle1(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.inPos = new Vec3d(xCoordIn,yCoordIn,zCoordIn);
        this.targetPos = new Vec3d(xSpeedIn,ySpeedIn,zSpeedIn);
        this.canCollide = false;
        float r,g,b;
        r = ((color & 0xff0000) >> 16)/255f;
        g = ((color & 0x00ff00) >> 8)/255f;
        b = (color & 0x0000ff)/255f;
        this.setRBGColorF(r,g,b);
        this.disLight = true;
        this.particleMaxAge = 60;
        double dx,dy,dz;
        dx = this.targetPos.x-this.inPos.x;
        dy = this.targetPos.y-this.inPos.y;
        dz = this.targetPos.z-this.inPos.z;
        Vec3d motion = new Vec3d(dx/this.particleMaxAge, dy/this.particleMaxAge, dz/this.particleMaxAge);
        this.motionX=motion.x;
        this.motionY=motion.y;
        this.motionZ=motion.z;
        Vec3d direction =  new Vec3d(dx,dy,dz);
        Vec3d randomVec1 = direction.crossProduct(new Vec3d(this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble())).normalize();
        Vec3d randomVec2 = direction.crossProduct(new Vec3d(this.rand.nextDouble(), this.rand.nextDouble(), this.rand.nextDouble())).normalize();
        double r1 = this.rand.nextDouble();
        double r2 = (1-r1)*this.rand.nextDouble();
        inPoint1 = new Vec3d(xCoordIn+dx*r1+randomVec1.x,yCoordIn+dy*r1+randomVec1.y,zCoordIn+dz*r1+randomVec1.z);
        inPoint2 = new Vec3d(xCoordIn+dx*r2+randomVec2.x,yCoordIn+dy*r2+randomVec2.y,zCoordIn+dz*r2+randomVec2.z);
        this.motionX = MathUtils.dBezier(inPos.x,inPoint1.x,inPoint2.x,targetPos.x,0)/this.particleMaxAge;
        this.motionY = MathUtils.dBezier(inPos.y,inPoint1.y,inPoint2.y,targetPos.y,0)/this.particleMaxAge;
        this.motionZ = MathUtils.dBezier(inPos.z,inPoint1.z,inPoint2.z,targetPos.z,0)/this.particleMaxAge;
        this.setPosition(xCoordIn,yCoordIn,zCoordIn);
    }

    public int getFXLayer()
    {
        return 0;
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
        double t = (double) this.particleAge/(double) this.particleMaxAge;
        this.motionX = MathUtils.dBezier(inPos.x,inPoint1.x,inPoint2.x,targetPos.x,t)/this.particleMaxAge;
        this.motionY = MathUtils.dBezier(inPos.y,inPoint1.y,inPoint2.y,targetPos.y,t)/this.particleMaxAge;
        this.motionZ = MathUtils.dBezier(inPos.z,inPoint1.z,inPoint2.z,targetPos.z,t)/this.particleMaxAge;
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        if(disLight){
            return 0xF000F0;
        }
        return super.getBrightnessForRender(partialTicks);
    }

    public boolean shouldDisableDepth()
    {
        return true;
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
            f = this.particleTexture.getInterpolatedU((this.particleTextureJitterX / 8.0F * 16.0F));
            f1 = this.particleTexture.getInterpolatedU((this.particleTextureJitterX / 8.0F * 16.0F));
            f2 = this.particleTexture.getInterpolatedV((this.particleTextureJitterY / 8.0F * 16.0F));
            f3 = this.particleTexture.getInterpolatedV((this.particleTextureJitterY / 8.0F * 16.0F));
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};

        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }

    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, @Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            int color=0xffffff;
            if(p_178902_15_.length>0){
                color = p_178902_15_[0];
            }
            return new ManaParticle1(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, color);
        }
    }
}