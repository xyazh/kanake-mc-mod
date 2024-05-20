package com.xyazh.kanake.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

public class FramebufferExample {
    public static final Minecraft MC = Minecraft.getMinecraft();
    public int width;
    public int height;
    private final boolean useDepth;
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

    public void tryReCreate(){
        if (shouldRecreate()) {
            width = MC.displayWidth;
            height = MC.displayHeight;
            framebuffer.deleteFramebuffer();
            framebuffer = new Framebuffer(width, height, useDepth);
        }
    }


    public void bindFramebufferTexture()
    {
        //tryReCreate();
        framebuffer.bindFramebufferTexture();
    }

    public void unbindFramebufferTexture()
    {
        framebuffer.unbindFramebufferTexture();
    }

    public void bindFramebuffer(boolean resetViewport)
    {
        tryReCreate();
        framebuffer.bindFramebuffer(resetViewport);
    }

    public void unbindFramebuffer()
    {
        framebuffer.unbindFramebuffer();
    }

    public void renderToFramebufferStart(boolean resetViewport) {
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        bindFramebuffer(resetViewport);
        setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        framebufferClear();
        if(useDepth){
            GlStateManager.enableDepth();
        }
    }

    public void renderToFramebufferEnd() {
        unbindFramebuffer();
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }

    public void setFramebufferColor(float red, float green, float blue, float alpha)
    {
        framebuffer.setFramebufferColor(red, green, blue, alpha);
    }

    public void framebufferClear()
    {
        GlStateManager.clearColor(framebuffer.framebufferColor[0], framebuffer.framebufferColor[1], framebuffer.framebufferColor[2], framebuffer.framebufferColor[3]);
        if (useDepth)
        {
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        }else {
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        }
    }

    public void renderFboQuad() {
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

        GlStateManager.enableTexture2D();
        framebuffer.bindFramebufferTexture();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0, 0,0).tex(0, 1).color(255,255,255,255).endVertex();
        bufferbuilder.pos(width, 0,0).tex(1, 1).color(255,255,255,255).endVertex();
        bufferbuilder.pos(width, -height,0).tex(1, 0).color(255,255,255,255).endVertex();
        bufferbuilder.pos(0, -height,0).tex(0,0).color(255,255,255,255).endVertex();
        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        // 恢复之前的投影矩阵
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        // 恢复之前的模型视图矩阵
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
    }
}
