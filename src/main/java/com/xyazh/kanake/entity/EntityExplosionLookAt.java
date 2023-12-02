package com.xyazh.kanake.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityExplosionLookAt extends EntityFireBallLookAt{
    public EntityExplosionLookAt(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void setDead() {
        super.setDead();
        if(!this.world.isRemote){
            this.world.createExplosion(this.shootingEntity==null?this:this.shootingEntity,this.posX,this.posY,this.posZ,3,true);
        }
    }
}
