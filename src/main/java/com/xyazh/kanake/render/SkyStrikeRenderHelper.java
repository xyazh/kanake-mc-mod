package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntitySkyStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fully procedural geometry for Sky Strike. Everything is emitted into the
 * particle buffer with opaque colors, which makes every ring and beam enter the
 * existing framebuffer bloom pass without any texture or alpha tricks.
 */
@SideOnly(Side.CLIENT)
public final class SkyStrikeRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] CYAN = {0.18F, 0.78F, 1.0F};
    private static final float[] BLUE = {0.25F, 0.58F, 1.0F};
    private static final float[] PALE = {0.72F, 0.92F, 1.0F};
    private static final float[] GOLD = {1.0F, 0.80F, 0.30F};
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};

    private SkyStrikeRenderHelper() {
    }

    public static void renderArray(BufferBuilder buffer, double ox, double oy, double oz,
                                   double age, double effectBottomY, double effectTopY) {
        double end = endScale(age);
        if (end <= 0.001D) {
            return;
        }

        double descent = smoothstep(clamp(age / (double) EntitySkyStrike.BEAM_DESCENT_TICKS));
        double charge = smoothstep(clamp((age - EntitySkyStrike.CHARGE_START)
                / (double) EntitySkyStrike.CHARGE_TICKS));
        double attack = smoothstep(clamp((age - EntitySkyStrike.ATTACK_START) / 12.0D));

        // The beam reaches the ceiling of the current dimension.
        double skyY = effectTopY;
        double topOffset = effectTopY - oy;
        double bottomY;
        if (age < EntitySkyStrike.BEAM_DESCENT_TICKS) {
            bottomY = skyY + (oy - skyY) * descent;
        } else if (age < EntitySkyStrike.ATTACK_START) {
            bottomY = oy;
        } else {
            bottomY = oy + (effectBottomY - oy) * attack;
        }

        double beamRadius;
        if (age < EntitySkyStrike.ATTACK_START) {
            beamRadius = (0.28D + 0.62D * charge) * end;
        } else {
            beamRadius = (0.28D + (EntitySkyStrike.BEAM_RADIUS - 0.28D) * attack) * end;
        }

        if (bottomY < skyY - 0.001D) {
            drawBeam(buffer, ox, oz, bottomY, skyY, beamRadius, age, attack, charge, end);
        }

        // A small bright crown travels down with the initial thin beam.
        double tipRadius = Math.max(0.35D, beamRadius * 2.0D) * end;
        drawArcRing(buffer, ox, bottomY + 0.02D, oz,
                tipRadius * 0.70D, tipRadius, 32, 0.0D, TWO_PI,
                WHITE[0], WHITE[1], WHITE[2]);

        // A bright charging core grows between the main array and the beam.
        if (charge > 0.01D && age < EntitySkyStrike.ATTACK_START) {
            double coreRadius = (0.45D + 5.0D * charge) * end;
            double coreY = effectTopY;
            double spin = age * 0.55D;
            drawArcRing(buffer, ox, coreY, oz, coreRadius * 0.70D, coreRadius,
                    48, spin, spin + TWO_PI,
                    mix(CYAN[0], WHITE[0], 0.55F),
                    mix(CYAN[1], WHITE[1], 0.55F),
                    mix(CYAN[2], WHITE[2], 0.55F));
            drawArcRing(buffer, ox, coreY + 0.25D, oz,
                    coreRadius * 1.05D, coreRadius * 1.18D,
                    32, -spin * 1.4D, -spin * 1.4D + TWO_PI,
                    GOLD[0], GOLD[1], GOLD[2]);
        }

        // The largest main array sits exactly at the top of the light pillar.
        drawCircleLayer(buffer, ox, oy, oz, topOffset,
                48.0D, 0.0D, 0.0D, 0.0D,
                0.22D, 24.0D, 76.0D, 96, age, end, charge, true, CYAN, effectTopY);
        // Smaller arrays are stacked below the main array. Fractions keep the
        // layout sensible in dimensions with different ceiling heights.
        drawCircleLayer(buffer, ox, oy, oz, topOffset * 0.80D,
                32.0D, 8.0D, 0.018D, 1.37D,
                -0.16D, 32.0D, 68.0D, 80, age, end, charge, false, BLUE, effectTopY);
        drawCircleLayer(buffer, ox, oy, oz, topOffset * 0.60D,
                22.0D, 14.0D, 0.024D, 2.74D,
                0.26D, 44.0D, 62.0D, 64, age, end, charge, false, PALE, effectTopY);
        drawCircleLayer(buffer, ox, oy, oz, topOffset * 0.40D,
                12.0D, 18.0D, 0.030D, 4.11D,
                -0.34D, 56.0D, 54.0D, 48, age, end, charge, false, GOLD, effectTopY);

        if (age >= EntitySkyStrike.ATTACK_START) {
            drawImpact(buffer, ox, oz, effectBottomY, age, end);
        }
    }

    private static void drawImpact(BufferBuilder buffer, double ox, double oz,
                                   double effectBottomY, double age, double end) {
        double floorY = effectBottomY + 0.10D;
        double edge = EntitySkyStrike.DAMAGE_RADIUS * end;
        double pulse = 0.04D * Math.sin(age * 0.35D);
        // Exactly mark the 16-block-wide destruction/damage edge.
        drawArcRing(buffer, ox, floorY, oz,
                Math.max(0.05D, edge - 0.55D - pulse), edge + 0.05D,
                96, 0.0D, TWO_PI, WHITE[0], WHITE[1], WHITE[2]);
        drawArcRing(buffer, ox, floorY + 0.03D, oz,
                Math.max(0.05D, edge + 0.10D), edge + 0.22D + pulse,
                96, 0.0D, TWO_PI, GOLD[0], GOLD[1], GOLD[2]);
        drawArcRing(buffer, ox, floorY + 0.06D, oz,
                Math.max(0.05D, edge - 0.12D), edge,
                128, 0.0D, TWO_PI, GOLD[0], GOLD[1], GOLD[2]);
    }

    private static void drawCircleLayer(BufferBuilder buffer, double ox, double oy, double oz,
                                        double yOffset, double fullRadius,
                                        double orbitRadius, double orbitSpeed, double phase,
                                        double rotationSpeed, double buildStart, double buildTicks,
                                        int segments, double age, double end, double charge,
                                        boolean main, float[] color, double convergenceY) {
        double build = smoothstep(clamp((age - buildStart) / buildTicks));
        if (build <= 0.0D) {
            return;
        }

        double rotation = age * rotationSpeed + phase;
        double cx = ox + Math.cos(age * orbitSpeed + phase) * orbitRadius;
        double cz = oz + Math.sin(age * orbitSpeed + phase) * orbitRadius;
        // The array rises to its assigned height while its runes are being
        // constructed, then bobs gently after construction.
        double cy = oy + yOffset * (0.15D + 0.85D * build)
                + Math.sin(age * 0.035D + phase) * 0.45D;
        double radius = fullRadius * (0.10D + 0.90D * build) * end;
        if (radius < 0.01D) {
            return;
        }

        float bright = (float) (0.10D + 0.55D * charge + 0.10D * build);
        float r = mix(color[0], WHITE[0], bright);
        float g = mix(color[1], WHITE[1], bright);
        float b = mix(color[2], WHITE[2], bright);
        double arc = TWO_PI * build;
        double width = Math.max(0.08D, fullRadius * 0.018D) * end;

        drawArcRing(buffer, cx, cy, cz, radius - width, radius, segments,
                rotation, rotation + arc, r, g, b);

        double innerBuild = smoothstep(clamp((build - 0.30D) / 0.70D));
        if (innerBuild > 0.0D) {
            double innerRadius = fullRadius * 0.76D * end * (0.10D + 0.90D * innerBuild);
            double innerArc = TWO_PI * innerBuild;
            float innerBright = Math.min(1.0F, bright + 0.20F);
            drawArcRing(buffer, cx, cy, cz, innerRadius - width * 0.65D, innerRadius,
                    Math.max(24, segments * 2 / 3),
                    -rotation * 1.25D + phase, -rotation * 1.25D + phase + innerArc,
                    mix(color[0], WHITE[0], innerBright),
                    mix(color[1], WHITE[1], innerBright),
                    mix(color[2], WHITE[2], innerBright));
        }

        int spokes = main ? 36 : 20;
        for (int i = 0; i < spokes; ++i) {
            if ((i + 1.0D) / spokes > build) {
                break;
            }
            double angle = rotation + TWO_PI * i / spokes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            flatBar(buffer,
                    cx + cos * radius * 0.62D, cy, cz + sin * radius * 0.62D,
                    cx + cos * radius * 0.93D, cy, cz + sin * radius * 0.93D,
                    Math.max(0.07D, width * 0.55D), r, g, b);
        }

        int runes = main ? 24 : 12;
        for (int i = 0; i < runes; ++i) {
            if ((i + 1.0D) / runes > build) {
                break;
            }
            double angle = rotation * 1.10D + TWO_PI * i / runes;
            drawRune(buffer, cx, cy, cz, angle, radius * 0.84D,
                    Math.max(0.16D, radius * 0.055D), color[0], color[1], color[2]);
        }

        if (build > 0.55D) {
            double starBuild = smoothstep(clamp((build - 0.55D) / 0.45D));
            for (int i = 0; i < 6; ++i) {
                double angle = -rotation * 1.7D + phase + TWO_PI * i / 6.0D;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                flatBar(buffer,
                        cx + cos * radius * 0.10D, cy, cz + sin * radius * 0.10D,
                        cx + cos * radius * (0.10D + 0.30D * starBuild), cy,
                        cz + sin * radius * (0.10D + 0.30D * starBuild),
                        Math.max(0.06D, width * 0.42D), GOLD[0], GOLD[1], GOLD[2]);
            }
        }

        if (charge > 0.0D && age >= EntitySkyStrike.CHARGE_START) {
            int rays = main ? 12 : 6;
            for (int i = 0; i < rays; ++i) {
                double angle = rotation * 1.6D + TWO_PI * i / rays;
                double edgeX = cx + Math.cos(angle) * radius * 0.94D;
                double edgeZ = cz + Math.sin(angle) * radius * 0.94D;
                double flow = (age * 0.055D + i * 0.19D) % 1.0D;
                double t0 = flow * 0.88D;
                double t1 = Math.min(1.0D, t0 + 0.26D);
                double x0 = edgeX + (ox - edgeX) * t0;
                double y0 = cy + (convergenceY - cy) * t0;
                double z0 = edgeZ + (oz - edgeZ) * t0;
                double x1 = edgeX + (ox - edgeX) * t1;
                double y1 = cy + (convergenceY - cy) * t1;
                double z1 = edgeZ + (oz - edgeZ) * t1;
                line3D(buffer, x0, y0, z0, x1, y1, z1, 0.09D,
                        (i % 3 == 0) ? GOLD[0] : WHITE[0],
                        (i % 3 == 0) ? GOLD[1] : WHITE[1],
                        (i % 3 == 0) ? GOLD[2] : WHITE[2]);
            }
        }
    }

    private static void drawRune(BufferBuilder buffer, double cx, double cy, double cz,
                                 double angle, double radius, double size,
                                 float r, float g, float b) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double px = cx + cos * radius;
        double pz = cz + sin * radius;
        double tx = -sin;
        double tz = cos;
        flatBar(buffer,
                px + cos * size * 0.7D, cy, pz + sin * size * 0.7D,
                px + cos * size * 1.35D, cy, pz + sin * size * 1.35D,
                size * 0.22D, r, g, b);
        flatBar(buffer,
                px - tx * size * 0.65D, cy, pz - tz * size * 0.65D,
                px + tx * size * 0.65D, cy, pz + tz * size * 0.65D,
                size * 0.18D, r, g, b);
        flatBar(buffer,
                px - tx * size * 0.35D + cos * size * 0.25D, cy,
                pz - tz * size * 0.35D + sin * size * 0.25D,
                px + tx * size * 0.35D + cos * size * 0.25D, cy,
                pz + tz * size * 0.35D + sin * size * 0.25D,
                size * 0.13D, GOLD[0], GOLD[1], GOLD[2]);
    }

    private static void drawBeam(BufferBuilder buffer, double cx, double cz,
                                 double bottom, double top, double radius,
                                 double age, double attack, double charge, double end) {
        int segments = 48;
        for (int i = 0; i < segments; ++i) {
            double a0 = TWO_PI * i / segments;
            double a1 = TWO_PI * (i + 1) / segments;
            float wave = (float) (0.5D + 0.5D * Math.sin(a0 * 3.0D + age * 0.35D));
            float r = mix(CYAN[0], WHITE[0], wave * 0.55F);
            float g = mix(CYAN[1], WHITE[1], wave * 0.55F);
            float b = mix(CYAN[2], WHITE[2], wave * 0.55F);
            drawCylinderSide(buffer, cx, cz, bottom, top, radius, a0, a1, r, g, b);
        }

        // Bright vertical filaments make the cylinder read as a fast energy
        // flow instead of a flat solid tube.
        int strips = 16;
        for (int i = 0; i < strips; ++i) {
            double angle = age * 0.32D + TWO_PI * i / strips;
            double halfWidth = TWO_PI / strips * 0.22D;
            float[] color = (i % 3 == 0) ? GOLD : WHITE;
            drawCylinderSide(buffer, cx, cz, bottom, top, radius * 1.015D,
                    angle - halfWidth, angle + halfWidth, color[0], color[1], color[2]);
        }

        // Helical ribbons rotating around the cannon.
        int helices = 3;
        int steps = 30;
        for (int h = 0; h < helices; ++h) {
            double prevX = 0.0D;
            double prevY = 0.0D;
            double prevZ = 0.0D;
            for (int i = 0; i <= steps; ++i) {
                double t = i / (double) steps;
                double y = bottom + (top - bottom) * t;
                double angle = age * 0.75D + h * TWO_PI / helices + t * TWO_PI * 4.0D;
                double x = cx + Math.cos(angle) * radius * 1.035D;
                double z = cz + Math.sin(angle) * radius * 1.035D;
                if (i > 0) {
                    line3D(buffer, prevX, prevY, prevZ, x, y, z, 0.075D,
                            (h == 1) ? GOLD[0] : WHITE[0],
                            (h == 1) ? GOLD[1] : WHITE[1],
                            (h == 1) ? GOLD[2] : WHITE[2]);
                }
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }

        // Pulsing rings travelling up the beam.
        int ringCount = 8;
        for (int i = 0; i < ringCount; ++i) {
            double y = bottom + (top - bottom) * (i + 0.5D) / ringCount;
            double pulse = 0.94D + 0.08D * Math.sin(age * 0.33D + i * 1.7D);
            float mixValue = (float) (0.25D + 0.55D * charge + 0.20D * attack);
            drawArcRing(buffer, cx, y, cz,
                    radius * (pulse - 0.055D), radius * (pulse + 0.055D),
                    40, age * 0.45D, age * 0.45D + TWO_PI,
                    mix(CYAN[0], WHITE[0], mixValue),
                    mix(CYAN[1], WHITE[1], mixValue),
                    mix(CYAN[2], WHITE[2], mixValue));
        }

        // Bright rim at the lower end, marking the 16-block-wide impact point.
        drawArcRing(buffer, cx, bottom + 0.04D, cz,
                radius * 0.78D, radius * 1.12D, 64, 0.0D, TWO_PI,
                WHITE[0], WHITE[1], WHITE[2]);
    }

    private static void drawCylinderSide(BufferBuilder buffer, double cx, double cz,
                                         double bottom, double top, double radius,
                                         double a0, double a1,
                                         float r, float g, float b) {
        double x0 = cx + Math.cos(a0) * radius;
        double z0 = cz + Math.sin(a0) * radius;
        double x1 = cx + Math.cos(a1) * radius;
        double z1 = cz + Math.sin(a1) * radius;
        quad(buffer,
                x0, bottom, z0,
                x0, top, z0,
                x1, top, z1,
                x1, bottom, z1,
                r, g, b);
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

    private static void flatBar(BufferBuilder buffer,
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

    private static double endScale(double age) {
        double left = EntitySkyStrike.TOTAL_TICKS - age;
        if (left >= 20.0D) {
            return 1.0D;
        }
        return smoothstep(clamp(left / 20.0D));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
