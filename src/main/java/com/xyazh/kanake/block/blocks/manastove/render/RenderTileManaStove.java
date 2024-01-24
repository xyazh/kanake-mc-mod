package com.xyazh.kanake.block.blocks.manastove.render;

import com.xyazh.kanake.block.blocks.manastorage.TileManaStorage;
import com.xyazh.kanake.block.blocks.manastove.TileManaStove;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;


@SideOnly(Side.CLIENT)
public class RenderTileManaStove extends TileEntitySpecialRenderer<TileManaStove> {
    public RenderTileManaStove() {
        super();
    }

    public boolean isGlobalRenderer(@Nonnull TileManaStove te) {
        return false;
    }

    public void renderSquare(TileManaStove te, BufferBuilder bufferbuilder, float partialTicks, double x, double y, double z, double size, int color) {
        if (te.getManaStored() <= 0) {
            return;
        }
        x += 0;
        y += 0;
        z += 0;
        double x1, x2, y1, y2, z1, z2;
        x1 = x + 0.125;
        x2 = x + 0.875;
        y1 = y + 0.5625;
        y2 = y + 0.5625 + (0.875 - 0.5625) * te.getManaStored() / te.getMaxManaStored();
        z1 = z + 0.125;
        z2 = z + 0.875;
        double[] vertices1 = {
                x1, y1, z1,
                x1, y2, z1,
                x2, y2, z1,
                x2, y1, z1
        };
        double[] vertices2 = {
                x1, y1, z2,
                x1, y2, z2,
                x2, y2, z2,
                x2, y1, z2
        };
        double[] vertices3 = {
                x1, y1, z1,
                x1, y2, z1,
                x1, y2, z2,
                x1, y1, z2
        };
        double[] vertices4 = {
                x2, y1, z1,
                x2, y2, z1,
                x2, y2, z2,
                x2, y1, z2
        };
        double[] vertices5 = {
                x1, y2, z1,
                x1, y2, z2,
                x2, y2, z2,
                x2, y2, z1
        };
        this.draw(vertices1, bufferbuilder, color);
        this.draw(vertices2, bufferbuilder, color);
        this.draw(vertices3, bufferbuilder, color);
        this.draw(vertices4, bufferbuilder, color);
        this.draw(vertices5, bufferbuilder, color);
    }

    protected void draw(double[] vertices, BufferBuilder bufferbuilder, int color) {
        for (int i = 0; i < 4; i++) {
            bufferbuilder.pos(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2])
                    .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                    .endVertex();
        }
    }

    public void render(TileManaStove te, BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        renderSquare(te, bufferbuilder, partialTicks, x, y, z, 1.0, 0xAAFF00FF);
    }

    public void render(@Nonnull TileManaStove te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.disableFog();
        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        this.render(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
    }

}
