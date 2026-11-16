package com.xyazh.kanake.util;

import com.xyazh.kanake.damage.CleanDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.chunk.Chunk;

import java.util.List;

public class KillUtil {
     public static void remove(Entity target){
        List<Entity> entities = target.world.loadedEntityList;
        int index = -1;
        for(int i = 0;i<entities.size();i++){
            if (entities.get(i) == target){
                index = i;
                break;
            }
        }
        if(index == -1){
            return;
        }
        Chunk chunk = target.world.getChunkFromBlockCoords(target.getPosition());
        entities.set(index,null);
        entities.remove(null);
        chunk.removeEntity(target);
    }

    public static void damage(Entity target){
        target.hurtResistantTime = 0;
        DamageSource damageSource = new DamageSource("");
        damageSource.setDamageBypassesArmor();
        damageSource.setDamageAllowedInCreativeMode();
        damageSource.setDamageIsAbsolute();
        target.attackEntityFrom(damageSource, Float.POSITIVE_INFINITY);
    }

    public static void deHealth(Entity target){
        if(!(target instanceof EntityLivingBase)){
            return;
        }
        EntityLivingBase entityLivingBase = ((EntityLivingBase) target);
        entityLivingBase.setHealth(0);
        IAttributeInstance attribute =  entityLivingBase.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        attribute.setBaseValue(0);
    }

    public static void dead(Entity target){
        target.onKillCommand();
        target.setDead();
        target.isDead = true;
    }
}
