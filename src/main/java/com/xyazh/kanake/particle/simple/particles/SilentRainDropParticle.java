package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.SuperMagicRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural rain-drop streak falling from the Silent Rain array.
 */
@SideOnly(Side.CLIENT)
public class SilentRainDropParticle extends SimpleParticle {
    private final double groundY;

    public SilentRainDropParticle(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z, 0.0D, -0.85D - worldIn.rand.nextDouble() * 0.35D, 0.0D);
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleScale = 1.0F;
        this.groundY = worldIn.getHeight(new BlockPos(x, y, z)).getY() + 0.05D;
        double fall = Math.max(1.0D, y - this.groundY);
        this.particleMaxAge = (int) Math.min(160.0D, Math.max(8.0D, fall / 0.9D + 10.0D));
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
        this.posY += this.motionY;
        this.posX += this.motionX;
        this.posZ += this.motionZ;
        if (this.posY <= this.groundY) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        double x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX;
        double y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY;
        double z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ;
        SuperMagicRenderHelper.renderRainDrop(buffer, x, y, z,
                this.particleAge + partialTicks);
    }
}
