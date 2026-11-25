package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityGuardianBeam;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.GuardianBeamRenderHelper;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural thin blue-white beam that follows the Guardian Beam projectile.
 * When the projectile reports an impact, this particle spawns the burst effect
 * and expires.
 */
@SideOnly(Side.CLIENT)
public class GuardianBeamParticle extends SimpleParticle {
    private final int sourceId;

    public GuardianBeamParticle(EntityGuardianBeam source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = 80;
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
        if (!(entity instanceof EntityGuardianBeam)) {
            this.setExpired();
            return;
        }

        EntityGuardianBeam beam = (EntityGuardianBeam) entity;
        if (beam.isImpacted()) {
            this.setExpired();
            return;
        }
        if (beam.isDead) {
            this.setExpired();
            return;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.posX = beam.posX;
        this.posY = beam.posY;
        this.posZ = beam.posZ;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Entity entity = this.world.getEntityByID(this.sourceId);
        if (!(entity instanceof EntityGuardianBeam)) {
            return;
        }
        EntityGuardianBeam beam = (EntityGuardianBeam) entity;
        double x = beam.prevPosX + (beam.posX - beam.prevPosX) * partialTicks - interpPosX;
        double y = beam.prevPosY + (beam.posY - beam.prevPosY) * partialTicks - interpPosY;
        double z = beam.prevPosZ + (beam.posZ - beam.prevPosZ) * partialTicks - interpPosZ;
        Vec3d direction = beam.getForwardVec();
        double age = beam.ticksExisted + partialTicks;
        GuardianBeamRenderHelper.renderProjectile(buffer, x, y, z,
                direction.x, direction.y, direction.z, age);
    }
}
