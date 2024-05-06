package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandStop extends OrderCommand {
    public final boolean isStop;
    public CommandStop(int order, boolean need_sync, boolean isStop) {
        super(order, need_sync);
        this.isStop = isStop;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.isStop = this.isStop;
        }
    }
}
