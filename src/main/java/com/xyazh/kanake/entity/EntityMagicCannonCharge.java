package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.simple.particles.MagicCannonChargeParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Invisible charging focus for the Magic Cannon scroll. It follows the caster
 * while the item is being used; the actual beam is spawned when the item's use
 * duration finishes.
 */
public class EntityMagicCannonCharge extends EntityShoot {
    public static final int CHARGE_TICKS = 60;

    private boolean chargeParticleSpawned;

    public EntityMagicCannonCharge(World worldIn) {
        super(worldIn);
        this.setSize(0.1F, 0.1F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        // The item removes the charge on release/finish. The longer lifetime is
        // only an orphan cleanup fallback.
        this.livingMaxAge = CHARGE_TICKS + 60;
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
        if (this.isDead) {
            return;
        }
        if (this.world.isRemote) {
            return;
        }

        EntityLivingBase owner = this.shootingEntity;
        if (owner == null || !owner.isEntityAlive() || owner.world != this.world) {
            this.setDead();
            return;
        }
        this.setPosition(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.chargeParticleSpawned) {
            this.chargeParticleSpawned = true;
            new MagicCannonChargeParticle(this);
        }

        EntityLivingBase owner = this.shootingEntity;
        if (owner == null) {
            return;
        }
        int count = this.consumeParticleBudget(18.0D, deltaTicks);
        if (count <= 0) {
            return;
        }

        double age = this.ticksExisted + partialTicks;
        double eyeY = owner.posY + owner.getEyeHeight();
        double lookX = -Math.sin(owner.rotationYaw * 0.017453292F) * Math.cos(owner.rotationPitch * 0.017453292F);
        double lookY = -Math.sin(owner.rotationPitch * 0.017453292F);
        double lookZ = Math.cos(owner.rotationYaw * 0.017453292F) * Math.cos(owner.rotationPitch * 0.017453292F);
        double frontX = owner.posX + lookX * 2.3D;
        double frontY = eyeY + lookY * 2.3D;
        double frontZ = owner.posZ + lookZ * 2.3D;

        for (int i = 0; i < count; ++i) {
            boolean front = this.rand.nextBoolean();
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius;
            double px;
            double py;
            double pz;
            if (front) {
                // Build a basis perpendicular to the player's look direction.
                double ux = 0.0D;
                double uy = 1.0D;
                double uz = 0.0D;
                if (Math.abs(lookY) > 0.95D) {
                    ux = 1.0D;
                    uy = 0.0D;
                }
                double rx = lookY * uz - lookZ * uy;
                double ry = lookZ * ux - lookX * uz;
                double rz = lookX * uy - lookY * ux;
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
                double ux2 = ry * lookZ - rz * lookY;
                double uy2 = rz * lookX - rx * lookZ;
                double uz2 = rx * lookY - ry * lookX;
                radius = 3.2D * (0.35D + this.rand.nextDouble() * 0.65D);
                px = frontX + (rx * Math.cos(angle) + ux2 * Math.sin(angle)) * radius;
                py = frontY + (ry * Math.cos(angle) + uy2 * Math.sin(angle)) * radius;
                pz = frontZ + (rz * Math.cos(angle) + uz2 * Math.sin(angle)) * radius;
            } else {
                radius = 4.0D * (0.35D + this.rand.nextDouble() * 0.65D);
                px = owner.posX + Math.cos(angle) * radius;
                py = owner.posY + 0.12D + this.rand.nextDouble() * 0.2D;
                pz = owner.posZ + Math.sin(angle) * radius;
            }

            double vx = (frontX - px) * (age < 24.0D ? 0.006D : 0.018D);
            double vy = (frontY - py) * (age < 24.0D ? 0.006D : 0.018D);
            double vz = (frontZ - pz) * (age < 24.0D ? 0.006D : 0.018D);
            float red = front ? 0.72F : 0.45F;
            float green = front ? 0.92F : 0.78F;
            float blue = 1.0F;
            if (this.rand.nextInt(5) == 0) {
                red = 1.0F;
                green = 0.78F;
                blue = 0.25F;
            }
            new SkyStrikeSparkParticle(this.world, px, py, pz, vx, vy, vz,
                    red, green, blue, 0.7F + this.rand.nextFloat() * 1.2F,
                    8 + this.rand.nextInt(12));
        }
    }
}
