package com.xyazh.kanake.entity.render;


import com.xyazh.kanake.entity.EntityEye;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


class RenderEye extends RenderLiving<EntityEye> {
    private static final ResourceLocation MY_ENTITY_TEXTURE = new ResourceLocation("kanake", "textures/entity/modeleye.png");

    public RenderEye(RenderManager manager) {
        super(manager, new ModelEye(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityEye entity) {
        return MY_ENTITY_TEXTURE;
    }
}