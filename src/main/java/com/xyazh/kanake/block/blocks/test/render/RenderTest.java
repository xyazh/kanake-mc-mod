package com.xyazh.kanake.block.blocks.test.render;

import com.xyazh.kanake.render.RenderBezierTube;
import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.test.TileTest;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderTest extends TileEntitySpecialRenderer<TileTest> {
    /*
    public RenderTest() {
        super();
    }

    public boolean isGlobalRenderer(@Nonnull TilePlatform te) {
        return true;
    }

    public void renderSquare(TilePlatform te, BufferBuilder bufferbuilder, float partialTicks, double x, double y, double z, double size, int color) {

    }

    protected void draw(double[] vertices, double[] texCoords, BufferBuilder bufferbuilder, int color) {

    }

    public void renderBezierTube(BufferBuilder bufferBuilder, double x1, double x2, double x3, double x4, double y1, double y2, double y3, double y4, double z1, double z2, double z3, double z4) {

    }


    public void render(TilePlatform te, BufferBuilder bufferbuilder, double cx, double cy, double cz, float partialTicks, int destroyStage, float alpha) {
        for (int i = 0; i < te.points.length; i++) {
            double x, y, z;
            x = te.points[i].x + te.m[i].x * partialTicks;
            y = te.points[i].y + te.m[i].x * partialTicks;
            z = te.points[i].z + te.m[i].z * partialTicks;
            Vec3d vl = new Vec3d(x, y, z);
            int l = (int) (Math.min(4 / vl.length(), 1) * 256);
            bufferbuilder.pos(cx + x, cy + y, cz + z).color(255, 128, 64, l).endVertex();
        }
    }

    public void render(@Nonnull TilePlatform te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //this.bindTexture(RenderTileTeleportation.TEXTURE_TP);
        GlStateManager.disableTexture2D();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        this.render(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
    }*/
}
