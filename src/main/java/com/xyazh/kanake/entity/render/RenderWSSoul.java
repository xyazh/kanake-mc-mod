package com.xyazh.kanake.entity.render;


import com.xyazh.kanake.entity.EntityWSSoul;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


class RenderWSSoul extends RenderLiving<EntityWSSoul> {

    private static final ResourceLocation MY_ENTITY_TEXTURE = new ResourceLocation("kanake","textures/entity/modelwssoul.png");

    public RenderWSSoul(RenderManager manager) {
        super(manager,new net.minecraft.client.model.ModelGhast(),0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityWSSoul entity) {
        return MY_ENTITY_TEXTURE;
    }

}