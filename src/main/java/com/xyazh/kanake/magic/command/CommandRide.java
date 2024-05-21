package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandRide extends OrderCommand {

    public CommandRide(int order, boolean need_sync) {
        super(order, need_sync);
    }

    public void execute(Entity entity) {
        if (entity instanceof EntityEmptyMagic) {
            EntityEmptyMagic emptyMagic = (EntityEmptyMagic) entity;
            if(emptyMagic.shootingEntity == null){
                return;
            }
            if(emptyMagic.shootingEntity instanceof EntityPlayerMP){
                EntityPlayerMP player = (EntityPlayerMP) emptyMagic.shootingEntity;
                player.setPosition(emptyMagic.posX, emptyMagic.posY, emptyMagic.posZ);
                player.connection.setPlayerLocation(emptyMagic.posX, emptyMagic.posY, emptyMagic.posZ, 0,0);
            }
            emptyMagic.shootingEntity.startRiding(emptyMagic);
        }
    }
}
