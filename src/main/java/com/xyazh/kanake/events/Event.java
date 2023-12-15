package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.block.blocks.platform.TilePlatform;
import com.xyazh.kanake.damage.KillSlimeDamage;
import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.render.GLRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Loader;
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
    public static void setPlatform(TickEvent.PlayerTickEvent event){
        if(!Kanake.HAS_BAUBLES){
            return;
        }
        EntityPlayer player = event.player;
        if(player.isSneaking()){
            return;
        }
        if(baubles.api.BaublesApi.isBaubleEquipped(player,ModItems.MANA_RING_LOW)<=-1){
           return;
        }
        if(!player.isCreative()){
            if(ManaData.sub(player,0.002)>0){
                return;
            }
        }
        World world = player.world;
        BlockPos pos = player.getPosition();
        int x = pos.getX();
        int y = pos.getY()-((Math.abs(player.motionY)>0.1)?2:1);
        int z = pos.getZ();
        for(int i=-1;i<=1;i++){
            for(int k=-1;k<=1;k++){
                pos = new BlockPos(x+i, y, z+k);
                if(world.isAirBlock(pos)){
                    world.setBlockState(pos,ModBlocks.PLATFORM.getDefaultState());
                }
                TileEntity te = world.getTileEntity(pos);
                if(te instanceof TilePlatform){
                    TilePlatform platform = (TilePlatform) te;
                    platform.age = 4;
                }
            }
        }
    }
}
