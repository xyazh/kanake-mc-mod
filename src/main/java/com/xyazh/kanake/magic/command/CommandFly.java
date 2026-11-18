package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandFly extends OrderCommand {
    public enum Mode {
        UP,
        DOWN,
        LAUNCH
    }

    private final Mode mode;

    public CommandFly(int order, boolean needSync, Mode mode) {
        super(order, needSync);
        this.mode = mode;
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        int enhance = GlyphData.getInt(glyphOrder.data, GlyphData.ENHANCE, 0);
        switch (this.mode) {
            case UP:
                magic.setForward(new Vec3d(0, 1, 0));
                magic.speed = 0.2 + 0.05 * enhance;
                break;
            case DOWN:
                magic.setForward(new Vec3d(0, -1, 0));
                magic.speed = 0.2 + 0.05 * enhance;
                break;
            case LAUNCH:
                magic.setForward(magic.getForwardVec());
                magic.speed = 0.5 + 0.1 * enhance;
                break;
            default:
                break;
        }
        magic.orbit = false;
        magic.g = 0;
        magic.gdy = 0;
        magic.isStop = false;
        magic.continuousManaCost = this.mode == Mode.LAUNCH ? 1.0 : 0.5;
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return this.mode == Mode.LAUNCH ? 1.0 : 0.5;
    }

    @Override
    public double getSpellSpeed(GlyphOrder order) {
        int enhance = GlyphData.getInt(order.data, GlyphData.ENHANCE, 0);
        return this.mode == Mode.LAUNCH ? 0.5 + 0.1 * enhance : 0.2 + 0.05 * enhance;
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
