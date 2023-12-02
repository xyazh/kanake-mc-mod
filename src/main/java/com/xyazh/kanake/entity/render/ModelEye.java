package com.xyazh.kanake.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class ModelEye extends ModelBase {
    public ModelRenderer field_178710_a0;
    public ModelRenderer field_178710_a1;
    public ModelRenderer field_178710_a2;
    public ModelRenderer field_178710_a3;
    public ModelRenderer field_178710_a4;
    public ModelRenderer field_178710_aChild;

    public ModelEye() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_178710_a2 = new ModelRenderer(this, 0, 28);
        this.field_178710_a2.mirror = true;
        this.field_178710_a2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_178710_a2.addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, 0.0F);
        this.field_178710_a3 = new ModelRenderer(this, 16, 40);
        this.field_178710_a3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_178710_a3.addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12, 0.0F);
        this.field_178710_a4 = new ModelRenderer(this, 16, 40);
        this.field_178710_a4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_178710_a4.addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12, 0.0F);
        this.field_178710_aChild = new ModelRenderer(this, 8, 0);
        this.field_178710_aChild.setRotationPoint(0.0F, 0.0F, -8.25F);
        this.field_178710_aChild.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1, 0.0F);
        this.field_178710_a0 = new ModelRenderer(this, 0, 0);
        this.field_178710_a0.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_178710_a0.addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16, 0.0F);
        this.field_178710_a1 = new ModelRenderer(this, 0, 28);
        this.field_178710_a1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.field_178710_a1.addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12, 0.0F);
        this.field_178710_a0.addChild(this.field_178710_aChild);
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.field_178710_a2.render(f5);
        this.field_178710_a3.render(f5);
        this.field_178710_a4.render(f5);
        this.field_178710_a0.render(f5);
        this.field_178710_a1.render(f5);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
