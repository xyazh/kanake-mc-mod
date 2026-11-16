package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandHidden extends OrderCommand {

    public CommandHidden(int order, boolean need_sync) {
        super(order, need_sync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.hidden = !emptyMagic.hidden;
            emptyMagic.hiddenInherit = GlyphData.getInt(glyphOrder.data, GlyphData.INHERIT, 0) > 0;
        }
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 5.0;
    }

    @Override
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.INHERIT, data.getInteger(GlyphData.INHERIT) + 1);
        }
    }
}
