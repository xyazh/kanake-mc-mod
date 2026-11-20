package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.particle.simple.particles.MagicCannonBeamParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A fixed magic cannon beam. It carves a 128-block-long, radius-2 tunnel
 * through blocks with blast resistance below 20, then damages living targets
 * in the beam volume once per second for five seconds.
 */
public class EntityMagicCannonBeam extends EntityShoot {
    public static final double BEAM_LENGTH = 128.0D;
    public static final double BEAM_RADIUS = 2.0D;
    public static final float MAX_BLAST_RESISTANCE = 20.0F;
    public static final int DURATION_TICKS = 100;
    public static final int DAMAGE_INTERVAL = 20;
    public static final float DAMAGE = 30.0F;

    private boolean beamParticleSpawned;
    private boolean blocksDestroyed;

    public EntityMagicCannonBeam(World worldIn) {
        super(worldIn);
        this.setSize(0.1F, 0.1F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = DURATION_TICKS + 20;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected boolean customMotion() {
        return true;
    }

    public void setOwner(EntityLivingBase owner) {
        this.shootingEntity = owner;
    }

    public EntityLivingBase getOwner() {
        return this.shootingEntity;
    }

    public int getSpellAge() {
        return Math.max(this.ticksExisted, this.livingAge);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead || this.world.isRemote) {
            return;
        }

        int age = this.getSpellAge();
        if (!this.blocksDestroyed) {
            this.blocksDestroyed = true;
            this.destroyBlocksInBeam();
        }
        // Entity.onUpdate() has already advanced the age to 1 on the first
        // server tick, so the first damage tick is age 1.
        int attackAge = age - 1;
        if (attackAge >= 0 && attackAge < DURATION_TICKS
                && attackAge % DAMAGE_INTERVAL == 0) {
            this.damageEntitiesInBeam();
        }
        if (age >= DURATION_TICKS) {
            this.setDead();
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    private Vec3d getDirection() {
        Vec3d direction = this.getForwardVec();
        direction.normalize();
        if (direction.length() < 0.0001D) {
            direction.set(0.0D, 0.0D, 1.0D);
        }
        return direction;
    }

    private void destroyBlocksInBeam() {
        Vec3d direction = this.getDirection();
        int steps = (int) Math.ceil(BEAM_LENGTH / 0.25D);
        int radius = (int) Math.ceil(BEAM_RADIUS);
        for (int step = 0; step <= steps; ++step) {
            double t = Math.min(BEAM_LENGTH, step * 0.25D);
            double centerX = this.posX + direction.x * t;
            double centerY = this.posY + direction.y * t;
            double centerZ = this.posZ + direction.z * t;
            int minX = (int) Math.floor(centerX - radius);
            int maxX = (int) Math.ceil(centerX + radius);
            int minY = (int) Math.floor(centerY - radius);
            int maxY = (int) Math.ceil(centerY + radius);
            int minZ = (int) Math.floor(centerZ - radius);
            int maxZ = (int) Math.ceil(centerZ + radius);
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        double dx = x + 0.5D - centerX;
                        double dy = y + 0.5D - centerY;
                        double dz = z + 0.5D - centerZ;
                        if (dx * dx + dy * dy + dz * dz > BEAM_RADIUS * BEAM_RADIUS) {
                            continue;
                        }
                        this.destroyBlockAt(x, y, z);
                    }
                }
            }
        }
    }

