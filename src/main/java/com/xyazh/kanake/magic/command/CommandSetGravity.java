package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandSetGravity extends OrderCommand {
    public final float num;
    public CommandSetGravity(int order, boolean need_sync,float num) {
        super(order, need_sync);
        this.num = num;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.gdy = 0;
            emptyMagic.g += num;
        }
    }
}
