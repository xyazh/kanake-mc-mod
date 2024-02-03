package com.xyazh.kanake.block.blocks.clean.render;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.clean.TileClean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderClean extends TileEntitySpecialRenderer<TileClean> {
    public static final ResourceLocation TEXTURE_TP = new ResourceLocation(Kanake.MODID, "textures/misc/kaimeitsu.png");
    public static RenderCrystal RENDER_CRYSTAL;
    protected static boolean init = true;
    public RenderClean() {
        super();
    }

    public boolean isGlobalRenderer(@Nonnull TileClean te) {
        return true;
    }

    public void render(@Nonnull TileClean te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(init){
            RENDER_CRYSTAL = new RenderCrystal(Minecraft.getMinecraft().getRenderManager());
            init = false;
        }
        if(te.shouldRenderCircle()){
            this.bindTexture(TEXTURE_TP);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x,y,z);
            float r = (float) (te.lastRotate + (te.innerRotate - te.lastRotate) * partialTicks);
            GlStateManager.rotate(r,0,1,0);
            GlStateManager.translate(-x,-y,-z);
            GlStateManager.enableTexture2D();
            GlStateManager.disableFog();
            GlStateManager.disableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int i = 15728880;
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            this.render(te, bufferbuilder, x, y, z, partialTicks, destroyStage, alpha);
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
        }
        RENDER_CRYSTAL.doRender(te,x+0.5,y+1,z+0.5,partialTicks);
    }

    private void render(TileClean te, BufferBuilder bufferbuilder, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int size = te.range*2;
        double x1 = x - size / 2.0;
        double z1 = z - size / 2.0;
        double x2 = x + size / 2.0;
        double z2 = z + size / 2.0;
        y += te.lastRenderY + (te.renderY - te.lastRenderY) * (double) partialTicks;
        int i = 15728880;
        int j = i % 65536;
        int k = i / 65536;
        bufferbuilder.pos(x1, y, z1).tex(0,0).color(255, 255, 255, 164).lightmap(j,k).endVertex();
        bufferbuilder.pos(x1, y, z2).tex(0,1).color(255, 255, 255, 164).lightmap(j,k).endVertex();
        bufferbuilder.pos(x2, y, z2).tex(1,1).color(255, 255, 255, 164).lightmap(j,k).endVertex();
        bufferbuilder.pos(x2, y, z1).tex(1,0).color(255, 255, 255, 164).lightmap(j,k).endVertex();
    }
}
