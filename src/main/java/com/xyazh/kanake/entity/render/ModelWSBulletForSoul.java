package com.xyazh.kanake.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class ModelWSBulletForSoul extends ModelBase {
    public ModelRenderer field_78128_a;

    public ModelWSBulletForSoul() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.field_78128_a = new ModelRenderer(this, 0, 0);
        this.field_78128_a.setRotationPoint(0.0F, 21.0F, 0.0F);
        this.field_78128_a.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F);
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.field_78128_a.offsetX, this.field_78128_a.offsetY, this.field_78128_a.offsetZ);
        GlStateManager.translate(this.field_78128_a.rotationPointX * f5, this.field_78128_a.rotationPointY * f5, this.field_78128_a.rotationPointZ * f5);
        GlStateManager.scale(0.33D, 0.33D, 0.33D);
        GlStateManager.translate(-this.field_78128_a.offsetX, -this.field_78128_a.offsetY, -this.field_78128_a.offsetZ);
        GlStateManager.translate(-this.field_78128_a.rotationPointX * f5, -this.field_78128_a.rotationPointY * f5, -this.field_78128_a.rotationPointZ * f5);
        this.field_78128_a.render(f5);
        GlStateManager.popMatrix();
    }
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
