package com.xyazh.kanake.block.blocks;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.particle.simple.particles.TestParticle;
import com.xyazh.kanake.particle.simple.particles.TestParticle1;
import com.xyazh.kanake.particle.simple.particles.WanaParticle1;
import com.xyazh.kanake.potion.buff.PotionKoori;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
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

public class BlockNIce extends Block {
    public BlockNIce(String name)
    {
        super(Material.FIRE);
        setUnlocalizedName(name);
        setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
        this.setTickRandomly(true);
        setLightLevel(0.2F);
    }

    public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand)
    {
        if(rand.nextFloat()<0.33){
            this.breakBlock(worldIn,pos,this.getDefaultState());
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand){
        for(int i=0;i<10;i++){
            SimpleParticle particle = new WanaParticle1(worldIn,pos.getX()+rand.nextFloat(),pos.getY(),pos.getZ()+rand.nextFloat(),0,0,0);
        }
    }



    private void trigger(World world,BlockPos pos,Entity entity){
        if(entity instanceof EntityLivingBase){
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            DamageSource damageSource = DamageSource.MAGIC;
            entityLivingBase.attackEntityFrom(damageSource, 8);
            entityLivingBase.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI,200));
            this.breakBlock(world,pos,this.getDefaultState());
        }
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if(!this.equals(worldIn.getBlockState(pos).getBlock())){
           return;
        }
        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockState(pos, Blocks.ICE.getDefaultState(), 11);
        if (worldIn.isRemote) {
            for(int i = 0;i<300;i++){
                SimpleParticle particle = new TestParticle1(worldIn, pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,0,0,0);
            }
        }
    }

    @Override
    public void onEntityWalk(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn)
    {
        this.trigger(worldIn,pos,entityIn);
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn)
    {
        this.trigger(worldIn,pos,entityIn);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state)
    {
        return false;
    }

    @Override
    public int quantityDropped(@Nonnull Random random)
    {
        return 0;
    }

    @Override
    public int tickRate(@Nonnull World worldIn)
    {
        return 30;
    }

    @Override
    public boolean requiresUpdates()
    {
        return false;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}
