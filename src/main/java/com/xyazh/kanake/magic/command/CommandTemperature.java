package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandTemperature extends OrderCommand {
    protected int temperature;
    public CommandTemperature(int order, boolean need_sync, int temperature) {
        super(order, need_sync);
        this.temperature = temperature;
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            ((EntityEmptyMagic) entity).temperature += this.temperature;
        }
    }
}
