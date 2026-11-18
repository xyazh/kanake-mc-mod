package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntitySkyStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.SkyStrikeRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A single procedural particle that owns the visual of one Sky Strike ritual.
 * It is intentionally long-lived; the arrays, beam and charging geometry are
 * rebuilt from scratch in every render pass so the animation is frame-smooth.
 */
@SideOnly(Side.CLIENT)
public class SkyStrikeArrayParticle extends SimpleParticle {
    private final int sourceId;

    public SkyStrikeArrayParticle(EntitySkyStrike source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntitySkyStrike.TOTAL_TICKS + 10;
        this.particleScale = 1.0F;
        this.setRBGColorF(0.55F, 0.9F, 1.0F);
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        Entity entity = this.world.getEntityByID(this.sourceId);
        if (entity == null) {
            // Entity tracking can lag one client tick behind particle creation.
            if (this.particleAge > 20) {
                this.setExpired();
            }
            return;
        }
        if (!(entity instanceof EntitySkyStrike) || entity.isDead) {
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
        if (!(entity instanceof EntitySkyStrike)) {
            return;
        }
        double x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks - interpPosX;
        double y = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks - interpPosY;
        double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks - interpPosZ;
        double age = ((EntitySkyStrike) entity).getSpellAge() + partialTicks;
        // The helper works in camera-relative coordinates, so convert the
        // absolute world-space crater floor into the same space.
        double effectBottomY = EntitySkyStrike.getEffectMinY(entity.posY) - interpPosY;
        double effectTopY = EntitySkyStrike.getBeamTopY(entity.world) - interpPosY;
        SkyStrikeRenderHelper.renderArray(buffer, x, y, z, age, effectBottomY, effectTopY);
    }
}
