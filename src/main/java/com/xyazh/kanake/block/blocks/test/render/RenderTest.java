package com.xyazh.kanake.block.blocks.test.render;

import com.xyazh.kanake.render.RenderBezierTube;
import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.test.TileTest;
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
    public static final ResourceLocation TEXTURE_TP = new ResourceLocation(Kanake.MODID, "textures/misc/tp.png");
    private final RenderBezierTube renderBezierTube;

    public RenderTest() {
        super();
        this.renderBezierTube = new RenderBezierTube();
    }

    public boolean isGlobalRenderer(@Nonnull TileTest te) {
        return true;
    }

    public void renderSquare(TileTest te, BufferBuilder bufferbuilder, float partialTicks, double x, double y, double z, double size, int color) {

    }

    protected void draw(double[] vertices, double[] texCoords, BufferBuilder bufferbuilder, int color) {

    }

    public void renderBezierTube(BufferBuilder bufferBuilder, double x1, double x2, double x3, double x4, double y1, double y2, double y3, double y4, double z1, double z2, double z3, double z4) {

    }

    public void render(TileTest te, BufferBuilder bufferbuilder, double cx, double cy, double cz, float partialTicks, int destroyStage, float alpha) {

    }

    public void render(@Nonnull TileTest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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
    }
}
