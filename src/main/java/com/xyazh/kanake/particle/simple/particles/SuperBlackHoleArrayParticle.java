package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntitySuperBlackHole;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.SuperBlackHoleRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Long-lived procedural renderer for the super-tier Black Hole. The ground
 * array, event horizon and final flare are rebuilt from the entity state every
 * frame.
 */
@SideOnly(Side.CLIENT)
public class SuperBlackHoleArrayParticle extends SimpleParticle {
    private final int sourceId;

    public SuperBlackHoleArrayParticle(EntitySuperBlackHole source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntitySuperBlackHole.MAX_LIFE_TICKS + 40;
        this.particleScale = 1.0F;
        this.setRBGColorF(0.58F, 0.18F, 1.0F);
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
        if (!(entity instanceof EntitySuperBlackHole) || entity.isDead) {
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
        if (!(entity instanceof EntitySuperBlackHole)) {
            return;
        }
        EntitySuperBlackHole hole = (EntitySuperBlackHole) entity;
        double x = hole.prevPosX + (hole.posX - hole.prevPosX) * partialTicks
                - interpPosX;
        double y = hole.prevPosY + (hole.posY - hole.prevPosY) * partialTicks
                - interpPosY;
        double z = hole.prevPosZ + (hole.posZ - hole.prevPosZ) * partialTicks
                - interpPosZ;
        if (x * x + y * y + z * z > 384.0D * 384.0D) {
            return;
        }
        double age = hole.getSpellAge() + partialTicks;
        // Radius is derived locally from the same timeline as the server. It
        // uses the interpolated render age, so partialTicks now drives size
        // directly instead of interpolating between network snapshots.
        double radius = EntitySuperBlackHole.getRadiusAtAge(age);
        double centerY = y + EntitySuperBlackHole.CENTER_HEIGHT;
        SuperBlackHoleRenderHelper.render(buffer, x, y, z, age, radius,
                centerY, hole.getFinishTick());
    }
}
