package com.xyazh.kanake.block.blocks.test.render;

import com.xyazh.kanake.block.blocks.test.TileTest;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderTest1 extends TileEntitySpecialRenderer<TileTest> {
    private final Vec3d[] vec3ds= new Vec3d[6];
    private final int[] index = {0,1,3,5,4,2};

    public RenderTest1() {
        super();
        vec3ds[0] = new Vec3d(0,0,1);
        vec3ds[1] = new Vec3d(0.866025,0,0.5);
        vec3ds[2] = new Vec3d(-0.866025,0,0.5);
        vec3ds[3] = new Vec3d(0.866025,0,-0.5);
        vec3ds[4] = new Vec3d(-0.866025,0,-0.5);
        vec3ds[5] = new Vec3d(0,0,-1);
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


    public void renderB(TileTest te, BufferBuilder bufferbuilder, double cx, double cy, double cz, float partialTicks, int destroyStage, float alpha) {
        double size = 1.0;
        cx += 0.5;
        cy += 1.0;
        cz += 0.5;
        for (Vec3d vec3d : vec3ds) {
            bufferbuilder.pos(cx + size * vec3d.x, cy, cz + size * vec3d.z).color(32, 164, 164, 164).endVertex();
        }
    }

    public void renderL(TileTest te, BufferBuilder bufferbuilder, double cx, double cy, double cz, float partialTicks, int destroyStage, float alpha) {
        double size = 1.0;
        cx += 0.5;
        cy += 1.0;
        cz += 0.5;
        for (int i : index) {
            Vec3d vec3d = vec3ds[i];
            bufferbuilder.pos(cx + size * vec3d.x, cy, cz + size * vec3d.z).color(64, 255, 255, 255).endVertex();
        }
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
        this.renderB(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
        tessellator.draw();
        BufferBuilder bufferbuilder1 = tessellator.getBuffer();
        GL11.glLineWidth(2.0f);
        bufferbuilder1.begin(2, DefaultVertexFormats.POSITION_COLOR);
        this.renderL(te, bufferbuilder1, x, y, z, partialTicks, destroyStage, alpha);
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
    }
}
