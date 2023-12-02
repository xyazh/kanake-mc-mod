package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityExplosion extends EntityFireBall{
    public EntityExplosion(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void setDead() {
        super.setDead();
        if(!this.world.isRemote){
            this.world.createExplosion(this.shootingEntity==null?this:this.shootingEntity,this.posX,this.posY,this.posZ,4,true);
        }
    }
}
