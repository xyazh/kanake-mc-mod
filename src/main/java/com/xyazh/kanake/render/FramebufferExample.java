package com.xyazh.kanake.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FramebufferExample {
    public static final Minecraft MC = Minecraft.getMinecraft();
    private final boolean useDepth;
    public int width;
    public int height;
    public int realWidth;
    public int realHeight;
    public float scale = 1;
    protected int lastBindFBO = 0;
    private Framebuffer framebuffer;

    public FramebufferExample(boolean useDepthIn) {
        width = MC.displayWidth;
        height = MC.displayHeight;
        realWidth = MC.displayWidth;
        realHeight = MC.displayHeight;
        useDepth = useDepthIn;
        framebuffer = new Framebuffer(realWidth, realHeight, useDepthIn);
    }

    public FramebufferExample(boolean useDepthIn, float scale) {
        width = MC.displayWidth;
        height = MC.displayHeight;
        realWidth = (int) (MC.displayWidth * scale);
        realHeight = (int) (MC.displayHeight * scale);
        useDepth = useDepthIn;
        this.scale = scale;
        framebuffer = new Framebuffer(realWidth, realHeight, useDepthIn);
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
        if (!shouldRecreate()) {
            return false;
        }
        width = MC.displayWidth;
        height = MC.displayHeight;
        realWidth = (int) (MC.displayWidth * scale);
        realHeight = (int) (MC.displayHeight * scale);
        framebuffer.deleteFramebuffer();
        framebuffer = new Framebuffer(realWidth, realHeight, useDepth);
        return true;
    }

    public void bindFramebufferTexture() {
        tryReCreate();
        framebuffer.bindFramebufferTexture();
    }

    public void unbindFramebufferTexture() {
        framebuffer.unbindFramebufferTexture();
    }

    public void bindFramebuffer(boolean resetViewport) {
        this.lastBindFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        framebuffer.bindFramebuffer(resetViewport || tryReCreate());
    }

    public void bindFramebuffer() {
        this.bindFramebuffer(false);
    }


    public void unbindFramebuffer() {
        this.framebuffer.unbindFramebuffer();
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.lastBindFBO);
    }

    public void renderToFramebufferStart(boolean resetViewport) {
        this.bindFramebuffer(resetViewport || tryReCreate());
        this.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.framebufferClear();
        if (this.useDepth) {
            GlStateManager.enableDepth();
        }
    }

    public void renderToFramebufferStart() {
        this.renderToFramebufferStart(false);
    }

    public void renderToFramebufferEnd() {
        this.unbindFramebuffer();
    }

    public void setFramebufferColor(float red, float green, float blue, float alpha) {
        framebuffer.setFramebufferColor(red, green, blue, alpha);
    }

    public void framebufferClear() {
        float[] this$frame = this.framebuffer.framebufferColor;
        GlStateManager.clearColor(this$frame[0], this$frame[1], this$frame[2], this$frame[3]);
        int i = 16384;
        if (this.useDepth) {
            GlStateManager.clearDepth(1.0D);
            i |= 256;
        }
        GlStateManager.clear(i);
    }

    public void pushMatrix() {
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, -height, 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
    }

    public void popMatrix() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
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


    public void copyFrameBuffer(int frameBuffer) {
        this.lastBindFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        tryReCreate();
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
        OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.framebuffer.framebufferObject);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        this.unbindFramebuffer();
    }

    public void copyFrameBuffer(Framebuffer frameBuffer) {
        this.copyFrameBuffer(frameBuffer.framebufferObject);
    }

    public void copyDisFrameBuffer() {
        this.copyFrameBuffer(Minecraft.getMinecraft().getFramebuffer());
    }

    public void copyWindowBuffer() {
        this.copyFrameBuffer(0);
    }

    public void copyBindBuffer() {
        int bindFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        this.copyFrameBuffer(bindFBO);
    }

    protected void delTexture() {
        if (this.framebuffer.framebufferTexture > -1) {
            TextureUtil.deleteTexture(this.framebuffer.framebufferTexture);
            this.framebuffer.framebufferTexture = -1;
        }
    }
}
