package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;

public class CommandTerminate extends OrderCommand {
    public CommandTerminate(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic magic = (EntityEmptyMagic) entity;
            magic.terminateAll();
            if (!magic.world.isRemote) {
                magic.setDead();
            }
        }
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 0;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 2.0;
    }
}
