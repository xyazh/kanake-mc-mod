package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandNoGravity extends OrderCommand {

    public CommandNoGravity(int order, boolean need_sync) {
        super(order, need_sync);
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.gdy = 0;
            emptyMagic.g = 0;
        }
    }
}
