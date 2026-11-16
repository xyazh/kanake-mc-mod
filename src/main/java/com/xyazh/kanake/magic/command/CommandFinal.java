package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;

public class CommandFinal extends OrderCommand {
    public CommandFinal(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        if (magic.world.isRemote) {
            magic.clientFinishEffects();
            return;
        }
        magic.effects.clear();
        magic.executeFinishOrders();
        magic.order.clear();
        magic.terminated = true;
        magic.isStop = true;
        magic.orbit = false;
        magic.continuousManaCost = 0;
        magic.cooldown = Integer.MAX_VALUE;
        magic.setDead();
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
