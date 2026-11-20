package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityMagicCannonCharge;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.MagicCannonRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Follows the charge focus and draws the feet/front magic arrays procedurally.
 */
@SideOnly(Side.CLIENT)
public class MagicCannonChargeParticle extends SimpleParticle {
    private final int sourceId;

    public MagicCannonChargeParticle(EntityMagicCannonCharge source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityMagicCannonCharge.CHARGE_TICKS + 20;
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
        if (!(entity instanceof EntityMagicCannonCharge) || entity.isDead) {
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
        if (!(entity instanceof EntityMagicCannonCharge)) {
            return;
        }
        EntityMagicCannonCharge charge = (EntityMagicCannonCharge) entity;
        EntityLivingBase owner = charge.getOwner();
        if (owner == null || owner.isDead) {
            return;
        }
        double ox = owner.prevPosX + (owner.posX - owner.prevPosX) * partialTicks - interpPosX;
        double oy = owner.prevPosY + (owner.posY - owner.prevPosY) * partialTicks - interpPosY;
        double oz = owner.prevPosZ + (owner.posZ - owner.prevPosZ) * partialTicks - interpPosZ;
        double eyeY = owner.prevPosY + (owner.posY - owner.prevPosY) * partialTicks
                + owner.getEyeHeight() - interpPosY;
        Vec3d look = owner.getLook(partialTicks);
        double age = charge.getSpellAge() + partialTicks;
        MagicCannonRenderHelper.renderCharge(buffer, ox, oy, oz, eyeY,
                look.x, look.y, look.z, age);
    }
}
