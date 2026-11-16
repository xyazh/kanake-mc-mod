package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandExplodeMagic extends OrderCommand {
    public CommandExplodeMagic(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        float power = 3.0f + 0.5f * GlyphData.getInt(glyphOrder.data, GlyphData.ENHANCE, 0);
        if (entity.world.isRemote) {
            ((EntityEmptyMagic) entity).spawnExplosionParticles(power);
        } else {
            entity.world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, power, true);
        }
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 10 + 3 * (3.0 + 0.5 * GlyphData.getInt(order.data, GlyphData.ENHANCE, 0));
    }

    @Override
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.ENHANCE, data.getInteger(GlyphData.ENHANCE) + 1);
        }
    }
}
