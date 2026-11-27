package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntitySuperBlackHole;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fully procedural geometry for the super-tier spell Black Hole. No texture is
 * used: bright runes, the feeding beam, photon rings and in-fall streams are
 * opaque vertex-coloured quads which the existing framebuffer bloom pass turns
 * into glowing light.
 */
@SideOnly(Side.CLIENT)
public final class SuperBlackHoleRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] PALE = {0.78F, 0.68F, 1.0F};
    private static final float[] VIOLET = {0.48F, 0.12F, 1.0F};
    private static final float[] CYAN = {0.30F, 0.76F, 1.0F};
    private static final float[] DEEP = {0.10F, 0.005F, 0.22F};
    private static final float[] DARK = {0.015F, 0.0F, 0.035F};
    private static final double LAYER = 0.07D;
    private static float fadeScale = 1.0F;

    private SuperBlackHoleRenderHelper() {
    }

    public static void render(BufferBuilder buffer, double ox, double oy, double oz,
                              double age, double radius, double centerY,
                              int finishTick) {
        double finishFade = finishTick >= 0
                ? smoothstep(clamp(1.0D - (age - finishTick)
                / EntitySuperBlackHole.FINISH_FADE_TICKS)) : 1.0D;
        if (finishFade <= 0.001D) {
            return;
        }
        double build = smoothstep(clamp(age / EntitySuperBlackHole.ARRAY_BUILD_TICKS));
        double arrayFade = age <= EntitySuperBlackHole.HOLD_END_TICK ? 1.0D
                : 1.0D - smoothstep(clamp((age - EntitySuperBlackHole.HOLD_END_TICK)
                / EntitySuperBlackHole.ARRAY_DISSIPATE_TICKS));
        arrayFade *= finishFade;
        if (build > 0.001D && arrayFade > 0.001D) {
            renderArray(buffer, ox, oy, oz, age, build, arrayFade);
        }
        if (radius > 0.05D && age >= EntitySuperBlackHole.FORM_START_TICK) {
            renderHole(buffer, ox, oy, oz, age, radius, centerY, finishFade);
        }
        if (finishTick >= 0) {
            renderFinish(buffer, ox, oy, oz, centerY, age, finishTick);
        }
    }

    private static void renderArray(BufferBuilder buffer, double ox, double oy, double oz,
                                    double age, double build, double fade) {
        double r = EntitySuperBlackHole.ARRAY_RADIUS
                * (0.06D + 0.94D * build) * (0.84D + 0.16D * fade);
        if (r < 0.2D) {
            return;
        }
        double cy = oy + EntitySuperBlackHole.ARRAY_FLOAT_HEIGHT;
        double so = age * 0.0038D;
        double si = -age * 0.0071D;
        double sb = age * 0.0022D;
        double pulse = 1.0D + 0.008D * Math.sin(age * 0.13D);
        double charge = smoothRange(age, EntitySuperBlackHole.FORM_START_TICK,
                EntitySuperBlackHole.FORM_END_TICK);
        float bright = (float) (0.72D + 0.28D * charge);

        fadeScale = (float) fade;
        try {
            drawCircle(buffer, ox, cy, oz, r * 0.235D * pulse, DARK, 1.0F);
            drawArc(buffer, ox, cy + LAYER, oz, r * 0.232D * pulse,
                    r * 0.252D * pulse, 128, si, si + TWO_PI, PALE, bright);
            drawArc(buffer, ox, cy + LAYER * 2, oz, r * 0.273D, r * 0.280D,
                    128, -si * 0.8D, -si * 0.8D + TWO_PI, VIOLET, 1.0F);
            drawArc(buffer, ox, cy + LAYER * 3, oz, r * 0.318D, r * 0.324D,
                    128, si * 0.6D, si * 0.6D + TWO_PI * build, WHITE, bright);

            drawBand(buffer, ox, cy + LAYER * 4, oz, r * 0.405D, r * 0.498D,
                    sb, build, 4, 0.17D, DEEP, VIOLET, bright, false);
            drawArc(buffer, ox, cy + LAYER * 6, oz, r * 0.522D, r * 0.529D,
                    160, -si * 1.2D, -si * 1.2D + TWO_PI * build, PALE, bright);

            drawBand(buffer, ox, cy + LAYER * 7, oz, r * 0.625D, r * 0.714D,
                    -so, build, 6, 0.13D, DEEP, CYAN, bright, true);
            drawArc(buffer, ox, cy + LAYER * 9, oz, r * 0.742D, r * 0.750D,
                    192, so * 1.4D, so * 1.4D + TWO_PI * build, WHITE, 1.0F);

            drawBand(buffer, ox, cy + LAYER * 10, oz, r * 0.825D, r * 0.918D,
                    so * 0.75D, build, 8, 0.10D, DARK, VIOLET, bright, true);
            drawArc(buffer, ox, cy + LAYER * 12, oz, r * 0.945D, r * 0.956D,
                    224, -so * 1.1D, -so * 1.1D + TWO_PI * build, PALE, 1.0F);

            drawArraySpikes(buffer, ox, cy + LAYER * 13, oz, r, build, age, bright);
            drawArrayRunes(buffer, ox, cy + LAYER * 14, oz, r, build, age, bright);
            drawVoidSigil(buffer, ox, cy + LAYER * 15, oz, r, age, build, charge, bright);
        } finally {
            fadeScale = 1.0F;
        }
    }

    private static void drawArraySpikes(BufferBuilder buffer, double cx, double cy,
                                        double cz, double r, double build,
                                        double age, float bright) {
        if (build < 0.45D) {
            return;
        }
        for (int i = 0; i < 8; ++i) {
            double part = smoothstep(clamp((build - i * 0.035D) / 0.62D));
            double angle = Math.PI * 0.25D + i * Math.PI * 0.25D + age * 0.0014D;
            double width = Math.max(0.08D, r * 0.010D);
            drawRadial(buffer, cx, cy, cz, angle, r * 0.975D,
                    r * (1.0D + 0.16D * part), width, DEEP, 1.0F);
            drawRadial(buffer, cx, cy + 0.06D, cz, angle, r * 0.975D,
                    r * 0.975D + r * 0.16D * part * 0.86D,
                    width * 0.34D, PALE, bright);
        }
        for (int i = 0; i < 16; ++i) {
            double part = smoothstep(clamp((build - 0.58D) / 0.42D));
            double angle = i * Math.PI * 0.125D - age * 0.0021D;
            drawRadial(buffer, cx, cy + 0.03D, cz, angle, r * 0.95D,
                    r * (0.95D + 0.055D * part),
                    Math.max(0.05D, r * 0.0045D), VIOLET, 1.0F);
        }
    }

    private static void drawArrayRunes(BufferBuilder buffer, double cx, double cy,
                                       double cz, double r, double build,
                                       double age, float bright) {
        int runes = 40;
        for (int i = 0; i < runes; ++i) {
            if ((i + 1.0D) / runes > build) {
                break;
            }
            double angle = age * 0.0028D + TWO_PI * i / runes;
            double rr = r * 0.885D;
            double x = cx + Math.cos(angle) * rr;
            double z = cz + Math.sin(angle) * rr;
            double tx = -Math.sin(angle);
            double tz = Math.cos(angle);
            double size = Math.max(0.10D, r * 0.0065D);
            line3D(buffer, x - tx * size, cy, z - tz * size,
                    x + tx * size, cy, z + tz * size,
                    size * 0.23D, PALE, 1.0F);
            line3D(buffer, x, cy + 0.03D, z,
                    x + Math.cos(angle) * size * 1.8D, cy + 0.03D,
                    z + Math.sin(angle) * size * 1.8D,
                    size * 0.18D, WHITE, bright);
        }
        for (int i = 0; i < 12; ++i) {
            if ((i + 1.0D) / 12 > build) {
                break;
            }
            double angle = -age * 0.0042D + TWO_PI * i / 12;
            double rr = r * 0.732D;
            drawDiamond(buffer, cx + Math.cos(angle) * rr, cy,
                    cz + Math.sin(angle) * rr, Math.max(0.12D, r * 0.009D),
                    PALE, bright);
        }
    }

    private static void drawVoidSigil(BufferBuilder buffer, double cx, double cy,
                                      double cz, double r, double age,
                                      double build, double charge, float bright) {
        double s = r * 0.145D;
        double spin = -age * 0.010D;
        for (int lobe = 0; lobe < 3; ++lobe) {
            double angle = spin + lobe * TWO_PI / 3.0D;
            double x1 = cx + Math.cos(angle) * s * 0.42D;
            double z1 = cz + Math.sin(angle) * s * 0.42D;
            double x2 = cx + Math.cos(angle + 0.34D) * s * 1.15D;
            double z2 = cz + Math.sin(angle + 0.34D) * s * 1.15D;
            line3D(buffer, x1, cy, z1, x2, cy, z2,
                    Math.max(0.05D, s * 0.055D), PALE, bright);
            line3D(buffer, x1, cy + 0.03D, z1,
                    cx + Math.cos(angle - 0.34D) * s * 0.92D, cy + 0.03D,
                    cz + Math.sin(angle - 0.34D) * s * 0.92D,
                    Math.max(0.04D, s * 0.040D), VIOLET, 1.0F);
        }
        drawCircle(buffer, cx, cy + 0.02D, cz, s * 0.22D, WHITE, bright);
        drawArc(buffer, cx, cy + 0.04D, cz, s * 0.34D, s * 0.40D, 48,
                age * 0.016D, age * 0.016D + TWO_PI * build, PALE, 1.0F);
        if (charge > 0.01D) {
            for (int i = 0; i < 3; ++i) {
                double start = age * 0.047D + i * TWO_PI / 3.0D;
                drawArc(buffer, cx, cy + 0.06D, cz,
                        s * (0.52D + i * 0.12D), s * (0.55D + i * 0.12D),
                        36, start, start + 0.55D + charge * 0.65D, PALE, 1.0F);
            }
        }
    }

    private static void renderHole(BufferBuilder buffer, double ox, double oy, double oz,
                                   double age, double radius, double centerY,
                                   double finishFade) {
        double cy = centerY + Math.sin(age * 0.021D)
                * 0.75D * clamp(radius / 24.0D);
        fadeScale = (float) finishFade;
        try {
            drawSphere(buffer, ox, cy, oz, radius * 0.985D,
                    radius > 16.0D ? 18 : 12, radius > 16.0D ? 32 : 20, DARK);
            drawArc(buffer, ox, cy, oz, radius * 0.995D, radius * 1.008D,
                    192, 0.0D, TWO_PI, PALE, 1.0F);
            if (age <= EntitySuperBlackHole.HOLD_END_TICK
                    + EntitySuperBlackHole.ARRAY_DISSIPATE_TICKS) {
                drawArrayLink(buffer, ox, oy, oz, cy, radius, age);
            }
        } finally {
            fadeScale = 1.0F;
        }
    }

    private static void drawArrayLink(BufferBuilder buffer, double ox, double oy,
                                      double oz, double holeY, double radius,
                                      double age) {
        double start = oy + EntitySuperBlackHole.ARRAY_FLOAT_HEIGHT + 0.18D;
        double bottom = holeY - radius * 0.88D;
        if (bottom <= start + 0.5D) {
            return;
        }
        double width = Math.max(0.22D, radius * 0.015D);
        drawBeam(buffer, ox, start, bottom, oz, width, VIOLET);
        drawBeam(buffer, ox, start, bottom, oz, width * 0.34D, WHITE);
        for (int i = 1; i <= 8; ++i) {
            double t = i / 9.0D;
            double y = start + (bottom - start) * t;
            double pulse = 1.0D + 0.14D * Math.sin(age * 0.20D + i);
            drawArc(buffer, ox, y, oz, width * (2.4D * pulse - 0.06D),
                    width * (2.4D * pulse + 0.06D), 32,
                    age * 0.15D + i, age * 0.15D + i + TWO_PI, PALE, 1.0F);
        }
    }

    private static void renderFinish(BufferBuilder buffer, double ox, double oy,
                                     double oz, double centerY, double age,
                                     int finishTick) {
        double t = clamp((age - finishTick) / EntitySuperBlackHole.FINISH_FADE_TICKS);
        if (t >= 1.0D) {
            return;
        }
        fadeScale = (float) (1.0D - smoothstep(t));
        try {
            double baseY = oy + EntitySuperBlackHole.ARRAY_FLOAT_HEIGHT;
            double r = 4.0D + 58.0D * smoothstep(t);
            drawArc(buffer, ox, baseY + 0.15D, oz, Math.max(0.10D, r - 1.2D),
                    r, 224, 0.0D, TWO_PI, WHITE, 1.0F);
            drawArc(buffer, ox, baseY + 0.23D, oz, r * 0.76D, r * 0.77D,
                    192, age * 0.08D, age * 0.08D + TWO_PI, PALE, 1.0F);
            drawBeam(buffer, ox, baseY, centerY, oz,
                    1.5D + 4.0D * (1.0D - t), PALE);
            drawBeam(buffer, ox, baseY, centerY, oz,
                    0.5D + 1.2D * (1.0D - t), WHITE);
            for (int i = 0; i < 8; ++i) {
                double angle = i * Math.PI * 0.25D + age * 0.02D;
                double x = ox + Math.cos(angle) * r * 0.55D;
                double z = oz + Math.sin(angle) * r * 0.55D;
                drawBeam(buffer, x, baseY, centerY - 8.0D
                        * (0.5D + 0.5D * Math.sin(age * 0.2D + i)), z,
                        0.18D + 0.25D * (1.0D - t), VIOLET);
            }
        } finally {
            fadeScale = 1.0F;
        }
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
        double width = Math.max(0.055D, (outer - inner) * 0.070D);
        // A short sector does not need a full-circle subdivision. Keeping the
        // total angular resolution roughly constant cuts the array geometry by
        // an order of magnitude without visible faceting at radius 20-72.
        int arcSegments = Math.max(8, 192 / Math.max(1, sectors));
        for (int i = 0; i < sectors; ++i) {
            double local = smoothstep(clamp((build - i * 0.055D)
                    / Math.max(0.10D, 1.0D - (sectors - 1) * 0.055D)));
            if (local <= 0.001D) {
                continue;
            }
            double start = rotation + i * sweep + gap * 0.5D;
            double end = rotation + (i + 1) * sweep - gap * 0.5D;
            double actualEnd = start + (end - start) * local;
            double offset = mechanical && (i & 1) == 1 ? (outer - inner) * 0.09D : 0.0D;
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
        if (outer <= inner || segments < 1 || end <= start) {
            return;
        }
        double step = (end - start) / segments;
        float red = color[0] * bright;
        float green = color[1] * bright;
        float blue = color[2] * bright;
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

    private static void drawCircle(BufferBuilder buffer, double cx, double cy, double cz,
                                   double radius, float[] color, float bright) {
        if (radius <= 0.01D) {
            return;
        }
        float red = color[0] * bright;
        float green = color[1] * bright;
        float blue = color[2] * bright;
        for (int i = 0; i < 64; ++i) {
            double a0 = TWO_PI * i / 64;
            double a1 = TWO_PI * (i + 1) / 64;
            triangle(buffer, cx, cy, cz,
                    cx + Math.cos(a0) * radius, cy, cz + Math.sin(a0) * radius,
                    cx + Math.cos(a1) * radius, cy, cz + Math.sin(a1) * radius,
                    red, green, blue);
        }
    }

    private static void drawDiamond(BufferBuilder buffer, double cx, double cy,
                                    double cz, double size, float[] color,
                                    float bright) {
        quad(buffer, cx, cy, cz - size, cx + size, cy, cz,
                cx, cy, cz + size, cx - size, cy, cz,
                color[0] * bright, color[1] * bright, color[2] * bright);
    }

    private static void drawRadial(BufferBuilder buffer, double cx, double cy,
                                   double cz, double angle, double inner,
                                   double outer, double halfWidth,
                                   float[] color, float bright) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double tx = -sin * halfWidth;
        double tz = cos * halfWidth;
        quad(buffer,
                cx + cos * inner + tx, cy, cz + sin * inner + tz,
                cx + cos * outer + tx, cy, cz + sin * outer + tz,
                cx + cos * outer - tx, cy, cz + sin * outer - tz,
                cx + cos * inner - tx, cy, cz + sin * inner - tz,
                color[0] * bright, color[1] * bright, color[2] * bright);
    }

    private static void drawBeam(BufferBuilder buffer, double cx, double bottom,
                                 double top, double cz, double radius,
                                 float[] color) {
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
                    color[0], color[1], color[2]);
        }
    }

    private static void drawSphere(BufferBuilder buffer, double cx, double cy,
                                   double cz, double radius, int lat, int lon,
                                   float[] color) {
        if (radius <= 0.01D) {
            return;
        }
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
                        color[0], color[1], color[2]);
            }
        }
    }

    private static void line3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double width, float[] color, float bright) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        double horizontalLength = Math.sqrt(dx * dx + dz * dz);
        float red = color[0] * bright;
        float green = color[1] * bright;
        float blue = color[2] * bright;
        if (horizontalLength > 0.00001D) {
            double px = -dz / horizontalLength * width;
            double pz = dx / horizontalLength * width;
            quad(buffer,
                    x1 - px, y1, z1 - pz,
                    x1 + px, y1, z1 + pz,
                    x2 + px, y2, z2 + pz,
                    x2 - px, y2, z2 - pz,
                    red, green, blue);
        }
        if (Math.abs(y2 - y1) > 0.00001D) {
            quad(buffer,
                    x1, y1 - width, z1,
                    x1, y1 + width, z1,
                    x2, y2 + width, z2,
                    x2, y2 - width, z2,
                    red, green, blue);
        }
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
        float alpha = fadeScale;
        buffer.pos(x, y, z).tex(0.0D, 0.0D)
                .color(red * alpha, green * alpha, blue * alpha, alpha)
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

    private static double smoothRange(double value, double start, double end) {
        return smoothstep(clamp((value - start) / (end - start)));
    }
}
