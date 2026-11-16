package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandColorFlash extends OrderCommand {
    public CommandColorFlash(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        if (glyphOrder.data.hasKey(GlyphData.COLOR)) {
            magic.color = glyphOrder.data.getInteger(GlyphData.COLOR) & 0xFFFFFF;
        } else {
            magic.color = (glyphOrder.uid * 0x9E3779B1 | 0x404040) & 0xFFFFFF;
        }
        magic.spawnColorFlashParticles();
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 1.0;
    }

    @Override
    public boolean canApplyDye() {
        return true;
    }

    @Override
    public void applyDye(NBTTagCompound data, int rgb) {
        data.setInteger(GlyphData.COLOR, rgb & 0xFFFFFF);
    }
}
