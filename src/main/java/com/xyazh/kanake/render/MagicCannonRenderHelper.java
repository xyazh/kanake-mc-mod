package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityMagicCannonBeam;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural geometry for the Magic Cannon charge arrays and beam. All geometry
 * is emitted as opaque vertex-colored quads/triangles so the existing bloom
 * extraction can pick it up.
 */
@SideOnly(Side.CLIENT)
public final class MagicCannonRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] PURPLE = {0.58F, 0.28F, 1.0F};
    private static final float[] CYAN = {0.22F, 0.82F, 1.0F};
    private static final float[] BLUE = {0.34F, 0.55F, 1.0F};
    private static final float[] GOLD = {1.0F, 0.78F, 0.26F};
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};

    private MagicCannonRenderHelper() {
    }

    public static void renderCharge(BufferBuilder buffer,
                                    double ownerX, double ownerY, double ownerZ,
                                    double eyeY,
                                    double lookX, double lookY, double lookZ,
                                    double age) {
        double build = smoothstep(clamp(age / 24.0D));
        double charge = smoothstep(clamp((age - 24.0D) / 36.0D));
        if (age > 64.0D) {
            return;
        }

        double feetRadius = 4.0D * (0.12D + 0.88D * build);
        double frontRadius = 3.2D * (0.12D + 0.88D * build);
        double feetY = ownerY + 0.08D;

        double ll = Math.sqrt(lookX * lookX + lookY * lookY + lookZ * lookZ);
        if (ll < 0.0001D) {
            lookX = 0.0D;
            lookY = 0.0D;
            lookZ = 1.0D;
            ll = 1.0D;
        }
        lookX /= ll;
        lookY /= ll;
        lookZ /= ll;

        double ux = 0.0D;
        double uy = 1.0D;
        double uz = 0.0D;
        if (Math.abs(lookY) > 0.95D) {
            ux = 1.0D;
            uy = 0.0D;
            uz = 0.0D;
        }
        double frontRightX = lookY * uz - lookZ * uy;
        double frontRightY = lookZ * ux - lookX * uz;
        double frontRightZ = lookX * uy - lookY * ux;
        double frontRightLength = Math.sqrt(frontRightX * frontRightX
                + frontRightY * frontRightY + frontRightZ * frontRightZ);
        if (frontRightLength < 0.0001D) {
            frontRightX = 1.0D;
            frontRightY = 0.0D;
            frontRightZ = 0.0D;
            frontRightLength = 1.0D;
        }
        frontRightX /= frontRightLength;
        frontRightY /= frontRightLength;
        frontRightZ /= frontRightLength;
        double frontUpX = frontRightY * lookZ - frontRightZ * lookY;
        double frontUpY = frontRightZ * lookX - frontRightX * lookZ;
        double frontUpZ = frontRightX * lookY - frontRightY * lookX;

        double frontX = ownerX + lookX * 2.3D;
        double frontY = eyeY + lookY * 2.3D;
        double frontZ = ownerZ + lookZ * 2.3D;

        // Feet array: horizontal ring on the ground.
        drawArray(buffer, ownerX, feetY, ownerZ,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                feetRadius, age * 0.10D, build, charge, PURPLE, 64, true);
        // Front array: plane perpendicular to the caster's look direction.
        drawArray(buffer, frontX, frontY, frontZ,
                frontRightX, frontRightY, frontRightZ, frontUpX, frontUpY, frontUpZ,
                frontRadius, -age * 0.16D, build, charge, CYAN, 64, true);

        // Charge core at the front array.
        if (charge > 0.01D) {
            double coreRadius = (0.5D + 1.7D * charge);
            drawArcRing(buffer, frontX, frontY, frontZ,
                    frontRightX, frontRightY, frontRightZ,
                    frontUpX, frontUpY, frontUpZ,
                    coreRadius * 0.65D, coreRadius, 32,
                    age * 0.6D, age * 0.6D + TWO_PI,
                    WHITE[0], WHITE[1], WHITE[2]);
            drawArcRing(buffer, frontX, frontY, frontZ,
                    frontRightX, frontRightY, frontRightZ,
                    frontUpX, frontUpY, frontUpZ,
                    coreRadius * 1.10D, coreRadius * 1.25D, 24,
                    -age * 0.9D, -age * 0.9D + TWO_PI,
                    GOLD[0], GOLD[1], GOLD[2]);
        }

        // Energy drawn from both arrays into the front focus.
        if (charge > 0.0D) {
            int rays = 12;
            for (int i = 0; i < rays; ++i) {
                double angle = age * 0.12D + TWO_PI * i / rays;
                double feetX = ownerX + Math.cos(angle) * feetRadius * 0.86D;
                double feetZ = ownerZ + Math.sin(angle) * feetRadius * 0.86D;
                double flow = (age * 0.065D + i * 0.19D) % 1.0D;
                double t0 = flow * 0.85D;
                double t1 = Math.min(1.0D, t0 + 0.25D);
                line3D(buffer,
                        feetX + (frontX - feetX) * t0,
                        feetY + (frontY - feetY) * t0,
                        feetZ + (frontZ - feetZ) * t0,
                        feetX + (frontX - feetX) * t1,
                        feetY + (frontY - feetY) * t1,
                        feetZ + (frontZ - feetZ) * t1,
                        0.04D,
                        (i % 3 == 0) ? GOLD[0] : WHITE[0],
                        (i % 3 == 0) ? GOLD[1] : WHITE[1],
                        (i % 3 == 0) ? GOLD[2] : WHITE[2]);
            }
        }
    }

    private static void drawArray(BufferBuilder buffer,
                                  double cx, double cy, double cz,
                                  double rx, double ry, double rz,
                                  double ux, double uy, double uz,
                                  double radius, double rotation,
                                  double build, double charge,
                                  float[] color, int segments, boolean main) {
        if (build <= 0.0D || radius <= 0.01D) {
            return;
        }
        float bright = (float) (0.15D + 0.55D * charge);
        float r = mix(color[0], WHITE[0], bright);
        float g = mix(color[1], WHITE[1], bright);
        float b = mix(color[2], WHITE[2], bright);

        double normalX = ry * uz - rz * uy;
        double normalY = rz * ux - rx * uz;
        double normalZ = rx * uy - ry * ux;
        double width = Math.max(0.06D, radius * 0.018D);

        drawArcRing(buffer, cx, cy, cz, rx, ry, rz, ux, uy, uz,
                radius - width, radius, segments, rotation, rotation + TWO_PI * build,
                r, g, b);

        double innerBuild = smoothstep(clamp((build - 0.25D) / 0.75D));
        if (innerBuild > 0.0D) {
            drawArcRing(buffer, cx, cy, cz, rx, ry, rz, ux, uy, uz,
                    radius * 0.66D - width * 0.6D, radius * 0.66D,
                    Math.max(24, segments * 2 / 3),
                    -rotation * 1.22D, -rotation * 1.22D + TWO_PI * innerBuild,
                    mix(color[0], WHITE[0], bright + 0.18F),
                    mix(color[1], WHITE[1], bright + 0.18F),
                    mix(color[2], WHITE[2], bright + 0.18F));
        }

        int spokes = main ? 24 : 12;
        for (int i = 0; i < spokes; ++i) {
            if ((i + 1.0D) / spokes > build) {
                break;
            }
            double angle = rotation + TWO_PI * i / spokes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            barInPlane(buffer, cx, cy, cz, rx, ry, rz, ux, uy, uz,
                    normalX, normalY, normalZ,
                    radius * 0.58D, radius * 0.92D, cos, sin,
                    Math.max(0.05D, width * 0.5D), r, g, b);
        }

        int runes = main ? 12 : 8;
        for (int i = 0; i < runes; ++i) {
            if ((i + 1.0D) / runes > build) {
                break;
            }
            double angle = rotation * 1.08D + TWO_PI * i / runes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double centerRadius = radius * 0.80D;
            double tx = -sin;
            double tz = cos;
            // A pair of short bars reading as a small rune.
            barInPlane(buffer, cx, cy, cz, rx, ry, rz, ux, uy, uz,
                    normalX, normalY, normalZ,
                    centerRadius, centerRadius + radius * 0.10D, cos, sin,
                    Math.max(0.045D, width * 0.32D), GOLD[0], GOLD[1], GOLD[2]);
            barInPlane(buffer, cx, cy, cz, rx, ry, rz, ux, uy, uz,
                    normalX, normalY, normalZ,
                    centerRadius - radius * 0.03D, centerRadius + radius * 0.03D,
                    tx, tz, Math.max(0.035D, width * 0.24D), r, g, b);
        }

        if (build > 0.55D) {
            double starBuild = smoothstep(clamp((build - 0.55D) / 0.45D));
            for (int i = 0; i < 6; ++i) {
                double angle = -rotation * 1.7D + TWO_PI * i / 6.0D;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                barInPlane(buffer, cx, cy, cz, rx, ry, rz, ux, uy, uz,
                        normalX, normalY, normalZ,
                        radius * 0.10D, radius * (0.10D + 0.28D * starBuild),
                        cos, sin, Math.max(0.04D, width * 0.38D),
                        GOLD[0], GOLD[1], GOLD[2]);
            }
        }
    }

    private static void barInPlane(BufferBuilder buffer,
                                   double cx, double cy, double cz,
                                   double rx, double ry, double rz,
                                   double ux, double uy, double uz,
                                   double nx, double ny, double nz,
                                   double r0, double r1,
                                   double dirX, double dirZ,
                                   double width,
                                   float r, float g, float b) {
        double x0 = cx + (rx * dirX + ux * dirZ) * r0;
        double y0 = cy + (ry * dirX + uy * dirZ) * r0;
        double z0 = cz + (rz * dirX + uz * dirZ) * r0;
        double x1 = cx + (rx * dirX + ux * dirZ) * r1;
        double y1 = cy + (ry * dirX + uy * dirZ) * r1;
        double z1 = cz + (rz * dirX + uz * dirZ) * r1;
        double px = ny * (z1 - z0) - nz * (y1 - y0);
        double py = nz * (x1 - x0) - nx * (z1 - z0);
        double pz = nx * (y1 - y0) - ny * (x1 - x0);
        double length = Math.sqrt(px * px + py * py + pz * pz);
        if (length < 0.0001D) {
            return;
        }
        px = px / length * width;
        py = py / length * width;
        pz = pz / length * width;
        quad(buffer,
                x0 - px, y0 - py, z0 - pz,
                x0 + px, y0 + py, z0 + pz,
                x1 + px, y1 + py, z1 + pz,
                x1 - px, y1 - py, z1 - pz,
                r, g, b);
    }

    public static void renderBeam(BufferBuilder buffer,
                                  double ox, double oy, double oz,
                                  double dx, double dy, double dz,
                                  double age) {
        double endScale = beamEndScale(age);
        if (endScale <= 0.001D) {
            return;
        }
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.0001D) {
            dx = 0.0D;
            dy = 0.0D;
            dz = 1.0D;
        } else {
            dx /= length;
            dy /= length;
            dz /= length;
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

        double radius = EntityMagicCannonBeam.BEAM_RADIUS * endScale;
        double beamLength = EntityMagicCannonBeam.BEAM_LENGTH;

        // Main cylinder with bright angular bands.
        int segments = 40;
        for (int i = 0; i < segments; ++i) {
            double a0 = TWO_PI * i / segments;
            double a1 = TWO_PI * (i + 1) / segments;
            float wave = (float) (0.5D + 0.5D * Math.sin(a0 * 4.0D + age * 0.4D));
            float r = mix(BLUE[0], WHITE[0], wave * 0.72F);
            float g = mix(BLUE[1], WHITE[1], wave * 0.72F);
            float b = mix(BLUE[2], WHITE[2], wave * 0.72F);
            beamSide(buffer, ox, oy, oz, dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    beamLength, radius, a0, a1, r, g, b);
        }

        // Long bright filaments.
        int strips = 14;
        for (int i = 0; i < strips; ++i) {
            double angle = age * 0.18D + TWO_PI * i / strips;
            double half = TWO_PI / strips * 0.16D;
            float[] color = (i % 3 == 0) ? GOLD : WHITE;
            beamSide(buffer, ox, oy, oz, dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    beamLength, radius * 1.02D, angle - half, angle + half,
                    color[0], color[1], color[2]);
        }

        // Rings perpendicular to the beam.
        int ringCount = 16;
        for (int i = 0; i < ringCount; ++i) {
            double t = (i + 0.5D) / ringCount * beamLength;
            double cx = ox + dx * t;
            double cy = oy + dy * t;
            double cz = oz + dz * t;
            double pulse = 1.0D + 0.06D * Math.sin(age * 0.35D + i * 1.4D);
            drawArcRing(buffer, cx, cy, cz, rx, ry, rz, ux2, uy2, uz2,
                    radius * 0.92D, radius * (1.05D * pulse), 32,
                    age * 0.35D, age * 0.35D + TWO_PI,
                    mix(CYAN[0], WHITE[0], 0.55F),
                    mix(CYAN[1], WHITE[1], 0.55F),
                    mix(CYAN[2], WHITE[2], 0.55F));
        }

        // The cylinder has no open interiors: fill both ends with opaque
        // discs before drawing the decorative muzzle/end rims.
        drawArcRing(buffer, ox, oy, oz, rx, ry, rz, ux2, uy2, uz2,
                0.0D, radius, 48, 0.0D, TWO_PI,
                mix(CYAN[0], WHITE[0], 0.42F),
                mix(CYAN[1], WHITE[1], 0.42F),
                mix(CYAN[2], WHITE[2], 0.42F));
        double capX = ox + dx * beamLength;
        double capY = oy + dy * beamLength;
        double capZ = oz + dz * beamLength;
        drawArcRing(buffer, capX, capY, capZ, rx, ry, rz, ux2, uy2, uz2,
                0.0D, radius, 48, 0.0D, TWO_PI,
                WHITE[0], WHITE[1], WHITE[2]);

        // Muzzle array at the caster's end of the beam.
        drawArray(buffer, ox + dx * 0.08D, oy + dy * 0.08D, oz + dz * 0.08D,
                rx, ry, rz, ux2, uy2, uz2,
                Math.max(0.5D, 3.4D * endScale), age * 0.24D,
                1.0D, 1.0D, PURPLE, 64, true);

        // Helical energy ribbons along the full length.
        for (int h = 0; h < 2; ++h) {
            int steps = 40;
            double prevX = 0.0D;
            double prevY = 0.0D;
            double prevZ = 0.0D;
            for (int i = 0; i <= steps; ++i) {
                double t = i / (double) steps;
                double angle = age * 0.55D + h * Math.PI + t * TWO_PI * 3.0D;
                double cx = ox + dx * beamLength * t
                        + (rx * Math.cos(angle) + ux2 * Math.sin(angle)) * radius * 1.03D;
                double cy = oy + dy * beamLength * t
                        + (ry * Math.cos(angle) + uy2 * Math.sin(angle)) * radius * 1.03D;
                double cz = oz + dz * beamLength * t
                        + (rz * Math.cos(angle) + uz2 * Math.sin(angle)) * radius * 1.03D;
                if (i > 0) {
                    line3D(buffer, prevX, prevY, prevZ, cx, cy, cz, 0.035D,
                            (h == 0) ? WHITE[0] : GOLD[0],
                            (h == 0) ? WHITE[1] : GOLD[1],
                            (h == 0) ? WHITE[2] : GOLD[2]);
                }
                prevX = cx;
                prevY = cy;
                prevZ = cz;
            }
        }

        // Bright end rim.
        drawArcRing(buffer, ox + dx * beamLength, oy + dy * beamLength,
                oz + dz * beamLength, rx, ry, rz, ux2, uy2, uz2,
                radius * 0.75D, radius * 1.10D, 48,
                0.0D, TWO_PI, WHITE[0], WHITE[1], WHITE[2]);
    }

    private static void beamSide(BufferBuilder buffer,
                                 double ox, double oy, double oz,
                                 double dx, double dy, double dz,
                                 double rx, double ry, double rz,
                                 double ux, double uy, double uz,
                                 double length, double radius,
                                 double a0, double a1,
                                 float r, float g, float b) {
        double cos0 = Math.cos(a0);
        double sin0 = Math.sin(a0);
        double cos1 = Math.cos(a1);
        double sin1 = Math.sin(a1);
        double p0x = ox + (rx * cos0 + ux * sin0) * radius;
        double p0y = oy + (ry * cos0 + uy * sin0) * radius;
        double p0z = oz + (rz * cos0 + uz * sin0) * radius;
        double p1x = ox + (rx * cos1 + ux * sin1) * radius;
        double p1y = oy + (ry * cos1 + uy * sin1) * radius;
        double p1z = oz + (rz * cos1 + uz * sin1) * radius;
        quad(buffer,
                p0x, p0y, p0z,
                p0x + dx * length, p0y + dy * length, p0z + dz * length,
                p1x + dx * length, p1y + dy * length, p1z + dz * length,
                p1x, p1y, p1z,
                r, g, b);
    }

    private static double beamEndScale(double age) {
        double left = EntityMagicCannonBeam.DURATION_TICKS - age;
        if (left >= 10.0D) {
            return 1.0D;
        }
        return smoothstep(clamp(left / 10.0D));
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

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
