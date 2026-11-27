package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityWideLightningStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Vanilla-style lightning built from jagged box strips. The strips are drawn
 * as one opaque layer (alpha 1.0) with normal blending so the screen/effect
 * buffer comparison in the bloom shader can extract them. Chain links use the
 * same routine with smaller widths, jitter and segment counts.
 */
@SideOnly(Side.CLIENT)
public final class WideLightningWorldRenderer {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] LIGHTNING = {0.1019608F, 0.9843137F, 0.9843137F};
    private static final double VIEW_RANGE_SQ = 384.0D * 384.0D;
    /** Lightning keeps one jagged shape for eight ticks, then re-jags. */
    private static final double SHAPE_CHANGE_TICKS = 8.0D;

    private WideLightningWorldRenderer() {
    }

    public static boolean render(BufferBuilder buffer, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null) {
            return false;
        }
        double ix = SimpleParticle.interpPosX;
        double iy = SimpleParticle.interpPosY;
        double iz = SimpleParticle.interpPosZ;
        boolean drew = false;
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityWideLightningStrike)) {
                continue;
            }
            EntityWideLightningStrike strike = (EntityWideLightningStrike) entity;
            if (strike.isDead) {
                continue;
            }
            double x = strike.prevPosX + (strike.posX - strike.prevPosX) * partialTicks - ix;
            double y = strike.prevPosY + (strike.posY - strike.prevPosY) * partialTicks - iy;
            double z = strike.prevPosZ + (strike.posZ - strike.prevPosZ) * partialTicks - iz;
            double absY = strike.prevPosY + (strike.posY - strike.prevPosY) * partialTicks;
            double skyY = EntityWideLightningStrike.getArrayY(strike.world, absY) - iy;
            double age = strike.getSpellAge() + partialTicks;
            if (age < EntityWideLightningStrike.IMPACT_TICK) {
                continue;
            }
            boolean mainVisible = x * x + y * y + z * z <= VIEW_RANGE_SQ;
            if (mainVisible) {
                float boltFade = fadeBolt(age);
                if (boltFade > 0.01F) {
                    // Several full-height bolts strike together, instead of
                    // one lonely column.
                    int mainBolts = 5;
                    long mainSeed = strike.getEntityId() * 0x9E3779B97F4A7C15L;
                    for (int b = 0; b < mainBolts; ++b) {
                        double angle = b * TWO_PI / mainBolts + 0.25D;
                        double radius = b == 0 ? 0.0D : 2.5D + b * 1.25D;
                        double bx = x + Math.cos(angle) * radius;
                        double bz = z + Math.sin(angle) * radius;
                        float width = b == 0 ? 1.0F : 0.78F;
                        long seed = shapeSeed(mainSeed + b * 104729L, age);
                        drawVanillaBolt(buffer, bx, skyY, bz,
                                bx, y + 0.05D, bz,
                                seed,
                                1, 8, 4.6D, width, 1.0F,
                                b == 0 ? 2 : 1);
                    }
                    drew = true;
                }
            }
            if (renderChainLinks(buffer, strike, partialTicks, ix, iy, iz)) {
                drew = true;
            }
        }
        return drew;
    }

    private static boolean renderChainLinks(BufferBuilder buffer,
                                            EntityWideLightningStrike strike,
                                            float partialTicks,
                                            double ix, double iy, double iz) {
        boolean drew = false;
        for (EntityWideLightningStrike.ClientChainVisual link
                : strike.getClientChainVisuals()) {
            double chainAge = strike.ticksExisted + partialTicks - link.birthAge;
            if (chainAge < 0.0D
                    || chainAge > EntityWideLightningStrike.CHAIN_VISUAL_LIFE) {
                continue;
            }
            double sx = link.sourceX - ix;
            double sy = link.sourceY - iy;
            double sz = link.sourceZ - iz;
            double tx = link.targetX - ix;
            double ty = link.targetY - iy;
            double tz = link.targetZ - iz;
            if (sx * sx + sy * sy + sz * sz > VIEW_RANGE_SQ
                    && tx * tx + ty * ty + tz * tz > VIEW_RANGE_SQ) {
                continue;
            }
            long seed = shapeSeed(link.seed * 0x632BE59BD9B4E019L, chainAge);
            drawVanillaBolt(buffer, sx, sy, sz, tx, ty, tz,
                    seed,
                    1, 5, 1.15D, 0.38F, 1.0F, 1);
            drew = true;
        }
        return drew;
    }
    private static long shapeSeed(long baseSeed, double age) {
        long phase = (long) Math.floor(Math.max(0.0D, age)
                / SHAPE_CHANGE_TICKS);
        return baseSeed + phase * 0x9E3779B97F4A7C15L;
    }

    private static void drawVanillaBolt(BufferBuilder buffer,
                                        double sx, double sy, double sz,
                                        double tx, double ty, double tz,
                                        long seed, int layers, int segments,
                                        double jitter, float widthScale,
                                        float alpha, int branches) {
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.1D || alpha <= 0.005F) {
            return;
        }
        double ux = dx / distance;
        double uy = dy / distance;
        double uz = dz / distance;
        double rx;
        double ry;
        double rz;
        if (Math.abs(uy) < 0.92D) {
            rx = -uz;
            ry = 0.0D;
            rz = ux;
        } else {
            rx = 1.0D;
            ry = 0.0D;
            rz = 0.0D;
        }
        double rl = Math.sqrt(rx * rx + ry * ry + rz * rz);
        rx /= rl;
        ry /= rl;
        rz /= rl;
        double vx = uy * rz - uz * ry;
        double vy = uz * rx - ux * rz;
        double vz = ux * ry - uy * rx;
        double vl = Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (vl < 0.0001D) {
            vx = 0.0D;
            vy = 1.0D;
            vz = 0.0D;
        } else {
            vx /= vl;
            vy /= vl;
            vz /= vl;
        }

        Random random = new Random(seed);
        double[] xs = new double[segments + 1];
        double[] ys = new double[segments + 1];
        double[] zs = new double[segments + 1];
        for (int i = 0; i <= segments; ++i) {
            double t = i / (double) segments;
            double envelope = Math.sin(t * Math.PI);
            double offR = (random.nextDouble() * 2.0D - 1.0D) * jitter * envelope;
            double offV = (random.nextDouble() * 2.0D - 1.0D) * jitter
                    * envelope * 0.72D;
            if (i == 0 || i == segments) {
                offR = 0.0D;
                offV = 0.0D;
            }
            xs[i] = sx + dx * t + rx * offR + vx * offV;
            ys[i] = sy + dy * t + ry * offR + vy * offV;
            zs[i] = sz + dz * t + rz * offR + vz * offV;
        }

        drawLayeredBolt(buffer, xs, ys, zs, segments, layers,
                widthScale, alpha, rx, ry, rz, vx, vy, vz);

        if (branches > 0) {
            Random branchRand = new Random(seed ^ 0x27D4EB2F165667C5L);
            int actual = Math.min(branches, 2);
            for (int b = 0; b < actual; ++b) {
                int at = 2 + branchRand.nextInt(Math.max(1, segments - 3));
                double length = distance * (0.10D + branchRand.nextDouble() * 0.12D);
                double a = branchRand.nextDouble() * TWO_PI;
                double ex = xs[at] + Math.cos(a) * length;
                double ey = ys[at] - length * (0.15D + branchRand.nextDouble() * 0.25D);
                double ez = zs[at] + Math.sin(a) * length;
                drawVanillaBolt(buffer, xs[at], ys[at], zs[at], ex, ey, ez,
                        seed + b * 104729L, Math.max(1, layers - 1),
                        Math.max(3, segments / 2), jitter * 0.75D,
                        widthScale * 0.72F, alpha, 0);
            }
        }
    }

    private static void drawLayeredBolt(BufferBuilder buffer,
                                        double[] xs, double[] ys, double[] zs,
                                        int segments, int layers,
                                        float widthScale, float alpha,
                                        double rx, double ry, double rz,
                                        double vx, double vy, double vz) {
        for (int layer = 0; layer < layers; ++layer) {
            double width = layers == 1
                    ? widthScale * 0.40D
                    : widthScale * (0.10D + layer * 0.18D);
            if (width < 0.015D) {
                width = 0.015D;
            }
            float[] color = layers == 1 ? LIGHTNING
                    : (layer == layers - 1 ? WHITE
                    : LIGHTNING);
            float[] startColor = layers == 1 ? WHITE : color;
            float[] endColor = color;
            float layerAlpha = alpha * (layers == 1 ? 1.0F
                    : (layer == layers - 1 ? 1.0F : 0.78F));
            for (int i = 0; i < segments; ++i) {
                double t0 = i / (double) segments;
                double t1 = (i + 1.0D) / segments;
                double w0 = width * (1.0D + (1.0D - t0) * 0.35D);
                double w1 = width * (1.0D + (1.0D - t1) * 0.35D);
                for (int side = 0; side < 4; ++side) {
                    double a0 = side * Math.PI * 0.5D;
                    double a1 = a0 + Math.PI * 0.5D;
                    double c0 = Math.cos(a0);
                    double s0 = Math.sin(a0);
                    double c1 = Math.cos(a1);
                    double s1 = Math.sin(a1);
                    quad(buffer,
                            xs[i] + (rx * c0 + vx * s0) * w0,
                            ys[i] + (ry * c0 + vy * s0) * w0,
                            zs[i] + (rz * c0 + vz * s0) * w0,
                            xs[i] + (rx * c1 + vx * s1) * w0,
                            ys[i] + (ry * c1 + vy * s1) * w0,
                            zs[i] + (rz * c1 + vz * s1) * w0,
                            xs[i + 1] + (rx * c1 + vx * s1) * w1,
                            ys[i + 1] + (ry * c1 + vy * s1) * w1,
                            zs[i + 1] + (rz * c1 + vz * s1) * w1,
                            xs[i + 1] + (rx * c0 + vx * s0) * w1,
                            ys[i + 1] + (ry * c0 + vy * s0) * w1,
                            zs[i + 1] + (rz * c0 + vz * s0) * w1,
                            startColor, endColor, t0, t1, layerAlpha);
                }
            }
        }
    }

    private static float fadeBolt(double age) {
        return 1.0F - (float) smoothstep(clamp((age
                - EntityWideLightningStrike.IMPACT_TICK) / 80.0D));
    }

    private static void quad(BufferBuilder buffer,
                             double x0, double y0, double z0,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             float[] startColor, float[] endColor,
                             double t0, double t1, float alpha) {
        float sr = startColor[0] + (endColor[0] - startColor[0]) * (float) t0;
        float sg = startColor[1] + (endColor[1] - startColor[1]) * (float) t0;
        float sb = startColor[2] + (endColor[2] - startColor[2]) * (float) t0;
        float er = startColor[0] + (endColor[0] - startColor[0]) * (float) t1;
        float eg = startColor[1] + (endColor[1] - startColor[1]) * (float) t1;
        float eb = startColor[2] + (endColor[2] - startColor[2]) * (float) t1;
        vertex(buffer, x0, y0, z0, sr, sg, sb, alpha);
        vertex(buffer, x1, y1, z1, sr, sg, sb, alpha);
        vertex(buffer, x2, y2, z2, er, eg, eb, alpha);
        vertex(buffer, x3, y3, z3, er, eg, eb, alpha);
    }

    private static void vertex(BufferBuilder buffer,
                               double x, double y, double z,
                               float red, float green, float blue,
                               float alpha) {
        buffer.pos(x, y, z).tex(0.0D, 0.0D)
                .color(red, green, blue, alpha)
                .lightmap(SimpleParticle.FULLBRIGHT_LIGHT_X,
                        SimpleParticle.FULLBRIGHT_LIGHT_Y)
                .endVertex();
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }
}
