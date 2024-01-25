package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.clean.TileClean;
import com.xyazh.kanake.damage.CleanDamage;
import com.xyazh.kanake.damage.KillSlimeDamage;
import com.xyazh.kanake.entity.EntityWSKnight;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.item.bauble.ItemFlyNecklace;
import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class Event {
    @SubscribeEvent
    public static void drop(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote) {
            return;
        }
        if (entity instanceof EntitySlime && event.getSource() instanceof KillSlimeDamage) {
            int size = ((EntitySlime) entity).getSlimeSize();
            float f;
            if (size >= 4) {
                f = 0.5f;
            } else if (size >= 2) {
                f = 0.125f;
            } else {
                f = 0.03125f;
            }
            if (Kanake.rand.nextFloat() < f) {
                EntityItem entityItem = new EntityItem(entity.world);
                entityItem.setPosition(entity.posX, entity.posY, entity.posZ);
                ItemStack itemStack = new ItemStack(ModItems.SLIME_CORE);
                entityItem.setItem(itemStack);
                event.getDrops().add(entityItem);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onKnightHeal(LivingHurtEvent event) {
        Entity entity = event.getSource().getTrueSource();
        if (entity instanceof EntityWSKnight) {
            float damage = event.getAmount();
            if (damage > 0) {
                ((EntityWSKnight) entity).heal(damage);
                World world = entity.world;
                int amount = (int) damage;
                ModParticles.remoteSpawnParticle(world, amount,ModParticles.HEAL_PARTICLES,
                        entity.posX, entity.posY, entity.posZ,
                        0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onKnightDeath(LivingDeathEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(entity instanceof EntityWSKnight){
            World world = entity.world;
            world.addWeatherEffect(new EntityLightningBolt(world,entity.posX,entity.posY,entity.posZ,false));
            if(!world.isRemote){
                world.createExplosion(entity,entity.posX, entity.posY, entity.posZ, 1,true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onCleanDamage(LivingDamageEvent event) {
        if (event.getSource() instanceof CleanDamage) {
            event.setCanceled(false);
            event.setAmount(TileClean.DAMAGE);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onCleanDamage1(LivingHurtEvent event) {
        if (event.getSource() instanceof CleanDamage) {
            event.setCanceled(false);
            event.setAmount(TileClean.DAMAGE);
        }
    }
}
