package com.xyazh.kanake.gui.test;

import com.xyazh.kanake.Kanake;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiContainerTest extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Kanake.MODID, "textures/gui/gui_rune.png");

    public GuiContainerTest(ContainerTest inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 176;
        this.ySize = 256;
    }

    public void drawTexturedModalRect(BufferBuilder bufferbuilder, int x, int y, int textureX, int textureY, int width, int height) {
        bufferbuilder.pos((double) (x), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x), (double) (y), (double) this.zLevel).tex((double) ((float) (textureX) * 0.00390625F), (double) ((float) (textureY) * 0.00390625F)).endVertex();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int offsetX = (this.width - this.xSize) / 2;
        int offsetY = (this.height - this.ySize) / 2;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        this.drawTexturedModalRect(bufferbuilder, offsetX, offsetY, 0, 0, this.xSize, this.ySize);
        for (int n = 0; n < 78; n++) {
            int i, j;
            i = (n / 26) * 42 + 25;
            j = (n % 26) * 8 + 24;
            if (n % 2 == 0) {
                this.drawTexturedModalRect(bufferbuilder, offsetX + i, offsetY + j, 176, 0, 42, 8);
            } else {
                this.drawTexturedModalRect(bufferbuilder, offsetX + i, offsetY + j, 176, 8, 42, 8);
            }
        }
        tessellator.draw();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // TODO
    }
}