package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.Entity;

public class CommandSeries extends OrderCommand {
    public final boolean isSubMagic;
    public final int num;

    public CommandSeries(int order, boolean needSync,int num, boolean isSubMagic) {
        super(order, needSync);
        this.isSubMagic = isSubMagic;
        this.num = num;
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
        Vec3d speed = entity1.getSpeed();
        for(int i=0;i<this.num;i++){
            EntityEmptyMagic entityEmptyMagic = entity1.copy();
            entityEmptyMagic.isSubMagic = this.isSubMagic;
            Vec3d rand = Vec3d.random(0.05);
            rand.add(speed);
            entityEmptyMagic.setSpeed(rand);
            entity.world.spawnEntity(entityEmptyMagic);
        }
    }
}
