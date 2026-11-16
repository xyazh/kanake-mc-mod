package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandMultiCast extends OrderCommand {
    public CommandMultiCast(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        magic.repeatNext = Math.max(1, GlyphData.getInt(glyphOrder.data, GlyphData.COUNT, 2));
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 0;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 3 + 2 * Math.max(1, GlyphData.getInt(order.data, GlyphData.COUNT, 2));
    }

    @Override
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.COUNT, Math.max(2, data.getInteger(GlyphData.COUNT)) + 1);
        }
    }
}