    private void destroyBlockAt(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!this.world.isBlockLoaded(pos)) {
            return;
        }
        IBlockState state = this.world.getBlockState(pos);
        Block block = state.getBlock();
        if (block.isAir(state, this.world, pos)) {
            return;
        }
        if (block.getBlockHardness(state, this.world, pos) < 0.0F) {
            return;
        }
        if (block.getExplosionResistance(this) >= MAX_BLAST_RESISTANCE) {
            return;
        }
        this.world.destroyBlock(pos, false);
    }

    private void damageEntitiesInBeam() {
        Vec3d direction = this.getDirection();
        double endX = this.posX + direction.x * BEAM_LENGTH;
        double endY = this.posY + direction.y * BEAM_LENGTH;
        double endZ = this.posZ + direction.z * BEAM_LENGTH;
        double margin = BEAM_RADIUS + 2.0D;
        AxisAlignedBB box = new AxisAlignedBB(
                Math.min(this.posX, endX) - margin,
                Math.min(this.posY, endY) - margin,
                Math.min(this.posZ, endZ) - margin,
                Math.max(this.posX, endX) + margin,
                Math.max(this.posY, endY) + margin,
                Math.max(this.posZ, endZ) + margin
        );

        List<Entity> targets = this.world.getEntitiesWithinAABB(Entity.class, box);
        EntityLivingBase owner = this.shootingEntity;
        MagicDamage damage = new MagicDamage("magic_cannon");
        damage.setAttacker(owner);
        damage.setDamageIsAbsolute();

        for (Entity target : targets) {
            if (target == null || target == owner || target.isDead) {
                continue;
            }
            if (target instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) target;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            double centerY = target.posY + target.height * 0.5D;
            double distanceSq = distanceSqToSegment(target.posX, centerY, target.posZ,
                    this.posX, this.posY, this.posZ, endX, endY, endZ);
            double hitRadius = BEAM_RADIUS + target.width * 0.5D;
            if (distanceSq > hitRadius * hitRadius) {
                continue;
            }

            // Ender crystals, dragon parts and other non-living damageable
            // entities are included as well.
            if (target instanceof EntityLivingBase) {
                ((EntityLivingBase) target).hurtResistantTime = 0;
            }
            target.attackEntityFrom(damage, DAMAGE);
        }
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.beamParticleSpawned) {
            this.beamParticleSpawned = true;
            new MagicCannonBeamParticle(this);
        }

        int count = this.consumeParticleBudget(32.0D, deltaTicks);
        if (count <= 0) {
            return;
        }

        Vec3d direction = this.getDirection();
        double ux = 0.0D;
        double uy = 1.0D;
        double uz = 0.0D;
        if (Math.abs(direction.y) > 0.95D) {
            ux = 1.0D;
            uy = 0.0D;
        }
        double rx = direction.y * uz - direction.z * uy;
        double ry = direction.z * ux - direction.x * uz;
        double rz = direction.x * uy - direction.y * ux;
        double rl = Math.sqrt(rx * rx + ry * ry + rz * rz);
        if (rl < 0.0001D) {
            rx = 1.0D;
            ry = 0.0D;
            rz = 0.0D;
            rl = 1.0D;
        }
        rx /= rl;
        ry /= rl;
        rz /= rl;
        double ux2 = ry * direction.z - rz * direction.y;
        double uy2 = rz * direction.x - rx * direction.z;
        double uz2 = rx * direction.y - ry * direction.x;

        for (int i = 0; i < count; ++i) {
            double t = this.rand.nextDouble() * BEAM_LENGTH;
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = BEAM_RADIUS * (0.85D + this.rand.nextDouble() * 0.25D);
            double px = this.posX + direction.x * t + (rx * Math.cos(angle) + ux2 * Math.sin(angle)) * radius;
            double py = this.posY + direction.y * t + (ry * Math.cos(angle) + uy2 * Math.sin(angle)) * radius;
            double pz = this.posZ + direction.z * t + (rz * Math.cos(angle) + uz2 * Math.sin(angle)) * radius;
            double outward = 0.04D + this.rand.nextDouble() * 0.08D;
            double vx = (rx * Math.cos(angle) + ux2 * Math.sin(angle)) * outward;
            double vy = (ry * Math.cos(angle) + uy2 * Math.sin(angle)) * outward;
            double vz = (rz * Math.cos(angle) + uz2 * Math.sin(angle)) * outward;
            float red = this.rand.nextFloat() < 0.25F ? 1.0F : 0.55F;
            float green = 0.82F + this.rand.nextFloat() * 0.18F;
            float blue = 1.0F;
            new SkyStrikeSparkParticle(this.world, px, py, pz, vx, vy, vz,
                    red, green, blue, 0.8F + this.rand.nextFloat() * 1.3F,
                    10 + this.rand.nextInt(14));
        }
    }

    private static double distanceSqToSegment(double px, double py, double pz,
                                              double ax, double ay, double az,
                                              double bx, double by, double bz) {
        double abx = bx - ax;
        double aby = by - ay;
        double abz = bz - az;
        double apx = px - ax;
        double apy = py - ay;
        double apz = pz - az;
        double lengthSq = abx * abx + aby * aby + abz * abz;
        if (lengthSq < 0.000001D) {
            return apx * apx + apy * apy + apz * apz;
        }
        double t = (apx * abx + apy * aby + apz * abz) / lengthSq;
        t = Math.max(0.0D, Math.min(1.0D, t));
        double dx = apx - abx * t;
        double dy = apy - aby * t;
        double dz = apz - abz * t;
        return dx * dx + dy * dy + dz * dz;
    }
}
