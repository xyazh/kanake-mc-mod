package com.xyazh.kanake.render;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
    static final Vec3d VEC3D = new Vec3d(0, 0, 0);

    public static void renderSphere(BufferBuilder builder, double x, double y, double z, double radius, int r, int g, int b, int a) {
        y += 0.5;
        VEC3D.set(x, y, z);
        double distance = VEC3D.length();
        double l = Math.max(distance - radius, 0) / 9;
        int minStacks = 5;
        int maxStacks = 32;
        int stacks = (int) ((maxStacks - minStacks) * (radius / (l + radius)) + minStacks);

        double pi = Math.PI;
        double twoPi = 2 * pi;
        for (int i = 0; i < stacks; ++i) {
            double phi = i * pi / stacks;
            double nextPhi = (i + 1) * pi / stacks;
            for (int j = 0; j <= stacks; ++j) {
                double theta = j * twoPi / stacks;
                double cosTheta = Math.cos(theta);
                double sinTheta = Math.sin(theta);
                double cosPhi = Math.cos(phi);
                double sinPhi = Math.sin(phi);
                double cosNextPhi = Math.cos(nextPhi);
                double sinNextPhi = Math.sin(nextPhi);
                double x1 = x + radius * sinPhi * cosTheta;
                double y1 = y + radius * cosPhi;
                double z1 = z + radius * sinPhi * sinTheta;
                double x2 = x + radius * sinNextPhi * cosTheta;
                double y2 = y + radius * cosNextPhi;
                double z2 = z + radius * sinNextPhi * sinTheta;
                double nextTheta = (j + 1) * twoPi / stacks;
                double cosNextTheta = Math.cos(nextTheta);
                double sinNextTheta = Math.sin(nextTheta);
                double x3 = x + radius * sinPhi * cosNextTheta;
                double y3 = y + radius * cosPhi;
                double z3 = z + radius * sinPhi * sinNextTheta;
                double x4 = x + radius * sinNextPhi * cosNextTheta;
                double y4 = y + radius * cosNextPhi;
                double z4 = z + radius * sinNextPhi * sinNextTheta;
                builder.pos(x1, y1, z1).color(r, g, b, a).endVertex();
                builder.pos(x2, y2, z2).color(r, g, b, a).endVertex();
                builder.pos(x3, y3, z3).color(r, g, b, a).endVertex();
                builder.pos(x3, y3, z3).color(r, g, b, a).endVertex();
                builder.pos(x2, y2, z2).color(r, g, b, a).endVertex();
                builder.pos(x4, y4, z4).color(r, g, b, a).endVertex();
            }
        }
    }
}
