package com.xyazh.kanake.item.items;


import com.xyazh.kanake.entity.*;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.KillUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemTestItem extends ItemBase {

    public ItemTestItem(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        for (Entity target : world.getEntities(Entity.class,entity-> true)) {
            KillUtil.damage(target);
            KillUtil.deHealth(target);
            KillUtil.dead(target);
            KillUtil.remove(target);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        /*if(worldIn.isRemote){
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
        int chunkX, chunkZ;
        chunkX = pos.getX() >> 4;
        chunkZ = pos.getZ() >> 4;
        IChunkProvider provider = worldIn.getChunkProvider();
        Chunk thisChunk = provider.getLoadedChunk(chunkX, chunkZ);
        if (thisChunk == null) {
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
        IChunkGenerator generator = worldIn.provider.createChunkGenerator();
        Chunk chunk = generator.generateChunk(chunkX, chunkZ);
        generator.generateStructures(chunk, chunkX, chunkZ);
        chunk.populate(provider,generator);
        int posX, posZ;
        posX = chunkX << 4;
        posZ = chunkZ << 4;
        for (int i = 0; i < 16; i++) {
            for (int y = 0; y < 256; y++) {
                for (int k = 0; k < 16; k++) {
                    int x = posX + i;
                    int z = posZ + k;
                    BlockPos blockPos = new BlockPos(x, y, z);
                    IBlockState oldState = thisChunk.getBlockState(x,y,z);
                    IBlockState newState = chunk.getBlockState(x, y, z);
                    if(Block.getStateId(oldState)==Block.getStateId(newState)){
                        continue;
                    }
                    thisChunk.setBlockState(blockPos,newState);
                    worldIn.notifyBlockUpdate(blockPos,oldState,newState,2);
                }
            }
        }
        thisChunk.setBiomeArray(chunk.getBiomeArray());*/
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
        KillUtil.damage(target);
        KillUtil.deHealth(target);
        KillUtil.dead(target);
        KillUtil.remove(target);
        return true;
    }
}
