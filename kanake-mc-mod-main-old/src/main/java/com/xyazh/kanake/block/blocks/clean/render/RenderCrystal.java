package com.xyazh.kanake.block.blocks.clean.render;

import com.xyazh.kanake.block.blocks.clean.TileClean;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderCrystal {
    private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/endercrystal/endercrystal.png");
    private final ModelCrystal modelCrystal = new ModelCrystal(true);
    protected RenderManager renderManager;

    public RenderCrystal(RenderManager renderManagerIn) {
        this.renderManager = renderManagerIn;
    }

    public void bindTexture(ResourceLocation location)
    {
        this.renderManager.renderEngine.bindTexture(location);
    }

    public void doRender(TileClean tileClean,double x, double y, double z, float partialTicks) {
        float f = (float) tileClean.innerRotate + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        this.bindTexture(ENDER_CRYSTAL_TEXTURES);
        float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = f1 * f1 + f1;
        this.modelCrystal.render( f * 3.0F, f1 * 0.2F,  0.0625F);
        GlStateManager.popMatrix();
    }
}
