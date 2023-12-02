package com.xyazh.kanake.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ManaHUD extends Gui {
    public static ManaHUD hud = null;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public void renderHUD(double n, double maxN) {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        this.renderExpBar(scaledresolution, n, maxN);
    }

    private FontRenderer getFontRenderer() {
        return mc.fontRenderer;
    }

    public void renderExpBar(ScaledResolution scaledRes, double n, double maxN) {
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.color(1f,0.0f,1f);
        int k = (int) (n/maxN * 183.0F);
        int l = scaledRes.getScaledHeight() - 48 + 3;
        int x = scaledRes.getScaledWidth() / 2 - 91;
        this.drawTexturedModalRect(x, l, 0, 64, 182, 5);
        if (k > 0) {
            this.drawTexturedModalRect(x, l, 0, 69, k, 5);
        }
        String s = String.format("%.1f", n) + "/" + maxN;
        int i1 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
        int j1 = scaledRes.getScaledHeight() - 48 - 1;
        this.getFontRenderer().drawString(s, i1 + 1, j1, 0);
        this.getFontRenderer().drawString(s, i1 - 1, j1, 0);
        this.getFontRenderer().drawString(s, i1, j1 + 1, 0);
        this.getFontRenderer().drawString(s, i1, j1 - 1, 0);
        this.getFontRenderer().drawString(s, i1, j1, 0xff55ff);
        GlStateManager.disableBlend();
    }
}
