package com.xyazh.kanake.item.items;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.clean.TileClean;
import com.xyazh.kanake.entity.*;
import com.xyazh.kanake.gui.test.GuiHandlerTest;
import com.xyazh.kanake.util.TpHelper;
import com.xyazh.kanake.world.ModWorlds;
import com.xyazh.kanake.world.provider.ProviderArea;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;

public class ItemTestItem extends ItemBase {

    public ItemTestItem(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemStack = new ItemStack(this);
        //player.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI,1000));
        /*int dim = ModWorlds.getDimIdByName(ProviderArea.providerName);
        TpHelper.changeDimension(player,dim);8*/
        //ManaData.add(player,50);
        /*if(!world.isRemote){
            for(int i=0;i<100;i++){
                Vec3d m = Vec3d.fromPitchYaw(
                        (float) (player.rotationPitch + Kanake.rand.nextGaussian()*4),
                        (float) (player.rotationYaw + Kanake.rand.nextGaussian()*4));
                EntityShoot entity = new EntityExplosion(world);
                entity.entityShoot(player,m);
                world.spawnEntity(entity);
            }
        }*/

        /*if (!world.isRemote) {
            Vec3d m = Vec3d.fromPitchYaw(
                    (float) (player.rotationPitch),
                    (float) (player.rotationYaw));
            EntityShoot entity = new EntityBlackHole(world);
            entity.entityShoot(player, m);
            world.spawnEntity(entity);
        }*/
        if (!world.isRemote) {
            EntityBiimu entity = new EntityBiimu(world);
            entity.setPosition(player.posX, player.posY, player.posZ);
            world.spawnEntity(entity);
        }
        /*if (!world.isRemote){
            player.openGui(Kanake.instance, GuiHandlerTest.GUI_ID, world, 0, 0, 0);
        }*/
        return super.onItemRightClick(world, player, hand);
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
        //target.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI,1000));
        return super.hitEntity(stack, target, attacker);
    }
}
