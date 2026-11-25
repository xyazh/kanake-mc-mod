package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * A temporary life-drain focus anchored above the caster's head.
 *
 * <p>Every half second every valid living target within five blocks loses
 * two health points (four health points per second). The caster is healed for
 * one health point per half second while at least one target is being drained
 * (two health points per second).</p>
 */
public class EntityLifeDrain extends EntityShoot {
    public static final double RADIUS = 5.0D;
    public static final int DURATION_TICKS = 200;
    public static final int DRAIN_INTERVAL = 10;
    public static final float DRAIN_AMOUNT = 2.0F;
    public static final float HEAL_AMOUNT = 1.0F;

    public EntityLifeDrain(World worldIn) {
        super(worldIn);
        this.setSize(0.1F, 0.1F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = DURATION_TICKS;
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

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead) {
            return;
        }

        EntityLivingBase owner = this.shootingEntity;
        if (owner == null || !owner.isEntityAlive() || owner.world != this.world) {
            if (!this.world.isRemote) {
                this.setDead();
            }
            return;
        }

        this.followOwner(owner);
        if (!this.world.isRemote && this.ticksExisted % DRAIN_INTERVAL == 0) {
            this.drain(owner);
        }
    }

    private void followOwner(EntityLivingBase owner) {
        this.setPosition(owner.posX, owner.posY + owner.height + 0.7D, owner.posZ);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
    }

    private void drain(EntityLivingBase owner) {
        AxisAlignedBB box = owner.getEntityBoundingBox().grow(RADIUS);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        boolean drained = false;
        for (EntityLivingBase target : entities) {
            if (!isValidTarget(target, owner) || target.getDistanceSq(owner) > RADIUS * RADIUS) {
                continue;
            }

            // The drain ticks every half second. Resetting the normal hurt
            // cooldown keeps the configured four health points per second.
            target.hurtResistantTime = 0;
            MagicDamage damage = new MagicDamage();
            damage.setAttacker(owner);
            if (MagicDamageUtil.attackEntity(target, damage, DRAIN_AMOUNT, DRAIN_AMOUNT)) {
                drained = true;
            }
        }
        if (drained) {
            owner.heal(HEAL_AMOUNT);
        }
    }

    public static boolean isValidTarget(EntityLivingBase target, EntityLivingBase owner) {
        if (target == null || owner == null || target == owner || !target.isEntityAlive()) {
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

}
