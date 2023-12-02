package com.xyazh.kanake.block.blocks;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Random;

public class BlockNFire extends Block {
    public BlockNFire(String name)
    {
        super(Material.FIRE);
        setUnlocalizedName(name);
        setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
        this.setTickRandomly(true);
        setLightLevel(0.4F);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if(rand.nextFloat()<0.33){
            this.breakBlock(worldIn,pos,this.getDefaultState());
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand){
        for(int i=0;i<10;i++){
            worldIn.spawnParticle(ModParticles.WANA_PARTICLES,pos.getX()+rand.nextFloat(),pos.getY(),pos.getZ()+rand.nextFloat(),0,0,0);
        }
    }



    private void trigger(World world,BlockPos pos,Entity entity){
        if(entity instanceof EntityLivingBase){
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            DamageSource damageSource = DamageSource.IN_FIRE;
            entityLivingBase.attackEntityFrom(damageSource, 8);
            entityLivingBase.setFire(10);
            this.breakBlock(world,pos,this.getDefaultState());
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!this.equals(worldIn.getBlockState(pos).getBlock())){
           return;
        }
        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
        if (worldIn.isRemote) {
            for(int i = 0;i<300;i++){
                worldIn.spawnParticle(ModParticles.TEST_PARTICLES,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,0,0,0);
            }
        }
    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        this.trigger(worldIn,pos,entityIn);
    }

    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        this.trigger(worldIn,pos,entityIn);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public int quantityDropped(Random random)
    {
        return 0;
    }

    public int tickRate(World worldIn)
    {
        return 30;
    }

    public boolean requiresUpdates()
    {
        return false;
    }

    public boolean isCollidable()
    {
        return false;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}
