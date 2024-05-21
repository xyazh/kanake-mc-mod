package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.Entity;

public class CommandRandomSpeed extends OrderCommand {
    protected double speed;
    public CommandRandomSpeed(int order, boolean need_sync, double speed) {
        super(order, need_sync);
        this.speed = speed;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            Vec3d v1 = Vec3d.random(speed);
            emptyMagic.addSpeed(v1);
            emptyMagic.shouldSyncSpeed = true;
        }
    }
}
