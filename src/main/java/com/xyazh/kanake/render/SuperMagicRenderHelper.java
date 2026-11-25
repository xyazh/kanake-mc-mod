package com.xyazh.kanake.render;

import com.xyazh.kanake.entity.EntityGodWrath;
import com.xyazh.kanake.entity.EntityGodWrathBeam;
import com.xyazh.kanake.entity.EntitySilentRain;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Procedural rendering for the two super-tier arrays and their beams. All
 * geometry is emitted as opaque vertex-colored quads, allowing the existing
 * bloom framebuffer pass to pick it up without textures.
 */
@SideOnly(Side.CLIENT)
public final class SuperMagicRenderHelper {
    private static final double TWO_PI = Math.PI * 2.0D;
    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};
    private static final float[] SILVER = {0.72F, 0.88F, 1.0F};
    private static final float[] CYAN = {0.22F, 0.72F, 1.0F};
    private static final float[] BLUE = {0.35F, 0.50F, 1.0F};
    private static final float[] GOLD = {1.0F, 0.80F, 0.30F};
    private static final float[] PALE = {0.62F, 0.92F, 1.0F};

    private SuperMagicRenderHelper() {
    }

    public static void renderSilentRainArray(BufferBuilder buffer,
                                             double ox, double oy, double oz,
                                             double age, double build,
                                             double fade, double charge) {
        build = smoothstep(clamp(build));
        fade = clamp(fade);
        if (build <= 0.001D || fade <= 0.001D) {
            return;
        }

        double scale = (0.35D + 0.65D * build) * fade;
        double outer = EntitySilentRain.RADIUS * scale;
        if (outer < 1.0D) {
            return;
        }
        double pulse = 1.0D + 0.012D * Math.sin(age * 0.20D);
        double width = Math.max(0.12D, outer * 0.006D);
        double spin = age * 0.055D;
        double counter = -age * 0.085D;
        float bright = (float) (0.70D + 0.30D * charge);

        float r = mix(SILVER[0], WHITE[0], bright);
        float g = mix(SILVER[1], WHITE[1], bright);
        float b = mix(SILVER[2], WHITE[2], bright);

        drawArcRing(buffer, ox, oy, oz,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                outer * pulse - width, outer * pulse, 160,
                spin, spin + TWO_PI * build, r, g, b);

        if (build > 0.10D) {
            double innerBuild = smoothstep(clamp((build - 0.10D) / 0.90D));
            drawArcRing(buffer, ox, oy + 0.02D, oz,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    outer * 0.985D - width * 0.62D, outer * 0.985D,
                    144, counter, counter + TWO_PI * innerBuild,
                    CYAN[0], CYAN[1], CYAN[2]);
        }
        if (build > 0.28D) {
            double innerBuild = smoothstep(clamp((build - 0.28D) / 0.72D));
            drawArcRing(buffer, ox, oy + 0.04D, oz,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    outer * 0.80D - width * 0.45D, outer * 0.80D,
                    112, spin * 1.22D, spin * 1.22D + TWO_PI * innerBuild,
                    PALE[0], PALE[1], PALE[2]);
        }
        if (build > 0.46D) {
            double innerBuild = smoothstep(clamp((build - 0.46D) / 0.54D));
            drawArcRing(buffer, ox, oy + 0.06D, oz,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    outer * 0.56D - width * 0.35D, outer * 0.56D,
                    80, counter * 1.45D, counter * 1.45D + TWO_PI * innerBuild,
                    BLUE[0], BLUE[1], BLUE[2]);
        }

        int spokes = 32;
        double spokeBuild = smoothstep(clamp((build - 0.30D) / 0.70D));
        for (int i = 0; i < spokes; ++i) {
            if ((i + 1.0D) / spokes > spokeBuild) {
                break;
            }
            double angle = spin * 1.35D + TWO_PI * i / spokes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            line3D(buffer,
                    ox + cos * outer * 0.24D, oy + 0.08D, oz + sin * outer * 0.24D,
                    ox + cos * outer * 0.46D, oy + 0.08D, oz + sin * outer * 0.46D,
                    Math.max(0.05D, width * 0.38D), BLUE[0], BLUE[1], BLUE[2]);
        }

        int runes = 48;
        for (int i = 0; i < runes; ++i) {
            if ((i + 1.0D) / runes > build) {
                break;
            }
            double angle = spin * 1.45D + TWO_PI * i / runes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double tx = -sin;
            double tz = cos;
            double cx = ox + cos * outer * 0.87D;
            double cz = oz + sin * outer * 0.87D;
            line3D(buffer, cx, oy + 0.09D, cz,
                    cx + cos * width * 0.60D, oy + 0.09D, cz + sin * width * 0.60D,
                    Math.max(0.035D, width * 0.20D), GOLD[0], GOLD[1], GOLD[2]);
            line3D(buffer, cx + tx * width * 0.32D, oy + 0.09D, cz + tz * width * 0.32D,
                    cx - tx * width * 0.32D, oy + 0.09D, cz - tz * width * 0.32D,
                    Math.max(0.028D, width * 0.15D), r, g, b);
        }

        if (build > 0.52D) {
            double starBuild = smoothstep(clamp((build - 0.52D) / 0.48D));
            int star = 12;
            for (int i = 0; i < star; ++i) {
                double angle = -spin * 1.8D + TWO_PI * i / star;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                line3D(buffer,
                        ox + cos * outer * 0.05D, oy + 0.10D, oz + sin * outer * 0.05D,
                        ox + cos * outer * 0.27D * starBuild, oy + 0.10D,
                        oz + sin * outer * 0.27D * starBuild,
                        Math.max(0.045D, width * 0.22D), GOLD[0], GOLD[1], GOLD[2]);
            }
        }

        // A large six-pointed star inscribed in the array.
        double hexBuild = smoothstep(clamp((build - 0.42D) / 0.58D));
        if (hexBuild > 0.001D) {
            double hexRadius = outer * 0.58D;
            double hexRotation = -Math.PI / 2.0D + spin * 0.32D;
            double hexY = oy + 0.15D;
            double hexWidth = Math.max(0.10D, width * 0.34D);

            for (int tri = 0; tri < 2; ++tri) {
                double base = hexRotation + tri * Math.PI / 3.0D;
                float[] color = (tri == 0) ? GOLD : WHITE;
                for (int edge = 0; edge < 3; ++edge) {
                    if ((edge + 1.0D) / 3.0D > hexBuild) {
                        break;
                    }
                    double a0 = base + edge * TWO_PI / 3.0D;
                    double a1 = base + (edge + 1) * TWO_PI / 3.0D;
                    line3D(buffer,
                            ox + Math.cos(a0) * hexRadius, hexY, oz + Math.sin(a0) * hexRadius,
                            ox + Math.cos(a1) * hexRadius, hexY, oz + Math.sin(a1) * hexRadius,
                            hexWidth, color[0], color[1], color[2]);
                }
            }

            // A thin hexagonal frame and six small vertex runes add detail.
            for (int i = 0; i < 6; ++i) {
                if ((i + 1.0D) / 6.0D > hexBuild) {
                    break;
                }
                double a0 = hexRotation + TWO_PI * i / 6.0D;
                double a1 = hexRotation + TWO_PI * (i + 1) / 6.0D;
                line3D(buffer,
                        ox + Math.cos(a0) * hexRadius, hexY + 0.01D, oz + Math.sin(a0) * hexRadius,
                        ox + Math.cos(a1) * hexRadius, hexY + 0.01D, oz + Math.sin(a1) * hexRadius,
                        Math.max(0.045D, width * 0.16D), PALE[0], PALE[1], PALE[2]);

                double vx = ox + Math.cos(a0) * hexRadius;
                double vz = oz + Math.sin(a0) * hexRadius;
                line3D(buffer,
                        vx - Math.sin(a0) * hexWidth * 0.45D, hexY + 0.02D,
                        vz + Math.cos(a0) * hexWidth * 0.45D,
                        vx + Math.sin(a0) * hexWidth * 0.45D, hexY + 0.02D,
                        vz - Math.cos(a0) * hexWidth * 0.45D,
                        Math.max(0.035D, width * 0.13D), CYAN[0], CYAN[1], CYAN[2]);
            }
        }

        if (charge > 0.01D) {
            double core = (1.8D + 4.2D * charge) * scale;
            drawArcRing(buffer, ox, oy + 0.11D, oz,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    core * 0.70D, core, 48,
                    age * 0.50D, age * 0.50D + TWO_PI,
                    WHITE[0], WHITE[1], WHITE[2]);
            drawArcRing(buffer, ox, oy + 0.13D, oz,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    core * 1.20D, core * 1.38D, 32,
                    -age * 0.72D, -age * 0.72D + TWO_PI,
                    GOLD[0], GOLD[1], GOLD[2]);
        }
    }

    private static void renderSmallArray(BufferBuilder buffer,
                                         double x, double y, double z,
                                         double radius, double age, int index,
                                         double build, double charge, double fade) {
        if (build <= 0.001D || fade <= 0.001D) {
            return;
        }
        radius *= 0.35D + 0.65D * fade;
        if (radius < 0.10D) {
            return;
        }
        double rotation = age * 0.085D + index * 0.47D;
        double counter = -age * 0.125D + index * 0.31D;
        double width = Math.max(0.05D, radius * 0.018D);
        float bright = (float) (0.55D + 0.45D * charge);
        float r = mix(GOLD[0], WHITE[0], bright);
        float g = mix(GOLD[1], WHITE[1], bright);
        float b = mix(GOLD[2], WHITE[2], bright);

        drawArcRing(buffer, x, y, z,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                radius - width, radius, 40,
                rotation, rotation + TWO_PI * build, r, g, b);

        if (build > 0.22D) {
            double innerBuild = smoothstep(clamp((build - 0.22D) / 0.78D));
            drawArcRing(buffer, x, y + 0.01D, z,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    radius * 0.64D - width * 0.60D, radius * 0.64D,
                    28, counter, counter + TWO_PI * innerBuild,
                    PALE[0], PALE[1], PALE[2]);
        }

        int spokes = 8;
        for (int i = 0; i < spokes; ++i) {
            if ((i + 1.0D) / spokes > build) {
                break;
            }
            double angle = rotation * 1.20D + TWO_PI * i / spokes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            line3D(buffer,
                    x + cos * radius * 0.30D, y + 0.015D, z + sin * radius * 0.30D,
                    x + cos * radius * 0.86D, y + 0.015D, z + sin * radius * 0.86D,
                    Math.max(0.04D, width * 0.38D), GOLD[0], GOLD[1], GOLD[2]);
        }

        int runes = 6;
        for (int i = 0; i < runes; ++i) {
            if ((i + 1.0D) / runes > build) {
                break;
            }
            double angle = rotation * 1.40D + TWO_PI * i / runes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double tx = -sin;
            double tz = cos;
            double cx = x + cos * radius * 0.75D;
            double cz = z + sin * radius * 0.75D;
            line3D(buffer, cx, y + 0.02D, cz,
                    cx + cos * radius * 0.11D, y + 0.02D, cz + sin * radius * 0.11D,
                    Math.max(0.028D, width * 0.24D), WHITE[0], WHITE[1], WHITE[2]);
            line3D(buffer, cx + tx * radius * 0.055D, y + 0.02D, cz + tz * radius * 0.055D,
                    cx - tx * radius * 0.055D, y + 0.02D, cz - tz * radius * 0.055D,
                    Math.max(0.024D, width * 0.18D), PALE[0], PALE[1], PALE[2]);
        }

        if (charge > 0.02D) {
            double core = radius * (0.17D + 0.28D * charge);
            drawArcRing(buffer, x, y + 0.03D, z,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    core * 0.60D, core, 20,
                    -rotation * 1.8D, -rotation * 1.8D + TWO_PI,
                    WHITE[0], WHITE[1], WHITE[2]);
            drawArcRing(buffer, x, y + 0.04D, z,
                    1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                    core * 1.15D, core * 1.35D, 16,
                    rotation * 2.1D, rotation * 2.1D + TWO_PI,
                    GOLD[0], GOLD[1], GOLD[2]);
        }
    }

    public static void renderGodWrathBeam(BufferBuilder buffer,
                                          double sx, double sy, double sz,
                                          double tx, double ty, double tz,
                                          double age) {
        double progress = clamp(age / EntityGodWrathBeam.MAX_AGE);
        double fade = 1.0D - smoothstep(progress);
        if (fade <= 0.01D) {
            return;
        }
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.001D) {
            return;
        }
        dx /= length;
        dy /= length;
        dz /= length;

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

        double radius = 0.12D * (0.55D + 0.45D * fade);
        int segments = 10;
        for (int i = 0; i < segments; ++i) {
            double a0 = TWO_PI * i / segments;
            double a1 = TWO_PI * (i + 1) / segments;
            float wave = (float) (0.5D + 0.5D * Math.sin(a0 * 3.0D + age * 1.1D));
            beamSide(buffer, sx, sy, sz, dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    length, radius, a0, a1,
                    mix(GOLD[0], WHITE[0], wave * 0.72F),
                    mix(GOLD[1], WHITE[1], wave * 0.72F),
                    mix(GOLD[2], WHITE[2], wave * 0.72F));
        }

        // Bright inner line and a second thin white filament.
        for (int i = 0; i < 3; ++i) {
            double a0 = age * 0.8D + TWO_PI * i / 3.0D;
            double a1 = a0 + 0.12D;
            beamSide(buffer, sx, sy, sz, dx, dy, dz, rx, ry, rz, ux2, uy2, uz2,
                    length, radius * 1.10D, a0, a1,
                    WHITE[0], WHITE[1], WHITE[2]);
        }

        int rings = 7;
        for (int i = 1; i <= rings; ++i) {
            double t = i / (double) (rings + 1);
            double cx = sx + dx * length * t;
            double cy = sy + dy * length * t;
            double cz = sz + dz * length * t;
            drawArcRing(buffer, cx, cy, cz, rx, ry, rz, ux2, uy2, uz2,
                    radius * 0.95D, radius * 1.35D, 20,
                    age * 0.45D + i * 0.35D, age * 0.45D + i * 0.35D + TWO_PI,
                    PALE[0], PALE[1], PALE[2]);
        }

        // Source array crown and target impact halo.
        drawArcRing(buffer, sx, sy, sz,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                radius * 4.0D, radius * 6.0D, 24,
                age * 0.4D, age * 0.4D + TWO_PI,
                GOLD[0], GOLD[1], GOLD[2]);
        drawArcRing(buffer, tx, ty, tz,
                1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D,
                radius * 5.0D * fade, radius * 8.0D * fade, 24,
                -age * 0.7D, -age * 0.7D + TWO_PI,
                WHITE[0], WHITE[1], WHITE[2]);
    }

    public static void renderRainDrop(BufferBuilder buffer,
                                      double x, double y, double z,
                                      double age) {
        double pulse = 1.0D + 0.08D * Math.sin(age * 1.3D + x * 0.3D + z * 0.2D);
        double half = 0.055D * pulse;
        double tail = 0.62D * pulse;
        double bulb = 0.20D;
        quad(buffer,
                x, y + tail, z,
                x + half, y + tail * 0.38D, z,
                x + half * 0.62D, y - bulb, z,
                x, y - bulb * 1.35D, z,
                PALE[0], PALE[1], PALE[2]);
        quad(buffer,
                x, y + tail, z,
                x, y + tail * 0.38D, z + half,
                x, y - bulb, z + half * 0.62D,
                x, y - bulb * 1.35D, z,
                PALE[0], PALE[1], PALE[2]);
        quad(buffer,
                x, y + tail, z,
                x - half, y + tail * 0.38D, z,
                x - half * 0.62D, y - bulb, z,
                x, y - bulb * 1.35D, z,
                PALE[0], PALE[1], PALE[2]);
        quad(buffer,
                x, y + tail, z,
                x, y + tail * 0.38D, z - half,
                x, y - bulb, z - half * 0.62D,
                x, y - bulb * 1.35D, z,
                PALE[0], PALE[1], PALE[2]);
        line3D(buffer, x, y + tail * 1.05D, z,
                x, y - bulb * 1.4D, z, half * 0.30D,
                WHITE[0], WHITE[1], WHITE[2]);
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

    private static void beamSide(BufferBuilder buffer,
                                 double ox, double oy, double oz,
                                 double dx, double dy, double dz,
                                 double rx, double ry, double rz,
                                 double ux, double uy, double uz,
                                 double length, double radius,
                                 double a0, double a1,
                                 float r, float g, float b) {
        double c0 = Math.cos(a0);
        double s0 = Math.sin(a0);
        double c1 = Math.cos(a1);
        double s1 = Math.sin(a1);
        double x0 = ox + (rx * c0 + ux * s0) * radius;
        double y0 = oy + (ry * c0 + uy * s0) * radius;
        double z0 = oz + (rz * c0 + uz * s0) * radius;
        double x1 = ox + (rx * c1 + ux * s1) * radius;
        double y1 = oy + (ry * c1 + uy * s1) * radius;
        double z1 = oz + (rz * c1 + uz * s1) * radius;
        quad(buffer,
                x0, y0, z0,
                x0 + dx * length, y0 + dy * length, z0 + dz * length,
                x1 + dx * length, y1 + dy * length, z1 + dz * length,
                x1, y1, z1,
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

    public static void renderGodWrathArrays(BufferBuilder buffer,
                                            double ox, double oy, double oz,
                                            double age, double fade,
                                            boolean[] spentFlags,
                                            double[] spentStartTicks) {
        fade = clamp(fade);
        if (fade <= 0.001D) {
            return;
        }
        double[] arrayBuilds = new double[EntityGodWrath.ARRAY_COUNT];
        for (int i = 0; i < EntityGodWrath.ARRAY_COUNT; ++i) {
            double localAge = age - 4.0D - i * 1.25D;
            double build = smoothstep(clamp(localAge / EntityGodWrath.BUILD_TICKS));
            boolean spent = spentFlags != null && i < spentFlags.length && spentFlags[i];
            double arrayFade = fade;
            if (spent) {
                double spentAge = age - (spentStartTicks != null
                        && i < spentStartTicks.length ? spentStartTicks[i] : age);
                if (spentAge >= EntityGodWrath.SPENT_FADE_TICKS) {
                    arrayBuilds[i] = 0.0D;
                    continue;
                }
                arrayFade = Math.min(arrayFade, clamp(
                        (EntityGodWrath.SPENT_FADE_TICKS - spentAge)
                                / EntityGodWrath.SPENT_FADE_TICKS));
            }
            arrayBuilds[i] = build;
            if (build <= 0.001D || arrayFade <= 0.001D) {
                continue;
            }
            double ax = ox + EntityGodWrath.getArrayOffsetX(i);
            double az = oz + EntityGodWrath.getArrayOffsetZ(i);
            double ay = oy + Math.sin(age * 0.035D + i * 0.73D) * 0.35D;
            double charge = 0.30D + 0.70D * (0.5D + 0.5D
                    * Math.sin(age * 0.16D + i * 1.55D));
            renderSmallArray(buffer, ax, ay, az,
                    EntityGodWrath.ARRAY_RADIUS, age, i, build, charge, arrayFade);
        }

        // Faint constellation lines connect the spread-out array grid.
        if (age > EntityGodWrath.BUILD_TICKS + 20.0D && fade > 0.05D) {
            float lineR = mix(BLUE[0], WHITE[0], 0.18F);
            float lineG = mix(BLUE[1], WHITE[1], 0.18F);
            float lineB = mix(BLUE[2], WHITE[2], 0.18F);
            for (int row = 0; row < EntityGodWrath.GRID_SIZE; ++row) {
                for (int col = 0; col < EntityGodWrath.GRID_SIZE; ++col) {
                    int index = row * EntityGodWrath.GRID_SIZE + col;
                    if (arrayBuilds[index] < 0.5D) {
                        continue;
                    }
                    double ax = ox + EntityGodWrath.getArrayOffsetX(index);
                    double az = oz + EntityGodWrath.getArrayOffsetZ(index);
                    double ay = oy + Math.sin(age * 0.035D + index * 0.73D) * 0.35D;
                    if (col + 1 < EntityGodWrath.GRID_SIZE
                            && arrayBuilds[index + 1] >= 0.5D) {
                        double bx = ox + EntityGodWrath.getArrayOffsetX(index + 1);
                        double bz = oz + EntityGodWrath.getArrayOffsetZ(index + 1);
                        double by = oy + Math.sin(age * 0.035D + (index + 1) * 0.73D) * 0.35D;
                        line3D(buffer, ax, ay, az, bx, by, bz, 0.022D,
                                lineR, lineG, lineB);
                    }
                    if (row + 1 < EntityGodWrath.GRID_SIZE) {
                        int below = index + EntityGodWrath.GRID_SIZE;
                        if (arrayBuilds[below] >= 0.5D) {
                            double bx = ox + EntityGodWrath.getArrayOffsetX(below);
                            double bz = oz + EntityGodWrath.getArrayOffsetZ(below);
                            double by = oy + Math.sin(age * 0.035D + below * 0.73D) * 0.35D;
                            line3D(buffer, ax, ay, az, bx, by, bz, 0.022D,
                                    lineR, lineG, lineB);
                        }
                    }
                }
            }
        }
    }
}

