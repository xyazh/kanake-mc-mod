package com.xyazh.kanake.damage;

import com.xyazh.kanake.util.KillUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/**
 * Applies magic damage with a guaranteed minimum of 10% of the attack's
 * nominal maximum damage. The normal attack path is used first so aggro,
 * knockback, death events and modded damage handling still run. If a damage
 * cap or invulnerability leaves the actual health loss below that minimum,
 * the remaining loss is written directly to the synchronized health value.
 */
public final class MagicDamageUtil {
    public static final float MIN_DAMAGE_FRACTION = 0.10F;

    private MagicDamageUtil() {
    }

    public static boolean attackEntity(Entity target, DamageSource source,
                                       float damage, float maxDamage) {
        if (target == null || source == null) {
            return false;
        }
        if (!(target instanceof EntityLivingBase)) {
            return target.attackEntityFrom(source, damage);
        }
        return attackEntity((EntityLivingBase) target, source, damage, maxDamage);
    }

    public static boolean attackEntity(EntityLivingBase target, DamageSource source,
                                       float damage, float maxDamage) {
        if (target == null || source == null || target.world == null
                || target.world.isRemote || target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }

        float before = target.getHealth();
        if (before <= 0.0F) {
            return false;
        }

        // Magic attacks also ignore the vanilla hurt cooldown. Damage caps from
        // other mods are handled below when their reduction is too large.
        target.hurtResistantTime = 0;
        boolean hit = target.attackEntityFrom(source, damage);
        if (target.isDead) {
            return hit;
        }

        float actualLoss = Math.max(0.0F, before - target.getHealth());
        float minimumLoss = Math.max(0.0F, maxDamage) * MIN_DAMAGE_FRACTION;
        if (!Float.isFinite(minimumLoss) || minimumLoss <= 0.0F
                || actualLoss + 0.001F >= minimumLoss) {
            return hit;
        }

        // The ordinary hit was capped or cancelled. Remove the deficit from the
        // raw synchronized health value instead of calling the virtual
        // setHealth/attackEntityFrom path again.
        float newHealth = Math.max(0.0F, before - minimumLoss);
        boolean written = KillUtil.setHealthDirect(target, newHealth);
        if (!written) {
            target.setHealth(newHealth);
        }
        if (newHealth <= 0.0F && !target.isDead) {
            target.onDeath(source);
        }
        return true;
    }
}
