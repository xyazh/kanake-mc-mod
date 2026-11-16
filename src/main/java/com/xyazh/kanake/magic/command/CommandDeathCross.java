package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.effect.MagicEffectTask;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandDeathCross extends OrderCommand {
    public CommandDeathCross(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        int enhance = GlyphData.getInt(glyphOrder.data, GlyphData.ENHANCE, 0);
        magic.addEffect(new MagicEffectTask(
                MagicEffectTask.TYPE_DEATH_CROSS,
                60 + 20 * enhance,
                0.5 + 0.25 * enhance,
                4.0 + 1.0 * enhance,
                0.5 + 0.2 * enhance,
                0.5
        ));
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 15;
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
