package com.xyazh.kanake.entity;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class EntityBlackHole extends EntityDestroy {
    public float range;

    public boolean hasNoGravity()
    {
        return true;
    }

    public EntityBlackHole(World worldIn) {
        super(worldIn);
        this.range = this.radius * 32;
    }

    @Override
    public void setRadius(float radius) {
        super.setRadius(radius);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.attract();
    }

    public void attract(){
     Vec3d targetPos = Vec3d.fromEntityPos(this);
     AxisAlignedBB box = new AxisAlignedBB(targetPos.x - range, targetPos.y - range, targetPos.z - range, targetPos.x + range, targetPos.y + range, targetPos.z + range);
     List<Entity> list = world.getEntitiesWithinAABB(Entity.class, box,entity-> entity != this && entity.getDistance(this) <= range && entity != this.shootingEntity);
     for (Entity entity : list){
         Vec3d pos = Vec3d.fromEntityPos(entity);
         Vec3d motion = new Vec3d();
         motion.sub(targetPos, pos);
         double l = motion.normalizeAndLength();
         motion.mul(this.calculateSpeed(l));
         entity.motionX += motion.x;
         entity.motionY += motion.y;
         entity.motionZ += motion.z;
     }
    }

    private double calculateSpeed(double distance){
        double f = 1 / distance * distance;
        return 0;
    }

    @Override
    protected void entityInit() {

    }
}
