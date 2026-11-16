package com.xyazh.kanake.entity.render;


import com.xyazh.kanake.entity.EntityShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


class RenderShield extends RenderLiving<EntityShield> {
    private static final ResourceLocation MY_ENTITY_TEXTURE = new ResourceLocation("kanake","textures/entity/shield.png");

    public RenderShield(RenderManager manager) {
        super(manager,new ModelShield(),0.0f);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityShield entity) {
        return MY_ENTITY_TEXTURE;
    }

    @Override
    public void doRender(@Nonnull EntityShield entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(true);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
    }
}