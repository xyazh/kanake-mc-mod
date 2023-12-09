package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.damage.KillSlimeDamage;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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

    @SubscribeEvent
    public static void p(TickEvent.PlayerTickEvent event){
        if(event.player.world.isRemote){
            return;
        }
        /*EntityPlayer player = event.player;
        BlockPos pos0,pos;
        pos0 = player.getPosition();
        pos = new BlockPos(pos0.getX(),pos0.getY()-1,pos0.getZ());
        World world = player.world;
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(player.isSneaking()){
            if(block.equals(ModBlocks.TEST)){
                block.breakBlock(world,pos,blockState);
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        } else if(world.isAirBlock(pos)){
            world.setBlockState(pos, ModBlocks.TEST.getDefaultState());
        }*/
    }
}
