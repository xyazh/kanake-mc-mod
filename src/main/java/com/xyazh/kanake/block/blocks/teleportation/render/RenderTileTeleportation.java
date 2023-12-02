package com.xyazh.kanake.block.blocks.teleportation.render;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.teleportation.TileTeleportation;
import com.xyazh.kanake.util.MathUtils;
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
public class RenderTileTeleportation extends TileEntitySpecialRenderer<TileTeleportation> {
    public static final ResourceLocation TEXTURE_TP = new ResourceLocation(Kanake.MODID, "textures/misc/tp.png");

    public RenderTileTeleportation() {
        super();

    }

    public boolean isGlobalRenderer(@Nonnull TileTeleportation te) {
        return false;
    }

    public void renderSquare(TileTeleportation te, BufferBuilder bufferbuilder, float partialTicks, double x, double y, double z, double size, int color) {
        x += 0.5;
        y += 0.1;
        z += 0.5;
        double x1 = x - size / 2.0;
        double z1 = z - size / 2.0;
        double x2 = x + size / 2.0;
        double z2 = z + size / 2.0;
        float dt;
        if (partialTicks > te.lastPT) {
            dt = partialTicks - te.lastPT;
        } else {
            dt = partialTicks;
        }
        te.age += dt;
        for (int i = 0; i < te.y.length; i++) {
            if(te.t[i] > te.mt[i]){
                te.t[i] = 0;
                te.sy[i] = TileTeleportation.randVS();
                te.my[i] = TileTeleportation.randVM();
                te.mt[i] = TileTeleportation.randVS() * 200;
            }
            double t = te.t[i] / te.mt[i];
            te.y[i] = MathUtils.bezier(0,te.sy[i],te.my[i],0,t)/5;
            te.t[i] += dt;
        }
        te.lastPT = partialTicks;
        double[] vertices1 = {
                x1, y + te.y[0], z1,
                x1, y + te.y[1], z,
                x, y + te.y[2], z,
                x, y + te.y[3], z1
        };
        double[] texCoords1 = {
                0, 0,
                0, 0.5,
                0.5, 0.5,
                0.5, 0
        };
        double[] vertices2 = {
                x2, y + te.y[4], z1,
                x2, y + te.y[5], z,
                x, y + te.y[2], z,
                x, y + te.y[3], z1
        };
        double[] texCoords2 = {
                1, 0,
                1, 0.5,
                0.5, 0.5,
                0.5, 0
        };
        double[] vertices3 = {
                x2, y + te.y[6], z2,
                x2, y + te.y[5], z,
                x, y + te.y[2], z,
                x, y + te.y[7], z2
        };
        double[] texCoords3 = {
                1, 1,
                1, 0.5,
                0.5, 0.5,
                0.5, 1
        };
        double[] vertices4 = {
                x1, y + te.y[8], z2,
                x1, y + te.y[1], z,
                x, y + te.y[2], z,
                x, y + te.y[7], z2
        };
        double[] texCoords4 = {
                1, 0,
                1, 0.5,
                0.5, 0.5,
                0.5, 0,
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
        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5,y,z+0.5);
        GlStateManager.rotate((float) (te.age)/40,0,1,0);
        GlStateManager.translate(-(x+0.5),-y,-(z+0.5));
        GlStateManager.alphaFunc(516, 0.1F);
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
        GlStateManager.popMatrix();
    }
}
