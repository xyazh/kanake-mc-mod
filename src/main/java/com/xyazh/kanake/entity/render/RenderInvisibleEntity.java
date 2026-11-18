package com.xyazh.kanake.entity.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Explicit no-op renderer for projectile/effect entities that are represented
 * only by particles. Without a renderer some environments fall back to a
 * default white entity model, which must never be visible for these entities.
 */
@SideOnly(Side.CLIENT)
public class RenderInvisibleEntity<T extends Entity> extends Render<T> {
    public RenderInvisibleEntity(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull T entity) {
        return null;
    }

    @Override
    public void doRender(@Nonnull T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Intentionally empty: the entity is rendered by particles only.
    }
}
