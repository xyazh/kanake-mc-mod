package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandNoInherit extends OrderCommand {
    public CommandNoInherit(int order, boolean needSync) {
        super(order, needSync);
    }

    public int getCount(GlyphOrder order) {
        return Math.max(1, GlyphData.getInt(order.data, GlyphData.COUNT, 1));
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        // 真正的“不继承”发生在 copyForChild() 复制纹章列表时。
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
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.COUNT, Math.max(1, data.getInteger(GlyphData.COUNT)) + 1);
        }
    }
}
