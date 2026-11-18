package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntitySkyStrikeProjectile;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural billboard orb for the Sky Strike projectile. Several opaque,
 * camera-facing layers plus two rotating rings produce a bright core that the
 * framebuffer bloom pass can pick up.
 */
@SideOnly(Side.CLIENT)
public class SkyStrikeOrbParticle extends SimpleParticle {
    private final int sourceId;

    public SkyStrikeOrbParticle(EntitySkyStrikeProjectile source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = 400;
        this.particleScale = 1.0F;
        this.setRBGColorF(0.55F, 0.9F, 1.0F);
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
        if (!(entity instanceof EntitySkyStrikeProjectile) || entity.isDead) {
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
        if (!(entity instanceof EntitySkyStrikeProjectile)) {
            return;
        }
        float x = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks - interpPosX);
        float y = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks - interpPosY);
        float z = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks - interpPosZ);
        double age = entity.ticksExisted + partialTicks;
        float pulse = 1.0F + 0.10F * (float) Math.sin(age * 0.9D);
        float size = 0.95F * pulse;

        double x0 = -rotationX * size - rotationXY * size;
        double y0 = -rotationZ * size;
        double z0 = -rotationYZ * size - rotationXZ * size;
        double x1 = -rotationX * size + rotationXY * size;
        double y1 = rotationZ * size;
        double z1 = -rotationYZ * size + rotationXZ * size;
        double x2 = rotationX * size + rotationXY * size;
        double y2 = rotationZ * size;
        double z2 = rotationYZ * size + rotationXZ * size;
        double x3 = rotationX * size - rotationXY * size;
        double y3 = -rotationZ * size;
        double z3 = rotationYZ * size - rotationXZ * size;

        // The layers are shifted slightly toward the camera. Mc's default
        // depth function can be GL_LESS, so identical-depth coplanar layers
        // would otherwise reject the inner colors.
        double cameraLength = Math.sqrt(x * x + y * y + z * z);
        double nx = 0.0D;
        double ny = 0.0D;
        double nz = 0.0D;
        if (cameraLength > 0.0001D) {
            nx = -x / cameraLength;
            ny = -y / cameraLength;
            nz = -z / cameraLength;
        }
        float middleX = (float) (x + nx * 0.035D);
        float middleY = (float) (y + ny * 0.035D);
        float middleZ = (float) (z + nz * 0.035D);
        float coreX = (float) (x + nx * 0.070D);
        float coreY = (float) (y + ny * 0.070D);
        float coreZ = (float) (z + nz * 0.070D);

        // Opaque layered billboard: dark cyan rim, pale middle, white core.
        quad(buffer, x, y, z, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3,
                0.18F, 0.65F, 1.0F);
        quad(buffer, middleX, middleY, middleZ,
                x0 * 0.62D, y0 * 0.62D, z0 * 0.62D,
                x1 * 0.62D, y1 * 0.62D, z1 * 0.62D,
                x2 * 0.62D, y2 * 0.62D, z2 * 0.62D,
                x3 * 0.62D, y3 * 0.62D, z3 * 0.62D,
                0.65F, 0.92F, 1.0F);
        quad(buffer, coreX, coreY, coreZ,
                x0 * 0.27D, y0 * 0.27D, z0 * 0.27D,
                x1 * 0.27D, y1 * 0.27D, z1 * 0.27D,
                x2 * 0.27D, y2 * 0.27D, z2 * 0.27D,
                x3 * 0.27D, y3 * 0.27D, z3 * 0.27D,
                1.0F, 1.0F, 1.0F);

        // Two screen-plane rings rotating in opposite directions.
        double rx = (x2 - x0) * 0.5D;
        double ry = (y2 - y0) * 0.5D;
        double rz = (z2 - z0) * 0.5D;
        double ux = (x1 - x3) * 0.5D;
        double uy = (y1 - y3) * 0.5D;
        double uz = (z1 - z3) * 0.5D;
        ring(buffer, (float) (x + nx * 0.105D), (float) (y + ny * 0.105D),
                (float) (z + nz * 0.105D), rx, ry, rz, ux, uy, uz, 0.78D, 0.92D,
                age * 0.45D, 0.85F, 0.98F, 1.0F);
        ring(buffer, (float) (x + nx * 0.140D), (float) (y + ny * 0.140D),
                (float) (z + nz * 0.140D), rx, ry, rz, ux, uy, uz, 0.38D, 0.50D,
                -age * 0.70D, 1.0F, 0.82F, 0.32F);
    }

    private static void ring(BufferBuilder buffer, float cx, float cy, float cz,
                             double rx, double ry, double rz,
                             double ux, double uy, double uz,
                             double inner, double outer, double rotation,
                             float r, float g, float b) {
        int segments = 24;
        for (int i = 0; i < segments; ++i) {
            double a0 = Math.PI * 2.0D * i / segments + rotation;
            double a1 = Math.PI * 2.0D * (i + 1) / segments + rotation;
            double cos0 = Math.cos(a0);
            double sin0 = Math.sin(a0);
            double cos1 = Math.cos(a1);
            double sin1 = Math.sin(a1);
            vertex(buffer, (float) (cx + rx * cos0 * inner + ux * sin0 * inner),
                    (float) (cy + ry * cos0 * inner + uy * sin0 * inner),
                    (float) (cz + rz * cos0 * inner + uz * sin0 * inner), r, g, b);
            vertex(buffer, (float) (cx + rx * cos0 * outer + ux * sin0 * outer),
                    (float) (cy + ry * cos0 * outer + uy * sin0 * outer),
                    (float) (cz + rz * cos0 * outer + uz * sin0 * outer), r, g, b);
            vertex(buffer, (float) (cx + rx * cos1 * outer + ux * sin1 * outer),
                    (float) (cy + ry * cos1 * outer + uy * sin1 * outer),
                    (float) (cz + rz * cos1 * outer + uz * sin1 * outer), r, g, b);
            vertex(buffer, (float) (cx + rx * cos1 * inner + ux * sin1 * inner),
                    (float) (cy + ry * cos1 * inner + uy * sin1 * inner),
                    (float) (cz + rz * cos1 * inner + uz * sin1 * inner), r, g, b);
        }
    }

    private static void quad(BufferBuilder buffer, float cx, float cy, float cz,
                             double x0, double y0, double z0,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             float r, float g, float b) {
        vertex(buffer, cx + (float) x0, cy + (float) y0, cz + (float) z0, r, g, b);
        vertex(buffer, cx + (float) x1, cy + (float) y1, cz + (float) z1, r, g, b);
        vertex(buffer, cx + (float) x2, cy + (float) y2, cz + (float) z2, r, g, b);
        vertex(buffer, cx + (float) x3, cy + (float) y3, cz + (float) z3, r, g, b);
    }

    private static void vertex(BufferBuilder buffer, float x, float y, float z,
                               float r, float g, float b) {
        buffer.pos(x, y, z).tex(0.0D, 0.0D).color(r, g, b, 1.0F)
                .lightmap(FULLBRIGHT_LIGHT_X, FULLBRIGHT_LIGHT_Y).endVertex();
    }
}
