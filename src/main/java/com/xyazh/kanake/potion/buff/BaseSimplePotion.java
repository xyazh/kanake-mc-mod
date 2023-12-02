package com.xyazh.kanake.potion.buff;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BaseSimplePotion extends Potion {
    protected static final ResourceLocation resource = new ResourceLocation("kanake","textures/misc/potions.png");
    protected final int iconIndex;


    public BaseSimplePotion(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn);
        setPotionName("kanake.potion." + name);
        iconIndex = icon;
    }

    @SideOnly(Side.CLIENT)
    protected void render(int x, int y, float alpha) {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        GlStateManager.color(1, 1, 1, alpha);

        int textureX = iconIndex % 1024 * 128;
        int textureY = iconIndex / 1024 * 128;

        buf.pos(x, y + 18, 0).tex(textureX * 0.0009765625, (textureY + 128) * 0.0009765625).endVertex();
        buf.pos(x + 18, y + 18, 0).tex((textureX + 128) * 0.0009765625, (textureY + 128) * 0.0009765625).endVertex();
        buf.pos(x + 18, y, 0).tex((textureX + 128) * 0.0009765625, textureY * 0.0009765625).endVertex();
        buf.pos(x, y, 0).tex(textureX * 0.0009765625, textureY * 0.0009765625).endVertex();
        tessellator.draw();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        render(x + 6, y + 7, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        render(x + 3, y + 3, alpha);
    }
}
