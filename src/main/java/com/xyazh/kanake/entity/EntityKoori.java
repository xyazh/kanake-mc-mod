package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.particle.simple.particles.IceParticle;
import com.xyazh.kanake.particle.simple.particles.IceParticle1;
import com.xyazh.kanake.particle.simple.particles.TestParticle1;
import com.xyazh.kanake.potion.buff.PotionKoori;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityKoori extends EntityShoot{
    public EntityKoori(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i <= 10; i++) {
                SimpleParticle particle1 = new IceParticle(world, posX, posY, posZ, 0, 0, 0);
                SimpleParticle particle2 = new IceParticle1(world, posX, posY, posZ, 0, 0, 0);
            }
        }
        if(this.inWater){
            if(!this.world.isRemote){
                BlockPos pos = this.getPosition();
                IBlockState blockStateWeb = this.world.getBlockState(pos);
                Block blockWeb = blockStateWeb.getBlock();
                blockWeb.breakBlock(world,pos,blockStateWeb);
                this.world.setBlockState(pos,Blocks.ICE.getDefaultState());
            }
            this.setDead();
            this.setDeadParticle();
        }
        if(this.onHurt()){
            this.setDead();
            this.setDeadParticle();
        }
        if(this.collided || this.isInCanBurnBlock()){
            this.setIce();
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
            for (int i = 0; i <= 100; i++) {
                SimpleParticle particle = new TestParticle1(world, posX, posY, posZ, 0, 0, 0);
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
            entityLivingBase.attackEntityFrom(damageSource, 4);
            entityLivingBase.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI,100));
        }
        return flag;
    }

    public void setIce(){
        if(this.world.isRemote){
            return;
        }
        BlockPos pos = new BlockPos(this.prevPosX,this.prevPosY,this.prevPosZ);
        IBlockState blockState = this.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if(blockState.getBlockHardness(world,pos) <= Blocks.LEAVES.getDefaultState().getBlockHardness(world,pos)){
            block.breakBlock(world,pos,blockState);
            this.world.setBlockState(this.getBlockPosition(),Blocks.AIR.getDefaultState());
            this.world.setBlockState(this.getBlockPosition(), Blocks.ICE.getDefaultState(), 11);
        }
        if (this.world.isAirBlock(pos))
        {
            this.world.setBlockState(pos, Blocks.ICE.getDefaultState(), 11);
        }
    }
}
