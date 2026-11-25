package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.GuardianBeamRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blue-white procedural burst spawned at the Guardian Beam impact point.
 */
@SideOnly(Side.CLIENT)
public class GuardianBeamExplosionParticle extends SimpleParticle {
    public GuardianBeamExplosionParticle(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z, 0.0D, 0.0D, 0.0D);
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = 20;
        this.particleScale = 1.0F;
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        double x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
        double y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
        double z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;
        GuardianBeamRenderHelper.renderExplosion(buffer, x, y, z,
                this.particleAge + partialTicks);
    }
}
