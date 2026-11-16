package com.xyazh.kanake.magic.command;

import net.minecraft.entity.Entity;

public class CommandDead extends OrderCommand {

    public CommandDead(int order, boolean need_sync) {
        super(order, need_sync);
    }

    public void execute(Entity entity) {
        entity.setDead();
    }
}
