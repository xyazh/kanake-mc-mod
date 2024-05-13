package com.xyazh.kanake.render;

import com.xyazh.kanake.libs.weaponlib.shader.jim.Shader;
import com.xyazh.kanake.libs.weaponlib.shader.jim.ShaderManager;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;


public class LaserBeamRenderer implements CustomRenderer {
    public static Shader brightnessShader = ShaderManager.loadVMWShader("brightness");

	public LaserBeamRenderer() {
	}

    @Override
    public void render() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(1f, 0f, 0f, 1.0f);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        brightnessShader.use();
        brightnessShader.uniform1f("brightness", 4f);
        brightnessShader.uniform3f("color", 0f, 1f, 0f);


        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		float yOffset = -1.3f;
		float xOffset = 0.5f;
		bb.pos(xOffset, yOffset, -1.5).color(1.0f, 0.0f, 0.0f, 1.0f).endVertex();
        bb.pos(xOffset, yOffset, -50).color(1.0f, 0.0f, 0.0f, 0.1f).endVertex();
        tes.draw();

        brightnessShader.release();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
