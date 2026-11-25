package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityGodWrath;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.SuperMagicRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Draws the spread-out array grid for Wrath of God.
 */
@SideOnly(Side.CLIENT)
public class GodWrathArrayParticle extends SimpleParticle {
    private final int sourceId;
    private final boolean[] spentSeen = new boolean[EntityGodWrath.ARRAY_COUNT];
    private final double[] spentStart = new double[EntityGodWrath.ARRAY_COUNT];

    public GodWrathArrayParticle(EntityGodWrath source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityGodWrath.MAX_EXISTENCE_TICKS + 20;
        this.particleScale = 1.0F;
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        Entity entity = this.world.getEntityByID(this.sourceId);
        if (entity == null) {
            if (this.particleAge > 20) {
                this.setExpired();
            }
            return;
        }
        if (!(entity instanceof EntityGodWrath) || entity.isDead) {
            this.setExpired();
            return;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.posX = entity.posX;
        this.posY = entity.posY;
        this.posZ = entity.posZ;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ,
                               float rotationXY, float rotationXZ) {
        Entity entity = this.world.getEntityByID(this.sourceId);
        if (!(entity instanceof EntityGodWrath)) {
            return;
        }
        EntityGodWrath wrath = (EntityGodWrath) entity;
        double x = wrath.prevPosX + (wrath.posX - wrath.prevPosX) * partialTicks - interpPosX;
        double y = wrath.prevPosY + (wrath.posY - wrath.prevPosY) * partialTicks - interpPosY;
        double z = wrath.prevPosZ + (wrath.posZ - wrath.prevPosZ) * partialTicks - interpPosZ;
        double age = wrath.ticksExisted + partialTicks;
        double fade = Math.max(0.0D, Math.min(1.0D,
                (EntityGodWrath.MAX_EXISTENCE_TICKS - age) / EntityGodWrath.FADE_TICKS));
        for (int i = 0; i < EntityGodWrath.ARRAY_COUNT; ++i) {
            if (!this.spentSeen[i] && wrath.isArraySpent(i)) {
                this.spentSeen[i] = true;
                this.spentStart[i] = age;
            }
        }
        SuperMagicRenderHelper.renderGodWrathArrays(buffer, x, y, z, age, fade,
                this.spentSeen, this.spentStart);
    }
}
