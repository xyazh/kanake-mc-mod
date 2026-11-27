package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityWideLightningBolt;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.WideLightningRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Short-lived renderer anchor for one synchronized chain link.
 */
@SideOnly(Side.CLIENT)
public class WideLightningBoltParticle extends SimpleParticle {
    private final int sourceId;

    public WideLightningBoltParticle(EntityWideLightningBolt source) {
        super(source.world, source.posX, source.posY, source.posZ,
                0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityWideLightningBolt.MAX_AGE + 5;
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
        if (!(entity instanceof EntityWideLightningBolt) || entity.isDead) {
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
        if (!(entity instanceof EntityWideLightningBolt)) {
            return;
        }
        EntityWideLightningBolt bolt = (EntityWideLightningBolt) entity;
        double sx = bolt.prevPosX + (bolt.posX - bolt.prevPosX) * partialTicks
                - interpPosX;
        double sy = bolt.prevPosY + (bolt.posY - bolt.prevPosY) * partialTicks
                - interpPosY;
        double sz = bolt.prevPosZ + (bolt.posZ - bolt.prevPosZ) * partialTicks
                - interpPosZ;
        double age = bolt.ticksExisted + partialTicks;
        if (sx * sx + sy * sy + sz * sz > 384.0D * 384.0D) {
            return;
        }
        WideLightningRenderHelper.renderChainBolt(buffer,
                sx, sy, sz,
                bolt.getTargetX() - interpPosX,
                bolt.getTargetY() - interpPosY,
                bolt.getTargetZ() - interpPosZ,
                age, EntityWideLightningBolt.MAX_AGE, bolt.getSeed());
    }
}
