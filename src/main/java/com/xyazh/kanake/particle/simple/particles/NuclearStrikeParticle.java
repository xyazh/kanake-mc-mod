package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityNuclearStrike;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.render.NuclearStrikeRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Long-lived procedural visual owner for a Nuclear Strike ritual. The warning
 * arrays, descending star core, fireball, shockwaves, mushroom cloud and
 * radiation ring are all rebuilt from the entity age each frame.
 */
@SideOnly(Side.CLIENT)
public class NuclearStrikeParticle extends SimpleParticle {
    private final int sourceId;

    public NuclearStrikeParticle(EntityNuclearStrike source) {
        super(source.world, source.posX, source.posY, source.posZ, 0.0D, 0.0D, 0.0D);
        this.sourceId = source.getEntityId();
        this.canCollide = false;
        this.disLight = true;
        this.alpha = 1.0F;
        this.particleMaxAge = EntityNuclearStrike.TOTAL_TICKS + 20;
        this.particleScale = 1.0F;
        this.setRBGColorF(1.0F, 0.55F, 0.18F);
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
        if (!(entity instanceof EntityNuclearStrike) || entity.isDead) {
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
        if (!(entity instanceof EntityNuclearStrike)) {
            return;
        }
        EntityNuclearStrike strike = (EntityNuclearStrike) entity;
        double x = strike.prevPosX + (strike.posX - strike.prevPosX) * partialTicks - interpPosX;
        double y = strike.prevPosY + (strike.posY - strike.prevPosY) * partialTicks - interpPosY;
        double z = strike.prevPosZ + (strike.posZ - strike.prevPosZ) * partialTicks - interpPosZ;
        double topY = strike.world.getActualHeight() - interpPosY;
        NuclearStrikeRenderHelper.render(buffer, x, y, z,
                strike.getSpellAge() + partialTicks, strike.isAirburst(), topY);
    }
}
