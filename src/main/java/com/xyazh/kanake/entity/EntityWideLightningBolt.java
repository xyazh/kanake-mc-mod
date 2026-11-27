package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.simple.particles.WideLightningBoltParticle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * A short-lived visual chain link. Damage and block destruction are resolved
 * by {@link EntityWideLightningStrike}; this entity only synchronizes the
 * source/end point with the procedural renderer.
 */
public class EntityWideLightningBolt extends EntityShoot {
    public static final int MAX_AGE = 50;

    private static final DataParameter<Float> TARGET_X =
            EntityDataManager.createKey(EntityWideLightningBolt.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TARGET_Y =
            EntityDataManager.createKey(EntityWideLightningBolt.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TARGET_Z =
            EntityDataManager.createKey(EntityWideLightningBolt.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> SEED =
            EntityDataManager.createKey(EntityWideLightningBolt.class, DataSerializers.VARINT);

    @SideOnly(Side.CLIENT)
    private boolean particleSpawned;

    public EntityWideLightningBolt(World worldIn) {
        super(worldIn);
        this.setSize(0.08F, 0.08F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = MAX_AGE;
    }

    @Override
    public void entityInit() {
        this.dataManager.register(TARGET_X, 0.0F);
        this.dataManager.register(TARGET_Y, 0.0F);
        this.dataManager.register(TARGET_Z, 0.0F);
        this.dataManager.register(SEED, 0);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected boolean customMotion() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    public void setTarget(double x, double y, double z) {
        this.dataManager.set(TARGET_X, (float) x);
        this.dataManager.set(TARGET_Y, (float) y);
        this.dataManager.set(TARGET_Z, (float) z);
    }

    public void setSeed(int seed) {
        this.dataManager.set(SEED, seed);
    }

    public double getTargetX() {
        return this.dataManager.get(TARGET_X);
    }

    public double getTargetY() {
        return this.dataManager.get(TARGET_Y);
    }

    public double getTargetZ() {
        return this.dataManager.get(TARGET_Z);
    }

    public int getSeed() {
        return this.dataManager.get(SEED);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat((float) this.getTargetX());
        buffer.writeFloat((float) this.getTargetY());
        buffer.writeFloat((float) this.getTargetZ());
        buffer.writeInt(this.getSeed());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        this.dataManager.set(TARGET_X, buffer.readFloat());
        this.dataManager.set(TARGET_Y, buffer.readFloat());
        this.dataManager.set(TARGET_Z, buffer.readFloat());
        this.dataManager.set(SEED, buffer.readInt());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead || this.particleSpawned) {
            return;
        }
        this.particleSpawned = true;
        new WideLightningBoltParticle(this);
    }
}
