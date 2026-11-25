package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.util.KillUtil;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeArrayParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
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
 * Super-tier spell "Sky Strike" ritual.
 *
 * <p>The entity itself is invisible. Its complete visual (the descending beam,
 * the arrays, the charge and the final cannon) is drawn procedurally by
 * {@link SkyStrikeArrayParticle}. Server-side this entity only owns the timed
 * destruction and damage logic.</p>
 *
 * <p>Timeline (20 ticks per second):</p>
 * <ul>
 *     <li>0-24: a thin beam descends from the world ceiling at the impact X/Z.</li>
 *     <li>24-124: the arrays are constructed at several heights. The central
 *     main array has a radius of 48 blocks and is placed exactly at the top of
 *     the beam / world ceiling.</li>
 *     <li>124-224: the arrays charge.</li>
 *     <li>224-824: the beam expands to a diameter of 16 blocks, destroys the
 *     area down to the bedrock layer, creates a lava rim and damages living
 *     targets once per second. This final stage lasts exactly 30 seconds.</li>
 * </ul>
 */
public class EntitySkyStrike extends EntityShoot {
    /**
     * Destruction and damage are intentionally limited to a 16-block-wide
     * column (radius 8). The 48-block main array below is only the visual
     * formation, not the damage area.
     */
    public static final double DAMAGE_DIAMETER = 16.0D;
    public static final double DAMAGE_RADIUS = DAMAGE_DIAMETER * 0.5D;
    public static final double AREA_RADIUS = DAMAGE_RADIUS;
    public static final double AREA_RADIUS_SQ = AREA_RADIUS * AREA_RADIUS;

    public static final double MAIN_CIRCLE_RADIUS = 48.0D;
    public static final double BEAM_RADIUS = 8.0D;

    public static final int BEAM_DESCENT_TICKS = 24;
    public static final int ARRAY_BUILD_START = 24;
    public static final int ARRAY_BUILD_TICKS = 100;
    public static final int CHARGE_START = 124;
    public static final int CHARGE_TICKS = 100;
    public static final int ATTACK_START = 224;
    public static final int ATTACK_TICKS = 600;
    public static final int TOTAL_TICKS = ATTACK_START + ATTACK_TICKS;

    public static final int DAMAGE_INTERVAL = 20;
    public static final float DIRECT_HEALTH_FRACTION = 0.20F;
    public static final float ABSOLUTE_DAMAGE = 300.0F;

    public static final int EFFECT_BOTTOM_Y = 1;
    public static final int EFFECT_TOP_Y = 255;
    public static final int LAVA_RING_WIDTH = 3;
    // The full height scan is intentionally scheduled over the 30 second
    // attack. 4096 candidate positions per tick completes it without stalling
    // the server for a single huge burst.
    private static final int DESTROY_BUDGET = 4096;
    private static final double LAVA_RING_INNER = AREA_RADIUS - LAVA_RING_WIDTH;
    private static final double LAVA_RING_INNER_SQ = LAVA_RING_INNER * LAVA_RING_INNER;
    // Fractions of the vertical span between the impact point and the world
    // ceiling. Index 0 is the main array, exactly at the ceiling.
    private static final double[] RING_TOP_FRACTION = {1.0D, 0.80D, 0.60D, 0.40D};
    private static final double[] RING_RADIUS = {MAIN_CIRCLE_RADIUS, 32.0D, 22.0D, 12.0D};
    private static final double[] ORBIT_RADIUS = {0.0D, 8.0D, 14.0D, 18.0D};
    private static final double[] ORBIT_SPEED = {0.0D, 0.018D, 0.024D, 0.030D};

    private boolean arrayParticleSpawned;
    private boolean destructionStarted;
    private boolean destructionDone;
    private int scanX;
    private int scanY;
    private int scanZ;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;
    private int centerBX;
    private int centerBZ;

