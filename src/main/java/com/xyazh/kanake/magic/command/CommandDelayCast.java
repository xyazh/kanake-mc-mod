package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandDelayCast extends OrderCommand {
    public CommandDelayCast(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        int count = Math.max(1, GlyphData.getInt(glyphOrder.data, GlyphData.COUNT, 1));
        magic.burstRemaining = count - 1;
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        int delayLevel = GlyphData.getInt(order.data, GlyphData.DELAY, 0);
        return 20 + 20 * delayLevel;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        int count = Math.max(1, GlyphData.getInt(order.data, GlyphData.COUNT, 1));
        int delayLevel = GlyphData.getInt(order.data, GlyphData.DELAY, 0);
        return 2 + count + 0.1 * (20 + 20 * delayLevel);
    }

    @Override
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1 || crystalType == 3;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.COUNT, Math.max(1, data.getInteger(GlyphData.COUNT)) + 1);
        } else if (crystalType == 3) {
            data.setInteger(GlyphData.DELAY, data.getInteger(GlyphData.DELAY) + 1);
        }
    }
}
