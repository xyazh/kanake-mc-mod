package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;

public class CommandSpawn extends OrderCommand {
    public final boolean isSubMagic;

    public CommandSpawn(int order, boolean needSync, boolean isSubMagic) {
        super(order, needSync);
        this.isSubMagic = isSubMagic;
    }

    public void execute(Entity entity) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        if(entity.world.isRemote){
            return;
        }
        EntityEmptyMagic entity1 = (EntityEmptyMagic) entity;
        if(entity1.isSubMagic){
            return;
        }
        EntityEmptyMagic entityEmptyMagic = entity1.copy();
        entityEmptyMagic.isSubMagic = this.isSubMagic;
        entity.world.spawnEntity(entityEmptyMagic);
    }
}
