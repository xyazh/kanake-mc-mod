package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.particle.simple.particles.SilentRainArrayParticle;
import com.xyazh.kanake.particle.simple.particles.SilentRainDropParticle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Silent Rain: a giant overhead array that repeatedly rolls for random magic
 * damage on creatures inside its 64-block horizontal radius.
 */
public class EntitySilentRain extends EntityShoot {
    public static final double RADIUS = 64.0D;
    public static final int BUILD_TICKS = 40;
    public static final int ACTIVE_TICKS = 60 * 20;
    public static final int FADE_TICKS = 40;
    public static final int TOTAL_TICKS = BUILD_TICKS + ACTIVE_TICKS + FADE_TICKS;
    public static final double CHANCE = 0.25D;
    public static final int MAX_DAMAGE = 100;

    private boolean arrayParticleSpawned;

    public EntitySilentRain(World worldIn) {
        super(worldIn);
        this.setSize(0.1F, 0.1F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = TOTAL_TICKS + 20;
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
        return EntityGodWrath.getArrayBaseY(world);
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
        if (age >= TOTAL_TICKS) {
            this.setDead();
            return;
        }
        if (age >= BUILD_TICKS && age < BUILD_TICKS + ACTIVE_TICKS
                && (age - BUILD_TICKS) % 20 == 0) {
            this.damageTick();
        }
    }

    private void damageTick() {
        EntityLivingBase owner = this.shootingEntity;
        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - RADIUS, 0.0D, this.posZ - RADIUS,
                this.posX + RADIUS, this.world.getActualHeight(), this.posZ + RADIUS);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase entity : entities) {
            if (!entity.isEntityAlive() || entity == owner) {
                continue;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            double dx = entity.posX - this.posX;
            double dz = entity.posZ - this.posZ;
            if (dx * dx + dz * dz > RADIUS * RADIUS) {
                continue;
            }
            if (this.world.rand.nextDouble() >= CHANCE) {
                continue;
            }
            int damage = 1 + this.world.rand.nextInt(MAX_DAMAGE);
            MagicDamage source = new MagicDamage("silent_rain");
            source.setAttacker(owner);
            MagicDamageUtil.attackEntity(entity, source, damage, MAX_DAMAGE);
        }
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.arrayParticleSpawned) {
            this.arrayParticleSpawned = true;
            new SilentRainArrayParticle(this);
        }

        int age = this.ticksExisted;
        if (age < BUILD_TICKS || age >= BUILD_TICKS + ACTIVE_TICKS) {
            return;
        }
        int count = this.consumeParticleBudget(36.0D, deltaTicks);
        for (int i = 0; i < count; ++i) {
            double radius;
            if (this.rand.nextDouble() < 0.72D) {
                radius = this.rand.nextDouble() * 26.0D;
            } else {
                radius = this.rand.nextDouble() * RADIUS;
            }
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double x = this.posX + Math.cos(angle) * radius;
            double z = this.posZ + Math.sin(angle) * radius;
            double y = this.posY - 0.35D;
            new SilentRainDropParticle(this.world, x, y, z);
        }
    }
}
