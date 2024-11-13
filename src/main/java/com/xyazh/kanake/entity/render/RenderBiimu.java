package com.xyazh.kanake.entity.render;

import com.xyazh.kanake.entity.EntityBiimu;
import com.xyazh.kanake.events.Event;
import com.xyazh.kanake.events.RenderEvent;
import com.xyazh.kanake.render.FramebufferExample;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderBiimu extends Render<EntityBiimu> {
    private static final Vec3d VEC3D = Vec3d.ZERO.copy();
    private static final FramebufferExample FBO = new FramebufferExample(false);

    public RenderBiimu(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBiimu entity) {
        return null;
    }

    protected void draw(double[] vertices, double[] texCoords, BufferBuilder bufferbuilder, int color) {
        for (int i = 0; i < 4; i++) {
            bufferbuilder.pos(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2])
                    .tex(texCoords[i * 2], texCoords[i * 2 + 1])
                    .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                    .endVertex();
        }
    }

    @Override
    public void doRender(@Nonnull EntityBiimu entity, double x, double y, double z, float entityYaw, float partialTicks) {
        RenderEvent.addPostRenderTask(() -> {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableBlend();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            this.renderSphere(builder,x,y,z,0.2,255,255,0,255);
            tessellator.draw();

            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
        });
    }

    protected void render(double x, double y, double z, float partialTicks){
        GlStateManager.disableTexture2D();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        this.renderSphere(builder,x,y,z,0.2,255,255,0,255);
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
    }

    public void renderSphere(BufferBuilder builder, double x, double y, double z, double radius, int r, int g, int b, int a) {
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
                builder.pos(x1, y1, z1).tex(0,0).color(r, g, b, a).normal((float)(x1-x),(float)(y1-y),(float)(z1-z)).endVertex();
                builder.pos(x2, y2, z2).tex(0,0).color(r, g, b, a).normal((float)(x2-x),(float)(y2-y),(float)(z2-z)).endVertex();
                builder.pos(x3, y3, z3).tex(0,0).color(r, g, b, a).normal((float)(x3-x),(float)(y3-y),(float)(z3-z)).endVertex();
                builder.pos(x3, y3, z3).tex(0,0).color(r, g, b, a).normal((float)(x3-x),(float)(y3-y),(float)(z3-z)).endVertex();
                builder.pos(x2, y2, z2).tex(0,0).color(r, g, b, a).normal((float)(x2-x),(float)(y2-y),(float)(z2-z)).endVertex();
                builder.pos(x4, y4, z4).tex(0,0).color(r, g, b, a).normal((float)(x4-x),(float)(y4-y),(float)(z4-z)).endVertex();
            }
        }
    }
}
