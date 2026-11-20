package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityMagicCannonBeam;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.MagicCannonRenderHelper;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Long-lived procedural particle for the fixed Magic Cannon beam.
 */
@SideOnly(Side.CLIENT)
public class MagicCannonBeamParticle extends SimpleParticle {
    private final int sourceId;

    public MagicCannonBeamParticle(EntityMagicCannonBeam source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityMagicCannonBeam.DURATION_TICKS + 20;
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
        if (!(entity instanceof EntityMagicCannonBeam) || entity.isDead) {
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
        if (!(entity instanceof EntityMagicCannonBeam)) {
            return;
        }
        EntityMagicCannonBeam beam = (EntityMagicCannonBeam) entity;
        double ox = beam.prevPosX + (beam.posX - beam.prevPosX) * partialTicks - interpPosX;
        double oy = beam.prevPosY + (beam.posY - beam.prevPosY) * partialTicks - interpPosY;
        double oz = beam.prevPosZ + (beam.posZ - beam.prevPosZ) * partialTicks - interpPosZ;
        Vec3d direction = beam.getForwardVec();
        double age = beam.getSpellAge() + partialTicks;
        MagicCannonRenderHelper.renderBeam(buffer, ox, oy, oz,
                direction.x, direction.y, direction.z, age);
    }
}
