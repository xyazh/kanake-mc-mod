package com.xyazh.kanake.entity.render;

import com.xyazh.kanake.entity.EntityBall;
import com.xyazh.kanake.entity.EntityBiimu;
import com.xyazh.kanake.render.LaserBeamRenderer;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderBiimu extends Render<EntityBiimu> {
    static final LaserBeamRenderer RENDER = new LaserBeamRenderer();

    public RenderBiimu(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBiimu entity) {
        return null;
    }

    @Override
    public void doRender(@Nonnull EntityBiimu entity, double x, double y, double z, float entityYaw, float partialTicks) {
        RENDER.render();
    }
}
