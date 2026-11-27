package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityNuclearStrike;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural geometry for Nuclear Strike. Bright elements use opaque vertex
 * colours so the existing bloom pass sees them. No texture is required.
 */
@SideOnly(Side.CLIENT)
public final class NuclearStrikeRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] BLUE = {0.35F, 0.62F, 1.0F};
    private static final float[] VIOLET = {0.55F, 0.45F, 1.0F};
    private static final float[] GOLD = {1.0F, 0.72F, 0.25F};
    private static final float[] ORANGE = {1.0F, 0.42F, 0.10F};
    private static final float[] RED = {0.92F, 0.12F, 0.06F};
    private static final float[] PALE_RED = {0.72F, 0.18F, 0.10F};
    private static final float[] ASH = {0.42F, 0.38F, 0.34F};

    private NuclearStrikeRenderHelper() {
    }

    public static void render(BufferBuilder buffer, double ox, double oy, double oz,
                              double age, boolean airburst, double topY) {
        double targetFade = age < EntityNuclearStrike.IMPACT_TICK ? 1.0D : 0.0D;
        if (targetFade > 0.001D) {
            renderWarningArray(buffer, ox, oy, oz, age, airburst, targetFade);
        }
        if (age < EntityNuclearStrike.IMPACT_TICK) {
            renderDescent(buffer, ox, oy, oz, age, topY);
        }
        if (age >= EntityNuclearStrike.IMPACT_TICK) {
            renderImpact(buffer, ox, oy, oz, age, airburst, topY);
        }
        if (age >= EntityNuclearStrike.IMPACT_TICK) {
            renderMushroomAndFallout(buffer, ox, oy, oz, age, airburst, topY);
        }
    }

    private static void renderWarningArray(BufferBuilder buffer, double ox, double oy, double oz,
                                           double age, boolean airburst, double fade) {
        double build = smoothstep(clamp(age / 80.0D));
        double spin = age * 0.035D;
        double innerRadius = EntityNuclearStrike.CRATER_RADIUS * (0.18D + 0.82D * build);
        double outerLimit = airburst ? EntityNuclearStrike.AIRBURST_RADIUS
                : EntityNuclearStrike.BLAST_RADIUS;
        double outerRadius = outerLimit * (0.18D + 0.82D * build);
        double y = oy + 0.12D;

        drawArcRing(buffer, ox, y, oz,
                Math.max(0.05D, innerRadius - 1.3D), innerRadius + 1.3D,
                192, spin, spin + TWO_PI * 0.995D, RED[0], RED[1], RED[2]);
        drawArcRing(buffer, ox, y + 0.02D, oz,
                Math.max(0.05D, outerRadius - 0.70D), outerRadius + 0.70D,
                256, -spin * 0.55D, -spin * 0.55D + TWO_PI * 0.995D,
                PALE_RED[0], PALE_RED[1], PALE_RED[2]);

        drawArcRing(buffer, ox, y + 0.05D, oz,
                innerRadius * 0.92D, innerRadius * 0.96D,
                160, -Math.PI / 2.0D, -Math.PI / 2.0D + TWO_PI * clamp(age / 80.0D),
                GOLD[0], GOLD[1], GOLD[2]);

        double sigilRadius = Math.max(5.0D, innerRadius * 0.16D);
        for (int i = 0; i < 3; ++i) {
            double start = spin + TWO_PI * i / 3.0D;
            drawArcRing(buffer, ox, y + 0.06D, oz,
                    sigilRadius, sigilRadius + 1.2D, 28,
                    start, start + 1.25D, GOLD[0], GOLD[1], GOLD[2]);
        }
        drawArcRing(buffer, ox, y + 0.08D, oz,
                1.5D, 2.0D, 24, spin, spin + TWO_PI, WHITE[0], WHITE[1], WHITE[2]);

        int runes = 48;
        for (int i = 0; i < runes; ++i) {
            if ((i + 0.5D) / runes > build) {
                break;
            }
            double angle = spin * 1.7D + TWO_PI * i / runes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double cx = ox + cos * innerRadius;
            double cz = oz + sin * innerRadius;
            line3D(buffer, cx, y + 0.07D, cz,
                    cx + cos * 2.0D, y + 0.07D, cz + sin * 2.0D,
                    0.28D, GOLD[0], GOLD[1], GOLD[2]);
        }
    }

    private static void renderDescent(BufferBuilder buffer, double ox, double oy, double oz,
                                      double age, double topY) {
        double beamTop = Math.max(oy, topY);
        double charge = smoothstep(clamp((age - 20.0D) / 60.0D));
        double beamRadius = 0.30D + 0.75D * charge;
        drawVerticalBeam(buffer, ox, oz, oy, beamTop, beamRadius,
                GOLD[0], GOLD[1], GOLD[2]);
        drawVerticalBeam(buffer, ox, oz, oy, beamTop, beamRadius * 0.35D,
                WHITE[0], WHITE[1], WHITE[2]);

        if (age < EntityNuclearStrike.CHARGE_END_TICK) {
            return;
        }
        double descent = smoothstep(clamp((age - EntityNuclearStrike.CHARGE_END_TICK)
                / (EntityNuclearStrike.IMPACT_TICK - EntityNuclearStrike.CHARGE_END_TICK)));
        double warheadY = beamTop + (oy - beamTop) * descent;
        double pulse = 1.0D + 0.10D * Math.sin(age * 0.85D);
        double coreRadius = 4.5D * pulse;

        drawWireSphere(buffer, ox, warheadY, oz, coreRadius, 28, 5,
                WHITE[0], WHITE[1], WHITE[2]);
        drawArcRing(buffer, ox, warheadY, oz,
                coreRadius * 1.2D, coreRadius * 1.55D, 32,
                age * 0.7D, age * 0.7D + TWO_PI, GOLD[0], GOLD[1], GOLD[2]);
        drawArcRing(buffer, ox, warheadY + 0.05D, oz,
                coreRadius * 1.8D, coreRadius * 2.15D, 24,
                -age * 0.9D, -age * 0.9D + TWO_PI, ORANGE[0], ORANGE[1], ORANGE[2]);

        drawVerticalBeam(buffer, ox, oz, warheadY, Math.min(beamTop, warheadY + 30.0D),
                1.2D, ORANGE[0], ORANGE[1], ORANGE[2]);
    }

    private static void renderImpact(BufferBuilder buffer, double ox, double oy, double oz,
                                     double age, boolean airburst, double topY) {
        double t = Math.max(0.0D, age - EntityNuclearStrike.IMPACT_TICK);
        double maxRadius = airburst ? EntityNuclearStrike.AIRBURST_RADIUS
                : EntityNuclearStrike.BLAST_RADIUS;

        // Cherenkov-blue first flash, then the white-gold fireball.
        if (t < 14.0D) {
            double flash = smoothstep(clamp(t / 5.0D));
            double radius = 25.0D + 55.0D * flash;
            drawWireSphere(buffer, ox, oy + 2.0D, oz, radius, 40, 6,
                    mix(VIOLET[0], WHITE[0], (float) t / 14.0F),
                    mix(VIOLET[1], WHITE[1], (float) t / 14.0F),
                    mix(VIOLET[2], WHITE[2], (float) t / 14.0F));
        }
        if (t < 110.0D) {
            double grow = smoothstep(clamp(t / 36.0D));
            double fireRadius = 14.0D + 70.0D * grow;
            double cool = clamp((t - 30.0D) / 80.0D);
            float r = mix(WHITE[0], ORANGE[0], (float) cool);
            float g = mix(WHITE[1], ORANGE[1], (float) cool);
            float b = mix(WHITE[2], ORANGE[2], (float) cool);
            drawWireSphere(buffer, ox, oy + 6.0D + 8.0D * grow, oz,
                    fireRadius, 40, 6, r, g, b);
            drawWireSphere(buffer, ox, oy + 6.0D + 8.0D * grow, oz,
                    fireRadius * 0.72D, 32, 5, WHITE[0], WHITE[1], WHITE[2]);
            drawArcRing(buffer, ox, oy + 0.4D, oz,
                    fireRadius * 0.15D, fireRadius * 0.20D, 80,
                    t * 0.10D, t * 0.10D + TWO_PI,
                    GOLD[0], GOLD[1], GOLD[2]);
        }

        if (t < 32.0D) {
            double shock = smoothstep(clamp(t / 20.0D));
            double radius = maxRadius * shock;
            double width = 2.0D + 3.0D * (1.0D - shock);
            drawArcRing(buffer, ox, oy + 0.15D, oz,
                    Math.max(0.10D, radius - width), radius + width,
                    256, 0.0D, TWO_PI, WHITE[0], WHITE[1], WHITE[2]);
            drawArcRing(buffer, ox, oy + 0.20D, oz,
                    Math.max(0.10D, radius * 0.985D - width * 0.5D), radius * 0.985D,
                    192, 0.0D, TWO_PI, GOLD[0], GOLD[1], GOLD[2]);
        }

        // A low, broad dust ring expands beyond the fireball.
        if (t < 70.0D) {
            double dust = smoothstep(clamp(t / 42.0D));
            double radius = maxRadius * 0.55D * dust;
            drawArcRing(buffer, ox, oy + 0.35D, oz,
                    Math.max(0.1D, radius * 0.90D), radius,
                    192, 0.0D, TWO_PI, ASH[0], ASH[1], ASH[2]);
        }

        // A few latitude rings suggest the three-dimensional shock dome.
        if (t < 30.0D) {
            double dome = smoothstep(clamp(t / 24.0D));
            double domeRadius = maxRadius * 0.72D * dome;
            for (int i = 1; i <= 3; ++i) {
                double vertical = domeRadius * i / 4.0D;
                double horizontal = Math.sqrt(Math.max(0.0D,
                        domeRadius * domeRadius - vertical * vertical));
                if (horizontal > 0.1D) {
                    drawArcRing(buffer, ox, oy + vertical, oz,
                            horizontal * 0.985D, horizontal,
                            160, 0.0D, TWO_PI, PALE_RED[0], PALE_RED[1], PALE_RED[2]);
                }
            }
        }

        // The 88-block crater lip stays readable after the flash.
        if (!airburst) {
            double lip = Math.max(0.0D, 1.0D - t / 220.0D);
            if (lip > 0.01D) {
                drawArcRing(buffer, ox, oy + 0.08D, oz,
                        EntityNuclearStrike.CRATER_RADIUS - 2.0D,
                        EntityNuclearStrike.CRATER_RADIUS + 1.0D,
                        192, 0.0D, TWO_PI,
                        ORANGE[0] * (float) lip, ORANGE[1] * (float) lip,
                        ORANGE[2] * (float) lip);
            }
        }
    }

    private static void renderMushroomAndFallout(BufferBuilder buffer, double ox, double oy, double oz,
                                                  double age, boolean airburst, double topY) {
        double t = Math.max(0.0D, age - EntityNuclearStrike.IMPACT_TICK);

        // The mushroom cloud itself is built from NuclearSmokeParticle puffs in
        // EntityNuclearStrike.spawnParticleFrame. The long-lived rotating
        // radiation ring is intentionally not rendered: only the static cooling
        // crater afterglow remains as procedural geometry.
        // Crater afterglow.
        if (!airburst) {
            double glow = Math.max(0.0D, 1.0D - t / 420.0D);
            if (glow > 0.01D) {
                drawArcRing(buffer, ox, oy + 0.04D, oz,
                        EntityNuclearStrike.CRATER_RADIUS * 0.20D,
                        EntityNuclearStrike.CRATER_RADIUS * 0.24D,
                        96, 0.0D, TWO_PI,
                        ORANGE[0] * (float) glow * 0.55F,
                        ORANGE[1] * (float) glow * 0.55F,
                        ORANGE[2] * (float) glow * 0.55F);
            }
        }
    }

    private static void drawArcRing(BufferBuilder buffer, double cx, double cy, double cz,
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
            double cos0 = Math.cos(a0);
            double sin0 = Math.sin(a0);
            double cos1 = Math.cos(a1);
            double sin1 = Math.sin(a1);
            quad(buffer,
                    cx + cos0 * inner, cy, cz + sin0 * inner,
                    cx + cos0 * outer, cy, cz + sin0 * outer,
                    cx + cos1 * outer, cy, cz + sin1 * outer,
                    cx + cos1 * inner, cy, cz + sin1 * inner,
                    r, g, b);
        }
    }

    private static void drawVerticalBeam(BufferBuilder buffer, double x, double z,
                                         double y0, double y1, double radius,
                                         float r, float g, float b) {
        double bottom = Math.min(y0, y1);
        double top = Math.max(y0, y1);
        if (top - bottom < 0.001D) {
            return;
        }
        quad(buffer,
                x - radius, bottom, z,
                x - radius, top, z,
                x + radius, top, z,
                x + radius, bottom, z,
                r, g, b);
        quad(buffer,
                x, bottom, z - radius,
                x, top, z - radius,
                x, top, z + radius,
                x, bottom, z + radius,
                r, g, b);
    }

    private static void drawWireSphere(BufferBuilder buffer, double cx, double cy, double cz,
                                       double radius, int longitudeSegments, int latitudeSegments,
                                       float r, float g, float b) {
        if (radius <= 0.01D) {
            return;
        }
        for (int lat = 1; lat < latitudeSegments; ++lat) {
            double phi = -Math.PI / 2.0D + Math.PI * lat / latitudeSegments;
            double ringRadius = radius * Math.cos(phi);
            double y = cy + radius * Math.sin(phi);
            if (ringRadius > 0.05D) {
                drawArcRing(buffer, cx, y, cz,
                        Math.max(0.02D, ringRadius - 0.25D), ringRadius,
                        Math.min(longitudeSegments, 48),
                        0.0D, TWO_PI, r, g, b);
            }
        }
        int segments = Math.min(longitudeSegments, 48);
        for (int i = 0; i < segments; ++i) {
            double a0 = TWO_PI * i / segments;
            double a1 = TWO_PI * (i + 1) / segments;
            double x0 = cx + Math.cos(a0) * radius;
            double y0 = cy + Math.sin(a0) * radius;
            double x1 = cx + Math.cos(a1) * radius;
            double y1 = cy + Math.sin(a1) * radius;
            quad(buffer,
                    x0, y0, cz - 0.18D,
                    x1, y1, cz - 0.18D,
                    x1, y1, cz + 0.18D,
                    x0, y0, cz + 0.18D,
                    r, g, b);
            double z0 = cz + Math.cos(a0) * radius;
            double z1 = cz + Math.cos(a1) * radius;
            quad(buffer,
                    cx - 0.18D, y0, z0,
                    cx - 0.18D, y1, z1,
                    cx + 0.18D, y1, z1,
                    cx + 0.18D, y0, z0,
                    r, g, b);
        }
    }

    private static void line3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double width, float r, float g, float b) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 0.00001D) {
            return;
        }
        double px = -dz / length * width;
        double pz = dx / length * width;
        quad(buffer,
                x1 - px, y1, z1 - pz,
                x1 + px, y1, z1 + pz,
                x2 + px, y2, z2 + pz,
                x2 - px, y2, z2 - pz,
                r, g, b);
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
                .lightmap(0xF000, 0xF0).endVertex();
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }

    private static float mix(float a, float b, float t) {
        float x = Math.max(0.0F, Math.min(1.0F, t));
        return a + (b - a) * x;
    }
}
