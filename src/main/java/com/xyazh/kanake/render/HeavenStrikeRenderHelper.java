package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityHeavenStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fully procedural geometry for the super-tier spell Sky Strike (天击).
 *
 * <p>The array follows the reference design: an empty circular core with a thin
 * purple rim, concentric inner rings with a thick interrupted band, two outer
 * mechanical bands, long cardinal spikes, shorter diagonal spikes, and a fixed
 * eye-shaped rune at the top. Everything is emitted as opaque vertex-coloured
 * quads so the existing framebuffer bloom pass picks up every bright edge and
 * beam without a texture.</p>
 */
@SideOnly(Side.CLIENT)
public final class HeavenStrikeRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 0.95F, 1.0F};
    private static final float[] PALE = {0.86F, 0.66F, 1.0F};
    private static final float[] BRIGHT = {0.64F, 0.14F, 1.0F};
    private static final float[] VIOLET = {0.44F, 0.04F, 0.90F};
    private static final float[] DEEP = {0.16F, 0.005F, 0.30F};
    private static final float[] DARK = {0.055F, 0.0F, 0.12F};
    /**
     * Vertical distance between adjacent array layers. It is deliberately much
     * larger than the old 0.006 offsets: at a 64-block radius the depth buffer
     * cannot distinguish nearly coplanar rings, which produced striped
     * self-occlusion. A visible layer stack removes that artifact entirely.
     */
    private static final double ARRAY_LAYER_STEP = 0.08D;
    /** Alpha multiplier used only while the array dissolves. */
    private static float fadeScale = 1.0F;

    private HeavenStrikeRenderHelper() {
    }

    public static void render(BufferBuilder buffer, double ox, double oy, double oz,
                              double age, double sphereCenterY) {
        double end = endScale(age);
        if (end <= 0.001D) {
            return;
        }
        fadeScale = 1.0F;

        if (age < EntityHeavenStrike.ARRAY_GONE_TICK) {
            renderArray(buffer, ox, oy, oz, age, end);
        }
        if (age >= EntityHeavenStrike.SPHERE_START_TICK
                && age < EntityHeavenStrike.IMPACT_TICK) {
            renderSphereStage(buffer, ox, oy, oz, sphereCenterY, age, end);
        }
        if (age >= EntityHeavenStrike.IMPACT_TICK) {
            renderImpact(buffer, ox, oy, oz, age, end);
        }
    }


    private static void renderArray(BufferBuilder buffer, double ox, double oy, double oz,
                                    double age, double end) {
        double build = smoothstep(clamp(age / (double) EntityHeavenStrike.ARRAY_BUILD_TICKS));
        if (build <= 0.001D) {
            return;
        }
        double fade = age <= EntityHeavenStrike.SPHERE_COMPLETE_TICK ? 1.0D
                : 1.0D - smoothstep(clamp((age - EntityHeavenStrike.SPHERE_COMPLETE_TICK)
                / (double) EntityHeavenStrike.ARRAY_DISSIPATE_TICKS));
        // Avoid emitting almost invisible geometry that would still write depth.
        if (fade <= 0.02D) {
            return;
        }

        double charge = smoothRange(age, EntityHeavenStrike.SPHERE_START_TICK,
                EntityHeavenStrike.SPHERE_COMPLETE_TICK);
        // The array collapses towards its centre while its alpha fades out.
        double scale = (0.05D + 0.95D * build) * (0.72D + 0.28D * fade) * end;
        double r = EntityHeavenStrike.ARRAY_RADIUS * scale;
        if (r < 0.30D) {
            return;
        }
        double cy = oy + EntityHeavenStrike.ARRAY_FLOAT_HEIGHT;
        double spinOuter = age * 0.0036D;
        double spinInner = -age * 0.0062D;
        double spinBand = age * 0.0024D;
        double pulse = 1.0D + 0.006D * Math.sin(age * 0.11D);
        float bright = (float) (0.72D + 0.28D * charge);

        double coreBuild = smoothRange(build, 0.00D, 0.30D);
        double innerBuild = smoothRange(build, 0.10D, 0.44D);
        double thickBuild = smoothRange(build, 0.24D, 0.62D);
        double auxBuild = smoothRange(build, 0.40D, 0.74D);
        double outerBuildA = smoothRange(build, 0.48D, 0.82D);
        double outerBuildB = smoothRange(build, 0.62D, 0.94D);
        double spikeBuild = smoothRange(build, 0.66D, 1.00D);
        double eyeBuild = smoothRange(build, 0.14D, 0.52D);

        fadeScale = (float) fade;
        try {
            drawCoreAndInnerRings(buffer, ox, cy, oz, r, pulse,
                    spinInner, spinBand, coreBuild, innerBuild, thickBuild,
                    auxBuild, bright);
            drawOuterRings(buffer, ox, cy, oz, r, age, spinOuter, outerBuildA,
                    outerBuildB, bright);
            drawSpikes(buffer, ox, cy + ARRAY_LAYER_STEP * 16.0D, oz,
                    r, spikeBuild, bright, charge);
            drawEyeRune(buffer, ox, cy + ARRAY_LAYER_STEP * 17.0D, oz,
                    r, eyeBuild, bright);

            if (charge > 0.01D && outerBuildA > 0.35D) {
                drawChargeDetails(buffer, ox, cy + ARRAY_LAYER_STEP * 18.0D, oz,
                        r, age, charge, bright, fade);
            }
        } finally {
            fadeScale = 1.0F;
        }
    }

    private static void drawCoreAndInnerRings(BufferBuilder buffer,
                                              double ox, double cy, double oz, double r,
                                              double pulse, double spinInner,
                                              double spinBand, double coreBuild,
                                              double innerBuild, double thickBuild,
                                              double auxBuild, float bright) {
        drawArcRing(buffer, ox, cy, oz, r * 0.386D * pulse, r * 0.398D * pulse,
                192, spinInner, spinInner + TWO_PI * coreBuild,
                DEEP[0], DEEP[1], DEEP[2]);
        drawArcRing(buffer, ox, cy + ARRAY_LAYER_STEP, oz,
                r * 0.404D * pulse, r * 0.414D * pulse,
                192, spinInner, spinInner + TWO_PI * coreBuild,
                PALE[0] * bright, PALE[1] * bright, PALE[2] * bright);
        drawArcRing(buffer, ox, cy + ARRAY_LAYER_STEP * 2.0D, oz,
                r * 0.420D * pulse, r * 0.426D * pulse,
                192, -spinInner * 0.7D, -spinInner * 0.7D + TWO_PI * coreBuild,
                BRIGHT[0], BRIGHT[1], BRIGHT[2]);

        drawArcRing(buffer, ox, cy + ARRAY_LAYER_STEP * 3.0D, oz,
                r * 0.466D, r * 0.473D,
                160, spinInner * 1.2D, spinInner * 1.2D + TWO_PI * innerBuild,
                BRIGHT[0], BRIGHT[1], BRIGHT[2]);
        drawSegmentedBand(buffer, ox, cy + ARRAY_LAYER_STEP * 4.0D, oz,
                r * 0.548D, r * 0.652D, spinBand, thickBuild, 4, 0.18D,
                DEEP, BRIGHT, bright, false);
        drawArcRing(buffer, ox, cy + ARRAY_LAYER_STEP * 6.0D, oz,
                r * 0.665D, r * 0.671D,
                160, -spinInner * 0.9D, -spinInner * 0.9D + TWO_PI * thickBuild,
                PALE[0], PALE[1], PALE[2]);
        drawArcRing(buffer, ox, cy + ARRAY_LAYER_STEP * 7.0D, oz,
                r * 0.694D, r * 0.701D,
                160, spinInner * 1.5D, spinInner * 1.5D + TWO_PI * auxBuild,
                BRIGHT[0], BRIGHT[1], BRIGHT[2]);
    }


    private static void drawOuterRings(BufferBuilder buffer, double ox, double cy,
                                       double oz, double r, double age,
                                       double spinOuter, double outerBuildA,
                                       double outerBuildB, float bright) {
        drawSegmentedBand(buffer, ox, cy + ARRAY_LAYER_STEP * 8.0D, oz,
                r * 0.736D, r * 0.812D, spinOuter, outerBuildA, 6, 0.15D,
                DEEP, BRIGHT, bright, true);
        drawSegmentedBand(buffer, ox, cy + ARRAY_LAYER_STEP * 10.0D, oz,
                r * 0.834D, r * 0.914D, -spinOuter * 0.82D, outerBuildB, 8, 0.12D,
                DARK, VIOLET, bright, true);
        drawArcRing(buffer, ox, cy + ARRAY_LAYER_STEP * 12.0D, oz,
                r * 0.924D, r * 0.933D,
                224, spinOuter * 1.25D, spinOuter * 1.25D + TWO_PI * outerBuildB,
                PALE[0], PALE[1], PALE[2]);
        drawOuterMechanics(buffer, ox, cy + ARRAY_LAYER_STEP * 13.0D, oz,
                r, age, outerBuildA, outerBuildB, spinOuter, bright);
    }

    private static void drawSegmentedBand(BufferBuilder buffer,
                                          double cx, double cy, double cz,
                                          double inner, double outer,
                                          double rotation, double build,
                                          int sectors, double gap,
                                          float[] fill, float[] edge,
                                          float bright, boolean mechanical) {
        if (build <= 0.001D || outer <= inner) {
            return;
        }
        double sweep = TWO_PI / sectors;
        double width = Math.max(0.08D, (outer - inner) * 0.075D);
        for (int i = 0; i < sectors; ++i) {
            double local = smoothstep(clamp((build - i * 0.065D)
                    / Math.max(0.10D, 1.0D - (sectors - 1) * 0.065D)));
            if (local <= 0.001D) {
                continue;
            }
            double start = rotation + i * sweep + gap * 0.5D;
            double end = rotation + (i + 1) * sweep - gap * 0.5D;
            double actualEnd = start + (end - start) * local;
            double offset = mechanical && (i & 1) == 1 ? (outer - inner) * 0.10D : 0.0D;
            double bandInner = inner + offset;
            double bandOuter = outer - offset * 0.45D;
            drawArcRing(buffer, cx, cy, cz, bandInner, bandOuter,
                    192, start, actualEnd, fill[0], fill[1], fill[2]);
            drawArcRing(buffer, cx, cy + ARRAY_LAYER_STEP, cz,
                    bandInner, bandInner + width, 192, start, actualEnd,
                    edge[0], edge[1], edge[2]);
            drawArcRing(buffer, cx, cy + ARRAY_LAYER_STEP, cz,
                    bandOuter - width, bandOuter, 192, start, actualEnd,
                    edge[0] * bright, edge[1] * bright, edge[2] * bright);
        }
    }


    private static void drawOuterMechanics(BufferBuilder buffer,
                                          double ox, double cy, double oz,
                                          double r, double age,
                                          double buildA, double buildB,
                                          double spin, float bright) {
        if (buildA <= 0.001D && buildB <= 0.001D) {
            return;
        }
        // Short rectangular teeth crossing the first outer band.
        int teeth = 16;
        for (int i = 0; i < teeth; ++i) {
            if ((i + 1.0D) / teeth > buildA) {
                break;
            }
            double angle = spin * 1.3D + TWO_PI * i / teeth;
            drawRadialBlock(buffer, ox, cy, oz,
                    angle, r * 0.760D, r * 0.804D, 0.019D,
                    DEEP[0], DEEP[1], DEEP[2]);
            drawRadialBlock(buffer, ox, cy + 0.04D, oz,
                    angle, r * 0.774D, r * 0.800D, 0.008D,
                    BRIGHT[0], BRIGHT[1], BRIGHT[2]);
        }

        // Slanted trapezoids on the outer band.
        int traps = 8;
        for (int i = 0; i < traps; ++i) {
            if ((i + 1.0D) / traps > buildB) {
                break;
            }
            double angle = -spin * 0.9D + TWO_PI * i / traps + Math.PI / traps;
            drawTrapezoid(buffer, ox, cy + 0.08D, oz, angle,
                    r * 0.836D, r * 0.918D, 0.042D, 0.018D,
                    DEEP[0], DEEP[1], DEEP[2]);
            drawTrapezoid(buffer, ox, cy + 0.12D, oz, angle,
                    r * 0.902D, r * 0.924D, 0.016D, 0.006D,
                    PALE[0], PALE[1], PALE[2]);
        }

        // Sharp diagonal connectors where a band is interrupted.
        if (buildB > 0.20D) {
            for (int i = 0; i < 4; ++i) {
                double angle = spin + Math.PI * 0.25D + i * Math.PI * 0.5D;
                double x0 = ox + Math.cos(angle) * r * 0.812D;
                double z0 = oz + Math.sin(angle) * r * 0.812D;
                double x1 = ox + Math.cos(angle + 0.09D) * r * 0.925D;
                double z1 = oz + Math.sin(angle + 0.09D) * r * 0.925D;
                line3D(buffer, x0, cy + 0.16D, z0, x1, cy + 0.16D, z1,
                        Math.max(0.08D, r * 0.004D), PALE[0], PALE[1], PALE[2]);
            }
        }

        // Thin radial separators in the inner gap.
        if (buildA > 0.20D) {
            int separators = 12;
            for (int i = 0; i < separators; ++i) {
                double angle = -spin * 1.7D + TWO_PI * i / separators;
                drawRadialBlock(buffer, ox, cy + 0.20D, oz,
                        angle, r * 0.706D, r * 0.732D, 0.010D,
                        BRIGHT[0], BRIGHT[1], BRIGHT[2]);
            }
        }
    }

    private static void drawRadialBlock(BufferBuilder buffer,
                                        double cx, double cy, double cz,
                                        double angle, double innerVal, double outerVal,
                                        double halfAngle,
                                        float red, float green, float blue) {
        double a0 = angle - halfAngle;
        double a1 = angle + halfAngle;
        quad(buffer,
                cx + Math.cos(a0) * innerVal, cy, cz + Math.sin(a0) * innerVal,
                cx + Math.cos(a0) * outerVal, cy, cz + Math.sin(a0) * outerVal,
                cx + Math.cos(a1) * outerVal, cy, cz + Math.sin(a1) * outerVal,
                cx + Math.cos(a1) * innerVal, cy, cz + Math.sin(a1) * innerVal,
                red, green, blue);
    }

    private static void drawTrapezoid(BufferBuilder buffer,
                                      double cx, double cy, double cz,
                                      double angle, double innerVal, double outerVal,
                                      double innerHalf, double outerHalf,
                                      float red, float green, float blue) {
        double a0 = angle - innerHalf;
        double a1 = angle + innerHalf;
        double b0 = angle - outerHalf;
        double b1 = angle + outerHalf;
        quad(buffer,
                cx + Math.cos(a0) * innerVal, cy, cz + Math.sin(a0) * innerVal,
                cx + Math.cos(b0) * outerVal, cy, cz + Math.sin(b0) * outerVal,
                cx + Math.cos(b1) * outerVal, cy, cz + Math.sin(b1) * outerVal,
                cx + Math.cos(a1) * innerVal, cy, cz + Math.sin(a1) * innerVal,
                red, green, blue);
    }


    private static void drawSpikes(BufferBuilder buffer, double cx, double cy, double cz,
                                   double r, double build, float bright, double charge) {
        if (build <= 0.001D) {
            return;
        }
        float edge = (float) (0.65D + 0.35D * charge);
        // Four cardinal spikes: the longest in the design.
        double[] cardinal = {-Math.PI * 0.5D, 0.0D, Math.PI * 0.5D, Math.PI};
        for (double angle : cardinal) {
            drawSpike(buffer, cx, cy, cz, angle, r * 0.910D, r * 0.340D,
                    r * 0.030D, build, bright, edge, true);
        }
        // Four secondary diagonal spikes.
        double[] diagonal = {-Math.PI * 0.75D, -Math.PI * 0.25D,
                Math.PI * 0.25D, Math.PI * 0.75D};
        for (double angle : diagonal) {
            drawSpike(buffer, cx, cy, cz, angle, r * 0.922D, r * 0.220D,
                    r * 0.021D, build, bright, edge, false);
        }
        // Eight medium rim spikes between the main directions.
        for (int i = 0; i < 8; ++i) {
            double angle = Math.PI * 0.125D + i * Math.PI * 0.25D;
            drawSpike(buffer, cx, cy, cz, angle, r * 0.934D, r * 0.130D,
                    r * 0.014D, build, bright, edge, false);
        }
        // Sixteen small rim needles.
        for (int i = 0; i < 16; ++i) {
            double angle = Math.PI * 0.0625D + i * Math.PI * 0.125D;
            drawSpike(buffer, cx, cy, cz, angle, r * 0.940D, r * 0.064D,
                    r * 0.008D, build, bright, edge, false);
        }
    }

    private static void drawSpike(BufferBuilder buffer,
                                  double cx, double cy, double cz,
                                  double angle, double rootRadius,
                                  double length, double halfWidth,
                                  double build, float bright, float edge,
                                  boolean extraLayer) {
        if (build <= 0.001D) {
            return;
        }
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double tx = -sin;
        double tz = cos;
        double tip = rootRadius + length * build;
        double rx = cx + cos * rootRadius;
        double rz = cz + sin * rootRadius;
        double px = cx + cos * tip;
        double pz = cz + sin * tip;
        float r = DEEP[0] * bright;
        float g = DEEP[1] * bright;
        float b = DEEP[2] * bright;
        triangle(buffer,
                rx + tx * halfWidth, cy, rz + tz * halfWidth,
                rx - tx * halfWidth, cy, rz - tz * halfWidth,
                px, cy, pz, r, g, b);

        double innerWidth = halfWidth * 0.32D;
        double innerTip = rootRadius + length * build * 0.94D;
        triangle(buffer,
                rx + tx * innerWidth, cy + 0.06D, rz + tz * innerWidth,
                rx - tx * innerWidth, cy + 0.06D, rz - tz * innerWidth,
                cx + cos * innerTip, cy + 0.06D, cz + sin * innerTip,
                BRIGHT[0] * bright, BRIGHT[1] * bright, BRIGHT[2] * bright);

        line3D(buffer, rx, cy + 0.12D, rz,
                cx + cos * innerTip, cy + 0.12D, cz + sin * innerTip,
                Math.max(0.035D, halfWidth * 0.18D),
                PALE[0] * edge, PALE[1] * edge, PALE[2] * edge);

        if (extraLayer) {
            double sideOffset = halfWidth * 1.10D;
            double sideTip = rootRadius + length * build * 0.55D;
            triangle(buffer,
                    rx + tx * sideOffset, cy + 0.03D, rz + tz * sideOffset,
                    rx + tx * halfWidth * 0.28D, cy + 0.03D,
                    rz + tz * halfWidth * 0.28D,
                    cx + cos * sideTip + tx * sideOffset * 0.24D, cy + 0.03D,
                    cz + sin * sideTip + tz * sideOffset * 0.24D,
                    VIOLET[0] * bright, VIOLET[1] * bright, VIOLET[2] * bright);
            triangle(buffer,
                    rx - tx * sideOffset, cy + 0.03D, rz - tz * sideOffset,
                    rx - tx * halfWidth * 0.28D, cy + 0.03D,
                    rz - tz * halfWidth * 0.28D,
                    cx + cos * sideTip - tx * sideOffset * 0.24D, cy + 0.03D,
                    cz + sin * sideTip - tz * sideOffset * 0.24D,
                    VIOLET[0] * bright, VIOLET[1] * bright, VIOLET[2] * bright);
        }
    }


    private static void drawEyeRune(BufferBuilder buffer,
                                    double cx, double cy, double cz,
                                    double r, double build, float bright) {
        if (build <= 0.001D) {
            return;
        }
        double ex = cx;
        double ez = cz - r * 0.345D;
        double s = r * 0.052D;
        double width = Math.max(0.05D, s * 0.13D);
        float eye = 0.80F * bright;
        float glow = 1.00F * bright;

        // Only the upper eyelid remains. A cusp at the centre and pointed ends
        // make the whole rune read as a sharp eye instead of a rounded oval.
        int steps = 6;
        double prevX = 0.0D;
        double prevZ = 0.0D;
        for (int i = 0; i <= steps; ++i) {
            double t = -1.0D + 2.0D * i / steps;
            double x = ex + t * 1.45D * s;
            double height = 0.72D * s
                    * Math.pow(Math.max(0.0D, 1.0D - Math.abs(t)), 0.62D);
            double z = ez - height * (1.0D + 0.08D * t);
            double part = clamp(build * steps - (i - 1));
            if (i > 0 && part > 0.001D) {
                double px = prevX + (x - prevX) * part;
                double pz = prevZ + (z - prevZ) * part;
                line3D(buffer, prevX, cy, prevZ, px, cy, pz,
                        width, PALE[0] * glow, PALE[1] * glow, PALE[2] * glow);
            }
            prevX = x;
            prevZ = z;
        }

        // Sharp outward horns at both corners, upper half only.
        drawSharpHorn(buffer, ex - 1.45D * s, cy, ez, -1.0D, s, build, eye);
        drawSharpHorn(buffer, ex + 1.45D * s, cy, ez, 1.0D, s, build, eye);

        // Diamond core. Each nested face has a clear vertical offset so it
        // cannot z-fight at long range.
        drawDiamond(buffer, ex, cy + 0.06D, ez, s * 0.38D,
                BRIGHT[0], BRIGHT[1], BRIGHT[2], build);
        drawDiamond(buffer, ex, cy + 0.12D, ez, s * 0.20D,
                DARK[0], DARK[1], DARK[2], build);
        drawDiamond(buffer, ex, cy + 0.18D, ez, s * 0.08D,
                WHITE[0], WHITE[1], WHITE[2], build);

        // Directional aura: a short sharp slit above the diamond.
        line3D(buffer, ex, cy + 0.20D, ez - s * 0.58D,
                ex, cy + 0.20D, ez - s * 0.92D,
                Math.max(0.04D, s * 0.07D), PALE[0], PALE[1], PALE[2]);
    }

    private static void drawSharpHorn(BufferBuilder buffer,
                                      double x, double cy, double z,
                                      double side, double s,
                                      double build, float intensity) {
        double width = Math.max(0.04D, s * 0.10D);
        double x1 = x + side * 0.34D * s;
        double z1 = z - 0.30D * s;
        double x2 = x + side * 0.68D * s;
        double z2 = z - 0.08D * s;

        double first = smoothstep(clamp((build - 0.25D) / 0.35D));
        if (first > 0.001D) {
            double tx = x + (x1 - x) * first;
            double tz = z + (z1 - z) * first;
            line3D(buffer, x, cy, z, tx, cy, tz, width,
                    VIOLET[0] * intensity, VIOLET[1] * intensity,
                    VIOLET[2] * intensity);
        }
        double second = smoothstep(clamp((build - 0.60D) / 0.25D));
        if (second > 0.001D) {
            double tx = x1 + (x2 - x1) * second;
            double tz = z1 + (z2 - z1) * second;
            line3D(buffer, x1, cy, z1, tx, cy, tz, width * 0.70D,
                    PALE[0] * intensity, PALE[1] * intensity,
                    PALE[2] * intensity);
        }
    }

    private static void drawDiamond(BufferBuilder buffer,
                                    double cx, double cy, double cz,
                                    double size, float red, float green, float blue,
                                    double build) {
        if (build <= 0.001D) {
            return;
        }
        double s = size * smoothstep(build);
        quad(buffer,
                cx, cy, cz - s,
                cx + s, cy, cz,
                cx, cy, cz + s,
                cx - s, cy, cz,
                red, green, blue);
    }


    private static void drawChargeDetails(BufferBuilder buffer,
                                          double ox, double cy, double oz,
                                          double r, double age, double charge,
                                          float bright, double fade) {
        // Moving bright arcs travelling around three rings.
        double[] radii = {0.522D, 0.724D, 0.872D};
        for (int ring = 0; ring < radii.length; ++ring) {
            for (int i = 0; i < 3; ++i) {
                double flow = (age * 0.018D + i / 3.0D + ring * 0.17D) % 1.0D;
                double start = flow * TWO_PI;
                double length = 0.20D + 0.24D * charge;
                float rr = ring == 1 ? PALE[0] : WHITE[0];
                float gg = ring == 1 ? PALE[1] : WHITE[1];
                float bb = ring == 1 ? PALE[2] : WHITE[2];
                drawArcRing(buffer, ox, cy + ring * 0.03D, oz,
                        r * (radii[ring] - 0.006D), r * (radii[ring] + 0.006D),
                        48, start, start + length, rr, gg, bb);
            }
        }

        // Small runes along the thick band.
        int runes = 20;
        for (int i = 0; i < runes; ++i) {
            double angle = -age * 0.004D + TWO_PI * i / runes;
            double rr = r * 0.600D;
            double x = ox + Math.cos(angle) * rr;
            double z = oz + Math.sin(angle) * rr;
            double tx = -Math.sin(angle);
            double tz = Math.cos(angle);
            double size = Math.max(0.18D, r * 0.012D);
            line3D(buffer, x - tx * size, cy + 0.10D, z - tz * size,
                    x + tx * size, cy + 0.10D, z + tz * size,
                    size * 0.25D, PALE[0], PALE[1], PALE[2]);
            line3D(buffer, x, cy + 0.12D, z,
                    x + Math.cos(angle) * size * 1.3D, cy + 0.12D,
                    z + Math.sin(angle) * size * 1.3D,
                    size * 0.22D, WHITE[0], WHITE[1], WHITE[2]);
        }

        // Charge corona: short radial rays around the rim.
        int rays = 16;
        for (int i = 0; i < rays; ++i) {
            double angle = age * 0.009D + TWO_PI * i / rays;
            double innerVal = r * 0.934D;
            double outerVal = r * (0.960D + 0.026D * charge
                    * (0.5D + 0.5D * Math.sin(age * 0.3D + i * 1.7D)));
            drawRadialBlock(buffer, ox, cy + 0.16D, oz,
                    angle, innerVal, outerVal, 0.006D,
                    PALE[0] * bright, PALE[1] * bright, PALE[2] * bright);
        }
    }


    private static void renderSphereStage(BufferBuilder buffer,
                                          double ox, double oy, double oz,
                                          double sphereCenterY,
                                          double age, double end) {
        double grow = smoothRange(age, EntityHeavenStrike.SPHERE_START_TICK,
                EntityHeavenStrike.SPHERE_COMPLETE_TICK);
        double radius = EntityHeavenStrike.getSphereRadiusAt(age) * end;
        if (radius < 0.35D) {
            return;
        }

        boolean falling = age >= EntityHeavenStrike.SPHERE_COMPLETE_TICK;
        double fall = falling ? clamp((age - EntityHeavenStrike.SPHERE_COMPLETE_TICK)
                / (double) EntityHeavenStrike.DESCENT_TICKS) : 0.0D;
        double fallEase = fall * fall;
        double xzScale = 1.0D;
        double yScale = 1.0D;
        double centerY = sphereCenterY;
        if (falling) {
            double stretch = smoothstep(fall);
            xzScale = 1.0D - 0.22D * stretch;
            yScale = 1.0D + 0.68D * stretch;
            centerY = sphereCenterY + ((oy + 8.0D) - sphereCenterY) * fallEase;
        }
        double rx = radius * xzScale;
        double ry = radius * yScale;

        // Bright shock ring emitted at the instant the array dissolves.
        if (age >= EntityHeavenStrike.SPHERE_COMPLETE_TICK
                && age < EntityHeavenStrike.ARRAY_GONE_TICK + 16.0D) {
            double burst = clamp((age - EntityHeavenStrike.SPHERE_COMPLETE_TICK)
                    / (double) (EntityHeavenStrike.ARRAY_DISSIPATE_TICKS + 16));
            double burstRadius = EntityHeavenStrike.ARRAY_RADIUS
                    * (0.90D + 0.62D * burst);
            float burstAlpha = (float) (1.0D - smoothstep(burst));
            if (burstAlpha > 0.02F) {
                fadeScale = burstAlpha;
                try {
                    double burstY = oy + EntityHeavenStrike.ARRAY_FLOAT_HEIGHT
                            + ARRAY_LAYER_STEP * 15.0D;
                    drawArcRing(buffer, ox, burstY, oz,
                            Math.max(0.10D, burstRadius - 1.1D), burstRadius,
                            224, 0.0D, TWO_PI,
                            WHITE[0], WHITE[1], WHITE[2]);
                    drawArcRing(buffer, ox, burstY + ARRAY_LAYER_STEP, oz,
                            burstRadius * 0.90D, burstRadius * 0.915D,
                            192, -age * 0.05D, -age * 0.05D + TWO_PI,
                            PALE[0], PALE[1], PALE[2]);
                } finally {
                    fadeScale = 1.0F;
                }
            }
        }

        drawPlasmaSphere(buffer, ox, centerY, oz, rx, ry, rx, age, grow);
        drawSphereBands(buffer, ox, centerY, oz, rx, ry, rx, age, grow);
        drawOrbitRings(buffer, ox, centerY, oz, rx, ry, rx, age, grow, falling);

        if (!falling) {
            drawArrayLink(buffer, ox, oy, oz, centerY - ry, radius, age, grow);
        } else {
            drawFallEffects(buffer, ox, oy, oz, centerY, ry, rx, fall, age);
        }
    }

    private static void drawPlasmaSphere(BufferBuilder buffer,
                                         double cx, double cy, double cz,
                                         double rx, double ry, double rz,
                                         double age, double grow) {
        int lat = 20 + (int) Math.round(6.0D * grow);
        int lon = 36 + (int) Math.round(12.0D * grow);
        double phase = 0.37D;

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

                double w00 = plasmaWave(t0, p0, age, phase);
                double w01 = plasmaWave(t0, p1, age, phase);
                double w11 = plasmaWave(t1, p1, age, phase);
                double w10 = plasmaWave(t1, p0, age, phase);

                double x0 = cx + c0 * sin0 * rx * w00;
                double y0 = cy + cos0 * ry * w00;
                double z0 = cz + s0 * sin0 * rz * w00;
                double x1 = cx + c1 * sin0 * rx * w01;
                double y1 = cy + cos0 * ry * w01;
                double z1 = cz + s1 * sin0 * rz * w01;
                double x2 = cx + c1 * sin1 * rx * w11;
                double y2 = cy + cos1 * ry * w11;
                double z2 = cz + s1 * sin1 * rz * w11;
                double x3 = cx + c0 * sin1 * rx * w10;
                double y3 = cy + cos1 * ry * w10;
                double z3 = cz + s0 * sin1 * rz * w10;

                double theta = (t0 + t1) * 0.5D;
                double phi = (p0 + p1) * 0.5D;
                double band = 0.5D + 0.5D * Math.sin(theta * 7.0D - age * 0.30D
                        + Math.sin(phi * 4.0D + age * 0.11D) * 1.7D);
                double shade = 0.16D + 0.44D * band
                        + 0.18D * (1.0D - theta / Math.PI) + 0.18D * grow;
                float red = mix(DARK[0], BRIGHT[0], (float) clamp(shade));
                float green = mix(DARK[1], BRIGHT[1], (float) clamp(shade));
                float blue = mix(DARK[2], BRIGHT[2], (float) clamp(shade));

                double vein = 0.5D + 0.5D * Math.sin(phi * 9.0D
                        + theta * 4.0D + age * 0.52D);
                if (vein > 0.92D) {
                    float t = (float) clamp((vein - 0.92D) / 0.08D) * 0.55F;
                    red = mix(red, WHITE[0], t);
                    green = mix(green, WHITE[1], t);
                    blue = mix(blue, WHITE[2], t);
                }
                quad(buffer, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3,
                        red, green, blue);
            }
        }
    }

    private static double plasmaWave(double theta, double phi, double age, double phase) {
        return 1.0D
                + 0.030D * Math.sin(theta * 5.0D + age * 0.17D + phase)
                + 0.020D * Math.sin(phi * 7.0D - age * 0.23D + theta * 2.5D)
                + 0.012D * Math.sin((phi + theta) * 11.0D + age * 0.41D);
    }


    private static void drawSphereBands(BufferBuilder buffer,
                                        double cx, double cy, double cz,
                                        double rx, double ry, double rz,
                                        double age, double grow) {
        float b = (float) (0.45D + 0.55D * grow);
        double width = Math.max(0.20D, rx * 0.006D);

        // Latitudes: moving energy bands that follow the rippling surface so
        // they are never swallowed by the sphere's bumps.
        int latitudes = 7;
        for (int i = 1; i < latitudes; ++i) {
            double theta = Math.PI * i / latitudes;
            double sin = Math.sin(theta);
            if (sin * rx < 0.25D) {
                continue;
            }
            double drift = age * (0.020D + i * 0.0018D) * (i % 2 == 0 ? 1.0D : -1.0D);
            drawSphereLatitude(buffer, cx, cy, cz, rx, ry, rz,
                    theta, age, drift, width, PALE, b, i);
        }

        // Meridians: six great circles that make the ball read as a sphere.
        int meridians = 6;
        int steps = 30;
        for (int m = 0; m < meridians; ++m) {
            double phi = age * 0.026D + Math.PI * m / meridians;
            double prevX = 0.0D;
            double prevY = 0.0D;
            double prevZ = 0.0D;
            for (int i = 0; i <= steps; ++i) {
                double theta = Math.PI * i / steps;
                double sin = Math.sin(theta);
                double wave = plasmaWave(theta, phi, age, 0.37D);
                double x = cx + Math.cos(phi) * sin * rx * wave * 1.008D;
                double y = cy + Math.cos(theta) * ry * wave * 1.008D;
                double z = cz + Math.sin(phi) * sin * rz * wave * 1.008D;
                if (i > 0) {
                    line3D(buffer, prevX, prevY, prevZ, x, y, z,
                            Math.max(0.12D, rx * 0.0035D),
                            BRIGHT[0] * b, BRIGHT[1] * b, BRIGHT[2] * b);
                }
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
    }

    private static void drawSphereLatitude(BufferBuilder buffer,
                                           double cx, double cy, double cz,
                                           double rx, double ry, double rz,
                                           double theta, double age, double drift,
                                           double width, float[] color, float bright,
                                           int index) {
        int segments = 96;
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);
        double prevX = 0.0D;
        double prevY = 0.0D;
        double prevZ = 0.0D;
        for (int i = 0; i <= segments; ++i) {
            double phi = drift + TWO_PI * i / segments;
            double wave = plasmaWave(theta, phi, age, 0.37D) * 1.012D;
            double x = cx + Math.cos(phi) * sin * rx * wave;
            double y = cy + cos * ry * wave;
            double z = cz + Math.sin(phi) * sin * rz * wave;
            if (i > 0) {
                boolean accent = (i + index * 3) % 9 == 0;
                line3D(buffer, prevX, prevY, prevZ, x, y, z,
                        accent ? width * 1.6D : width,
                        accent ? WHITE[0] : color[0] * bright,
                        accent ? WHITE[1] : color[1] * bright,
                        accent ? WHITE[2] : color[2] * bright);
            }
            prevX = x;
            prevY = y;
            prevZ = z;
        }
    }

    private static void drawOrbitRings(BufferBuilder buffer,
                                       double cx, double cy, double cz,
                                       double rx, double ry, double rz,
                                       double age, double grow, boolean falling) {
        float b = (float) (0.55D + 0.45D * grow);
        double width = Math.max(0.22D, rx * 0.007D);
        double spin = age * 0.018D;
        double stretch = falling ? 1.0D : 1.0D;

        double[] horizontalU = {1.0D, 0.0D, 0.0D};
        double[] horizontalV = {0.0D, 0.0D, 1.0D};
        drawRing3D(buffer, cx, cy, cz, rx, ry, rz,
                horizontalU, horizontalV, 1.10D, width,
                spin * 1.3D, PALE[0] * b, PALE[1] * b, PALE[2] * b);

        double[] verticalU = {Math.cos(spin), 0.0D, Math.sin(spin)};
        double[] verticalV = {0.0D, 1.0D, 0.0D};
        drawRing3D(buffer, cx, cy, cz, rx, ry, rz,
                verticalU, verticalV, 1.080D * stretch, width * 0.85D,
                -spin * 1.8D, BRIGHT[0] * b, BRIGHT[1] * b, BRIGHT[2] * b);

        double[] crossU = {Math.cos(spin + Math.PI * 0.5D), 0.0D,
                Math.sin(spin + Math.PI * 0.5D)};
        drawRing3D(buffer, cx, cy, cz, rx, ry, rz,
                crossU, verticalV, 1.110D * stretch, width * 0.70D,
                spin * 2.2D, WHITE[0] * 0.8F * b, WHITE[1] * 0.8F * b,
                WHITE[2] * 0.8F * b);
    }

    private static void drawRing3D(BufferBuilder buffer,
                                   double cx, double cy, double cz,
                                   double rx, double ry, double rz,
                                   double[] u, double[] v,
                                   double radiusScale, double width,
                                   double rotation, float red, float green, float blue) {
        int segments = 72;
        for (int i = 0; i < segments; ++i) {
            double a0 = rotation + TWO_PI * i / segments;
            double a1 = rotation + TWO_PI * (i + 1) / segments;
            double c0 = Math.cos(a0);
            double s0 = Math.sin(a0);
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            double x0 = cx + (u[0] * c0 + v[0] * s0) * rx * radiusScale;
            double y0 = cy + (u[1] * c0 + v[1] * s0) * ry * radiusScale;
            double z0 = cz + (u[2] * c0 + v[2] * s0) * rz * radiusScale;
            double x1 = cx + (u[0] * c1 + v[0] * s1) * rx * radiusScale;
            double y1 = cy + (u[1] * c1 + v[1] * s1) * ry * radiusScale;
            double z1 = cz + (u[2] * c1 + v[2] * s1) * rz * radiusScale;
            boolean bright = i % 8 == 0;
            line3D(buffer, x0, y0, z0, x1, y1, z1,
                    bright ? width * 1.7D : width,
                    bright ? WHITE[0] : red,
                    bright ? WHITE[1] : green,
                    bright ? WHITE[2] : blue);
        }
    }


    private static void drawArrayLink(BufferBuilder buffer,
                                      double ox, double oy, double oz,
                                      double sphereBottom, double radius,
                                      double age, double grow) {
        double startY = oy + EntityHeavenStrike.ARRAY_FLOAT_HEIGHT + 0.10D;
        if (sphereBottom > startY + 0.5D) {
            double beamRadius = Math.max(0.55D, radius * 0.030D) * (0.40D + 0.60D * grow);
            drawVerticalBeam(buffer, ox, startY, sphereBottom, oz, beamRadius, age,
                    BRIGHT, PALE);

            int helices = 3;
            int steps = 42;
            for (int h = 0; h < helices; ++h) {
                double prevX = 0.0D;
                double prevY = 0.0D;
                double prevZ = 0.0D;
                for (int i = 0; i <= steps; ++i) {
                    double t = i / (double) steps;
                    double y = startY + (sphereBottom - startY) * t;
                    double angle = age * 0.045D + h * TWO_PI / helices
                            + t * TWO_PI * 4.0D;
                    double x = ox + Math.cos(angle) * beamRadius * 1.8D;
                    double z = oz + Math.sin(angle) * beamRadius * 1.8D;
                    if (i > 0) {
                        line3D(buffer, prevX, prevY, prevZ, x, y, z,
                                Math.max(0.10D, beamRadius * 0.14D),
                                h == 1 ? PALE[0] : WHITE[0],
                                h == 1 ? PALE[1] : WHITE[1],
                                h == 1 ? PALE[2] : WHITE[2]);
                    }
                    prevX = x;
                    prevY = y;
                    prevZ = z;
                }
            }

            int rings = 9;
            for (int i = 1; i <= rings; ++i) {
                double t = i / (double) (rings + 1);
                double y = startY + (sphereBottom - startY) * t;
                double pulse = 1.0D + 0.10D * Math.sin(age * 0.28D + i * 1.3D);
                drawArcRing(buffer, ox, y, oz,
                        beamRadius * (1.25D * pulse - 0.08D),
                        beamRadius * (1.25D * pulse + 0.08D),
                        36, age * 0.20D + i, age * 0.20D + i + TWO_PI,
                        PALE[0], PALE[1], PALE[2]);
            }
        }

        if (age >= EntityHeavenStrike.ARRAY_GONE_TICK) {
            return;
        }
        double arrayBuild = smoothstep(clamp(age
                / (double) EntityHeavenStrike.ARRAY_BUILD_TICKS));
        double arrayFade = age <= EntityHeavenStrike.SPHERE_COMPLETE_TICK ? 1.0D
                : 1.0D - smoothstep(clamp((age - EntityHeavenStrike.SPHERE_COMPLETE_TICK)
                / (double) EntityHeavenStrike.ARRAY_DISSIPATE_TICKS));
        double arrayScale = (0.05D + 0.95D * arrayBuild)
                * (0.72D + 0.28D * arrayFade);
        double rayRadius = EntityHeavenStrike.ARRAY_RADIUS * 0.72D * arrayScale;
        // Rising energy rays converge from the array edge into the sphere.
        int rays = 18;
        for (int i = 0; i < rays; ++i) {
            double angle = age * 0.006D + TWO_PI * i / rays;
            double sx = ox + Math.cos(angle) * rayRadius;
            double sz = oz + Math.sin(angle) * rayRadius;
            // Start above the outer bands; otherwise the lowest part of every
            // ray is hidden by the stacked array layers.
            double sy = oy + EntityHeavenStrike.ARRAY_FLOAT_HEIGHT
                    + ARRAY_LAYER_STEP * 15.0D;
            for (int dash = 0; dash < 2; ++dash) {
                double flow = (age * 0.014D + i * 0.127D + dash * 0.5D) % 1.0D;
                double t0 = flow;
                double t1 = Math.min(1.0D, flow + 0.16D);
                if (t1 <= t0) {
                    continue;
                }
                double x0 = sx + (ox - sx) * t0;
                double y0 = sy + (sphereBottom - sy) * t0;
                double z0 = sz + (oz - sz) * t0;
                double x1 = sx + (ox - sx) * t1;
                double y1 = sy + (sphereBottom - sy) * t1;
                double z1 = sz + (oz - sz) * t1;
                line3D(buffer, x0, y0, z0, x1, y1, z1,
                        Math.max(0.07D, radius * 0.0045D),
                        (i % 3 == 0) ? WHITE[0] : BRIGHT[0],
                        (i % 3 == 0) ? WHITE[1] : BRIGHT[1],
                        (i % 3 == 0) ? WHITE[2] : BRIGHT[2]);
            }
        }
    }

    private static void drawFallEffects(BufferBuilder buffer,
                                        double ox, double oy, double oz,
                                        double centerY, double ry,
                                        double rx, double fall, double age) {
        double trail = 70.0D * fall;
        int streaks = 18;
        for (int i = 0; i < streaks; ++i) {
            double angle = TWO_PI * i / streaks + age * 0.010D;
            double x = ox + Math.cos(angle) * rx * 0.72D;
            double z = oz + Math.sin(angle) * rx * 0.72D;
            double y = centerY + ry * 0.35D;
            line3D(buffer, x, y, z, x, y + trail, z,
                    Math.max(0.12D, rx * 0.004D),
                    PALE[0], PALE[1], PALE[2]);
        }

        int downward = 16;
        for (int i = 0; i < downward; ++i) {
            double angle = TWO_PI * i / downward - age * 0.016D;
            double x = ox + Math.cos(angle) * rx * 0.55D;
            double z = oz + Math.sin(angle) * rx * 0.55D;
            double y0 = centerY + ry * 0.15D;
            double y1 = oy + 0.3D;
            double flow = (age * 0.020D + i * 0.11D) % 1.0D;
            double t0 = Math.min(0.9D, flow);
            double t1 = Math.min(1.0D, t0 + 0.20D);
            line3D(buffer, x, y0 + (y1 - y0) * t0, z,
                    x, y0 + (y1 - y0) * t1, z,
                    Math.max(0.10D, rx * 0.0035D),
                    (i % 4 == 0) ? WHITE[0] : BRIGHT[0],
                    (i % 4 == 0) ? WHITE[1] : BRIGHT[1],
                    (i % 4 == 0) ? WHITE[2] : BRIGHT[2]);
        }

        double targetRadius = EntityHeavenStrike.DAMAGE_RADIUS * (0.20D + 0.80D * fall);
        drawArcRing(buffer, ox, oy + 0.12D, oz,
                Math.max(0.10D, targetRadius - 0.55D), targetRadius,
                128, -age * 0.03D, -age * 0.03D + TWO_PI,
                WHITE[0], WHITE[1], WHITE[2]);
        drawArcRing(buffer, ox, oy + 0.15D, oz,
                targetRadius * 0.92D, targetRadius * 0.96D,
                96, age * 0.05D, age * 0.05D + TWO_PI,
                PALE[0], PALE[1], PALE[2]);
    }


    private static void renderImpact(BufferBuilder buffer,
                                     double ox, double oy, double oz,
                                     double age, double end) {
        double t = age - EntityHeavenStrike.IMPACT_TICK;
        double rim = EntityHeavenStrike.DAMAGE_RADIUS;

        if (t < 18.0D) {
            double flash = smoothstep(clamp(t / 8.0D));
            double radius = (64.0D + 72.0D * flash) * end;
            double cy = oy + 10.0D + 20.0D * flash;
            float flashAlpha = (float) (1.0D - smoothstep(clamp((t - 10.0D) / 8.0D)));
            if (flashAlpha > 0.02F) {
                fadeScale = flashAlpha;
                try {
                    drawSolidSphere(buffer, ox, cy, oz, radius, 20, 40,
                            VIOLET[0], VIOLET[1], VIOLET[2]);
                    double length = Math.sqrt(ox * ox + cy * cy + oz * oz);
                    double nx = length > 0.0001D ? -ox / length : 0.0D;
                    double ny = length > 0.0001D ? -cy / length : 0.0D;
                    double nz = length > 0.0001D ? -oz / length : 0.0D;
                    drawSolidSphere(buffer, ox + nx * radius * 0.34D,
                            cy + ny * radius * 0.34D, oz + nz * radius * 0.34D,
                            radius * 0.72D, 16, 32,
                            PALE[0], PALE[1], PALE[2]);
                    drawSolidSphere(buffer, ox + nx * radius * 0.66D,
                            cy + ny * radius * 0.66D, oz + nz * radius * 0.66D,
                            radius * 0.42D, 12, 24,
                            WHITE[0], WHITE[1], WHITE[2]);
                } finally {
                    fadeScale = 1.0F;
                }
            }
        }

        if (t < 62.0D) {
            double shock = smoothstep(clamp(t / 38.0D));
            double edge = rim * (0.15D + 1.35D * shock);
            float shockAlpha = (float) (1.0D - smoothstep(clamp((t - 38.0D) / 24.0D)));
            if (shockAlpha < 0.02F) {
                shockAlpha = 0.02F;
            }
            fadeScale = shockAlpha;
            try {
                drawArcRing(buffer, ox, oy + 0.20D, oz,
                        Math.max(0.10D, edge - 1.2D), edge,
                        224, 0.0D, TWO_PI, WHITE[0], WHITE[1], WHITE[2]);
                drawArcRing(buffer, ox, oy + 0.28D, oz,
                        edge * 0.86D, edge * 0.90D,
                        192, -age * 0.04D, -age * 0.04D + TWO_PI,
                        PALE[0], PALE[1], PALE[2]);
                drawArcRing(buffer, ox, oy + 0.36D, oz,
                        edge * 0.53D, edge * 0.57D,
                        160, age * 0.06D, age * 0.06D + TWO_PI,
                        BRIGHT[0], BRIGHT[1], BRIGHT[2]);
            } finally {
                fadeScale = 1.0F;
            }
        }

        if (t < 110.0D) {
            double fade = 1.0D - smoothstep(clamp(t / 110.0D));
            double height = 165.0D * (1.0D - smoothstep(clamp(t / 95.0D)));
            if (height > 1.0D) {
                float a = (float) (fade * end);
                fadeScale = a;
                try {
                    drawVerticalBeam(buffer, ox, oy + 0.4D, oy + height, oz,
                            2.0D + 7.0D * fade, age, BRIGHT, PALE);
                    for (int i = 0; i < 6; ++i) {
                        double angle = TWO_PI * i / 6.0D + age * 0.015D;
                        double x = ox + Math.cos(angle) * rim * 0.45D;
                        double z = oz + Math.sin(angle) * rim * 0.45D;
                        drawVerticalBeam(buffer, x, oy + 0.4D,
                                oy + height * 0.72D, z, 0.7D + 1.8D * fade,
                                age, PALE, WHITE);
                    }
                    // A bright cap on top of the eruption column.
                    drawArcRing(buffer, ox, oy + height, oz,
                            Math.max(0.5D, 6.0D * fade), 8.5D * fade,
                            96, age * 0.03D, age * 0.03D + TWO_PI,
                            WHITE[0], WHITE[1], WHITE[2]);
                } finally {
                    fadeScale = 1.0F;
                }
            }
        }
    }


    private static void drawArcRing(BufferBuilder buffer,
                                    double cx, double cy, double cz,
                                    double inner, double outer, int segments,
                                    double start, double end,
                                    float red, float green, float blue) {
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
                    cx + c0 * inner, cy, cz + s0 * inner,
                    cx + c0 * outer, cy, cz + s0 * outer,
                    cx + c1 * outer, cy, cz + s1 * outer,
                    cx + c1 * inner, cy, cz + s1 * inner,
                    red, green, blue);
        }
    }

    private static void drawVerticalBeam(BufferBuilder buffer,
                                         double cx, double bottom, double top,
                                         double cz, double radius,
                                         double age, float[] outer, float[] inner) {
        if (top <= bottom + 0.05D || radius <= 0.01D) {
            return;
        }
        int segments = 32;
        for (int i = 0; i < segments; ++i) {
            double a0 = TWO_PI * i / segments;
            double a1 = TWO_PI * (i + 1) / segments;
            float wave = (float) (0.45D + 0.55D * (0.5D + 0.5D
                    * Math.sin(a0 * 3.0D + age * 0.35D)));
            cylinderSide(buffer, cx, cz, bottom, top, radius, a0, a1,
                    mix(outer[0], 1.0F, wave * 0.30F),
                    mix(outer[1], 1.0F, wave * 0.30F),
                    mix(outer[2], 1.0F, wave * 0.30F));
        }

        // Bright inner core.
        for (int i = 0; i < 16; ++i) {
            double a0 = TWO_PI * i / 16.0D;
            double a1 = TWO_PI * (i + 1) / 16.0D;
            cylinderSide(buffer, cx, cz, bottom, top, radius * 0.38D,
                    a0, a1, inner[0], inner[1], inner[2]);
        }

        // Two helical filaments around the column.
        double prevX = 0.0D;
        double prevY = 0.0D;
        double prevZ = 0.0D;
        int steps = 36;
        for (int h = 0; h < 2; ++h) {
            for (int i = 0; i <= steps; ++i) {
                double f = i / (double) steps;
                double y = bottom + (top - bottom) * f;
                double angle = age * 0.45D + h * Math.PI
                        + f * TWO_PI * 3.0D;
                double x = cx + Math.cos(angle) * radius * 1.55D;
                double z = cz + Math.sin(angle) * radius * 1.55D;
                if (i > 0) {
                    line3D(buffer, prevX, prevY, prevZ, x, y, z,
                            Math.max(0.09D, radius * 0.10D),
                            inner[0], inner[1], inner[2]);
                }
                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
    }

    private static void cylinderSide(BufferBuilder buffer,
                                     double cx, double cz,
                                     double bottom, double top,
                                     double radius,
                                     double a0, double a1,
                                     float red, float green, float blue) {
        double x0 = cx + Math.cos(a0) * radius;
        double z0 = cz + Math.sin(a0) * radius;
        double x1 = cx + Math.cos(a1) * radius;
        double z1 = cz + Math.sin(a1) * radius;
        quad(buffer,
                x0, bottom, z0,
                x0, top, z0,
                x1, top, z1,
                x1, bottom, z1,
                red, green, blue);
    }

    private static void drawSolidSphere(BufferBuilder buffer,
                                        double cx, double cy, double cz,
                                        double radius, int lat, int lon,
                                        float red, float green, float blue) {
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
                        red, green, blue);
            }
        }
    }


    private static void line3D(BufferBuilder buffer,
                               double x1, double y1, double z1,
                               double x2, double y2, double z2,
                               double width, float red, float green, float blue) {
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

    private static void vertex(BufferBuilder buffer,
                               double x, double y, double z,
                               float red, float green, float blue) {
        float alpha = fadeScale;
        buffer.pos(x, y, z).tex(0.0D, 0.0D)
                .color(red * alpha, green * alpha, blue * alpha, alpha)
                .lightmap(SimpleParticle.FULLBRIGHT_LIGHT_X,
                        SimpleParticle.FULLBRIGHT_LIGHT_Y)
                .endVertex();
    }

    private static double endScale(double age) {
        double left = EntityHeavenStrike.VISUAL_END_TICK - age;
        if (left >= 24.0D) {
            return 1.0D;
        }
        return smoothstep(clamp(left / 24.0D));
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

    private static double smoothRange(double value, double start, double end) {
        return smoothstep(clamp((value - start) / (end - start)));
    }
}
