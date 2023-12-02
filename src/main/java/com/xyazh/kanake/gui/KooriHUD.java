package com.xyazh.kanake.gui;

import com.xyazh.kanake.Kanake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KooriHUD extends Gui {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation ICE_TEXTURE = new ResourceLocation(Kanake.MODID,"textures/gui/ice.png"); // 替换为你的mod的资源路径
    public void renderHUD() {
        int screenWidth = mc.displayWidth;
        int screenHeight = mc.displayHeight;


        mc.getTextureManager().bindTexture(ICE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.98F);
        KooriHUD.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, screenWidth, screenHeight, 64, 64);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double)x, (double)(y + height), 0.0D)
                .tex((double)(u * f), (double)((v + (float)height) * f1))
                .endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D)
                .tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1))
                .endVertex();
        bufferbuilder.pos((double)(x + width), (double)y, 0.0D)
                .tex((double)((u + (float)width) * f), (double)(v * f1))
                .endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D)
                .tex((double)(u * f), (double)(v * f1))
                .endVertex();
        tessellator.draw();
    }
}
