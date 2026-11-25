package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.particle.simple.particles.GodWrathArrayParticle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Wrath of God: dozens of small arrays spread across the cloud layer. Each
 * array independently fires a thin magic beam at nearby creatures.
 */
public class EntityGodWrath extends EntityShoot {
    public static final int GRID_SIZE = 6;
    public static final int ARRAY_COUNT = GRID_SIZE * GRID_SIZE;
    public static final double ARRAY_SPACING = 28.0D;
    public static final double ARRAY_RADIUS = 3.6D;
    public static final int BUILD_TICKS = 60;
    public static final int MAX_ATTACKS = 10;
    public static final int ATTACK_INTERVAL_TICKS = 80;
    public static final int ATTACK_VARIANCE_TICKS = 20;
    public static final double TARGET_RANGE = 128.0D;
    public static final float DAMAGE = 20.0F;
    public static final int MAX_EXISTENCE_TICKS = (5 + MAX_ATTACKS) * 60 * 20;
    public static final int FADE_TICKS = 60;
    public static final int SPENT_FADE_TICKS = 20;
    private static final DataParameter<Integer> SPENT_LOW =
            EntityDataManager.createKey(EntityGodWrath.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SPENT_HIGH =
            EntityDataManager.createKey(EntityGodWrath.class, DataSerializers.VARINT);

    private final int[] remainingAttacks = new int[ARRAY_COUNT];
    private final int[] nextAttackTicks = new int[ARRAY_COUNT];
    private int spentLow;
    private int spentHigh;
    private int spentCount;
    private int allSpentAge = -1;
    private boolean arrayParticleSpawned;

    public EntityGodWrath(World worldIn) {
        super(worldIn);
        this.setSize(0.2F, 0.2F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = MAX_EXISTENCE_TICKS + 20;
        for (int i = 0; i < ARRAY_COUNT; ++i) {
            this.remainingAttacks[i] = MAX_ATTACKS;
            this.nextAttackTicks[i] = BUILD_TICKS + 60 + this.rand.nextInt(41)
                    + (i % GRID_SIZE) * 2;
        }
    }

    @Override
    public void entityInit() {
        this.dataManager.register(SPENT_LOW, 0);
        this.dataManager.register(SPENT_HIGH, 0);
    }

    public boolean isArraySpent(int index) {
        if (index < 32) {
            return (this.dataManager.get(SPENT_LOW) & (1 << index)) != 0;
        }
        return (this.dataManager.get(SPENT_HIGH) & (1 << (index - 32))) != 0;
    }

    private void markArraySpent(int index) {
        if (index < 32) {
            this.spentLow |= 1 << index;
            this.dataManager.set(SPENT_LOW, this.spentLow);
        } else {
            this.spentHigh |= 1 << (index - 32);
            this.dataManager.set(SPENT_HIGH, this.spentHigh);
        }
        ++this.spentCount;
        if (this.spentCount >= ARRAY_COUNT) {
            this.allSpentAge = this.ticksExisted;
        }
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

    public int getSpellAge() {
        return Math.max(this.ticksExisted, 0);
    }

    public static double getArrayBaseY(World world) {
        if (!world.provider.isSurfaceWorld()) {
            return world.getActualHeight();
        }
        float cloudHeight = world.provider.getCloudHeight();
        if (Float.isNaN(cloudHeight) || cloudHeight <= 0.0F) {
            return world.getActualHeight();
        }
        return cloudHeight + 1.0D;
    }

    public static double getArrayOffsetX(int index) {
        int column = index % GRID_SIZE;
        return (column - (GRID_SIZE - 1) / 2.0D) * ARRAY_SPACING;
    }

    public static double getArrayOffsetZ(int index) {
        int row = index / GRID_SIZE;
        return (row - (GRID_SIZE - 1) / 2.0D) * ARRAY_SPACING;
    }

    public double getArrayX(int index) {
        return this.posX + getArrayOffsetX(index);
    }

    public double getArrayZ(int index) {
        return this.posZ + getArrayOffsetZ(index);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead) {
            return;
        }
        if (this.world.isRemote) {
            return;
        }
        int age = this.ticksExisted;
        if (age >= MAX_EXISTENCE_TICKS
                || (this.allSpentAge >= 0 && age >= this.allSpentAge + SPENT_FADE_TICKS + 5)) {
            this.setDead();
            return;
        }
        if (age < BUILD_TICKS) {
            return;
        }

        for (int i = 0; i < ARRAY_COUNT; ++i) {
            if (this.remainingAttacks[i] <= 0 || age < this.nextAttackTicks[i]) {
                continue;
            }
            EntityLivingBase target = this.findTarget(i);
            if (target != null) {
                this.fireAt(i, target);
                --this.remainingAttacks[i];
                if (this.remainingAttacks[i] <= 0) {
                    this.markArraySpent(i);
                }
            }
            this.nextAttackTicks[i] = age + ATTACK_INTERVAL_TICKS
                    - ATTACK_VARIANCE_TICKS + this.rand.nextInt(ATTACK_VARIANCE_TICKS * 2 + 1);
        }
    }

    private EntityLivingBase findTarget(int arrayIndex) {
        double arrayX = this.getArrayX(arrayIndex);
        double arrayZ = this.getArrayZ(arrayIndex);
        AxisAlignedBB area = new AxisAlignedBB(
                arrayX - TARGET_RANGE, 0.0D, arrayZ - TARGET_RANGE,
                arrayX + TARGET_RANGE, this.world.getActualHeight(), arrayZ + TARGET_RANGE);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        EntityLivingBase nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (EntityLivingBase entity : entities) {
            if (!entity.isEntityAlive() || entity == this.shootingEntity) {
                continue;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            double dx = entity.posX - arrayX;
            double dz = entity.posZ - arrayZ;
            double distance = dx * dx + dz * dz;
            if (distance > TARGET_RANGE * TARGET_RANGE || distance >= nearestDistance) {
                continue;
            }
            nearestDistance = distance;
            nearest = entity;
        }
        return nearest;
    }

    private void fireAt(int arrayIndex, EntityLivingBase target) {
        MagicDamage source = new MagicDamage("god_wrath");
        source.setAttacker(this.shootingEntity);
        MagicDamageUtil.attackEntity(target, source, DAMAGE, DAMAGE);

        EntityGodWrathBeam beam = new EntityGodWrathBeam(this.world);
        beam.setPosition(this.getArrayX(arrayIndex), this.posY, this.getArrayZ(arrayIndex));
        beam.setTarget(target.posX, target.posY + target.height * 0.5D, target.posZ);
        this.world.spawnEntity(beam);
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.arrayParticleSpawned) {
            this.arrayParticleSpawned = true;
            new GodWrathArrayParticle(this);
        }
    }
}
