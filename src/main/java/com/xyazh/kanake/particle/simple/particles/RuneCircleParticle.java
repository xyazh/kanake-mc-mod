package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RuneCircleParticle extends SimpleParticle {
    private final double radius;
    private final float rotationSpeed;
    private final int segments = 24;

    public RuneCircleParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                              double radius, float red, float green, float blue) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.radius = radius;
        this.rotationSpeed = 8.0f + this.rand.nextFloat() * 8.0f;
        this.particleMaxAge = 20;
        this.particleScale = (float) radius;
        this.setRBGColorF(red, green, blue);
        this.setPosition(xCoordIn, yCoordIn + 0.06, zCoordIn);
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
        float r = (float) this.color.x;
        float g = (float) this.color.y;
        float b = (float) this.color.z;
        float a = 1.0f;
        double rotation = Math.toRadians(this.particleAge * this.rotationSpeed);
        double progress = (double) this.particleAge / (double) this.particleMaxAge;
        double outer = this.radius * SIZE_SCALE * (1.0 - 0.4 * progress);
        double inner = outer * 0.82;

        for (int i = 0; i < this.segments; i++) {
            double a0 = Math.PI * 2 * i / this.segments + rotation;
            double a1 = Math.PI * 2 * (i + 1) / this.segments + rotation;
            double x0 = Math.cos(a0), z0 = Math.sin(a0);
            double x1 = Math.cos(a1), z1 = Math.sin(a1);
            buffer.pos(x + x0 * inner, y, z + z0 * inner).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + x0 * outer, y, z + z0 * outer).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + x1 * outer, y, z + z1 * outer).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + x1 * inner, y, z + z1 * inner).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
        }

        for (int i = 0; i < 4; i++) {
            double angle = rotation + Math.PI / 2 * i;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            double px = -dz * 0.04;
            double pz = dx * 0.04;
            buffer.pos(x + dx * inner - px, y, z + dz * inner - pz).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + dx * outer - px, y, z + dz * outer - pz).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + dx * outer + px, y, z + dz * outer + pz).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
            buffer.pos(x + dx * inner + px, y, z + dz * inner + pz).tex(0.0D, 0.0D).color(r, g, b, a).lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
        }
    }
}
