package com.xyazh.kanake.render;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural blue-white rendering for the Guardian Beam projectile and its
 * impact burst. The geometry is opaque so it is included by the framebuffer
 * bloom pass.
 */
@SideOnly(Side.CLIENT)
public final class GuardianBeamRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] BLUE = {0.18F, 0.62F, 1.0F};
    private static final float[] CYAN = {0.42F, 0.92F, 1.0F};
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};

    private GuardianBeamRenderHelper() {
    }

    public static void renderProjectile(BufferBuilder buffer,
                                        double ox, double oy, double oz,
                                        double dx, double dy, double dz,
                                        double age) {
        double dl = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dl < 0.0001D) {
            dx = 0.0D;
            dy = 0.0D;
            dz = 1.0D;
        } else {
            dx /= dl;
            dy /= dl;
            dz /= dl;
        }

        double ux = 0.0D;
        double uy = 1.0D;
        double uz = 0.0D;
        if (Math.abs(dy) > 0.95D) {
            ux = 1.0D;
            uy = 0.0D;
        }
        double rx = dy * uz - dz * uy;
        double ry = dz * ux - dx * uz;
        double rz = dx * uy - dy * ux;
        double rl = Math.sqrt(rx * rx + ry * ry + rz * rz);
        if (rl < 0.0001D) {
            rx = 1.0D;
            ry = 0.0D;
            rz = 0.0D;
            rl = 1.0D;
        }
        rx /= rl;
        ry /= rl;
        rz /= rl;
        double ux2 = ry * dz - rz * dy;
        double uy2 = rz * dx - rx * dz;
        double uz2 = rx * dy - ry * dx;

        double tailBegin = -14.0D;
        // The bright core is now twice as long as the original 7.2-block body.
        double coreBegin = -5.0D;
        double end = 9.4D;
        double radius = 0.13D;
        double tailRadius = 0.018D;
        int segments = 12;

        // Long tapered tail: thin at the far end, merging into the bright core.
        int tailSlices = 14;
        for (int s = 0; s < tailSlices; ++s) {
            double t0 = (double) s / tailSlices;
            double t1 = (double) (s + 1) / tailSlices;
            double z0 = tailBegin + (coreBegin - tailBegin) * t0;
            double z1 = tailBegin + (coreBegin - tailBegin) * t1;
            double r0 = tailRadius + (radius * 0.92D - tailRadius) * t0;
            double r1 = tailRadius + (radius * 0.92D - tailRadius) * t1;
            for (int i = 0; i < segments; ++i) {
                double a0 = TWO_PI * i / segments;
                double a1 = TWO_PI * (i + 1) / segments;
                float wave = (float) (0.5D + 0.5D * Math.sin(
                        a0 * 4.0D + age * 0.7D + s * 0.45D));
                beamSideTapered(buffer, ox + dx * z0, oy + dy * z0, oz + dz * z0,
                        dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                        z1 - z0, r0, r1, a0, a1,
                        mix(BLUE[0], WHITE[0], wave * 0.58F),
                        mix(BLUE[1], WHITE[1], wave * 0.58F),
                        mix(BLUE[2], WHITE[2], wave * 0.58F));
            }
        }

        // Bright main body around the projectile.
        for (int i = 0; i < segments; ++i) {
            double a0 = TWO_PI * i / segments;
            double a1 = TWO_PI * (i + 1) / segments;
            float wave = (float) (0.5D + 0.5D * Math.sin(a0 * 4.0D + age * 0.7D));
            beamSide(buffer, ox + dx * coreBegin, oy + dy * coreBegin, oz + dz * coreBegin,
                    dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    end - coreBegin, radius,
                    a0, a1,
                    mix(BLUE[0], WHITE[0], wave * 0.72F),
                    mix(BLUE[1], WHITE[1], wave * 0.72F),
                    mix(BLUE[2], WHITE[2], wave * 0.72F));
        }

        // White filaments run down the core and the tapered tail.
        for (int i = 0; i < 4; ++i) {
            double a0 = age * 0.6D + TWO_PI * i / 4.0D;
            double a1 = a0 + 0.25D;
            beamSide(buffer, ox + dx * coreBegin, oy + dy * coreBegin, oz + dz * coreBegin,
                    dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    end - coreBegin, radius * 1.10D, a0, a1,
                    WHITE[0], WHITE[1], WHITE[2]);
        }
        for (int i = 0; i < 3; ++i) {
            double a0 = age * 0.6D + TWO_PI * i / 3.0D;
            double a1 = a0 + 0.16D;
            beamSideTapered(buffer,
                    ox + dx * tailBegin, oy + dy * tailBegin, oz + dz * tailBegin,
                    dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    coreBegin - tailBegin, tailRadius, radius * 1.04D, a0, a1,
                    WHITE[0], WHITE[1], WHITE[2]);
        }

        // Close the tapered tail and the bright front tip.
        drawArcRing(buffer, ox + dx * tailBegin, oy + dy * tailBegin, oz + dz * tailBegin,
                rx, ry, rz, ux2, uy2, uz2, 0.0D, tailRadius, 12,
                0.0D, TWO_PI, CYAN[0], CYAN[1], CYAN[2]);
        drawArcRing(buffer, ox + dx * end, oy + dy * end, oz + dz * end,
                rx, ry, rz, ux2, uy2, uz2, 0.0D, radius, 16,
                0.0D, TWO_PI, CYAN[0], CYAN[1], CYAN[2]);

        // A helical highlight follows the whole tail and core.
        double prevX = 0.0D;
        double prevY = 0.0D;
        double prevZ = 0.0D;
        int steps = 48;
        for (int i = 0; i <= steps; ++i) {
            double t = i / (double) steps;
            double along = tailBegin + (end - tailBegin) * t;
            double localRadius = along < coreBegin
                    ? tailRadius + (radius * 1.10D - tailRadius)
                    * ((along - tailBegin) / (coreBegin - tailBegin))
                    : radius * 1.10D;
            double angle = age * 1.1D + t * TWO_PI * 3.0D;
            double cx = ox + dx * along
                    + (rx * Math.cos(angle) + ux2 * Math.sin(angle)) * localRadius * 1.8D;
            double cy = oy + dy * along
                    + (ry * Math.cos(angle) + uy2 * Math.sin(angle)) * localRadius * 1.8D;
            double cz = oz + dz * along
                    + (rz * Math.cos(angle) + uz2 * Math.sin(angle)) * localRadius * 1.8D;
            if (i > 0) {
                line3D(buffer, prevX, prevY, prevZ, cx, cy, cz, 0.015D,
                        WHITE[0], WHITE[1], WHITE[2]);
            }
            prevX = cx;
            prevY = cy;
            prevZ = cz;
        }

        for (int i = 0; i < 5; ++i) {
            double t = coreBegin + (end - coreBegin) * i / 4.0D;
            drawArcRing(buffer, ox + dx * t, oy + dy * t, oz + dz * t,
                    rx, ry, rz, ux2, uy2, uz2,
                    radius * 1.15D, radius * 1.65D, 20,
                    age * 0.6D + i * 0.4D, age * 0.6D + i * 0.4D + TWO_PI,
                    CYAN[0], CYAN[1], CYAN[2]);
        }
        for (int i = 0; i < 4; ++i) {
            double t = (i + 1) / 5.0D;
            double along = tailBegin + (coreBegin - tailBegin) * t;
            double localRadius = tailRadius + (radius * 0.92D - tailRadius) * t;
            drawArcRing(buffer, ox + dx * along, oy + dy * along, oz + dz * along,
                    rx, ry, rz, ux2, uy2, uz2,
                    localRadius * 1.15D, localRadius * 1.70D, 20,
                    age * 0.6D + i * 0.35D, age * 0.6D + i * 0.35D + TWO_PI,
                    BLUE[0], BLUE[1], BLUE[2]);
        }
    }

    public static void renderExplosion(BufferBuilder buffer,
                                       double ox, double oy, double oz,
                                       double age) {
        double progress = clamp(age / 20.0D);
        double fade = age > 14.0D ? clamp((20.0D - age) / 6.0D) : 1.0D;
        double radius = (0.5D + 3.2D * progress) * fade;
        if (radius <= 0.01D) {
            return;
        }
        double rotation = age * 0.22D;
        drawArcRing(buffer, ox, oy, oz, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                radius * 0.84D, radius, 32, rotation, rotation + TWO_PI,
                WHITE[0], WHITE[1], WHITE[2]);
        drawArcRing(buffer, ox, oy, oz, 1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D,
                radius * 0.70D, radius * 0.86D, 32, -rotation, -rotation + TWO_PI,
                CYAN[0], CYAN[1], CYAN[2]);
        drawArcRing(buffer, ox, oy, oz, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                radius * 0.55D, radius * 0.70D, 32, rotation * 1.4D,
                rotation * 1.4D + TWO_PI, BLUE[0], BLUE[1], BLUE[2]);

        double inner = radius * 0.30D;
        for (int i = 0; i < 6; ++i) {
            double angle = rotation + TWO_PI * i / 6.0D;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            line3D(buffer, ox + dx * inner, oy, oz + dz * inner,
                    ox + dx * radius, oy, oz + dz * radius,
                    0.035D, WHITE[0], WHITE[1], WHITE[2]);
            line3D(buffer, ox, oy + dx * inner, oz + dz * inner,
                    ox, oy + dx * radius, oz + dz * radius,
                    0.035D, CYAN[0], CYAN[1], CYAN[2]);
        }
    }

    private static void beamSide(BufferBuilder buffer,
                                 double ox, double oy, double oz,
                                 double dx, double dy, double dz,
                                 double rx, double ry, double rz,
                                 double ux, double uy, double uz,
                                 double length, double radius,
                                 double a0, double a1,
                                 float r, float g, float b) {
        beamSideTapered(buffer, ox, oy, oz, dx, dy, dz,
                rx, ry, rz, ux, uy, uz, length, radius, radius,
                a0, a1, r, g, b);
    }

    private static void beamSideTapered(BufferBuilder buffer,
                                        double ox, double oy, double oz,
                                        double dx, double dy, double dz,
                                        double rx, double ry, double rz,
                                        double ux, double uy, double uz,
                                        double length,
                                        double radiusStart, double radiusEnd,
                                        double a0, double a1,
                                        float r, float g, float b) {
        double c0 = Math.cos(a0);
        double s0 = Math.sin(a0);
        double c1 = Math.cos(a1);
        double s1 = Math.sin(a1);
        double x0 = ox + (rx * c0 + ux * s0) * radiusStart;
        double y0 = oy + (ry * c0 + uy * s0) * radiusStart;
        double z0 = oz + (rz * c0 + uz * s0) * radiusStart;
        double x1 = ox + (rx * c1 + ux * s1) * radiusStart;
        double y1 = oy + (ry * c1 + uy * s1) * radiusStart;
        double z1 = oz + (rz * c1 + uz * s1) * radiusStart;
        double endX = ox + dx * length;
        double endY = oy + dy * length;
        double endZ = oz + dz * length;
        double ex0 = endX + (rx * c0 + ux * s0) * radiusEnd;
        double ey0 = endY + (ry * c0 + uy * s0) * radiusEnd;
        double ez0 = endZ + (rz * c0 + uz * s0) * radiusEnd;
        double ex1 = endX + (rx * c1 + ux * s1) * radiusEnd;
        double ey1 = endY + (ry * c1 + uy * s1) * radiusEnd;
        double ez1 = endZ + (rz * c1 + uz * s1) * radiusEnd;
        quad(buffer,
                x0, y0, z0,
                ex0, ey0, ez0,
                ex1, ey1, ez1,
                x1, y1, z1,
                r, g, b);
    }

    private static void drawArcRing(BufferBuilder buffer,
                                    double cx, double cy, double cz,
                                    double rx, double ry, double rz,
                                    double ux, double uy, double uz,
                                    double inner, double outer, int segments,
                                    double start, double end,
                                    float r, float g, float b) {
        double span = end - start;
        if (span <= 0.00001D || outer <= inner) {
            return;
        }
        int count = Math.max(2, (int) Math.ceil(segments * Math.abs(span) / TWO_PI));
        for (int i = 0; i < count; ++i) {
            double a0 = start + span * i / count;
            double a1 = start + span * (i + 1) / count;
            double c0 = Math.cos(a0);
            double s0 = Math.sin(a0);
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            quad(buffer,
                    cx + (rx * c0 + ux * s0) * inner,
                    cy + (ry * c0 + uy * s0) * inner,
                    cz + (rz * c0 + uz * s0) * inner,
                    cx + (rx * c0 + ux * s0) * outer,
                    cy + (ry * c0 + uy * s0) * outer,
                    cz + (rz * c0 + uz * s0) * outer,
                    cx + (rx * c1 + ux * s1) * outer,
                    cy + (ry * c1 + uy * s1) * outer,
                    cz + (rz * c1 + uz * s1) * outer,
                    cx + (rx * c1 + ux * s1) * inner,
                    cy + (ry * c1 + uy * s1) * inner,
                    cz + (rz * c1 + uz * s1) * inner,
                    r, g, b);
        }
    }

    private static void line3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double width, float r, float g, float b) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double horizontalLength = Math.sqrt(dx * dx + dz * dz);
        if (horizontalLength > 0.00001D) {
            double px = -dz / horizontalLength * width;
            double pz = dx / horizontalLength * width;
            quad(buffer,
                    x1 - px, y1, z1 - pz,
                    x1 + px, y1, z1 + pz,
                    x2 + px, y2, z2 + pz,
                    x2 - px, y2, z2 - pz,
                    r, g, b);
        }
        if (Math.abs(y2 - y1) > 0.00001D) {
            quad(buffer,
                    x1, y1 - width, z1,
                    x1, y1 + width, z1,
                    x2, y2 + width, z2,
                    x2, y2 - width, z2,
                    r, g, b);
        }
    }

    private static void quad(BufferBuilder buffer,
                             double x0, double y0, double z0,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             float r, float g, float b) {
        vertex(buffer, x0, y0, z0, r, g, b);
        vertex(buffer, x1, y1, z1, r, g, b);
        vertex(buffer, x2, y2, z2, r, g, b);
        vertex(buffer, x3, y3, z3, r, g, b);
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               float r, float g, float b) {
        buffer.pos(x, y, z).tex(0.0D, 0.0D).color(r, g, b, 1.0F)
                .lightmap(SimpleParticle.FULLBRIGHT_LIGHT_X, SimpleParticle.FULLBRIGHT_LIGHT_Y)
                .endVertex();
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
