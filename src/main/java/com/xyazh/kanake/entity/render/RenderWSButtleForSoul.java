package com.xyazh.kanake.entity.render;



import com.xyazh.kanake.entity.EntityWSSoulBullet;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;


class RenderWSButtleForSoul extends RenderLiving<EntityWSSoulBullet> {

    private static final ResourceLocation MY_ENTITY_TEXTURE = new ResourceLocation("kanake","textures/entity/modelwsbullet.png");

    public RenderWSButtleForSoul(RenderManager manager) {
        super(manager,new com.xyazh.kanake.entity.render.ModelWSBulletForSoul(),0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityWSSoulBullet entity) {
        return MY_ENTITY_TEXTURE;
    }

}