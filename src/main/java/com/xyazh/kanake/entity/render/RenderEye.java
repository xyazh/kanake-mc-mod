package com.xyazh.kanake.entity.render;


import com.xyazh.kanake.entity.EntityEye;
import com.xyazh.kanake.render.RenderBezierTube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


class RenderEye extends RenderLiving<EntityEye> {
    private final RenderBezierTube renderBezierTube = new RenderBezierTube();
    private static final ResourceLocation MY_ENTITY_TEXTURE = new ResourceLocation("kanake","textures/entity/modeleye.png");

    public RenderEye(RenderManager manager) {
        super(manager,new com.xyazh.kanake.entity.render.ModelEye(),0.5f);
        this.renderBezierTube.width = 0.01;
        this.renderBezierTube.step = 0.05;
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityEye entity) {
        return MY_ENTITY_TEXTURE;
    }

    @Override
    public void doRender(@Nonnull EntityEye entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        y += 0.5;
        Tessellator tessellator = Tessellator.getInstance();
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
        this.render(tessellator,entity,x,y,z,entityYaw,partialTicks);
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
    }

    private void render(Tessellator tessellator,EntityEye entity, double x, double y, double z, float entityYaw, float partialTicks){
    }

}