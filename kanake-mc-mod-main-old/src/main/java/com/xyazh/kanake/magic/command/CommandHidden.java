package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandHidden extends OrderCommand {

    public CommandHidden(int order, boolean need_sync) {
        super(order, need_sync);
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.hidden = !emptyMagic.hidden;
        }
    }
}
