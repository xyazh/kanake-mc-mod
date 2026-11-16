package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.Entity;

public class CommandCrossSpawn extends OrderCommand {
    public final boolean isSubMagic;

    public CommandCrossSpawn(int order, boolean needSync, boolean isSubMagic) {
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
        EntityEmptyMagic entityEmptyMagic1, entityEmptyMagic2, entityEmptyMagic3,entityEmptyMagic4;
        entityEmptyMagic1 = entity1.copy();
        entityEmptyMagic2 = entity1.copy();
        entityEmptyMagic3 = entity1.copy();
        entityEmptyMagic4 = entity1.copy();
        entityEmptyMagic1.setForward(new Vec3d(1.0,0.0,0.0));
        entityEmptyMagic2.setForward(new Vec3d(-1.0,0.0,0.0));
        entityEmptyMagic3.setForward(new Vec3d(0.0,0.0,1.0));
        entityEmptyMagic4.setForward(new Vec3d(0.0,0.0,-1.0));
        entityEmptyMagic1.isSubMagic = isSubMagic;
        entityEmptyMagic2.isSubMagic = isSubMagic;
        entityEmptyMagic3.isSubMagic = isSubMagic;
        entityEmptyMagic4.isSubMagic = isSubMagic;
        entity1.world.spawnEntity(entityEmptyMagic1);
        entity1.world.spawnEntity(entityEmptyMagic2);
        entity1.world.spawnEntity(entityEmptyMagic3);
        entity1.world.spawnEntity(entityEmptyMagic4);
    }
}
