package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.simple.particles.SkyStrikeOrbParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * The fast projectile of the super-tier spell "Sky Strike".
 *
 * <p>The projectile is represented by procedural particles only. When it hits a
 * block, a living entity or reaches 96 blocks of travel it creates the
 * {@link EntitySkyStrike} ritual at the impact position.</p>
 */
public class EntitySkyStrikeProjectile extends EntityShoot {
    public static final double MAX_TRAVEL_DISTANCE = 96.0D;

    private double startX;
    private double startY;
    private double startZ;
    private boolean hasStartPosition;
    private boolean triggered;
    private boolean orbParticleSpawned;

    public EntitySkyStrikeProjectile(World worldIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
        this.setInvisible(true);
        this.speed = 2.4D;
        this.livingMaxAge = 80;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void onUpdate() {
        // Capture the launch position before EntityShoot moves the projectile.
        if (!this.hasStartPosition) {
            this.startX = this.posX;
            this.startY = this.posY;
            this.startZ = this.posZ;
            this.hasStartPosition = true;
        }

        super.onUpdate();

        if (this.isDead || this.triggered) {
            return;
        }
        if (this.world.isRemote) {
            return;
        }

        double dx = this.posX - this.startX;
        double dy = this.posY - this.startY;
        double dz = this.posZ - this.startZ;
        double travelledSq = dx * dx + dy * dy + dz * dz;
        if (travelledSq >= MAX_TRAVEL_DISTANCE * MAX_TRAVEL_DISTANCE) {
            this.trigger();
            return;
        }

        if (this.collided || this.isInWater() || this.isInLava()) {
            this.trigger();
            return;
        }

        if (this.findTarget() != null) {
            this.trigger();
        }
    }

    private Entity findTarget() {
        AxisAlignedBB box = this.getEntityBoundingBox().grow(0.7D);
        List<Entity> entities = this.world.getEntitiesWithinAABB(Entity.class, box);
        for (Entity entity : entities) {
            if (entity == this || entity == this.shootingEntity || entity.isDead) {
                continue;
            }
            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) {
                continue;
            }
            if (entity instanceof EntityLivingBase || entity.canBeCollidedWith()) {
                return entity;
            }
        }
        return null;
    }

    private void trigger() {
        if (this.triggered) {
            return;
        }
        this.triggered = true;

        if (!this.world.isRemote) {
            EntitySkyStrike strike = new EntitySkyStrike(this.world);
            strike.setOwner(this.shootingEntity);
            strike.setPosition(this.posX, this.posY, this.posZ);
            this.world.spawnEntity(strike);
        }
        this.setDead();
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }

        if (!this.orbParticleSpawned) {
            this.orbParticleSpawned = true;
            new SkyStrikeOrbParticle(this);
        }

        int count = this.consumeParticleBudget(26.0D, deltaTicks);
        if (count <= 0) {
            return;
        }

        double x = this.getParticleX(partialTicks);
        double y = this.getParticleY(partialTicks);
        double z = this.getParticleZ(partialTicks);
        for (int i = 0; i < count; ++i) {
            double px = x + (this.rand.nextDouble() - 0.5D) * 0.65D;
            double py = y + (this.rand.nextDouble() - 0.5D) * 0.65D;
            double pz = z + (this.rand.nextDouble() - 0.5D) * 0.65D;
            double vx = (this.rand.nextDouble() - 0.5D) * 0.10D - this.motionX * 0.07D;
            double vy = (this.rand.nextDouble() - 0.5D) * 0.10D - this.motionY * 0.07D;
            double vz = (this.rand.nextDouble() - 0.5D) * 0.10D - this.motionZ * 0.07D;
            float size = 0.65F + this.rand.nextFloat() * 1.25F;
            new SkyStrikeSparkParticle(this.world, px, py, pz, vx, vy, vz,
                    0.55F + this.rand.nextFloat() * 0.45F,
                    0.85F + this.rand.nextFloat() * 0.15F,
                    1.0F,
                    size,
                    8 + this.rand.nextInt(9));
        }
    }
}
