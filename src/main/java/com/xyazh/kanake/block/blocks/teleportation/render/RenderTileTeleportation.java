package com.xyazh.kanake.block.blocks.teleportation.render;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.teleportation.TileTeleportation;
import com.xyazh.kanake.block.blocks.unstableteleportation.TileUnstableTeleportation;
import com.xyazh.kanake.events.RenderEvent;
import com.xyazh.kanake.util.MathUtils;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderTileTeleportation extends TileEntitySpecialRenderer<TileTeleportation> {
    public static final ResourceLocation TEXTURE_TP = new ResourceLocation(Kanake.MODID, "textures/misc/tp.png");

    public RenderTileTeleportation() {
        super();

    }

    public boolean isGlobalRenderer(@Nonnull TileTeleportation te) {
        return false;
    }

    public void renderSquare(TileTeleportation te, BufferBuilder bufferbuilder, float partialTicks, double x, double y, double z, double size, int color) {
        Vec3d offset = te.datumOffset(partialTicks);
        x += 0.5 + offset.x;
        y += 0.1 + offset.y;
        z += 0.5 + offset.z;
        double x1 = x - size / 2.0;
        double z1 = z - size / 2.0;
        double x2 = x + size / 2.0;
        double z2 = z + size / 2.0;
        double[] pointsY = {0,0,0,0,0,0,0,0,0};
        for(int i = 0; i < pointsY.length; i++){
            pointsY[i] = te.pointsY[i] + (te.lastPointY[i] - te.pointsY[i]) * partialTicks;
        }
        double[] vertices1 = {
                x1,y+pointsY[0],z2,
                x,y+pointsY[1],z2,
                x,y+pointsY[4],z,
                x1,y+pointsY[3],z
        };
        double[] texCoords1 = {
                0,0,
                0.5,0,
                0.5,0.5,
                0,0.5
        };
        double[] vertices2 = {
                x,y+pointsY[1],z2,
                x2,y+pointsY[2],z2,
                x2,y+pointsY[5],z,
                x,y+pointsY[4],z
        };
        double[] texCoords2 = {
                0.5,0,
                1,0,
                1,0.5,
                0.5,0.5
        };
        double[] vertices3 = {
                x1,y+pointsY[3],z,
                x,y+pointsY[4],z,
                x,y+pointsY[7],z1,
                x1,y+pointsY[6],z1
        };
        double[] texCoords3 = {
                0,0.5,
                0.5,0.5,
                0.5,1,
                0,1
        };
        double[] vertices4 = {
                x,y+pointsY[4],z,
                x2,y+pointsY[5],z,
                x2,y+pointsY[8],z1,
                x,y+pointsY[7],z1
        };
        double[] texCoords4 = {
                0.5,0.5,
                1,0.5,
                1,1,
                0.5,1
        };
        this.draw(vertices1, texCoords1, bufferbuilder, color);
        this.draw(vertices2, texCoords2, bufferbuilder, color);
        this.draw(vertices3, texCoords3, bufferbuilder, color);
        this.draw(vertices4, texCoords4, bufferbuilder, color);
    }

    protected void draw(double[] vertices, double[] texCoords, BufferBuilder bufferbuilder, int color) {
        for (int i = 0; i < 4; i++) {
            bufferbuilder.pos(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2])
                    .tex(texCoords[i * 2], texCoords[i * 2 + 1])
                    .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, (color >> 24) & 0xFF)
                    .endVertex();
        }
    }

    public void render(TileTeleportation te, BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        renderSquare(te, bufferbuilder, partialTicks, x, y, z, 2.0, 0xFFFFFFFF);
    }

    public void render(@Nonnull TileTeleportation te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RenderEvent.addPostRenderTask(() -> {
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableBlend();
            this.bindTexture(TEXTURE_TP);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            this.render(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
            tessellator.draw();

            GlStateManager.enableLighting();
            GlStateManager.enableCull();

        });

        /*GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5,y,z+0.5);
        GlStateManager.rotate((te.age + partialTicks)/40,0,1,0);
        GlStateManager.translate(-(x+0.5),-y,-(z+0.5));
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableTexture2D();
        this.bindTexture(TEXTURE_TP);
        GlStateManager.disableFog();
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.render(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();*/
    }
}
