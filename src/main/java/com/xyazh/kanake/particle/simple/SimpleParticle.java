package com.xyazh.kanake.particle.simple;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.events.RenderEvent;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SimpleParticle {
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;
    private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private AxisAlignedBB boundingBox;
    public boolean disLight = false;
    public int particleAge = 0;
    public int particleMaxAge = 1000;
    public boolean isExpired = false;
    public boolean onGround = false;
    public boolean canCollide = true;
    public double posX;
    public double posY;
    public double posZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public double prevPosX = 0.0D;
    public double prevPosY = 0.0D;
    public double prevPosZ = 0.0D;
    public float particleScale;
    public Vec3d color = new Vec3d(1, 1, 1);
    public float alpha = 1.0F;
    protected float width;
    protected float height;
    public World world;
    protected Random rand;

    public SimpleParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double ...arg)
    {
        RenderEvent.PARTICLES.add(this);
        this.boundingBox = EMPTY_AABB;
        this.width = 0.6F;
        this.height = 1.8F;
        this.world = worldIn;
        this.setPosition(xCoordIn, yCoordIn, zCoordIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.rand = Kanake.rand;
        this.particleScale = 0.8f;
    }

    public void setAlphaF(float alpha){
        this.alpha = alpha;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn)
    {
        this.color.x = particleRedIn;
        this.color.y = particleGreenIn;
        this.color.z = particleBlueIn;
    }

    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }

    protected void setSize(float w, float h)
    {
        if (w != this.width || h != this.height)
        {
            this.width = w;
            this.height = h;
            setPosition(posX, posY, posZ);
        }
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

    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f4 = 0.1F * this.particleScale;
        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        double x0, y0, z0;
        double x1, y1, z1;
        double x2, y2, z2;
        double x3, y3, z3;
        x0 = -rotationX * f4 - rotationXY * f4;
        y0 = -rotationZ * f4;
        z0 = -rotationYZ * f4 - rotationXZ * f4;
        x1 = -rotationX * f4 + rotationXY * f4;
        y1 = rotationZ * f4;
        z1 = -rotationYZ * f4 + rotationXZ * f4;
        x2 = rotationX * f4 + rotationXY * f4;
        y2 = rotationZ * f4;
        z2 = rotationYZ * f4 + rotationXZ * f4;
        x3 = rotationX * f4 - rotationXY * f4;
        y3 = -rotationZ * f4;
        z3 = rotationYZ * f4 - rotationXZ * f4;
        buffer.pos(f5 + x0, f6 + y0, f7 + z0).color((float) this.color.x, (float) this.color.y, (float) this.color.z, this.alpha).endVertex();
        buffer.pos(f5 + x1, f6 + y1, f7 + z1).color((float) this.color.x, (float) this.color.y, (float) this.color.z, this.alpha).endVertex();
        buffer.pos(f5 + x2, f6 + y2, f7 + z2).color((float) this.color.x, (float) this.color.y, (float) this.color.z, this.alpha).endVertex();
        buffer.pos(f5 + x3, f6 + y3, f7 + z3).color((float) this.color.x, (float) this.color.y, (float) this.color.z, this.alpha).endVertex();
    }

    public void move(double x, double y, double z)
    {
        double d0 = y;
        double origX = x;
        double origZ = z;

        if (this.canCollide)
        {
            List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getBoundingBox().expand(x, y, z));

            for (AxisAlignedBB axisalignedbb : list)
            {
                y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

            for (AxisAlignedBB axisalignedbb1 : list)
            {
                x = axisalignedbb1.calculateXOffset(this.getBoundingBox(), x);
            }

            this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

            for (AxisAlignedBB axisalignedbb2 : list)
            {
                z = axisalignedbb2.calculateZOffset(this.getBoundingBox(), z);
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
        }
        else
        {
            this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        }

        this.resetPositionToBB();
        this.onGround = d0 != y && d0 < 0.0D;

        if (origX != x)
        {
            this.motionX = 0.0D;
        }

        if (origZ != z)
        {
            this.motionZ = 0.0D;
        }
    }

    protected void resetPositionToBB()
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.posY = axisalignedbb.minY;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
    }

    public void setExpired()
    {
        this.isExpired = true;
    }

    public boolean isAlive()
    {
        return !this.isExpired;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB bb)
    {
        this.boundingBox = bb;
    }
}
