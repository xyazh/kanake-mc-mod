package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityGodWrathBeam;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.SuperMagicRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural thin laser emitted by one of the Wrath of God arrays.
 */
@SideOnly(Side.CLIENT)
public class GodWrathBeamParticle extends SimpleParticle {
    private final int sourceId;

    public GodWrathBeamParticle(EntityGodWrathBeam source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityGodWrathBeam.MAX_AGE + 5;
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
        if (!(entity instanceof EntityGodWrathBeam) || entity.isDead) {
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
        if (!(entity instanceof EntityGodWrathBeam)) {
            return;
        }
        EntityGodWrathBeam beam = (EntityGodWrathBeam) entity;
        double sx = beam.prevPosX + (beam.posX - beam.prevPosX) * partialTicks - interpPosX;
        double sy = beam.prevPosY + (beam.posY - beam.prevPosY) * partialTicks - interpPosY;
        double sz = beam.prevPosZ + (beam.posZ - beam.prevPosZ) * partialTicks - interpPosZ;
        double age = beam.ticksExisted + partialTicks;
        SuperMagicRenderHelper.renderGodWrathBeam(buffer,
                sx, sy, sz,
                beam.getTargetX() - interpPosX,
                beam.getTargetY() - interpPosY,
                beam.getTargetZ() - interpPosZ,
                age);
    }
}
