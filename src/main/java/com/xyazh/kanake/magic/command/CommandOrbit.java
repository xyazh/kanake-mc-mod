package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandOrbit extends OrderCommand {
    public CommandOrbit(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        int speedLevel = GlyphData.getInt(glyphOrder.data, GlyphData.SPEED, 0);
        int radiusLevel = GlyphData.getInt(glyphOrder.data, GlyphData.RADIUS, 0);
        magic.orbit = true;
        magic.orbitRadius = 2.0 + 0.5 * radiusLevel;
        magic.orbitSpeed = 0.05 + 0.01 * speedLevel;
        magic.orbitMode = GlyphData.getInt(glyphOrder.data, GlyphData.MODE, 0) % 3;
        magic.g = 0;
        magic.gdy = 0;
        magic.isStop = false;
        magic.continuousManaCost = 1.0;
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
        return crystalType == 1 || crystalType == 2 || crystalType == 4;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.SPEED, data.getInteger(GlyphData.SPEED) + 1);
        } else if (crystalType == 2) {
            data.setInteger(GlyphData.RADIUS, data.getInteger(GlyphData.RADIUS) + 1);
        } else if (crystalType == 4) {
            int mode = data.getInteger(GlyphData.MODE);
            data.setInteger(GlyphData.MODE, (mode + 1) % 3);
        }
    }
}
