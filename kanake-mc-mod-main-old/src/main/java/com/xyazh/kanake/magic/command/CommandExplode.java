package com.xyazh.kanake.magic.command;

import net.minecraft.entity.Entity;

public class CommandExplode extends OrderCommand {
    protected float strength;
    public CommandExplode(int order, boolean need_sync, float strength) {
        super(order, need_sync);
        this.strength = strength;
    }

    public void execute(Entity entity) {
        entity.world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, this.strength, true);
    }
}
