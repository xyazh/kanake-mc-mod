package com.xyazh.kanake.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FramebufferExample {
    public static final Minecraft MC = Minecraft.getMinecraft();
    private final int width;
    private final int height;

    private final int framebuffer;
    private final int textureColorBuffer;
    private final int renderBuffer;

    public boolean shouldRecreateFBOs() {
        return MC.displayWidth != this.width || MC.displayHeight != this.height;
    }

    public static FramebufferExample recreateFramebuffer() {
        return new FramebufferExample();
    }

    public FramebufferExample() {
        width = MC.displayWidth;
        height = MC.displayHeight;

        // 创建帧缓冲区对象
        framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        // 创建纹理对象作为颜色附件
        textureColorBuffer = GL11.glGenTextures();
        GlStateManager.bindTexture(textureColorBuffer);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureColorBuffer);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureColorBuffer, 0);
        //GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, textureColorBuffer, 0);

        // 创建渲染缓冲区对象作为深度和模板附件
        renderBuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBuffer);
        // 检查帧缓冲区的完整性
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete!");
        }
        // 解绑帧缓冲区
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            GL30.glDeleteFramebuffers(framebuffer);
            GL11.glDeleteTextures(textureColorBuffer);
            GL30.glDeleteRenderbuffers(renderBuffer);
        } finally {
            super.finalize();
        }
    }

    public void renderToFramebufferStart() {
        // 绑定帧缓冲区作为当前渲染目标
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        GlStateManager.clearColor(0.f,0.f,0.f, 1.0f);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glViewport(0, 0, width, height);
    }

    public void renderToFramebufferEnd() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }

    public int getFramebufferTexture() {
        return textureColorBuffer;
    }
}
