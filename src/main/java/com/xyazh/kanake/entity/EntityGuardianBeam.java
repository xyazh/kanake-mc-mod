package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.simple.particles.GuardianBeamExplosionParticle;
import com.xyazh.kanake.particle.simple.particles.GuardianBeamParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Thin, fast blue-white Guardian Beam projectile. On impact it creates a
 * flaming explosion and explicitly ignites the hit entity and nearby blocks.
 */
public class EntityGuardianBeam extends EntityShoot {
    public static final double PROJECTILE_SPEED = 6.0D;
    public static final double MAX_TRAVEL_DISTANCE = 288.0D;
    public static final int MAX_LIFETIME_TICKS = 60;
    public static final float EXPLOSION_STRENGTH = 2.5F;
    public static final int FIRE_SECONDS = 8;
    private static final DataParameter<Boolean> IMPACTED =
            EntityDataManager.createKey(EntityGuardianBeam.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> IMPACT_X =
            EntityDataManager.createKey(EntityGuardianBeam.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> IMPACT_Y =
            EntityDataManager.createKey(EntityGuardianBeam.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> IMPACT_Z =
            EntityDataManager.createKey(EntityGuardianBeam.class, DataSerializers.FLOAT);

    private double startX;
    private double startY;
    private double startZ;
    private double lastTickX;
    private double lastTickY;
    private double lastTickZ;
    private boolean started;
    private boolean beamParticleSpawned;
    private boolean impactParticleSpawned;
    private int impactDelay = -1;

    public EntityGuardianBeam(World worldIn) {
        super(worldIn);
        this.setSize(0.15F, 0.15F);
        this.setInvisible(true);
        this.speed = PROJECTILE_SPEED;
        this.livingMaxAge = MAX_LIFETIME_TICKS;
    }

    @Override
    public void entityInit() {
        this.dataManager.register(IMPACTED, Boolean.FALSE);
        this.dataManager.register(IMPACT_X, 0.0F);
        this.dataManager.register(IMPACT_Y, 0.0F);
        this.dataManager.register(IMPACT_Z, 0.0F);
    }

    @Override
    public boolean hasNoGravity() {
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

    public boolean isImpacted() {
        return this.dataManager.get(IMPACTED);
    }

    public double getImpactX() {
        return this.dataManager.get(IMPACT_X);
    }

    public double getImpactY() {
        return this.dataManager.get(IMPACT_Y);
    }

    public double getImpactZ() {
        return this.dataManager.get(IMPACT_Z);
    }

    @Override
    public void onUpdate() {
        if (!this.started) {
            this.startX = this.posX;
            this.startY = this.posY;
            this.startZ = this.posZ;
            this.started = true;
        }
        this.lastTickX = this.posX;
        this.lastTickY = this.posY;
        this.lastTickZ = this.posZ;
        super.onUpdate();
        if (this.isDead) {
            return;
        }
        if (this.world.isRemote) {
            if (this.isImpacted()) {
                this.speed = 0.0D;
                if (!this.impactParticleSpawned) {
                    this.impactParticleSpawned = true;
                    new GuardianBeamExplosionParticle(this.world,
                            this.getImpactX(), this.getImpactY(), this.getImpactZ());
                }
            }
            return;
        }
        if (this.isImpacted()) {
            this.speed = 0.0D;
            if (this.impactDelay >= 0 && --this.impactDelay <= 0) {
                this.setDead();
            }
            return;
        }

        double dx = this.posX - this.startX;
        double dy = this.posY - this.startY;
        double dz = this.posZ - this.startZ;
        if (dx * dx + dy * dy + dz * dz >= MAX_TRAVEL_DISTANCE * MAX_TRAVEL_DISTANCE) {
            this.setDead();
            return;
        }
        if (this.collided || this.isInWater() || this.isInLava()) {
            this.impact(null);
            return;
        }
        Entity target = this.findTarget();
        if (target != null) {
            this.impact(target);
        }
    }

    private Entity findTarget() {
        double minX = Math.min(this.lastTickX, this.posX) - 0.75D;
        double minY = Math.min(this.lastTickY, this.posY) - 0.75D;
        double minZ = Math.min(this.lastTickZ, this.posZ) - 0.75D;
        double maxX = Math.max(this.lastTickX, this.posX) + 0.75D;
        double maxY = Math.max(this.lastTickY, this.posY) + 0.75D;
        double maxZ = Math.max(this.lastTickZ, this.posZ) + 0.75D;
        AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        List<Entity> entities = this.world.getEntitiesWithinAABB(Entity.class, box);
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity entity : entities) {
            if (entity == this || entity == this.shootingEntity || entity.isDead) {
                continue;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            if (entity instanceof EntityItem || entity instanceof EntityXPOrb) {
                continue;
            }
            double distance = entity.getDistanceSq(this.posX, this.posY, this.posZ);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }
        return nearest;
    }

    private void impact(Entity target) {
        if (this.isImpacted()) {
            return;
        }
        this.dataManager.set(IMPACT_X, (float) this.posX);
        this.dataManager.set(IMPACT_Y, (float) this.posY);
        this.dataManager.set(IMPACT_Z, (float) this.posZ);
        this.dataManager.set(IMPACTED, Boolean.TRUE);
        this.speed = 0.0D;
        if (target != null && target != this.shootingEntity) {
            target.setFire(FIRE_SECONDS);
        }

        Entity exploder = this.shootingEntity != null ? this.shootingEntity : this;
        this.world.newExplosion(exploder, this.posX, this.posY, this.posZ,
                EXPLOSION_STRENGTH, true, true);
        this.igniteNearbyBlocks();
        this.impactDelay = 3;
    }

    private void igniteNearbyBlocks() {
        BlockPos center = new BlockPos(this.posX, this.posY, this.posZ);
        int radius = 3;
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos pos = center.add(x, y, z);
                    if (!this.world.isBlockLoaded(pos) || !this.world.isAirBlock(pos)) {
                        continue;
                    }
                    IBlockState below = this.world.getBlockState(pos.down());
                    if (below.isFullBlock()) {
                        this.world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
                    }
                }
            }
        }
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.beamParticleSpawned) {
            this.beamParticleSpawned = true;
            new GuardianBeamParticle(this);
        }

        int count = this.consumeParticleBudget(48.0D, deltaTicks);
        if (count <= 0) {
            return;
        }
        Vec3d direction = this.getForwardVec();
        direction.normalize();
        if (direction.length() < 0.0001D) {
            direction.set(0.0D, 0.0D, 1.0D);
        }
        for (int i = 0; i < count; ++i) {
            double back = 1.5D + this.rand.nextDouble() * 12.5D;
            double px = this.posX - direction.x * back + (this.rand.nextDouble() - 0.5D) * 0.3D;
            double py = this.posY - direction.y * back + (this.rand.nextDouble() - 0.5D) * 0.3D;
            double pz = this.posZ - direction.z * back + (this.rand.nextDouble() - 0.5D) * 0.3D;
            new SkyStrikeSparkParticle(this.world, px, py, pz,
                    (this.rand.nextDouble() - 0.5D) * 0.05D,
                    (this.rand.nextDouble() - 0.5D) * 0.05D,
                    (this.rand.nextDouble() - 0.5D) * 0.05D,
                    0.55F, 0.88F, 1.0F,
                    0.55F + this.rand.nextFloat() * 0.9F,
                    10 + this.rand.nextInt(12));
        }
    }
}
