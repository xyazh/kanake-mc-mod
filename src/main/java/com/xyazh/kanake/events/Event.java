package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.damage.KillSlimeDamage;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class Event {
    @SubscribeEvent
    public static void drop(LivingDropsEvent event){
        Entity entity = event.getEntity();
        if(entity.world.isRemote){
            return;
        }
        if(entity instanceof EntitySlime && event.getSource() instanceof KillSlimeDamage){
            int size = ((EntitySlime)entity).getSlimeSize();
            float f;
            if(size>=4){
                f = 0.5f;
            }else if(size>=2){
                f = 0.125f;
            }else{
                f = 0.03125f;
            }
            if(Kanake.rand.nextFloat() < f){
                EntityItem entityItem = new EntityItem(entity.world);
                entityItem.setPosition(entity.posX,entity.posY,entity.posZ);
                ItemStack itemStack = new ItemStack(ModItems.SLIME_CORE);
                entityItem.setItem(itemStack);
                event.getDrops().add(entityItem);
            }
        }
        if(entity instanceof EntityPlayer || entity instanceof EntityVillager){
            EntityItem entityItem = new EntityItem(entity.world);
            entityItem.setPosition(entity.posX,entity.posY,entity.posZ);
            ItemStack itemStack = new ItemStack(ModItems.HEART);
            entityItem.setItem(itemStack);
            event.getDrops().add(entityItem);
        }
    }
}
