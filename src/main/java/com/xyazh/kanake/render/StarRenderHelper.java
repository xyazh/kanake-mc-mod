package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityStar;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.util.TemperatureColor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fully procedural visuals for the super-tier spell Star. The ground array,
 * temperature-coloured stellar surface, corona, prominences, feeding beam and
 * the final collapse/fade are all generated as vertex-coloured geometry.
 */
@SideOnly(Side.CLIENT)
public final class StarRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] DARK_RED = {0.16F, 0.015F, 0.004F};
    private static final double LAYER = 0.07D;
    private static float fadeScale = 1.0F;
    private static float globalFade = 1.0F;

    private StarRenderHelper() {
    }

    public static void render(BufferBuilder buffer, double ox, double oy, double oz,
                              double age, double centerY) {
        double progress = EntityStar.getHeatingProgress(age);
        float[] color = TemperatureColor.spectrum(progress);
        double tempProgress = clamp((EntityStar.getTemperatureAt(age)
                - EntityStar.MIN_TEMPERATURE)
                / (EntityStar.MAX_TEMPERATURE - EntityStar.MIN_TEMPERATURE));
        double build = smoothstep(clamp(age / EntityStar.ARRAY_BUILD_TICKS));
        double fade = age <= EntityStar.BURN_END_TICK ? 1.0D
                : 1.0D - smoothstep(clamp((age - EntityStar.BURN_END_TICK)
                / EntityStar.DISSIPATE_TICKS));
        if (age >= EntityStar.BURN_END_TICK + EntityStar.DISSIPATE_TICKS) {
            return;
        }
        globalFade = (float) fade;
        try {
            if (build > 0.001D && fade > 0.001D) {
                renderArray(buffer, ox, oy, oz, age, build, fade, color, progress);
            }
            double radius = EntityStar.getStarRadiusAt(age);
            if (radius > 0.05D) {
                renderStar(buffer, ox, oy, oz, centerY, age, radius,
                        color, progress, tempProgress);
            }
            if (age >= EntityStar.STAR_APPEAR_TICK) {
                renderLink(buffer, ox, oy, oz, centerY, radius, age, color);
            }
            if (age >= EntityStar.BURN_END_TICK) {
                renderDissipation(buffer, ox, oy, oz, centerY, age, color, radius);
            }
        } finally {
            globalFade = 1.0F;
        }
    }

    private static void renderArray(BufferBuilder buffer, double ox, double oy,
                                    double oz, double age, double build,
                                    double fade, float[] color, double progress) {
        double r = EntityStar.ARRAY_RADIUS
                * (0.05D + 0.95D * build) * (0.84D + 0.16D * fade);
        if (r < 0.2D) {
            return;
        }
        double cy = oy + EntityStar.ARRAY_FLOAT_HEIGHT;
        double so = age * 0.0031D;
        double si = -age * 0.0057D;
        double pulse = 1.0D + 0.010D * Math.sin(age * 0.12D);
        float bright = (float) (0.62D + 0.38D * progress);
        float[] glow = {
                mix(color[0], WHITE[0], (float) (0.18D + 0.42D * progress)),
                mix(color[1], WHITE[1], (float) (0.18D + 0.42D * progress)),
                mix(color[2], WHITE[2], (float) (0.18D + 0.42D * progress))
        };
        float[] core = {
                mix(glow[0], WHITE[0], 0.38F),
                mix(glow[1], WHITE[1], 0.32F),
                mix(glow[2], WHITE[2], 0.24F)
        };
        float[] fill = {
                mix(DARK_RED[0], glow[0], (float) (0.24D + 0.68D * progress)),
                mix(DARK_RED[1], glow[1], (float) (0.24D + 0.68D * progress)),
                mix(DARK_RED[2], glow[2], (float) (0.24D + 0.68D * progress))
        };
        fadeScale = (float) fade;
        try {
            // Solar disc and layers. Fill colours also brighten with the star,
            // so the whole array heats up instead of keeping fixed dark parts.
            drawCircle(buffer, ox, cy, oz, r * 0.185D * pulse, fill, 1.0F);
            drawCircle(buffer, ox, cy + 0.02D, oz, r * 0.064D * pulse,
                    core, bright);
            drawArc(buffer, ox, cy + LAYER, oz, r * 0.170D, r * 0.190D,
                    128, si, si + TWO_PI * build, glow, bright);
            drawArc(buffer, ox, cy + LAYER * 2, oz, r * 0.226D, r * 0.234D,
                    144, -si * 0.8D, -si * 0.8D + TWO_PI * build,
                    core, bright);

            drawBand(buffer, ox, cy + LAYER * 3, oz, r * 0.290D, r * 0.405D,
                    so, build, 6, 0.13D, fill, glow, bright, true);
            drawArc(buffer, ox, cy + LAYER * 5, oz, r * 0.438D, r * 0.446D,
                    160, -si, -si + TWO_PI * build, glow, bright);
            drawArc(buffer, ox, cy + LAYER * 6, oz, r * 0.486D, r * 0.492D,
                    176, si * 1.2D, si * 1.2D + TWO_PI * build, core, bright);

            drawBand(buffer, ox, cy + LAYER * 7, oz, r * 0.540D, r * 0.660D,
                    -so * 0.8D, build, 10, 0.085D, fill, glow, bright, true);
            drawArc(buffer, ox, cy + LAYER * 9, oz, r * 0.695D, r * 0.704D,
                    192, -si * 1.3D, -si * 1.3D + TWO_PI * build, glow, bright);

            drawBand(buffer, ox, cy + LAYER * 10, oz, r * 0.750D, r * 0.882D,
                    so * 1.05D, build, 12, 0.07D, fill, glow, bright, true);
            drawArc(buffer, ox, cy + LAYER * 12, oz, r * 0.915D, r * 0.926D,
                    224, -so * 1.35D, -so * 1.35D + TWO_PI * build,
                    core, bright);

            drawSunSpikes(buffer, ox, cy + LAYER * 13, oz, r, age, build,
                    glow, core, progress, bright);
            drawSunSigil(buffer, ox, cy + LAYER * 14, oz, r, age, build,
                    progress, glow, core, bright);
            drawRunes(buffer, ox, cy + LAYER * 15, oz, r, age, build,
                    glow, bright);
        } finally {
            fadeScale = 1.0F;
        }
    }

    private static void drawSunSpikes(BufferBuilder buffer, double cx, double cy,
                                      double cz, double r, double age,
                                      double build, float[] glow, float[] core,
                                      double progress, float bright) {
        if (build < 0.42D) {
            return;
        }
        // Twelve solar flames. Both the outer flame and its hotter core use
        // the shared temperature colors, so the ring heats together with the
        // star instead of leaving fixed gold highlights behind.
        for (int i = 0; i < 12; ++i) {
            double part = smoothstep(clamp((build - i * 0.025D) / 0.65D));
            double angle = i * TWO_PI / 12.0D + age * 0.0022D;
            double inner = r * 0.935D;
            double outer = r * (1.02D + 0.19D * part
                    * (0.65D + 0.35D * Math.sin(age * 0.07D + i * 1.9D)));
            double half = Math.max(0.08D, r * 0.012D);
            drawRadial(buffer, cx, cy, cz, angle, inner, outer, half,
                    glow, bright);
            drawRadial(buffer, cx, cy + 0.05D, cz, angle, inner,
                    inner + (outer - inner) * 0.82D, half * 0.32D,
                    core, bright);
            if (progress > 0.16D) {
                drawRadial(buffer, cx, cy + 0.02D, cz, angle + 0.040D,
                        inner * 0.98D, inner + (outer - inner) * 0.58D,
                        half * 0.20D, glow, bright * 0.82F);
                drawRadial(buffer, cx, cy + 0.02D, cz, angle - 0.040D,
                        inner * 0.98D, inner + (outer - inner) * 0.44D,
                        half * 0.15D, glow, bright * 0.72F);
            }
        }
        for (int i = 0; i < 24; ++i) {
            double part = smoothstep(clamp((build - 0.56D) / 0.44D));
            double angle = i * TWO_PI / 24.0D - age * 0.0035D;
            drawRadial(buffer, cx, cy + 0.02D, cz, angle, r * 0.94D,
                    r * (0.94D + 0.055D * part),
                    Math.max(0.04D, r * 0.0045D), glow, bright);
        }
        // Solar prominences hovering over the outer rim.
        for (int i = 0; i < 6; ++i) {
            double start = age * 0.008D + i * TWO_PI / 6.0D;
            drawArc(buffer, cx, cy + 0.07D, cz,
                    r * 0.965D, r * 0.978D, 48,
                    start, start + 0.30D + 0.35D * progress,
                    core, bright);
        }
    }

    private static void drawSunSigil(BufferBuilder buffer, double cx, double cy,
                                     double cz, double r, double age,
                                     double build, double progress,
                                     float[] glow, float[] core, float bright) {
        double s = r * 0.205D;
        double spin = age * 0.006D;
        float reveal = (float) (0.35D + 0.65D * smoothstep(build));
        float[] g = {glow[0] * bright, glow[1] * bright, glow[2] * bright};
        float[] c = {core[0] * bright, core[1] * bright, core[2] * bright};
        double[] xs = new double[3];
        double[] zs = new double[3];
        for (int i = 0; i < 3; ++i) {
            double angle = -Math.PI * 0.5D + spin + TWO_PI * i / 3.0D;
            xs[i] = cx + Math.cos(angle) * s;
            zs[i] = cz + Math.sin(angle) * s;
        }

        // The central triangle is the fixed core of the stellar sigil.
        for (int i = 0; i < 3; ++i) {
            int next = (i + 1) % 3;
            beam3D(buffer, xs[i], cy, zs[i], xs[next], cy, zs[next],
                    Math.max(0.06D, s * 0.030D),
                    g[0] * reveal, g[1] * reveal, g[2] * reveal);
        }
        // Sun disc in the triangle's centre.
        drawCircle(buffer, cx, cy + 0.02D, cz, s * 0.30D, core, bright);
        drawCircle(buffer, cx, cy + 0.04D, cz, s * 0.14D, c, bright);
        drawArc(buffer, cx, cy + 0.05D, cz, s * 0.40D, s * 0.455D, 48,
                -spin * 1.7D, -spin * 1.7D + TWO_PI * build, core, bright);

        // Vertex nodes and three short rays pointing out of the triangle.
        for (int i = 0; i < 3; ++i) {
            drawCircle(buffer, xs[i], cy + 0.03D, zs[i], s * 0.075D,
                    core, bright);
            double dx = xs[i] - cx;
            double dz = zs[i] - cz;
            double length = Math.sqrt(dx * dx + dz * dz);
            if (length > 0.001D) {
                double tipX = cx + dx / length * s * 1.38D;
                double tipZ = cz + dz / length * s * 1.38D;
                beam3D(buffer, xs[i], cy + 0.03D, zs[i],
                        tipX, cy + 0.03D, tipZ,
                        Math.max(0.04D, s * 0.018D),
                        c[0] * reveal, c[1] * reveal, c[2] * reveal);
            }
        }

        if (progress > 0.12D) {
            for (int i = 0; i < 3; ++i) {
                double start = age * 0.030D + i * TWO_PI / 3.0D;
                drawArc(buffer, cx, cy + 0.06D, cz,
                        s * (0.62D + i * 0.12D), s * (0.65D + i * 0.12D),
                        36, start, start + 0.55D + progress * 0.85D,
                        core, bright);
            }
        }
    }

    private static void drawRunes(BufferBuilder buffer, double cx, double cy,
                                  double cz, double r, double age,
                                  double build, float[] glow, float bright) {
        float[] g = {glow[0] * bright, glow[1] * bright, glow[2] * bright};
        for (int i = 0; i < 36; ++i) {
            if ((i + 1.0D) / 36.0D > build) {
                break;
            }
            double angle = -age * 0.0025D + TWO_PI * i / 36.0D;
            double rr = r * 0.712D;
            double x = cx + Math.cos(angle) * rr;
            double z = cz + Math.sin(angle) * rr;
            double tx = -Math.sin(angle);
            double tz = Math.cos(angle);
            double size = Math.max(0.09D, r * 0.006D);
            // Small sun-glyph: a cross bar with a short radial stroke.
            beam3D(buffer, x - tx * size, cy, z - tz * size,
                    x + tx * size, cy, z + tz * size,
                    size * 0.22D, g[0], g[1], g[2]);
            beam3D(buffer, x, cy + 0.03D, z,
                    x + Math.cos(angle) * size * 1.65D, cy + 0.03D,
                    z + Math.sin(angle) * size * 1.65D,
                    size * 0.16D, g[0], g[1], g[2]);
            beam3D(buffer, x - tx * size * 0.55D, cy + 0.055D,
                    z - tz * size * 0.55D,
                    x + tx * size * 0.55D, cy + 0.055D,
                    z + tz * size * 0.55D,
                    size * 0.10D, g[0], g[1], g[2]);
        }
    }

    private static void renderStar(BufferBuilder buffer, double ox, double oy,
                                   double oz, double centerY, double age,
                                   double radius, float[] color, double progress,
                                   double tempProgress) {
        double cy = centerY + Math.sin(age * 0.031D) * 0.55D;
        fadeScale = 1.0F;
        drawSurface(buffer, ox, cy, oz, radius, age, color, progress, tempProgress);
        drawCorona(buffer, ox, cy, oz, radius, age, color, progress);
        drawProminences(buffer, ox, cy, oz, radius, age, color, progress);
        drawRings(buffer, ox, cy, oz, radius, age, color, progress);
        if (age < EntityStar.IGNITION_START_TICK) {
            double charge = smoothstep(clamp((age - EntityStar.STAR_APPEAR_TICK)
                    / (double) (EntityStar.IGNITION_START_TICK
                    - EntityStar.STAR_APPEAR_TICK)));
            for (int i = 0; i < 7; ++i) {
                double start = age * 0.06D + i * TWO_PI / 7.0D;
                drawArc(buffer, ox, cy, oz,
                        radius * (1.30D + 0.08D * charge),
                        radius * (1.32D + 0.08D * charge), 48,
                        start, start + 0.8D + 1.2D * charge,
                        1.0F, 0.55F, 0.22F, 1.0F);
            }
        }
    }

    private static void drawSurface(BufferBuilder buffer, double cx, double cy,
                                    double cz, double radius, double age,
                                    float[] color, double progress,
                                    double tempProgress) {
        int lat = radius > 13.0D ? 18 : 14;
        int lon = radius > 13.0D ? 30 : 22;
        for (int i = 0; i < lat; ++i) {
            double t0 = Math.PI * i / lat;
            double t1 = Math.PI * (i + 1) / lat;
            double sin0 = Math.sin(t0);
            double cos0 = Math.cos(t0);
            double sin1 = Math.sin(t1);
            double cos1 = Math.cos(t1);
            for (int j = 0; j < lon; ++j) {
                double p0 = TWO_PI * j / lon;
                double p1 = TWO_PI * (j + 1) / lon;
                double c0 = Math.cos(p0);
                double s0 = Math.sin(p0);
                double c1 = Math.cos(p1);
                double s1 = Math.sin(p1);
                double w00 = wave(t0, p0, age);
                double w01 = wave(t0, p1, age);
                double w11 = wave(t1, p1, age);
                double w10 = wave(t1, p0, age);
                double x0 = cx + c0 * sin0 * radius * w00;
                double y0 = cy + cos0 * radius * w00;
                double z0 = cz + s0 * sin0 * radius * w00;
                double x1 = cx + c1 * sin0 * radius * w01;
                double y1 = cy + cos0 * radius * w01;
                double z1 = cz + s1 * sin0 * radius * w01;
                double x2 = cx + c1 * sin1 * radius * w11;
                double y2 = cy + cos1 * radius * w11;
                double z2 = cz + s1 * sin1 * radius * w11;
                double x3 = cx + c0 * sin1 * radius * w10;
                double y3 = cy + cos1 * radius * w10;
                double z3 = cz + s0 * sin1 * radius * w10;

                double theta = (t0 + t1) * 0.5D;
                double phi = (p0 + p1) * 0.5D;
                double granule = 0.5D + 0.5D * Math.sin(theta * 15.0D
                        - age * 0.31D + Math.sin(phi * 7.0D + age * 0.14D) * 2.1D);
                double hotSpot = Math.pow(Math.max(0.0D, Math.sin(phi * 5.0D
                        + theta * 7.0D - age * 0.22D)), 8.0D);
                double brightness = 0.62D + 0.24D * granule
                        + 0.14D * (1.0D - theta / Math.PI);
                float red = color[0] * (float) brightness;
                float green = color[1] * (float) brightness;
                float blue = color[2] * (float) brightness;
                if (hotSpot > 0.01D) {
                    float t = (float) (hotSpot * (0.15D + 0.32D * progress));
                    red = mix(red, 1.0F, t);
                    green = mix(green, 0.88F, t);
                    blue = mix(blue, 0.55F, t);
                }
                if (tempProgress > 0.78D) {
                    float t = (float) ((tempProgress - 0.78D) / 0.22D) * 0.18F;
                    red = mix(red, 1.0F, t);
                    green = mix(green, 0.96F, t);
                    blue = mix(blue, 0.88F, t);
                }
                quad(buffer, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3,
                        red, green, blue);
            }
        }
    }

    private static double wave(double theta, double phi, double age) {
        return 1.0D
                + 0.018D * Math.sin(theta * 9.0D + age * 0.14D + phi * 3.0D)
                + 0.012D * Math.sin(theta * 17.0D - age * 0.22D + phi * 7.0D)
                + 0.008D * Math.sin(theta * 25.0D + age * 0.36D - phi * 11.0D);
    }

    private static void drawCorona(BufferBuilder buffer, double cx, double cy,
                                   double cz, double radius, double age,
                                   float[] color, double progress) {
        for (int i = 0; i < 28; ++i) {
            double theta = Math.acos(1.0D - 2.0D * (i + 0.5D) / 28.0D);
            double phi = i * 2.399963D + age * 0.006D;
            double dx = Math.sin(theta) * Math.cos(phi);
            double dy = Math.cos(theta);
            double dz = Math.sin(theta) * Math.sin(phi);
            double pulse = 0.65D + 0.35D * Math.sin(age * 0.10D + i * 1.7D);
            double inner = radius * 1.005D;
            double outer = radius * (1.10D + 0.28D * pulse
                    * (0.45D + 0.55D * progress));
            float b = (float) (0.55D + 0.45D * pulse);
            beam3D(buffer,
                    cx + dx * inner, cy + dy * inner, cz + dz * inner,
                    cx + dx * outer, cy + dy * outer, cz + dz * outer,
                    Math.max(0.045D, radius * 0.0055D),
                    mix(color[0], 1.0F, 0.36F) * b,
                    mix(color[1], 0.92F, 0.30F) * b,
                    mix(color[2], 0.62F, 0.22F) * b);
        }
        drawRing(buffer, cx, cy, cz, radius * 1.09D,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                age * 0.014D, color, 1.0F, 128);
        drawRing(buffer, cx, cy, cz, radius * 1.16D,
                0.88D, 0.22D, 0.0D, 0.0D, 0.0D, 1.0D,
                -age * 0.020D, color, 1.0F, 112);
    }

    private static void drawProminences(BufferBuilder buffer, double cx, double cy,
                                        double cz, double radius, double age,
                                        float[] color, double progress) {
        if (progress < 0.06D) {
            return;
        }
        for (int i = 0; i < 7; ++i) {
            double theta = 0.55D + (i % 4) * 0.58D;
            double phi = i * 2.399963D + age * 0.0048D;
            double sinT = Math.sin(theta);
            double x0 = sinT * Math.cos(phi);
            double y0 = Math.cos(theta);
            double z0 = sinT * Math.sin(phi);
            double phi1 = phi + 0.65D + 0.18D * (i % 3);
            double x1 = sinT * Math.cos(phi1);
            double z1 = sinT * Math.sin(phi1);
            double y1 = Math.cos(theta) + 0.22D;
            double l = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
            x1 /= l;
            y1 /= l;
            z1 /= l;
            double mx = (x0 + x1) * 0.5D;
            double my = (y0 + y1) * 0.5D;
            double mz = (z0 + z1) * 0.5D;
            double ml = Math.sqrt(mx * mx + my * my + mz * mz);
            mx /= ml;
            my /= ml;
            mz /= ml;
            double control = radius * (1.52D + 0.24D
                    * Math.sin(age * 0.09D + i * 2.1D));
            double p0x = cx + x0 * radius * 1.005D;
            double p0y = cy + y0 * radius * 1.005D;
            double p0z = cz + z0 * radius * 1.005D;
            double p1x = cx + x1 * radius * 1.005D;
            double p1y = cy + y1 * radius * 1.005D;
            double p1z = cz + z1 * radius * 1.005D;
            double ctrlX = cx + mx * control;
            double ctrlY = cy + my * control;
            double ctrlZ = cz + mz * control;
            double prevX = p0x;
            double prevY = p0y;
            double prevZ = p0z;
            for (int step = 1; step <= 16; ++step) {
                double t = step / 16.0D;
                double om = 1.0D - t;
                double x = om * om * p0x + 2.0D * om * t * ctrlX + t * t * p1x;
                double y = om * om * p0y + 2.0D * om * t * ctrlY + t * t * p1y;
                double z = om * om * p0z + 2.0D * om * t * ctrlZ + t * t * p1z;
                float b = (float) (0.65D + 0.35D * Math.sin(t * Math.PI));
                beam3D(buffer, prevX, prevY, prevZ, x, y, z,
                        Math.max(0.035D, radius * 0.0045D),
                        mix(color[0], 1.0F, 0.45F) * b,
                        mix(color[1], 0.65F, 0.30F) * b,
                        mix(color[2], 0.30F, 0.18F) * b);
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
    }

    private static void drawRings(BufferBuilder buffer, double cx, double cy,
                                  double cz, double radius, double age,
                                  float[] color, double progress) {
        float[] hot = {mix(color[0], 1.0F, 0.30F),
                mix(color[1], 1.0F, 0.25F),
                mix(color[2], 1.0F, 0.18F)};
        drawRing(buffer, cx, cy, cz, radius * 1.015D,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                age * 0.018D, hot, 1.0F, 112);
        drawRing(buffer, cx, cy, cz, radius * 1.055D,
                1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D,
                -age * 0.022D, color, 1.0F, 96);
        if (progress > 0.45D) {
            drawRing(buffer, cx, cy, cz, radius * 1.095D,
                    0.0D, 0.0D, 1.0D, 0.0D, 1.0D, 0.0D,
                    age * 0.031D, hot, 1.0F, 96);
        }
    }

    private static void renderLink(BufferBuilder buffer, double ox, double oy,
                                   double oz, double centerY, double radius,
                                   double age, float[] color) {
        double start = oy + EntityStar.ARRAY_FLOAT_HEIGHT + 0.18D;
        double bottom = centerY - Math.max(0.6D, radius) * 0.92D;
        if (bottom <= start + 0.5D) {
            return;
        }
        double width = Math.max(0.18D, radius * 0.018D);
        drawBeam(buffer, ox, start, bottom, oz, width, color);
        drawBeam(buffer, ox, start, bottom, oz, width * 0.32D,
                1.0F, 0.82F, 0.48F, 1.0F);
        for (int h = 0; h < 3; ++h) {
            double prevX = 0.0D;
            double prevY = 0.0D;
            double prevZ = 0.0D;
            for (int i = 0; i <= 40; ++i) {
                double t = i / 40.0D;
                double y = start + (bottom - start) * t;
                double angle = age * 0.055D + h * TWO_PI / 3.0D
                        + t * TWO_PI * 3.6D;
                double rr = width * (3.0D - 1.7D * t);
                double x = ox + Math.cos(angle) * rr;
                double z = oz + Math.sin(angle) * rr;
                if (i > 0) {
                    beam3D(buffer, prevX, prevY, prevZ, x, y, z,
                            Math.max(0.035D, width * 0.10D),
                            color[0], color[1], color[2]);
                }
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
        for (int i = 1; i <= 7; ++i) {
            double t = i / 8.0D;
            double y = start + (bottom - start) * t;
            double pulse = 1.0D + 0.12D * Math.sin(age * 0.22D + i);
            drawArc(buffer, ox, y, oz, width * (2.5D * pulse - 0.06D),
                    width * (2.5D * pulse + 0.06D), 32,
                    -age * 0.14D + i, -age * 0.14D + i + TWO_PI,
                    1.0F, 0.72F, 0.34F, 1.0F);
        }
    }

    private static void renderDissipation(BufferBuilder buffer, double ox, double oy,
                                          double oz, double centerY, double age,
                                          float[] color, double radius) {
        double t = clamp((age - EntityStar.BURN_END_TICK)
                / EntityStar.DISSIPATE_TICKS);
        double cy = centerY;
        // Inward streams and tightening rings read as a collapse, not an
        // explosion. The whole ritual is additionally faded by globalFade.
        for (int i = 0; i < 16; ++i) {
            double theta = Math.acos(1.0D - 2.0D * (i + 0.5D) / 16.0D);
            double phi = i * 2.399963D + age * 0.03D;
            double dx = Math.sin(theta) * Math.cos(phi);
            double dy = Math.cos(theta);
            double dz = Math.sin(theta) * Math.sin(phi);
            double outer = radius * (1.4D + 0.7D * (1.0D - t));
            double inner = radius * (0.15D + 0.45D * t);
            beam3D(buffer, ox + dx * outer, cy + dy * outer, oz + dz * outer,
                    ox + dx * inner, cy + dy * inner, oz + dz * inner,
                    Math.max(0.04D, radius * 0.006D),
                    1.0F, 0.70F, 0.36F);
        }
        drawRing(buffer, ox, cy, oz, radius * (1.25D - 0.65D * t),
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                -age * 0.10D, 1.0F, 0.78F, 0.44F, 1.0F, 96);
        drawRing(buffer, ox, cy, oz, radius * (1.45D - 0.75D * t),
                1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D,
                age * 0.13D, color, 1.0F, 80);
        drawBeam(buffer, ox, oy + EntityStar.ARRAY_FLOAT_HEIGHT, cy, oz,
                0.3D + 2.0D * t, 1.0F, 0.82F, 0.56F, 1.0F);
    }

    private static void drawBand(BufferBuilder buffer, double cx, double cy, double cz,
                                 double inner, double outer, double rotation,
                                 double build, int sectors, double gap,
                                 float[] fill, float[] edge, float bright,
                                 boolean mechanical) {
        if (build <= 0.001D || outer <= inner) {
            return;
        }
        double sweep = TWO_PI / sectors;
        double width = Math.max(0.05D, (outer - inner) * 0.065D);
        // The old code subdivided every short sector into 160 segments. Since
        // the bloom pass renders all of this geometry a second time, that was
        // the dominating cost of the star array.
        int arcSegments = Math.max(8, 192 / Math.max(1, sectors));
        for (int i = 0; i < sectors; ++i) {
            double local = smoothstep(clamp((build - i * 0.05D)
                    / Math.max(0.10D, 1.0D - (sectors - 1) * 0.05D)));
            if (local <= 0.001D) {
                continue;
            }
            double start = rotation + i * sweep + gap * 0.5D;
            double end = rotation + (i + 1) * sweep - gap * 0.5D;
            double actualEnd = start + (end - start) * local;
            double offset = mechanical && (i & 1) == 1 ? (outer - inner) * 0.08D : 0.0D;
            double bandInner = inner + offset;
            double bandOuter = outer - offset * 0.45D;
            drawArc(buffer, cx, cy, cz, bandInner, bandOuter,
                    arcSegments, start, actualEnd, fill, 1.0F);
            drawArc(buffer, cx, cy + LAYER, cz, bandInner,
                    bandInner + width, arcSegments, start, actualEnd, edge, 1.0F);
            drawArc(buffer, cx, cy + LAYER, cz, bandOuter - width,
                    bandOuter, arcSegments, start, actualEnd, edge, bright);
        }
    }

    private static void drawArc(BufferBuilder buffer, double cx, double cy, double cz,
                                double inner, double outer, int segments,
                                double start, double end, float[] color,
                                float bright) {
        drawArc(buffer, cx, cy, cz, inner, outer, segments, start, end,
                color[0], color[1], color[2], bright);
    }

    private static void drawArc(BufferBuilder buffer, double cx, double cy, double cz,
                                double inner, double outer, int segments,
                                double start, double end, float red, float green,
                                float blue, float bright) {
        if (outer <= inner || segments < 1 || end <= start) {
            return;
        }
        double step = (end - start) / segments;
        red *= bright;
        green *= bright;
        blue *= bright;
        for (int i = 0; i < segments; ++i) {
            double a0 = start + step * i;
            double a1 = start + step * (i + 1);
            double c0 = Math.cos(a0);
            double s0 = Math.sin(a0);
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            quad(buffer,
                    cx + c0 * inner, cy, cz + s0 * inner,
                    cx + c1 * inner, cy, cz + s1 * inner,
                    cx + c1 * outer, cy, cz + s1 * outer,
                    cx + c0 * outer, cy, cz + s0 * outer,
                    red, green, blue);
        }
    }

    private static void drawRing(BufferBuilder buffer, double cx, double cy, double cz,
                                 double radius, double ux, double uy, double uz,
                                 double vx, double vy, double vz, double rotation,
                                 float[] color, float bright, int segments) {
        drawRing(buffer, cx, cy, cz, radius, ux, uy, uz, vx, vy, vz,
                rotation, color[0], color[1], color[2], bright, segments);
    }

    private static void drawRing(BufferBuilder buffer, double cx, double cy, double cz,
                                 double radius, double ux, double uy, double uz,
                                 double vx, double vy, double vz, double rotation,
                                 float red, float green, float blue, float bright,
                                 int segments) {
        if (radius <= 0.02D) {
            return;
        }
        double prevX = 0.0D;
        double prevY = 0.0D;
        double prevZ = 0.0D;
        for (int i = 0; i <= segments; ++i) {
            double angle = rotation + TWO_PI * i / segments;
            double c = Math.cos(angle);
            double s = Math.sin(angle);
            double x = cx + (ux * c + vx * s) * radius;
            double y = cy + (uy * c + vy * s) * radius;
            double z = cz + (uz * c + vz * s) * radius;
            if (i > 0) {
                boolean accent = i % 12 == 0;
                beam3D(buffer, prevX, prevY, prevZ, x, y, z,
                        Math.max(0.035D, radius * 0.0035D),
                        accent ? 1.0F : red * bright,
                        accent ? 1.0F : green * bright,
                        accent ? 1.0F : blue * bright);
            }
            prevX = x;
            prevY = y;
            prevZ = z;
        }
    }

    private static void drawCircle(BufferBuilder buffer, double cx, double cy, double cz,
                                   double radius, float[] color, float bright) {
        drawCircle(buffer, cx, cy, cz, radius, color[0], color[1], color[2], bright);
    }

    private static void drawCircle(BufferBuilder buffer, double cx, double cy, double cz,
                                   double radius, float red, float green, float blue,
                                   float bright) {
        if (radius <= 0.01D) {
            return;
        }
        red *= bright;
        green *= bright;
        blue *= bright;
        for (int i = 0; i < 64; ++i) {
            double a0 = TWO_PI * i / 64.0D;
            double a1 = TWO_PI * (i + 1) / 64.0D;
            triangle(buffer, cx, cy, cz,
                    cx + Math.cos(a0) * radius, cy, cz + Math.sin(a0) * radius,
                    cx + Math.cos(a1) * radius, cy, cz + Math.sin(a1) * radius,
                    red, green, blue);
        }
    }

    private static void drawRadial(BufferBuilder buffer, double cx, double cy,
                                   double cz, double angle, double inner,
                                   double outer, double halfWidth,
                                   float[] color, float bright) {
        drawRadial(buffer, cx, cy, cz, angle, inner, outer, halfWidth,
                color[0], color[1], color[2], bright);
    }

    private static void drawRadial(BufferBuilder buffer, double cx, double cy,
                                   double cz, double angle, double inner,
                                   double outer, double halfWidth,
                                   float red, float green, float blue,
                                   float bright) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double tx = -sin * halfWidth;
        double tz = cos * halfWidth;
        quad(buffer,
                cx + cos * inner + tx, cy, cz + sin * inner + tz,
                cx + cos * outer + tx, cy, cz + sin * outer + tz,
                cx + cos * outer - tx, cy, cz + sin * outer - tz,
                cx + cos * inner - tx, cy, cz + sin * inner - tz,
                red * bright, green * bright, blue * bright);
    }

    private static void drawBeam(BufferBuilder buffer, double cx, double bottom,
                                 double top, double cz, double radius,
                                 float[] color) {
        drawBeam(buffer, cx, bottom, top, cz, radius,
                color[0], color[1], color[2], 1.0F);
    }

    private static void drawBeam(BufferBuilder buffer, double cx, double bottom,
                                 double top, double cz, double radius,
                                 float red, float green, float blue,
                                 float bright) {
        if (top <= bottom || radius <= 0.005D) {
            return;
        }
        for (int i = 0; i < 8; ++i) {
            double a0 = TWO_PI * i / 8.0D;
            double a1 = TWO_PI * (i + 1) / 8.0D;
            double x0 = cx + Math.cos(a0) * radius;
            double z0 = cz + Math.sin(a0) * radius;
            double x1 = cx + Math.cos(a1) * radius;
            double z1 = cz + Math.sin(a1) * radius;
            quad(buffer, x0, bottom, z0, x0, top, z0,
                    x1, top, z1, x1, bottom, z1,
                    red * bright, green * bright, blue * bright);
        }
    }

    private static void drawSphere(BufferBuilder buffer, double cx, double cy,
                                   double cz, double radius, int lat, int lon,
                                   float[] color) {
        drawSphere(buffer, cx, cy, cz, radius, lat, lon,
                color[0], color[1], color[2], 1.0F);
    }

    private static void drawSphere(BufferBuilder buffer, double cx, double cy,
                                   double cz, double radius, int lat, int lon,
                                   float red, float green, float blue,
                                   float bright) {
        if (radius <= 0.01D) {
            return;
        }
        red *= bright;
        green *= bright;
        blue *= bright;
        for (int i = 0; i < lat; ++i) {
            double t0 = Math.PI * i / lat;
            double t1 = Math.PI * (i + 1) / lat;
            double sin0 = Math.sin(t0);
            double cos0 = Math.cos(t0);
            double sin1 = Math.sin(t1);
            double cos1 = Math.cos(t1);
            for (int j = 0; j < lon; ++j) {
                double p0 = TWO_PI * j / lon;
                double p1 = TWO_PI * (j + 1) / lon;
                double c0 = Math.cos(p0);
                double s0 = Math.sin(p0);
                double c1 = Math.cos(p1);
                double s1 = Math.sin(p1);
                quad(buffer,
                        cx + c0 * sin0 * radius, cy + cos0 * radius,
                        cz + s0 * sin0 * radius,
                        cx + c1 * sin0 * radius, cy + cos0 * radius,
                        cz + s1 * sin0 * radius,
                        cx + c1 * sin1 * radius, cy + cos1 * radius,
                        cz + s1 * sin1 * radius,
                        cx + c0 * sin1 * radius, cy + cos1 * radius,
                        cz + s0 * sin1 * radius,
                        red, green, blue);
            }
        }
    }

    private static void beam3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double width, float red, float green, float blue) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.00001D || width <= 0.00001D) {
            return;
        }
        dx /= length;
        dy /= length;
        dz /= length;
        double ux;
        double uy;
        double uz;
        if (Math.abs(dy) > 0.92D) {
            ux = 1.0D;
            uy = 0.0D;
            uz = 0.0D;
        } else {
            ux = 0.0D;
            uy = 1.0D;
            uz = 0.0D;
        }
        double px = dy * uz - dz * uy;
        double py = dz * ux - dx * uz;
        double pz = dx * uy - dy * ux;
        double pl = Math.sqrt(px * px + py * py + pz * pz);
        if (pl < 0.00001D) {
            return;
        }
        px = px / pl * width;
        py = py / pl * width;
        pz = pz / pl * width;
        double qx = dy * pz - dz * py;
        double qy = dz * px - dx * pz;
        double qz = dx * py - dy * px;
        double ql = Math.sqrt(qx * qx + qy * qy + qz * qz);
        if (ql > 0.00001D) {
            qx = qx / ql * width;
            qy = qy / ql * width;
            qz = qz / ql * width;
        }
        quad(buffer, x1 - px, y1 - py, z1 - pz,
                x1 + px, y1 + py, z1 + pz,
                x2 + px, y2 + py, z2 + pz,
                x2 - px, y2 - py, z2 - pz,
                red, green, blue);
        quad(buffer, x1 - qx, y1 - qy, z1 - qz,
                x1 + qx, y1 + qy, z1 + qz,
                x2 + qx, y2 + qy, z2 + qz,
                x2 - qx, y2 - qy, z2 - qz,
                red, green, blue);
    }

    private static void triangle(BufferBuilder buffer,
                                 double x0, double y0, double z0,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 float red, float green, float blue) {
        vertex(buffer, x0, y0, z0, red, green, blue);
        vertex(buffer, x1, y1, z1, red, green, blue);
        vertex(buffer, x2, y2, z2, red, green, blue);
        vertex(buffer, x2, y2, z2, red, green, blue);
    }

    private static void quad(BufferBuilder buffer,
                             double x0, double y0, double z0,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             float red, float green, float blue) {
        vertex(buffer, x0, y0, z0, red, green, blue);
        vertex(buffer, x1, y1, z1, red, green, blue);
        vertex(buffer, x2, y2, z2, red, green, blue);
        vertex(buffer, x3, y3, z3, red, green, blue);
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               float red, float green, float blue) {
        float alpha = fadeScale * globalFade;
        buffer.pos(x, y, z).tex(0.0D, 0.0D)
                .color(red * alpha, green * alpha, blue * alpha, alpha)
                .lightmap(SimpleParticle.FULLBRIGHT_LIGHT_X,
                        SimpleParticle.FULLBRIGHT_LIGHT_Y)
                .endVertex();
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }
}