    public EntitySkyStrike(World worldIn) {
        super(worldIn);
        this.setSize(0.1F, 0.1F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = TOTAL_TICKS;
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

    /**
     * Age used by both the server timeline and the client renderer. The spawn
     * packet carries livingAge, so an entity that enters tracking range during
     * the ritual continues at the correct phase instead of restarting the beam.
     */
    public int getSpellAge() {
        return Math.max(this.ticksExisted, this.livingAge);
    }

    /**
     * The top of the light pillar is the ceiling of the current dimension.
     */
    public static double getBeamTopY(World world) {
        return world.getActualHeight();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead || this.world.isRemote) {
            return;
        }

        int age = this.getSpellAge();
        if (age >= ATTACK_START) {
            if (!this.destructionStarted) {
                this.initDestruction();
            }
            if (!this.destructionDone) {
                this.destroyBlocks(DESTROY_BUDGET);
            }

            int attackAge = age - ATTACK_START;
            if (attackAge >= 0 && attackAge < ATTACK_TICKS && attackAge % DAMAGE_INTERVAL == 0) {
                this.applyDamage();
            }
        }

        if (age >= TOTAL_TICKS) {
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

    /**
     * The Y coordinate of the bottom of the destroyed cylinder. The block below
     * this layer is intentionally left intact so that the lava rim has a floor.
     */
    public static int getEffectMinY(double centerY) {
        return Math.max(1, Math.min(254, EFFECT_BOTTOM_Y));
    }

    public static int getEffectMaxY(double centerY) {
        return Math.max(getEffectMinY(centerY) + 1, EFFECT_TOP_Y);
    }

    private void initDestruction() {
        this.destructionStarted = true;
        this.centerBX = (int) Math.floor(this.posX);
        this.centerBZ = (int) Math.floor(this.posZ);
        this.minX = this.centerBX - (int) AREA_RADIUS;
        this.maxX = this.centerBX + (int) AREA_RADIUS;
        this.minZ = this.centerBZ - (int) AREA_RADIUS;
        this.maxZ = this.centerBZ + (int) AREA_RADIUS;
        this.minY = getEffectMinY(this.posY);
        this.maxY = getEffectMaxY(this.posY);
        this.scanX = this.minX;
        this.scanY = this.minY;
        this.scanZ = this.minZ;
    }

    private void destroyBlocks(int budget) {
        int left = budget;
        while (left-- > 0 && !this.destructionDone) {
            int dx = this.scanX - this.centerBX;
            int dz = this.scanZ - this.centerBZ;
            int distanceSq = dx * dx + dz * dz;
            if (distanceSq <= (int) AREA_RADIUS_SQ) {
                this.processBlock(this.scanX, this.scanY, this.scanZ, distanceSq);
            }
            this.advanceScan();
        }
    }

    private void processBlock(int x, int y, int z, int distanceSq) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!this.world.isBlockLoaded(pos)) {
            return;
        }

        IBlockState state = this.world.getBlockState(pos);
        Block block = state.getBlock();
        boolean isLava = block == net.minecraft.init.Blocks.LAVA
                || block == net.minecraft.init.Blocks.FLOWING_LAVA;
        boolean isAir = block.isAir(state, this.world, pos);
        float hardness = block.getBlockHardness(state, this.world, pos);
        if (hardness < 0.0F) {
            // Bedrock, barriers and every other hardness -1 block survive.
            return;
        }

        boolean lavaRing = y == this.minY && distanceSq >= (int) LAVA_RING_INNER_SQ;
        if (lavaRing) {
            if (!isLava) {
                this.world.setBlockState(pos, net.minecraft.init.Blocks.LAVA.getDefaultState(), 3);
            }
            return;
        }

        if (!isAir) {
            this.world.destroyBlock(pos, false);
        }
    }

    private void advanceScan() {
        this.scanY++;
        if (this.scanY <= this.maxY) {
            return;
        }
        this.scanY = this.minY;
        this.scanZ++;
        if (this.scanZ <= this.maxZ) {
            return;
        }
        this.scanZ = this.minZ;
        this.scanX++;
        if (this.scanX > this.maxX) {
            this.destructionDone = true;
        }
    }

    private void applyDamage() {
        if (this.minY >= this.maxY) {
            this.minY = getEffectMinY(this.posY);
            this.maxY = getEffectMaxY(this.posY);
        }

        AxisAlignedBB box = new AxisAlignedBB(
                this.posX - AREA_RADIUS, this.minY, this.posZ - AREA_RADIUS,
                this.posX + AREA_RADIUS, this.maxY, this.posZ + AREA_RADIUS
        );
        List<Entity> targets = this.world.getEntitiesWithinAABB(Entity.class, box);
        EntityLivingBase owner = this.shootingEntity;
        MagicDamage damage = new MagicDamage("sky_strike");
        damage.setAttacker(owner);
        damage.setDamageIsAbsolute();

        for (Entity target : targets) {
            if (!isValidTarget(target, owner)) {
                continue;
            }
            double dx = target.posX - this.posX;
            double dz = target.posZ - this.posZ;
            if (dx * dx + dz * dz > AREA_RADIUS_SQ) {
                continue;
            }
            if (target.posY + target.height < this.minY || target.posY > this.maxY) {
                continue;
            }

            // Non-living damageable entities such as ender crystals do not
            // have max health/setHealth semantics; hit them with the absolute
            // magic damage directly.
            if (!(target instanceof EntityLivingBase)) {
                target.attackEntityFrom(damage, ABSOLUTE_DAMAGE);
                continue;
            }

            EntityLivingBase living = (EntityLivingBase) target;
            // The direct setHealth step represents the irreversible part of the
            // spell: it ignores resistance and always removes one fifth of max HP.
            float directLoss = living.getMaxHealth() * DIRECT_HEALTH_FRACTION;
            float remaining = living.getHealth() - directLoss;
            if (remaining <= 0.0F) {
                if (!KillUtil.setHealthDirect(living, 0.0F)) {
                    living.setHealth(0.0F);
                }
                living.onDeath(damage);
                continue;
            }
            if (!KillUtil.setHealthDirect(living, remaining)) {
                living.setHealth(remaining);
            }

            // The flat 300 points are absolute magic damage. They bypass armor,
            // enchantment reduction and the normal hurt cooldown.
            living.hurtResistantTime = 0;
            MagicDamageUtil.attackEntity(living, damage, ABSOLUTE_DAMAGE, ABSOLUTE_DAMAGE);
        }
    }

    private static boolean isValidTarget(Entity target, EntityLivingBase owner) {
        if (target == null || owner == target || target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }

        if (!this.arrayParticleSpawned) {
            this.arrayParticleSpawned = true;
            new SkyStrikeArrayParticle(this);
        }

        double age = this.getSpellAge() + partialTicks;
        double rate;
        if (age >= ATTACK_START) {
            rate = 52.0D;
        } else if (age >= CHARGE_START) {
            rate = 26.0D;
        } else {
            rate = 18.0D;
        }
        int count = this.consumeParticleBudget(rate, deltaTicks);
        if (count <= 0) {
            return;
        }

        for (int i = 0; i < count; ++i) {
            this.spawnPhaseSpark(age);
        }
    }

    private void spawnPhaseSpark(double age) {
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;
        float size;
        int life;

        if (age < BEAM_DESCENT_TICKS) {
            double t = smoothstep(age / BEAM_DESCENT_TICKS);
            double top = getBeamTopY(this.world);
            double tip = top + (this.posY - top) * t;
            x = this.posX + (this.rand.nextDouble() - 0.5D) * 1.0D;
            y = tip + (this.rand.nextDouble() - 0.5D) * 1.2D;
            z = this.posZ + (this.rand.nextDouble() - 0.5D) * 1.0D;
            vx = (this.rand.nextDouble() - 0.5D) * 0.10D;
            vy = -0.05D - this.rand.nextDouble() * 0.12D;
            vz = (this.rand.nextDouble() - 0.5D) * 0.10D;
            size = 0.7F + this.rand.nextFloat() * 1.1F;
            life = 10 + this.rand.nextInt(10);
        } else if (age < ATTACK_START) {
            int ring = this.rand.nextInt(4);
            double phase = ring * 1.37D;
            double cx = this.posX + Math.cos(age * ORBIT_SPEED[ring] + phase) * ORBIT_RADIUS[ring];
            double cz = this.posZ + Math.sin(age * ORBIT_SPEED[ring] + phase) * ORBIT_RADIUS[ring];
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = RING_RADIUS[ring] * (0.25D + this.rand.nextDouble() * 0.75D);
            double ceiling = getBeamTopY(this.world);
            double ringY = this.posY + (ceiling - this.posY) * RING_TOP_FRACTION[ring];
            x = cx + Math.cos(angle) * radius;
            y = ringY + (this.rand.nextDouble() - 0.5D) * 1.5D;
            z = cz + Math.sin(angle) * radius;
            if (age >= CHARGE_START) {
                vx = (this.posX - x) * 0.018D;
                vy = (getBeamTopY(this.world) - y) * 0.010D;
                vz = (this.posZ - z) * 0.018D;
            } else {
                vx = (this.rand.nextDouble() - 0.5D) * 0.08D;
                vy = (this.rand.nextDouble() - 0.5D) * 0.08D;
                vz = (this.rand.nextDouble() - 0.5D) * 0.08D;
            }
            size = 0.65F + this.rand.nextFloat() * 1.5F;
            life = 12 + this.rand.nextInt(16);
        } else {
            double bottom = getEffectMinY(this.posY);
            if (this.rand.nextFloat() < 0.68F) {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double radius = BEAM_RADIUS * (0.75D + this.rand.nextDouble() * 0.45D);
                x = this.posX + Math.cos(angle) * radius;
                y = bottom + this.rand.nextDouble() * (this.posY + 40.0D - bottom);
                z = this.posZ + Math.sin(angle) * radius;
                vx = Math.cos(angle) * (0.04D + this.rand.nextDouble() * 0.09D);
                vy = 0.02D + this.rand.nextDouble() * 0.08D;
                vz = Math.sin(angle) * (0.04D + this.rand.nextDouble() * 0.09D);
            } else {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double radius = this.rand.nextDouble() * AREA_RADIUS;
                x = this.posX + Math.cos(angle) * radius;
                y = bottom + 0.2D + this.rand.nextDouble() * 1.5D;
                z = this.posZ + Math.sin(angle) * radius;
                vx = Math.cos(angle) * (0.03D + this.rand.nextDouble() * 0.08D);
                vy = 0.02D + this.rand.nextDouble() * 0.11D;
                vz = Math.sin(angle) * (0.03D + this.rand.nextDouble() * 0.08D);
            }
            size = 0.8F + this.rand.nextFloat() * 1.7F;
            life = 12 + this.rand.nextInt(18);
        }

        float red = 0.55F + this.rand.nextFloat() * 0.45F;
        float green = 0.82F + this.rand.nextFloat() * 0.18F;
        float blue = 1.0F;
        if (this.rand.nextInt(6) == 0) {
            red = 1.0F;
            green = 0.78F + this.rand.nextFloat() * 0.18F;
            blue = 0.28F;
        }
        new SkyStrikeSparkParticle(this.world, x, y, z, vx, vy, vz,
                red, green, blue, size, life);
    }

    private static double smoothstep(double value) {
        double x = Math.max(0.0D, Math.min(1.0D, value));
        return x * x * (3.0D - 2.0D * x);
    }
}
