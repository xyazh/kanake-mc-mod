package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityStar;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.StarRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Long-lived procedural renderer for the super-tier Star ritual. The complete
 * array, stellar surface, corona, beam and final collapse/fade are rebuilt
 * from the entity age every frame.
 */
@SideOnly(Side.CLIENT)
public class StarArrayParticle extends SimpleParticle {
    private final int sourceId;

    public StarArrayParticle(EntityStar source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityStar.MAX_LIFE_TICKS + 20;
        this.particleScale = 1.0F;
        this.setRBGColorF(1.0F, 0.62F, 0.20F);
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
        if (!(entity instanceof EntityStar) || entity.isDead) {
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
        if (!(entity instanceof EntityStar)) {
            return;
        }
        EntityStar star = (EntityStar) entity;
        double x = star.prevPosX + (star.posX - star.prevPosX) * partialTicks
                - interpPosX;
        double y = star.prevPosY + (star.posY - star.prevPosY) * partialTicks
                - interpPosY;
        double z = star.prevPosZ + (star.posZ - star.prevPosZ) * partialTicks
                - interpPosZ;
        if (x * x + y * y + z * z > 256.0D * 256.0D) {
            return;
        }
        double age = star.getSpellAge() + partialTicks;
        double centerY = y + EntityStar.STAR_CENTER_HEIGHT;
        StarRenderHelper.render(buffer, x, y, z, age, centerY);
    }
}
