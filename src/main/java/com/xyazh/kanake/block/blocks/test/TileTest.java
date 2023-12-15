package com.xyazh.kanake.block.blocks.test;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class TileTest extends TileEntity implements ITickable {
    public int range = 64;
    public double y = 0, r = 0;
    public double lastY = 0, lastR = 0;
    public int age = 0;
    public int maxAge = 2000;
    protected AxisAlignedBB aabb = null;
    protected final Vec3d p = new Vec3d();

    public TileTest() {
        super();
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    private void timeIt(double sy) {
        this.lastY = this.y;
        if (this.pos.getY() + this.y <= sy) {
            this.y += 0.2;
        }
        this.lastR = this.r;
        this.r += 1;
        this.age += 1;
    }

    protected void setDead() {
        this.world.removeTileEntity(this.pos);
    }

    protected void checkEnd() {
        if (this.age > this.maxAge) {
            this.setDead();
        }
    }

    @Override
    public void update() {
        this.checkEnd();
        double sy = 128;
        if (this.world.provider != null) {
            sy = this.world.provider.getCloudHeight();
        }
        this.timeIt(sy);
        if (this.world.isRemote) {
            for (int i = 0; i < 3; i++) {
                double radius = Kanake.rand.nextDouble() * range;
                double theta = 2 * Math.PI * Kanake.rand.nextDouble();
                double rx = radius * Math.cos(theta);
                double rz = radius * Math.sin(theta);
                double ry = Math.pow(Kanake.rand.nextDouble(), 2) * (sy < 128 ? 128 : sy);
                this.world.spawnParticle(ModParticles.KAKERA_PARTICLES, this.pos.getX() + rx, ry, this.pos.getZ() + rz, 0, (Kanake.rand.nextDouble() - 0.5) / 50, 0);
            }
        } else {
            if (Kanake.rand.nextInt(100) < 20) {
                double radius = Kanake.rand.nextDouble() * this.range;
                double theta = 2 * Math.PI * Kanake.rand.nextDouble();
                double rx = radius * Math.cos(theta);
                double rz = radius * Math.sin(theta);
                double ry = Kanake.rand.nextDouble() * this.world.getHeight(this.pos.getX() + (int) rx, this.pos.getZ() + (int) rz);
                BlockPos blockPos = new BlockPos(this.pos.getX() + rx, ry, this.pos.getZ() + rz);
                this.world.destroyBlock(blockPos, false);
            }
            if (this.aabb == null) {
                double ax, ay, az, ax1, ay1, az1;
                ax = this.pos.getX() + this.range;
                ay = 4096;
                az = this.pos.getZ() + this.range;
                ax1 = this.pos.getX() - this.range;
                ay1 = -4096;
                az1 = this.pos.getZ() - this.range;
                this.aabb = new AxisAlignedBB(ax, ay, az, ax1, ay1, az1);
            }
            this.p.x = this.pos.getX();
            this.p.y = this.pos.getY();
            this.p.z = this.pos.getZ();
            for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (e) -> {
                Vec3d p1 = new Vec3d(e.posX, e.posY, e.posZ);
                p1.sub(this.p);
                double l = p1.length();
                return l <= this.range;
            })) {
                if (Kanake.rand.nextInt(100) < 10) {
                    entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, 5);
                }
            }
        }
    }
}
