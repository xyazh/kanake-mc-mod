package com.xyazh.kanake.block.blocks.clean;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.damage.CleanDamage;
import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class TileClean extends TileEntity implements ITickable {
    public static final int DAMAGE = 10;
    public int range = 64;
    public double renderY = 0;
    public double rotate = 0;
    public double lastRenderY = 0;
    public double lastRotate = 0;
    public int age = 0;
    public int maxAge = 2000;
    protected AxisAlignedBB atkAABB = null;
    protected final Vec3d vecPos = new Vec3d();

    public TileClean() {
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
        this.lastRenderY = this.renderY;
        this.renderY = sy-this.pos.getY();
        this.lastRotate = this.rotate;
        this.rotate += 1;
        this.age += 1;
    }

    protected void setDead() {
        this.world.removeTileEntity(this.pos);
        this.world.setBlockState(this.pos, Blocks.BEDROCK.getDefaultState());
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
            if(sy<128){
                sy = 128;
            }
        }
        this.timeIt(sy);
        if (this.world.isRemote) {
            for (int i = 0; i < 3; i++) {
                double radius = Kanake.rand.nextDouble() * range;
                double theta = 2 * Math.PI * Kanake.rand.nextDouble();
                double rx = radius * Math.cos(theta);
                double rz = radius * Math.sin(theta);
                this.world.spawnParticle(ModParticles.BIIMU_PARTICLES, this.pos.getX() + rx, sy, this.pos.getZ() + rz, 0, -4, 0);
            }
        } else {
            if (this.atkAABB == null) {
                double ax, ay, az, ax1, ay1, az1;
                ax = this.pos.getX() + this.range;
                ay = 4096;
                az = this.pos.getZ() + this.range;
                ax1 = this.pos.getX() - this.range;
                ay1 = -16;
                az1 = this.pos.getZ() - this.range;
                this.atkAABB = new AxisAlignedBB(ax, ay, az, ax1, ay1, az1);
            }
            this.vecPos.x = this.pos.getX();
            this.vecPos.y = this.pos.getY();
            this.vecPos.z = this.pos.getZ();
            for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, atkAABB, (e) -> {
                if(e instanceof EntityPlayer){
                    return false;
                }
                Vec3d p1 = new Vec3d(e.posX, e.posY, e.posZ);
                p1.sub(this.vecPos);
                return p1.length() <= this.range;
            })) {
                if (Kanake.rand.nextInt(100) < 10) {
                    DamageSource damageSource = new CleanDamage();
                    float health = entity.getHealth();
                    entity.attackEntityFrom(damageSource,DAMAGE);
                    if(entity.getHealth()-health<DAMAGE){
                        entity.setHealth(Math.max(health-DAMAGE,0));
                    }
                }
            }
        }
    }
}
