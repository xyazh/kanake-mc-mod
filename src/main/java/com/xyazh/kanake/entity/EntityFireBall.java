package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityFireBall extends EntityShoot{
    public EntityFireBall(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i <= 20; i++) {
                this.world.spawnParticle(ModParticles.SPARK_PARTICLES, posX, posY, posZ, 0, 0, 0);
                this.world.spawnParticle(ModParticles.SPARK_PARTICLES1, posX, posY, posZ, 0, 0, 0);
            }
        }
        if(this.inWater){
            this.setDead();
            this.setDeadParticle();
        }
        if(this.onHurt()){
            this.setDead();
            this.setDeadParticle();
        }
        if(this.collided || this.isInCanBurnBlock()){
            this.setFire();
            this.setDeadParticle();
            this.setDead();
        }
        if(this.isInWeb){
            if(!this.world.isRemote){
                BlockPos pos = this.getPosition();
                IBlockState blockStateWeb = this.world.getBlockState(pos);
                Block blockWeb = blockStateWeb.getBlock();
                blockWeb.breakBlock(world,pos,blockStateWeb);
                this.world.setBlockState(pos,Blocks.AIR.getDefaultState());
            }
            this.setDeadParticle();
            this.setDead();
        }
    }

    protected BlockPos getBlockPosition() {
        if(this.blockPos == null){
            this.blockPos = this.getPosition();
        }
        return this.blockPos;
    }

    protected boolean isInCanBurnBlock(){
        IBlockState blockState = this.world.getBlockState(this.getBlockPosition());
        Material material = blockState.getMaterial();
        return material.getCanBurn();
    }

    protected void setDeadParticle(){
        if (world.isRemote) {
            for (int i = 0; i <= 200; i++) {
                this.world.spawnParticle(ModParticles.TEST_PARTICLES, posX, posY, posZ, 0, 0, 0);
            }
        }
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    protected boolean onHurt(){
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(posX+0.4,posY+0.4,posZ+0.4,posX-0.4,posY-0.4,posZ-0.4);
        List<EntityLivingBase> entityLivingBases = world.getEntitiesWithinAABB(EntityLivingBase.class,axisAlignedBB);
        boolean flag = false;
        for(EntityLivingBase entityLivingBase:entityLivingBases){
            if(entityLivingBase.equals(this.shootingEntity)){
                continue;
            }
            flag = true;
            DamageSource damageSource = DamageSource.causeIndirectDamage(this, this.shootingEntity);
            damageSource.setFireDamage();
            entityLivingBase.attackEntityFrom(damageSource, 4);
            entityLivingBase.setFire(5);
        }
        return flag;
    }

    public void setFire(){
        if(this.world.isRemote){
            return;
        }
        BlockPos pos = new BlockPos(this.prevPosX,this.prevPosY,this.prevPosZ);
        IBlockState blockState = this.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(blockState.getBlockHardness(world,pos) <= Blocks.LEAVES.getDefaultState().getBlockHardness(world,pos)){
            block.breakBlock(world,pos,blockState);
            this.world.setBlockState(this.getBlockPosition(),Blocks.AIR.getDefaultState());
            this.world.setBlockState(this.getBlockPosition(), Blocks.FIRE.getDefaultState(), 11);
        }
        if (this.world.isAirBlock(pos))
        {
            this.world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
        }
    }
}
