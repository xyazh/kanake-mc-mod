package com.xyazh.kanake.entity.render;


import com.xyazh.kanake.entity.EntityEye;
import com.xyazh.kanake.entity.EntityTreeMan;
import com.xyazh.kanake.render.RenderBezierTube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


class RenderTreeMan extends RenderLiving<EntityTreeMan> {
    private static final ResourceLocation MY_ENTITY_TEXTURE = new ResourceLocation("kanake","textures/entity/tree_man.png");

    public RenderTreeMan(RenderManager manager) {
        super(manager,new ModelTreeMan(),0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityTreeMan entity) {
        return MY_ENTITY_TEXTURE;
    }

    @Override
    public void doRender(@Nonnull EntityTreeMan entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}