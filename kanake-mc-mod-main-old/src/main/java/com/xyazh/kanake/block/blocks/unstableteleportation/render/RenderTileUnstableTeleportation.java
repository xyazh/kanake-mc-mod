package com.xyazh.kanake.block.blocks.unstableteleportation.render;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.teleportation.render.RenderTileTeleportation;
import com.xyazh.kanake.block.blocks.unstableteleportation.TileUnstableTeleportation;
import com.xyazh.kanake.libs.weaponlib.shader.jim.Shader;
import com.xyazh.kanake.libs.weaponlib.shader.jim.ShaderManager;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
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
public class RenderTileUnstableTeleportation extends TileEntitySpecialRenderer<TileUnstableTeleportation> {
    public static final ResourceLocation TEXTURE_NOISE = new ResourceLocation(Kanake.MODID, "textures/misc/noise1.png");
    public static Shader noiseShader = ShaderManager.loadVMWShader("noise1");

    public RenderTileUnstableTeleportation() {
        super();
    }

    public boolean isGlobalRenderer(@Nonnull TileUnstableTeleportation te) {
        return false;
    }

    public void renderSquare(TileUnstableTeleportation te, BufferBuilder bufferbuilder, float partialTicks, double x, double y, double z, double size, int color) {
        Vec3d offset = te.datumOffset(partialTicks);
        x += 0.5 + offset.x;
        y += 0.1 + offset.y;
        z += 0.5 + offset.z;
        double x1 = x - size / 2.0;
        double z1 = z - size / 2.0;
        double x2 = x + size / 2.0;
        double z2 = z + size / 2.0;
        double[] pointsY = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < pointsY.length; i++) {
            pointsY[i] = te.pointsY[i] + (te.lastPointY[i] - te.pointsY[i]) * partialTicks;
        }
        double[] vertices1 = {
                x1, y + pointsY[0], z2,
                x, y + pointsY[1], z2,
                x, y + pointsY[4], z,
                x1, y + pointsY[3], z
        };
        double[] texCoords1 = {
                0, 0,
                0.5, 0,
                0.5, 0.5,
                0, 0.5
        };
        double[] vertices2 = {
                x, y + pointsY[1], z2,
                x2, y + pointsY[2], z2,
                x2, y + pointsY[5], z,
                x, y + pointsY[4], z
        };
        double[] texCoords2 = {
                0.5, 0,
                1, 0,
                1, 0.5,
                0.5, 0.5
        };
        double[] vertices3 = {
                x1, y + pointsY[3], z,
                x, y + pointsY[4], z,
                x, y + pointsY[7], z1,
                x1, y + pointsY[6], z1
        };
        double[] texCoords3 = {
                0, 0.5,
                0.5, 0.5,
                0.5, 1,
                0, 1
        };
        double[] vertices4 = {
                x, y + pointsY[4], z,
                x2, y + pointsY[5], z,
                x2, y + pointsY[8], z1,
                x, y + pointsY[7], z1
        };
        double[] texCoords4 = {
                0.5, 0.5,
                1, 0.5,
                1, 1,
                0.5, 1
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

    public void render(TileUnstableTeleportation te, BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        renderSquare(te, bufferbuilder, partialTicks, x, y, z, 2.0, 0xFFFFFFFF);
    }

    public void render(@Nonnull TileUnstableTeleportation te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int activeTextureUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableLighting();
        noiseShader.use();
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.enableTexture2D();
        this.bindTexture(RenderTileTeleportation.TEXTURE_TP);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.enableTexture2D();
        this.bindTexture(TEXTURE_NOISE);
        noiseShader.uniform1i("tex1", 1);
        noiseShader.uniform2f("rand", (float) te.randVec3.x, (float) te.randVec3.y);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.render(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
        tessellator.draw();
        noiseShader.release();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GL11.glPopMatrix();
        GL11.glPopAttrib();

        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
        GlStateManager.setActiveTexture(activeTextureUnit);
    }
}
