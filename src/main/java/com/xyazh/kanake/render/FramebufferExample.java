package com.xyazh.kanake.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FramebufferExample {
    public static final Minecraft MC = Minecraft.getMinecraft();
    private final boolean useDepth;
    public int width;
    public int height;
    private Framebuffer framebuffer;

    public FramebufferExample(boolean useDepthIn) {
        width = MC.displayWidth;
        height = MC.displayHeight;
        useDepth = useDepthIn;
        framebuffer = new Framebuffer(width, height, useDepthIn);
    }

    public boolean shouldRecreate() {
        return MC.displayWidth != this.width || MC.displayHeight != this.height;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            framebuffer.deleteFramebuffer();
        } finally {
            super.finalize();
        }
    }

    public boolean tryReCreate() {
        if (shouldRecreate()) {
            width = MC.displayWidth;
            height = MC.displayHeight;
            framebuffer.deleteFramebuffer();
            framebuffer = new Framebuffer(width, height, useDepth);
            return true;
        }
        return false;
    }

    public void bindFramebufferTexture() {
        tryReCreate();
        framebuffer.bindFramebufferTexture();
    }

    public void unbindFramebufferTexture() {
        framebuffer.unbindFramebufferTexture();
    }

    public void bindFramebuffer(boolean resetViewport) {
        framebuffer.bindFramebuffer(resetViewport || tryReCreate());
    }

    public void bindFramebuffer() {
        this.bindFramebuffer(false);
    }


    public void unbindFramebuffer() {
        framebuffer.unbindFramebuffer();
    }

    public void renderToFramebufferStart(boolean resetViewport) {
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        bindFramebuffer(resetViewport || tryReCreate());
        setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        framebufferClear();
        if (useDepth) {
            GlStateManager.enableDepth();
        }
    }

    public void renderToFramebufferStart() {
        this.renderToFramebufferStart(false);
    }

    public void renderToFramebufferEnd() {
        unbindFramebuffer();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }

    public void setFramebufferColor(float red, float green, float blue, float alpha) {
        framebuffer.setFramebufferColor(red, green, blue, alpha);
    }

    public void framebufferClear() {
        this.framebuffer.framebufferClear();
    }

    public void pushMatrix() {
        GL11.glViewport(0, 0, width, height);
        // 保存当前投影矩阵
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, -height, 0, -1, 1);
        // 设置模型视图矩阵
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
    }

    public void popMatrix() {
        // 恢复之前的投影矩阵
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        // 恢复之前的模型视图矩阵
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }

    public void drawWindowRect() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0, 0, 0).tex(0, 1).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(width, 0, 0).tex(1, 1).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(width, -height, 0).tex(1, 0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(0, -height, 0).tex(0, 0).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
    }

    public void renderFboQuad() {
        this.pushMatrix();
        GlStateManager.enableTexture2D();
        this.bindFramebufferTexture();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        this.drawWindowRect();
        this.unbindFramebufferTexture();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        this.popMatrix();
    }


    public void copyFramebuffer(int frameBuffer) {
        tryReCreate();
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer); // 默认FBO（屏幕缓冲区）
        OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.framebuffer.framebufferObject); // 我们的目标FBO
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
    }

    public void copyFramebuffer(Framebuffer frameBuffer) {
        this.copyFramebuffer(frameBuffer.framebufferObject);
    }

    public void copyDisFramebuffer() {
        this.copyFramebuffer(Minecraft.getMinecraft().getFramebuffer());
    }

    public void copyWindowBuffer() {
        this.copyFramebuffer(0);
    }
}
