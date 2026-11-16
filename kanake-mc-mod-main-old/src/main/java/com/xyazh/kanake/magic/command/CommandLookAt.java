package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandLookAt extends OrderCommand {
    public int type;

    public CommandLookAt(int order, boolean need_sync, int type) {
        super(order, need_sync);
        this.type = type;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.lookAtType = this.type;
        }
    }
}
