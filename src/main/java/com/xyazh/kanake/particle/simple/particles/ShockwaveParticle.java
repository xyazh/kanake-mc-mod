package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ShockwaveParticle extends SimpleParticle {
    private final double maxRadius;
    private final int segments = 32;

    public ShockwaveParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                             double maxRadius, float red, float green, float blue) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.maxRadius = maxRadius;
        this.particleMaxAge = 10;
        this.particleScale = (float) maxRadius;
        this.setRBGColorF(red, green, blue);
        this.setPosition(xCoordIn, yCoordIn + 0.05, zCoordIn);
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
        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        float progress = (float) this.particleAge / (float) this.particleMaxAge;
        double scaledMaxRadius = this.maxRadius * SIZE_SCALE;
        double radius = scaledMaxRadius * (0.15 + 0.85 * progress);
        double inner = Math.max(0.0, radius - scaledMaxRadius * 0.18);
        float r = (float) this.color.x;
        float g = (float) this.color.y;
        float b = (float) this.color.z;
        float a = 1.0f;

        for (int i = 0; i < this.segments; i++) {
            double a0 = Math.PI * 2 * i / this.segments;
            double a1 = Math.PI * 2 * (i + 1) / this.segments;
            double x0 = Math.cos(a0), z0 = Math.sin(a0);
            double x1 = Math.cos(a1), z1 = Math.sin(a1);
            buffer.pos(x + x0 * inner, y, z + z0 * inner).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + x0 * radius, y, z + z0 * radius).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + x1 * radius, y, z + z1 * radius).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + x1 * inner, y, z + z1 * inner).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
        }
    }
}
