package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandSpeed extends OrderCommand {
    public double speed;

    public CommandSpeed(int order, boolean need_sync, double speed) {
        super(order, need_sync);
        this.speed = speed;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.speed += speed;
        }
    }
}
