package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.simple.particles.GodWrathBeamParticle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Short-lived visual beam spawned by a Wrath of God array. Damage is applied by
 * the array itself; this entity only carries the synchronized endpoint used by
 * the procedural renderer.
 */
public class EntityGodWrathBeam extends EntityShoot {
    public static final int MAX_AGE = 10;
    private static final DataParameter<Float> TARGET_X =
            EntityDataManager.createKey(EntityGodWrathBeam.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TARGET_Y =
            EntityDataManager.createKey(EntityGodWrathBeam.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TARGET_Z =
            EntityDataManager.createKey(EntityGodWrathBeam.class, DataSerializers.FLOAT);

    private boolean beamParticleSpawned;

    public EntityGodWrathBeam(World worldIn) {
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

    public double getTargetX() {
        return this.dataManager.get(TARGET_X);
    }

    public double getTargetY() {
        return this.dataManager.get(TARGET_Y);
    }

    public double getTargetZ() {
        return this.dataManager.get(TARGET_Z);
    }

    public float getBeamProgress(float partialTicks) {
        return Math.min(1.0F, (this.ticksExisted + partialTicks) / MAX_AGE);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat((float) this.getTargetX());
        buffer.writeFloat((float) this.getTargetY());
        buffer.writeFloat((float) this.getTargetZ());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        this.dataManager.set(TARGET_X, buffer.readFloat());
        this.dataManager.set(TARGET_Y, buffer.readFloat());
        this.dataManager.set(TARGET_Z, buffer.readFloat());
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.beamParticleSpawned) {
            this.beamParticleSpawned = true;
            new GodWrathBeamParticle(this);
        }
    }
}
