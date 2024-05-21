package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.Entity;

public class CommandOppositeVelocity extends OrderCommand {
    public CommandOppositeVelocity(int order, boolean need_sync) {
        super(order, need_sync);
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            Vec3d speed = emptyMagic.getSpeed();
            speed.mul(-1);
            emptyMagic.setSpeed(speed);
            emptyMagic.shouldSyncSpeed = true;
        }
    }
}
