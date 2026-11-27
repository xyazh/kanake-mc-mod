package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityWideLightningStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.WideLightningRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Long-lived anchor particle for the cloud array and central thunderbolt.
 */
@SideOnly(Side.CLIENT)
public class WideLightningArrayParticle extends SimpleParticle {
    private final int sourceId;

    public WideLightningArrayParticle(EntityWideLightningStrike source) {
        super(source.world, source.posX, source.posY, source.posZ,
                0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityWideLightningStrike.MAX_LIFE_TICKS + 20;
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
            if (this.particleAge > 5) {
                this.setExpired();
            }
            return;
        }
        if (!(entity instanceof EntityWideLightningStrike) || entity.isDead) {
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
        if (!(entity instanceof EntityWideLightningStrike)) {
            return;
        }
        EntityWideLightningStrike strike = (EntityWideLightningStrike) entity;
        double x = strike.prevPosX + (strike.posX - strike.prevPosX) * partialTicks
                - interpPosX;
        double y = strike.prevPosY + (strike.posY - strike.prevPosY) * partialTicks
                - interpPosY;
        double z = strike.prevPosZ + (strike.posZ - strike.prevPosZ) * partialTicks
                - interpPosZ;
        double absoluteY = strike.prevPosY
                + (strike.posY - strike.prevPosY) * partialTicks;
        double arrayY = EntityWideLightningStrike.getArrayY(strike.world, absoluteY)
                - absoluteY;
        double age = strike.getSpellAge() + partialTicks;
        if (x * x + y * y + z * z <= 384.0D * 384.0D) {
            WideLightningRenderHelper.render(buffer, x, y, z, arrayY, age,
                    strike.getEntityId());
        }
    }
}
