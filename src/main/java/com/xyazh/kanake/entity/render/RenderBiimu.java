package com.xyazh.kanake.entity.render;

import com.xyazh.kanake.entity.EntityBiimu;
import com.xyazh.kanake.render.FramebufferExample;
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
    public FramebufferExample fbo;

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
        double y1 = y + 1.0;
        double x1 = x - 2.0;
        double z1 = z - 2.0;
        double x2 = x + 2.0;
        double z2 = z + 2.0;
        double[] vertices1 = {
                x2, y1, z2,
                x1, y1, z2,
                x1, y1, z1,
                x2, y1, z1
        };
        double[] texCoords1 = {
                0.0, 0.0,
                0.5, 0.0,
                0.5, 0.5,
                0.0, 0.5
        };

        if(fbo == null || fbo.shouldRecreateFBOs()){
            fbo = FramebufferExample.recreateFramebuffer();
        }

        fbo.renderToFramebufferStart();

        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.draw(vertices1, texCoords1, bufferbuilder, 0xFF0000FF);
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();

        fbo.renderToFramebufferEnd();

        GlStateManager.bindTexture(fbo.getFramebufferTexture());
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();

        tessellator = Tessellator.getInstance();
        bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.draw(vertices1, texCoords1, bufferbuilder, 0xFFFFFFFF);
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }
}
