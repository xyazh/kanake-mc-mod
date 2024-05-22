package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandAddLife extends StaticCommand{
    public final int age;
    public CommandAddLife(int order, boolean need_sync, int age) {
        super(order, need_sync);
        this.age = age;
    }

    @Override
    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            emptyMagic.addLivingMaxAge(this.age);
        }
    }
}
