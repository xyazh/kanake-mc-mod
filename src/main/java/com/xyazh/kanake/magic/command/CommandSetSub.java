package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandSetSub extends OrderCommand {
    public final boolean isSubMagic;
    public CommandSetSub(int order, boolean need_sync, boolean isSubMagic) {
        super(order, need_sync);
        this.isSubMagic = isSubMagic;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic magic = (EntityEmptyMagic) entity;
            magic.isSubMagic = this.isSubMagic;
        }
    }
}
