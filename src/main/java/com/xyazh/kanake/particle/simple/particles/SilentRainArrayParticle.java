package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntitySilentRain;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.SuperMagicRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Follows the Silent Rain entity and draws its giant overhead array with
 * build, rotation, charge and dissipation phases.
 */
@SideOnly(Side.CLIENT)
public class SilentRainArrayParticle extends SimpleParticle {
    private final int sourceId;

    public SilentRainArrayParticle(EntitySilentRain source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntitySilentRain.TOTAL_TICKS + 20;
        this.particleScale = 1.0F;
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        Entity entity = this.world.getEntityByID(this.sourceId);
        if (entity == null) {
            if (this.particleAge > 20) {
                this.setExpired();
            }
            return;
        }
        if (!(entity instanceof EntitySilentRain) || entity.isDead) {
            this.setExpired();
            return;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.posX = entity.posX;
        this.posY = entity.posY;
        this.posZ = entity.posZ;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Entity entity = this.world.getEntityByID(this.sourceId);
        if (!(entity instanceof EntitySilentRain)) {
            return;
        }
        EntitySilentRain rain = (EntitySilentRain) entity;
        double x = rain.prevPosX + (rain.posX - rain.prevPosX) * partialTicks - interpPosX;
        double y = rain.prevPosY + (rain.posY - rain.prevPosY) * partialTicks - interpPosY;
        double z = rain.prevPosZ + (rain.posZ - rain.prevPosZ) * partialTicks - interpPosZ;
        double age = rain.ticksExisted + partialTicks;
        double build = clamp(age / EntitySilentRain.BUILD_TICKS);
        double fade = clamp((EntitySilentRain.TOTAL_TICKS - age) / EntitySilentRain.FADE_TICKS);
        double charge;
        if (age < EntitySilentRain.BUILD_TICKS) {
            charge = 0.20D + 0.50D * build;
        } else {
            charge = 0.55D + 0.45D * (0.5D + 0.5D
                    * Math.sin(age * 0.15D));
        }
        SuperMagicRenderHelper.renderSilentRainArray(buffer, x, y, z,
                age, build, fade, charge);
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
