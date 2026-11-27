package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityHeavenStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.HeavenStrikeRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A single long-lived procedural particle that owns every visual of a Sky
 * Strike ritual. The complete array, the charging sphere, the falling ellipsoid
 * and the impact effects are rebuilt from the entity age every frame.
 */
@SideOnly(Side.CLIENT)
public class HeavenStrikeArrayParticle extends SimpleParticle {
    private final int sourceId;

    public HeavenStrikeArrayParticle(EntityHeavenStrike source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityHeavenStrike.MAX_LIFE_TICKS + 40;
        this.particleScale = 1.0F;
        this.setRBGColorF(0.62F, 0.16F, 1.0F);
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
        if (!(entity instanceof EntityHeavenStrike) || entity.isDead) {
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
        if (!(entity instanceof EntityHeavenStrike)) {
            return;
        }
        EntityHeavenStrike strike = (EntityHeavenStrike) entity;
        double absoluteY = strike.prevPosY
                + (strike.posY - strike.prevPosY) * partialTicks;
        double x = strike.prevPosX + (strike.posX - strike.prevPosX) * partialTicks
                - interpPosX;
        double y = absoluteY - interpPosY;
        double z = strike.prevPosZ + (strike.posZ - strike.prevPosZ) * partialTicks
                - interpPosZ;
        double sphereCenterY = EntityHeavenStrike.getSphereCenterY(
                strike.world, absoluteY) - interpPosY;
        double age = strike.getSpellAge() + partialTicks;
        HeavenStrikeRenderHelper.render(buffer, x, y, z, age, sphereCenterY);
    }
}
