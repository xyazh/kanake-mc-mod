package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityWideLightningStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Procedural array, central thunderbolt and chain links for the Wide-Area
 * Lightning super spell. Everything is emitted as fullbright vertex-colored
 * geometry, so the existing bloom pass picks it up without any texture.
 */
@SideOnly(Side.CLIENT)
public final class WideLightningRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] LIGHTNING = {0.1019608F, 0.9843137F, 0.9843137F};
    private static final float[] PALE = {0.55F, 0.99F, 0.99F};
    private static final float[] CYAN = LIGHTNING;
    private static final float[] BLUE = LIGHTNING;
    private static final float[] VIOLET = LIGHTNING;
    private static final float[] DARK = {0.025F, 0.035F, 0.10F};
    private static final double LAYER = 0.07D;
    private static float fadeScale = 1.0F;
    private static float globalFade = 1.0F;

    private WideLightningRenderHelper() {
    }

    public static void render(BufferBuilder buffer, double ox, double oy, double oz,
                              double arrayY, double age, int seed) {
        if (age > EntityWideLightningStrike.MAX_LIFE_TICKS + 20) {
            return;
        }
        double build = smoothstep(clamp(age / EntityWideLightningStrike.ARRAY_BUILD_TICKS));
        double arrayFade = age <= EntityWideLightningStrike.IMPACT_TICK + 100.0D
                ? 1.0D : 1.0D - smoothstep(clamp((age
                - (EntityWideLightningStrike.IMPACT_TICK + 100.0D)) / 160.0D));
        double boltFade = age <= EntityWideLightningStrike.IMPACT_TICK ? 1.0D
                : 1.0D - smoothstep(clamp((age - EntityWideLightningStrike.IMPACT_TICK)
                / 80.0D));
        double charge = smoothstep(clamp((age - EntityWideLightningStrike.ARRAY_BUILD_TICKS)
                / 120.0D));
        globalFade = 1.0F;
        try {
            if (build > 0.001D && arrayFade > 0.001D) {
                renderArray(buffer, ox, oy + arrayY, oz, age, build, charge,
                        arrayFade, seed);
            }
        } finally {
            fadeScale = 1.0F;
            globalFade = 1.0F;
        }
    }
    private static void renderArray(BufferBuilder buffer, double cx, double cy,
                                    double cz, double age, double build,
                                    double charge, double fade, int seed) {
        double r = EntityWideLightningStrike.ARRAY_RADIUS
                * (0.08D + 0.92D * build) * (0.86D + 0.14D * fade);
        if (r < 0.2D) {
            return;
        }
        double rotation = age * 0.0040D;
        double outerBuild = smoothstep(clamp((build - 0.05D) / 0.72D));
        double innerBuild = smoothstep(clamp((build - 0.26D) / 0.70D));
        float bright = (float) (0.58D + 0.42D * charge);
        fadeScale = (float) fade;

        try {
            // Double hexagram plus a central charge core. The old rings,
            // ticks, gems and orbiting glints are intentionally gone; the new
            // array reads as a lightning circuit instead of a decorative dial.
            drawHexagram(buffer, cx, cy, cz, r * 0.94D, rotation,
                    outerBuild, charge, false);
            drawHexagram(buffer, cx, cy + LAYER * 2.0D, cz, r * 0.58D,
                    -rotation * 1.35D + Math.PI / 6.0D,
                    innerBuild, charge, true);

            drawEmitter(buffer, cx, cy + LAYER * 3.0D, cz, r, age, charge);
            if (charge > 0.01D) {
                drawRadialBolts(buffer, cx, cy + LAYER * 4.0D, cz, r,
                        age, charge, seed);
            }
        } finally {
            fadeScale = 1.0F;
        }
    }

    private static void drawHexagram(BufferBuilder buffer,
                                     double cx, double cy, double cz,
                                     double radius, double rotation,
                                     double reveal, double charge,
                                     boolean inner) {
        if (radius < 0.1D || reveal <= 0.001D) {
            return;
        }
        double[] x = new double[6];
        double[] z = new double[6];
        for (int i = 0; i < 6; ++i) {
            double angle = rotation + TWO_PI * i / 6.0D;
            x[i] = cx + Math.cos(angle) * radius;
            z[i] = cz + Math.sin(angle) * radius;
        }
        double width = Math.max(0.07D, radius * (inner ? 0.0020D : 0.0028D));
        int[][] triangles = {{0, 2, 4}, {1, 3, 5}};
        for (int tri = 0; tri < 2; ++tri) {
            for (int edge = 0; edge < 3; ++edge) {
                int a = triangles[tri][edge];
                int b = triangles[tri][(edge + 1) % 3];
                double delay = (tri * 3 + edge) * 0.055D;
                double progress = smoothstep(clamp((reveal - delay)
                        / Math.max(0.35D, 1.0D - delay)));
                if (progress <= 0.001D) {
                    continue;
                }
                double ex = x[a] + (x[b] - x[a]) * progress;
                double ez = z[a] + (z[b] - z[a]) * progress;
                boolean forward = ((tri + edge) & 1) == 0;
                float[] from = forward ? WHITE : LIGHTNING;
                float[] to = forward ? LIGHTNING : WHITE;
                line3DGradient(buffer, x[a], cy, z[a], ex, cy, ez,
                        width * (1.0D + 0.35D * charge),
                        from, to);
                if (inner) {
                    line3DGradient(buffer, x[a], cy + LAYER * 0.35D, z[a],
                            ex, cy + LAYER * 0.35D, ez,
                            width * 0.55D, LIGHTNING, WHITE);
                }
            }
        }
    }

    private static void drawOrnaments(BufferBuilder buffer,
                                      double cx, double cy, double cz,
                                      double r, double rotation,
                                      double build, double charge, float bright) {
        double reveal = smoothstep(clamp((build - 0.34D) / 0.66D));
        if (reveal <= 0.001D) {
            return;
        }

        // Outer tick marks along the retained circular ring.
        int ticks = 24;
        double tickWidth = Math.max(0.055D, r * 0.0016D);
        for (int i = 0; i < ticks; ++i) {
            if ((i + 1.0D) / ticks > reveal) {
                break;
            }
            double angle = rotation * 1.12D + TWO_PI * i / ticks;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double inner = r * 0.992D;
            double outer = r * (1.010D + 0.006D * charge);
            line3D(buffer, cx + cos * inner, cy, cz + sin * inner,
                    cx + cos * outer, cy, cz + sin * outer,
                    tickWidth, PALE[0] * bright, PALE[1] * bright,
                    PALE[2] * bright);
        }

        // Small diamond gems at the six outer hexagram vertices.
        double gemReveal = smoothstep(clamp((reveal - 0.34D) / 0.66D));
        double gemRadius = r * 0.94D;
        double gemSize = r * (0.016D + 0.005D * charge);
        for (int i = 0; i < 6; ++i) {
            if ((i + 1.0D) / 6.0D > gemReveal) {
                break;
            }
            double angle = rotation + TWO_PI * i / 6.0D;
            double x = cx + Math.cos(angle) * gemRadius;
            double z = cz + Math.sin(angle) * gemRadius;
            line3D(buffer, x - gemSize, cy, z, x, cy, z - gemSize,
                    tickWidth, WHITE[0], WHITE[1], WHITE[2]);
            line3D(buffer, x, cy, z - gemSize, x + gemSize, cy, z,
                    tickWidth, PALE[0], PALE[1], PALE[2]);
            line3D(buffer, x + gemSize, cy, z, x, cy, z + gemSize,
                    tickWidth, PALE[0], PALE[1], PALE[2]);
            line3D(buffer, x, cy, z + gemSize, x - gemSize, cy, z,
                    tickWidth, CYAN[0] * bright, CYAN[1] * bright,
                    CYAN[2] * bright);
        }

        // Six small orbiting glints between the two hexagrams.
        if (reveal > 0.55D) {
            int glints = 6;
            for (int i = 0; i < glints; ++i) {
                double angle = -rotation * 1.35D + TWO_PI * i / glints;
                double gx = cx + Math.cos(angle) * r * 0.865D;
                double gz = cz + Math.sin(angle) * r * 0.865D;
                double glow = r * (0.009D + 0.004D * charge);
                drawArc(buffer, gx, cy, gz, glow * 0.65D, glow, 10,
                        angle + rotation, angle + rotation + TWO_PI,
                        i % 2 == 0 ? WHITE : CYAN, bright * 0.80F);
            }
        }
    }

    private static void drawEmitter(BufferBuilder buffer, double cx, double cy,
                                    double cz, double r, double age,
                                    double charge) {
        double pulse = r * (0.030D + 0.010D * charge
                + 0.005D * Math.sin(age * 0.35D));
        float bright = (float) (0.75D + 0.25D * charge);
        // A small filled circle replaces the old cross/plus emitter.
        drawArc(buffer, cx, cy, cz, 0.0D, pulse, 32,
                age * 0.18D, age * 0.18D + TWO_PI, WHITE, bright);
        drawArc(buffer, cx, cy + LAYER * 0.15D, cz,
                pulse * 1.10D, pulse * 1.32D, 32,
                -age * 0.25D, -age * 0.25D + TWO_PI, LIGHTNING,
                bright * 0.72F);
    }

    private static void drawRadialBolts(BufferBuilder buffer,
                                        double cx, double cy, double cz,
                                        double r, double age, double charge,
                                        int seed) {
        int arms = 6;
        double spin = age * 0.0040D;
        // Quantize the seed so the forks re-jag in distinct phases instead of
        // shimmering on every render frame.
        double shapeAge = Math.floor(age / 10.0D) * 10.0D;
        double reach = r * (0.48D + 0.46D * charge);
        double width = Math.max(0.05D, r * 0.0018D);
        float intensity = (float) (0.42D + 0.58D * charge);
        for (int i = 0; i < arms; ++i) {
            double angle = spin + TWO_PI * i / arms;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double sx = cx + cos * Math.max(0.55D, r * 0.04D);
            double sz = cz + sin * Math.max(0.55D, r * 0.04D);
            double ex = cx + cos * reach;
            double ez = cz + sin * reach;
            drawBoltPath(buffer, sx, cy, sz, ex, cy, ez,
                    shapeAge + i * 3.0D, seed + i * 313,
                    width, LIGHTNING, intensity);
            // Small forked tips at the hexagram vertices, like a crown of
            // electric arcs feeding the outer star.
            if (charge > 0.24D) {
                double forkAngle = angle + (i % 2 == 0 ? 0.28D : -0.28D);
                double forkLength = r * (0.045D + 0.020D * charge);
                drawBoltPath(buffer, ex, cy, ez,
                        ex + Math.cos(forkAngle) * forkLength,
                        cy + r * 0.014D,
                        ez + Math.sin(forkAngle) * forkLength,
                        shapeAge + i * 5.0D + 2.0D, seed + i * 97 + 17,
                        width * 0.48D, PALE, intensity * 0.62F);
            }
        }
    }

    private static void drawSpikes(BufferBuilder buffer, double cx, double cy,
                                   double cz, double r, double age, double build,
                                   double charge, float bright) {
        if (build < 0.34D) {
            return;
        }
        int spikes = 16;
        double reveal = smoothstep(clamp((build - 0.34D) / 0.66D));
        for (int i = 0; i < spikes; ++i) {
            if ((i + 1.0D) / spikes > reveal) {
                break;
            }
            double angle = age * 0.0032D + TWO_PI * i / spikes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double inner = r * 0.925D;
            double outer = r * (1.015D + 0.060D * charge
                    + 0.025D * Math.sin(age * 0.18D + i * 1.7D));
            double mid = (inner + outer) * 0.5D;
            double bend = r * 0.030D * Math.sin(age * 0.21D + i * 2.3D);
            double tx = -sin * bend;
            double tz = cos * bend;
            line3D(buffer, cx + cos * inner, cy, cz + sin * inner,
                    cx + cos * mid + tx, cy + 0.02D, cz + sin * mid + tz,
                    Math.max(0.10D, r * 0.0055D), CYAN[0], CYAN[1], CYAN[2]);
            line3D(buffer, cx + cos * mid + tx, cy + 0.02D, cz + sin * mid + tz,
                    cx + cos * outer, cy, cz + sin * outer,
                    Math.max(0.06D, r * 0.0032D), PALE[0], PALE[1], PALE[2]);
        }
    }

    private static void drawRunes(BufferBuilder buffer, double cx, double cy,
                                  double cz, double r, double age, double build,
                                  double charge, float bright) {
        if (build < 0.32D) {
            return;
        }
        int runes = 48;
        double reveal = smoothstep(clamp((build - 0.32D) / 0.58D));
        double spin = age * 0.0060D;
        double width = Math.max(0.055D, r * 0.0028D);
        for (int i = 0; i < runes; ++i) {
            if ((i + 1.0D) / runes > reveal) {
                break;
            }
            double angle = spin + TWO_PI * i / runes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double d = r * 0.862D;
            double x = cx + cos * d;
            double z = cz + sin * d;
            double tx = -sin * width * 2.2D;
            double tz = cos * width * 2.2D;
            line3D(buffer, x - tx, cy, z - tz, x + tx, cy, z + tz,
                    width, PALE[0], PALE[1], PALE[2]);
            line3D(buffer, x + cos * width, cy, z + sin * width,
                    x + cos * width * 1.9D - tx * 0.7D, cy,
                    z + sin * width * 1.9D - tz * 0.7D,
                    width * 0.70D, CYAN[0], CYAN[1], CYAN[2]);
            if ((i & 3) == 0) {
                line3D(buffer, x - cos * width * 1.7D, cy, z - sin * width * 1.7D,
                        x + cos * width * 2.7D, cy, z + sin * width * 2.7D,
                        width * 0.55D, BLUE[0], BLUE[1], BLUE[2]);
            }
        }

        int major = 8;
        double majorReveal = smoothstep(clamp((build - 0.46D) / 0.54D));
        double majorRadius = r * 0.965D;
        for (int i = 0; i < major; ++i) {
            if ((i + 1.0D) / major > majorReveal) {
                break;
            }
            double angle = -spin * 1.35D + TWO_PI * i / major;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = cx + cos * majorRadius;
            double z = cz + sin * majorRadius;
            double tx = -sin * width * 3.4D;
            double tz = cos * width * 3.4D;
            line3D(buffer, x - tx, cy, z - tz, x, cy, z,
                    width * 1.15D, PALE[0], PALE[1], PALE[2]);
            line3D(buffer, x, cy, z, x + tx, cy, z + tz,
                    width * 0.80D, CYAN[0], CYAN[1], CYAN[2]);
            line3D(buffer, x - cos * width * 2.0D, cy + 0.01D,
                    z - sin * width * 2.0D,
                    x + cos * width * 3.0D, cy + 0.01D,
                    z + sin * width * 3.0D,
                    width * 0.55D, BLUE[0], BLUE[1], BLUE[2]);
        }
    }

    private static void drawCenterSigil(BufferBuilder buffer, double cx, double cy,
                                        double cz, double r, double age,
                                        double build, double charge, float bright) {
        if (build < 0.54D) {
            return;
        }
        double reveal = smoothstep(clamp((build - 0.54D) / 0.46D));
        double radius = r * 0.185D * (0.35D + 0.65D * reveal);
        double spin = age * 0.010D;
        double width = Math.max(0.09D, r * 0.0060D);
        drawArc(buffer, cx, cy, cz, radius * 0.96D, radius * 1.04D,
                Math.max(24, (int) (64 * reveal)), -spin,
                -spin + TWO_PI * reveal, PALE, bright);
        for (int i = 0; i < 6; ++i) {
            if ((i + 1.0D) / 6.0D > reveal) {
                break;
            }
            double a0 = spin + TWO_PI * i / 6.0D;
            double a1 = spin + TWO_PI * (i + 1) / 6.0D;
            double midA = (a0 + a1) * 0.5D;
            double inner = radius * (0.38D + 0.10D * Math.sin(age * 0.25D + i));
            double outer = radius * (0.96D + 0.18D * charge);
            line3D(buffer,
                    cx + Math.cos(a0) * inner, cy + 0.02D,
                    cz + Math.sin(a0) * inner,
                    cx + Math.cos(midA) * outer, cy + 0.05D,
                    cz + Math.sin(midA) * outer,
                    width, CYAN[0], CYAN[1], CYAN[2]);
            line3D(buffer,
                    cx + Math.cos(midA) * outer, cy + 0.05D,
                    cz + Math.sin(midA) * outer,
                    cx + Math.cos(a1) * inner, cy + 0.02D,
                    cz + Math.sin(a1) * inner,
                    width * 0.66D, PALE[0], PALE[1], PALE[2]);
        }
        // Central downward bolt symbol, brighter as the array charges.
        double h = radius * (0.45D + 0.55D * charge);
        line3D(buffer, cx, cy + 0.08D, cz, cx - radius * 0.18D, cy + h * 0.40D,
                cz, width * 1.6D, WHITE[0], WHITE[1], WHITE[2]);
        line3D(buffer, cx - radius * 0.18D, cy + h * 0.40D, cz,
                cx + radius * 0.20D, cy + h * 0.68D, cz,
                width * 1.3D, PALE[0], PALE[1], PALE[2]);
        line3D(buffer, cx + radius * 0.20D, cy + h * 0.68D, cz,
                cx - radius * 0.08D, cy + h, cz,
                width, VIOLET[0], VIOLET[1], VIOLET[2]);
    }

    private static void drawChargeArcs(BufferBuilder buffer, double cx, double cy,
                                       double cz, double r, double age,
                                       double charge, int seed) {
        double reveal = smoothstep(clamp((charge - 0.02D) / 0.98D));
        int arcs = 12;
        for (int i = 0; i < arcs; ++i) {
            if ((i + 1.0D) / arcs > reveal) {
                break;
            }
            double angle = age * 0.0125D + TWO_PI * i / arcs;
            double inner = r * 0.16D;
            double outer = r * (0.38D + 0.42D * ((i * 7) % 11) / 10.0D);
            double x0 = cx + Math.cos(angle) * inner;
            double y0 = cy + LAYER * 12.0D;
            double z0 = cz + Math.sin(angle) * inner;
            double x1 = cx + Math.cos(angle + 0.45D) * outer;
            double y1 = cy + LAYER * (13.0D + (i & 3) * 0.8D);
            double z1 = cz + Math.sin(angle + 0.45D) * outer;
            drawBoltPath(buffer, x0, y0, z0, x1, y1, z1,
                    age + i, seed + i * 37, Math.max(0.06D, r * 0.0026D),
                    CYAN, 0.65F + 0.35F * (float) charge);
            drawBoltPath(buffer, x1, y1, z1, x0, y0, z0,
                    age + i + 3, seed + i * 29, Math.max(0.035D, r * 0.0013D),
                    PALE, 0.50F);
        }
    }

    private static void renderChargeArc(BufferBuilder buffer, double ox, double oy,
                                        double oz, double arrayY, double age,
                                        double charge) {
        if (arrayY <= 1.0D) {
            return;
        }
        double sx = ox;
        double sy = oy + arrayY;
        double sz = oz;
        double endY = oy + arrayY * 0.24D;
        drawBoltPath(buffer, sx, sy, sz, ox, endY, oz,
                age, (int) (age * 17.0D) + 97,
                Math.max(0.055D, arrayY * 0.0016D), PALE,
                (float) (0.28D + 0.55D * charge));
        double ringY = oy + arrayY * (0.16D + 0.06D * Math.sin(age * 0.15D));
        drawArc(buffer, ox, ringY, oz, 0.9D, 1.4D, 40,
                age * 0.12D, age * 0.12D + TWO_PI,
                CYAN, (float) (0.48D + 0.45D * charge));
        drawArc(buffer, ox, ringY + 0.03D, oz, 2.6D, 3.2D, 48,
                -age * 0.09D, -age * 0.09D + TWO_PI,
                BLUE, 0.75F);
    }

    private static void renderBolt(BufferBuilder buffer,
                                   double sx, double sy, double sz,
                                   double tx, double ty, double tz,
                                   double age, int seed, double widthScale,
                                   float intensity, boolean main) {
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.05D || intensity <= 0.01F) {
            return;
        }

        int filaments = main ? 6 : 5;
        int segments = main ? 14 : 10;
        double filamentWidth = (main ? 0.23D : 0.14D) * widthScale;
        if (filamentWidth < 0.015D) {
            filamentWidth = 0.015D;
        }
        for (int f = 0; f < filaments; ++f) {
            double[] xs = new double[segments + 1];
            double[] ys = new double[segments + 1];
            double[] zs = new double[segments + 1];
            buildJaggedPath(xs, ys, zs, sx, sy, sz, tx, ty, tz,
                    age + f * 2.37D, seed + f * 104729,
                    distance, main);
            float colorScale = intensity * (f == 0 ? 1.0F : 0.82F);
            float[] color = (f % 3 == 0) ? WHITE : (f % 3 == 1 ? PALE : CYAN);
            for (int i = 0; i < segments; ++i) {
                beam3D(buffer,
                        xs[i], ys[i], zs[i], xs[i + 1], ys[i + 1], zs[i + 1],
                        filamentWidth, color[0] * colorScale,
                        color[1] * colorScale, color[2] * colorScale);
            }
            if (f < 3) {
                Random branchRand = new Random(seed * 0x9E3779B97F4A7C15L
                        + f * 0x27D4EB2F165667C5L + (long) (age * 19.0D));
                int at = 2 + branchRand.nextInt(Math.max(1, segments - 4));
                double bx = xs[at];
                double by = ys[at];
                double bz = zs[at];
                double length = distance * (main ? 0.17D : 0.22D)
                        * (0.55D + branchRand.nextDouble() * 0.75D);
                double a = branchRand.nextDouble() * TWO_PI;
                double rise = (branchRand.nextDouble() - 0.35D) * length * 0.55D;
                double ex = bx + Math.cos(a) * length;
                double ey = by + rise;
                double ez = bz + Math.sin(a) * length;
                drawBoltPath(buffer, bx, by, bz, ex, ey, ez,
                        age + f * 11.0D, seed + f * 53 + at,
                        filamentWidth * (main ? 0.46D : 0.56D),
                        f == 0 ? PALE : CYAN, intensity * 0.75F);
            }
        }
    }

    private static void buildJaggedPath(double[] xs, double[] ys, double[] zs,
                                        double sx, double sy, double sz,
                                        double tx, double ty, double tz,
                                        double age, int seed, double distance,
                                        boolean main) {
        int segments = xs.length - 1;
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double ux;
        double uz;
        if (horizontal > 0.0001D) {
            ux = -dz / horizontal;
            uz = dx / horizontal;
        } else {
            ux = 1.0D;
            uz = 0.0D;
        }
        double vx = dy * uz;
        double vy = dz * ux - dx * uz;
        double vz = -dy * ux;
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
        Random random = new Random(seed * 0x632BE59BD9B4E019L
                + (long) (age * 31.0D));
        double amp = Math.min(main ? 3.6D : 0.55D, distance * (main ? 0.045D : 0.085D));
        double curveU = (random.nextDouble() * 2.0D - 1.0D)
                * Math.min(main ? 2.2D : 2.6D, distance * (main ? 0.030D : 0.075D));
        double curveV = (random.nextDouble() * 2.0D - 1.0D)
                * Math.min(main ? 1.4D : 1.7D, distance * (main ? 0.020D : 0.050D));
        for (int i = 0; i <= segments; ++i) {
            double t = i / (double) segments;
            double envelope = Math.sin(t * Math.PI);
            double offsetU = (random.nextDouble() * 2.0D - 1.0D)
                    * amp * envelope + curveU * envelope;
            double offsetV = (random.nextDouble() * 2.0D - 1.0D)
                    * amp * envelope * (main ? 0.72D : 0.60D)
                    + curveV * envelope;
            if (i == 0 || i == segments) {
                offsetU = 0.0D;
                offsetV = 0.0D;
            }
            xs[i] = sx + dx * t + ux * offsetU + vx * offsetV;
            ys[i] = sy + dy * t + vy * offsetV;
            zs[i] = sz + dz * t + uz * offsetU + vz * offsetV;
        }
    }

    private static void renderMovingPillars(BufferBuilder buffer,
                                            double ox, double oy, double oz,
                                            double arrayY, double age, int seed,
                                            double fade) {
        double since = age - EntityWideLightningStrike.IMPACT_TICK;
        if (since < 0.0D) {
            return;
        }
        double chainEnd = EntityWideLightningStrike.MAX_CHAIN_DEPTH
                * EntityWideLightningStrike.CHAIN_DELAY_TICKS;
        double life = 1.0D - smoothstep(clamp((since - chainEnd) / 90.0D));
        if (life <= 0.01D) {
            return;
        }
        double skyY = oy + Math.max(2.0D, arrayY);
        int count = 12;
        double maxRadius = 80.0D;
        for (int i = 0; i < count; ++i) {
            double angle = i * TWO_PI / count
                    + Math.sin(since * 0.055D + i * 1.7D) * 0.13D;
            // Lightning is effectively instantaneous: these pillars race
            // outward and wrap back to the centre after reaching the edge.
            double radius = 2.0D + (since * 2.0D
                    + i * maxRadius / count) % maxRadius;
            double x = ox + Math.cos(angle) * radius;
            double z = oz + Math.sin(angle) * radius;
            float intensity = (float) (life * fade
                    * (0.86D + 0.14D * Math.sin(since * 0.8D + i * 2.1D)));
            if (intensity <= 0.01F) {
                continue;
            }
            drawPillar(buffer, x, skyY, z, x, oy + 0.05D, z,
                    age + i * 3.1D, seed + i * 701, intensity);
        }
    }

    private static void renderSkyPillars(BufferBuilder buffer,
                                         double ox, double oy, double oz,
                                         double arrayY, double age, int seed,
                                         double fade) {
        double since = age - EntityWideLightningStrike.IMPACT_TICK;
        if (since < 0.0D) {
            return;
        }
        double chainEnd = EntityWideLightningStrike.MAX_CHAIN_DEPTH
                * EntityWideLightningStrike.CHAIN_DELAY_TICKS;
        double life = 1.0D - smoothstep(clamp((since - chainEnd) / 90.0D));
        if (life <= 0.01D) {
            return;
        }
        double skyY = oy + Math.max(2.0D, arrayY);
        int count = 8;
        long bucket = (long) (age * 0.75D);
        for (int i = 0; i < count; ++i) {
            Random random = new Random(seed * 0x9E3779B97F4A7C15L
                    + bucket * 0xD1B54A32D192ED03L + i * 0x94D049BB133111EBL);
            double angle = random.nextDouble() * TWO_PI;
            double radius = 4.0D + random.nextDouble() * 30.0D;
            double x = ox + Math.cos(angle) * radius;
            double z = oz + Math.sin(angle) * radius;
            float intensity = (float) (life * fade
                    * (0.68D + 0.32D * random.nextDouble()));
            drawPillar(buffer, x, skyY, z, x, oy + 0.10D, z,
                    age + i * 2.0D, seed + i * 271 + (int) bucket, intensity);
        }
    }

    /**
     * A thin, four-filament pillar from sky height to the ground. It is
     * deliberately thinner than the main bolt, but still uses multiple jagged
     * lines and a small fork so it reads as lightning rather than a simple
     * beam.
     */
    private static void drawPillar(BufferBuilder buffer,
                                   double sx, double sy, double sz,
                                   double tx, double ty, double tz,
                                   double age, int seed, float intensity) {
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.1D || intensity <= 0.01F) {
            return;
        }
        int filaments = 4;
        int segments = 9;
        for (int f = 0; f < filaments; ++f) {
            double[] xs = new double[segments + 1];
            double[] ys = new double[segments + 1];
            double[] zs = new double[segments + 1];
            buildJaggedPath(xs, ys, zs, sx, sy, sz, tx, ty, tz,
                    age + f * 2.11D, seed + f * 65537, distance, false);
            float width = f == 0 ? 0.060F : 0.038F;
            float scale = intensity * (f == 0 ? 1.0F : 0.82F);
            float[] color = f == 0 ? WHITE : (f == 1 ? PALE : CYAN);
            for (int i = 0; i < segments; ++i) {
                beam3D(buffer, xs[i], ys[i], zs[i],
                        xs[i + 1], ys[i + 1], zs[i + 1],
                        width, color[0] * scale, color[1] * scale,
                        color[2] * scale);
            }
            if (f == 0) {
                Random branchRand = new Random(seed * 0x27D4EB2F165667C5L
                        + (long) (age * 43.0D));
                int at = 2 + branchRand.nextInt(Math.max(1, segments - 4));
                double length = Math.min(7.0D, distance * 0.075D);
                double a = branchRand.nextDouble() * TWO_PI;
                drawBoltPath(buffer, xs[at], ys[at], zs[at],
                        xs[at] + Math.cos(a) * length,
                        ys[at] + (branchRand.nextDouble() - 0.4D) * length * 0.45D,
                        zs[at] + Math.sin(a) * length,
                        age + 17.0D, seed + at * 97, 0.034D,
                        PALE, intensity * 0.72F);
            }
        }
    }

    private static void renderImpact(BufferBuilder buffer, double ox, double oy,
                                     double oz, double age, double fade, int seed) {
        double since = age - EntityWideLightningStrike.IMPACT_TICK;
        if (since < 0.0D || since > 90.0D) {
            return;
        }
        double expand = smoothstep(clamp(since / 55.0D));
        double intensity = (float) ((1.0D - smoothstep(clamp(since / 80.0D)))
                * (0.65D + 0.35D * fade));
        float bright = (float) intensity;

        // Shock rings race outward along the ground, followed by a slower
        // inner ring and twelve short radial scorch spokes.
        drawArc(buffer, ox, oy + 0.03D, oz,
                5.0D + 48.0D * expand, 6.0D + 50.0D * expand,
                112, -age * 0.22D, -age * 0.22D + TWO_PI,
                PALE, bright);
        drawArc(buffer, ox, oy + 0.05D, oz,
                2.5D + 26.0D * smoothstep(clamp(since / 42.0D)),
                3.5D + 28.0D * smoothstep(clamp(since / 42.0D)),
                80, age * 0.35D, age * 0.35D + TWO_PI,
                CYAN, bright * 0.82F);
        for (int i = 0; i < 12; ++i) {
            double angle = TWO_PI * i / 12.0D + 0.13D;
            double inner = 2.0D + 10.0D * expand;
            double outer = 8.0D + 42.0D * expand;
            line3D(buffer, ox + Math.cos(angle) * inner, oy + 0.06D,
                    oz + Math.sin(angle) * inner,
                    ox + Math.cos(angle) * outer, oy + 0.06D,
                    oz + Math.sin(angle) * outer,
                    0.16D, BLUE[0] * bright, BLUE[1] * bright,
                    BLUE[2] * bright);
        }
        // A short vertical afterglow spear remains for the first few ticks.
        if (since < 30.0D) {
            drawBoltPath(buffer, ox, oy, oz, ox, oy + 11.0D - since * 0.20D, oz,
                    since, seed + 413, 0.18D, WHITE, bright * 0.70F);
        }
    }

    private static void drawBoltPath(BufferBuilder buffer,
                                     double x0, double y0, double z0,
                                     double x1, double y1, double z1,
                                     double age, int seed, double width,
                                     float[] color, float intensity) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        double dz = z1 - z0;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001D || intensity <= 0.002F) {
            return;
        }
        int segments = 7;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        double ux;
        double uz;
        if (horizontal > 0.0001D) {
            ux = -dz / horizontal;
            uz = dx / horizontal;
        } else {
            ux = 1.0D;
            uz = 0.0D;
        }
        double amp = Math.min(0.42D, distance * 0.10D);
        Random random = new Random(seed * 0x27D4EB2F165667C5L
                + (long) (age * 43.0D));
        double px = x0;
        double py = y0;
        double pz = z0;
        for (int i = 1; i <= segments; ++i) {
            double t = i / (double) segments;
            double envelope = Math.sin(t * Math.PI);
            double offset = (random.nextDouble() * 2.0D - 1.0D) * amp * envelope;
            double nx = x0 + dx * t + ux * offset;
            double ny = y0 + dy * t
                    + (random.nextDouble() * 2.0D - 1.0D) * amp * 0.35D * envelope;
            double nz = z0 + dz * t + uz * offset;
            if (i == segments) {
                nx = x1;
                ny = y1;
                nz = z1;
            }
            beam3D(buffer, px, py, pz, nx, ny, nz, width,
                    color[0] * intensity, color[1] * intensity,
                    color[2] * intensity);
            beam3D(buffer, px, py, pz, nx, ny, nz, width * 0.34D,
                    WHITE[0] * intensity, WHITE[1] * intensity,
                    WHITE[2] * intensity);
            px = nx;
            py = ny;
            pz = nz;
        }
    }

    public static void renderChainBolt(BufferBuilder buffer,
                                       double sx, double sy, double sz,
                                       double tx, double ty, double tz,
                                       double age, int maxAge, int seed) {
        double fade = 1.0D - smoothstep(clamp(age / Math.max(1.0D, maxAge)));
        if (fade <= 0.01D) {
            return;
        }
        float intensity = (float) (0.75D + 0.25D * Math.sin(age * 1.7D));
        globalFade = (float) fade;
        fadeScale = 1.0F;
        try {
            renderBolt(buffer, sx, sy, sz, tx, ty, tz, age, seed,
                    0.90F, intensity, false);
        } finally {
            globalFade = 1.0F;
        }
    }

    private static void drawArc(BufferBuilder buffer,
                                double cx, double cy, double cz,
                                double inner, double outer, int segments,
                                double start, double end, float[] color,
                                float bright) {
        if (outer <= inner || bright <= 0.002F) {
            return;
        }
        double span = end - start;
        if (Math.abs(span) < 0.0001D) {
            return;
        }
        int count = Math.max(8, Math.min(segments, 256));
        double step = span / count;
        float cr = color[0] * bright;
        float cg = color[1] * bright;
        float cb = color[2] * bright;
        for (int i = 0; i < count; ++i) {
            double a0 = start + step * i;
            double a1 = a0 + step;
            double c0 = Math.cos(a0);
            double s0 = Math.sin(a0);
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            quad(buffer,
                    cx + c0 * inner, cy, cz + s0 * inner,
                    cx + c0 * outer, cy, cz + s0 * outer,
                    cx + c1 * outer, cy, cz + s1 * outer,
                    cx + c1 * inner, cy, cz + s1 * inner,
                    cr, cg, cb);
        }
    }

    private static void drawBand(BufferBuilder buffer,
                                 double cx, double cy, double cz,
                                 double inner, double outer, double rotation,
                                 double reveal, int sectors, double edgeFraction,
                                 float[] fill, float[] glow, float bright,
                                 boolean luminousEdges) {
        if (outer <= inner || reveal <= 0.001D) {
            return;
        }
        double span = TWO_PI * clamp(reveal);
        double edge = Math.max(0.02D, (outer - inner) * edgeFraction);
        for (int i = 0; i < sectors; ++i) {
            double sectorStart = rotation + TWO_PI * i / sectors;
            if (sectorStart >= rotation + span) {
                break;
            }
            double sectorEnd = Math.min(rotation + span,
                    rotation + TWO_PI * (i + 1) / sectors);
            if (sectorEnd <= sectorStart) {
                continue;
            }
            double c0 = Math.cos(sectorStart);
            double s0 = Math.sin(sectorStart);
            double c1 = Math.cos(sectorEnd);
            double s1 = Math.sin(sectorEnd);

            quad(buffer,
                    cx + c0 * inner, cy, cz + s0 * inner,
                    cx + c0 * outer, cy, cz + s0 * outer,
                    cx + c1 * outer, cy, cz + s1 * outer,
                    cx + c1 * inner, cy, cz + s1 * inner,
                    fill[0], fill[1], fill[2]);

            if (luminousEdges) {
                quad(buffer,
                        cx + c0 * (outer - edge), cy + 0.01D,
                        cz + s0 * (outer - edge),
                        cx + c0 * outer, cy + 0.01D, cz + s0 * outer,
                        cx + c1 * outer, cy + 0.01D, cz + s1 * outer,
                        cx + c1 * (outer - edge), cy + 0.01D,
                        cz + s1 * (outer - edge),
                        glow[0] * bright, glow[1] * bright, glow[2] * bright);
                quad(buffer,
                        cx + c0 * inner, cy + 0.01D, cz + s0 * inner,
                        cx + c0 * (inner + edge * 0.55D), cy + 0.01D,
                        cz + s0 * (inner + edge * 0.55D),
                        cx + c1 * (inner + edge * 0.55D), cy + 0.01D,
                        cz + s1 * (inner + edge * 0.55D),
                        cx + c1 * inner, cy + 0.01D, cz + s1 * inner,
                        glow[0] * bright * 0.76F, glow[1] * bright * 0.76F,
                        glow[2] * bright * 0.76F);
            }
        }
        if (luminousEdges) {
            drawArc(buffer, cx, cy + 0.02D, cz, outer - edge, outer,
                    Math.max(16, sectors * 2), rotation, rotation + span,
                    glow, bright);
            drawArc(buffer, cx, cy + 0.03D, cz, inner, inner + edge * 0.55D,
                    Math.max(16, sectors * 2), rotation, rotation + span,
                    glow, bright * 0.76F);
        }
    }

    private static void line3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double width, float r, float g, float b) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal > 0.00001D) {
            double px = -dz / horizontal * width;
            double pz = dx / horizontal * width;
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

    private static void line3DGradient(BufferBuilder buffer,
                                       double x1, double y1, double z1,
                                       double x2, double y2, double z2,
                                       double width,
                                       float[] startColor, float[] endColor) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal > 0.00001D) {
            double px = -dz / horizontal * width;
            double pz = dx / horizontal * width;
            quadGradient(buffer,
                    x1 - px, y1, z1 - pz,
                    x1 + px, y1, z1 + pz,
                    x2 + px, y2, z2 + pz,
                    x2 - px, y2, z2 - pz,
                    startColor, endColor);
        }
        if (Math.abs(y2 - y1) > 0.00001D) {
            quadGradient(buffer,
                    x1, y1 - width, z1,
                    x1, y1 + width, z1,
                    x2, y2 + width, z2,
                    x2, y2 - width, z2,
                    startColor, endColor);
        }
    }

    private static void quadGradient(BufferBuilder buffer,
                                     double x0, double y0, double z0,
                                     double x1, double y1, double z1,
                                     double x2, double y2, double z2,
                                     double x3, double y3, double z3,
                                     float[] startColor, float[] endColor) {
        vertex(buffer, x0, y0, z0,
                startColor[0], startColor[1], startColor[2]);
        vertex(buffer, x1, y1, z1,
                startColor[0], startColor[1], startColor[2]);
        vertex(buffer, x2, y2, z2,
                endColor[0], endColor[1], endColor[2]);
        vertex(buffer, x3, y3, z3,
                endColor[0], endColor[1], endColor[2]);
    }

    private static void beam3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double radius, float r, float g, float b) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.00001D) {
            return;
        }
        dx /= length;
        dy /= length;
        dz /= length;
        double ax;
        double ay;
        double az;
        if (Math.abs(dy) < 0.92D) {
            ax = -dz;
            ay = 0.0D;
            az = dx;
        } else {
            ax = 1.0D;
            ay = 0.0D;
            az = 0.0D;
        }
        double al = Math.sqrt(ax * ax + ay * ay + az * az);
        if (al < 0.00001D) {
            ax = 1.0D;
            ay = 0.0D;
            az = 0.0D;
            al = 1.0D;
        }
        ax /= al;
        ay /= al;
        az /= al;
        double bx = dy * az - dz * ay;
        double by = dz * ax - dx * az;
        double bz = dx * ay - dy * ax;
        double bl = Math.sqrt(bx * bx + by * by + bz * bz);
        if (bl < 0.00001D) {
            bx = 0.0D;
            by = 1.0D;
            bz = 0.0D;
            bl = 1.0D;
        }
        bx /= bl;
        by /= bl;
        bz /= bl;

        quad(buffer,
                x1 + ax * radius, y1 + ay * radius, z1 + az * radius,
                x1 + bx * radius, y1 + by * radius, z1 + bz * radius,
                x2 + bx * radius, y2 + by * radius, z2 + bz * radius,
                x2 + ax * radius, y2 + ay * radius, z2 + az * radius,
                r, g, b);
        quad(buffer,
                x1 - ax * radius, y1 - ay * radius, z1 - az * radius,
                x1 - bx * radius, y1 - by * radius, z1 - bz * radius,
                x2 - bx * radius, y2 - by * radius, z2 - bz * radius,
                x2 - ax * radius, y2 - ay * radius, z2 - az * radius,
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
        float alpha = fadeScale * globalFade;
        buffer.pos(x, y, z).tex(0.0D, 0.0D)
                .color(r * alpha, g * alpha, b * alpha, alpha)
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

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }
}


