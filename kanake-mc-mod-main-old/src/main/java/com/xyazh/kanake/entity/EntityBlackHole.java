package com.xyazh.kanake.entity;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;


public class EntityBlackHole extends EntityDestroy {
    public float range;
    protected int counter = 0;

    public boolean hasNoGravity()
    {
        return true;
    }

    public EntityBlackHole(World worldIn) {
        super(worldIn);
        this.range = this.radius * 40;
        this.speed = 0.1;
        this.livingMaxAge = Integer.MAX_VALUE;
    }

    @Override
    public void setRadius(float radius) {
        super.setRadius(radius);
        this.range = this.radius * 96;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.attract();
        if(!this.world.isRemote && this.counter++ > 40){
            this.shouldSyncSpeed = true;
            this.counter = 0;
        }
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
         double speed = this.calculateSpeed(l);
         if(entity instanceof EntityBlackHole){
             EntityBlackHole blackHole = (EntityBlackHole) entity;
             speed /= 200 * blackHole.radius;
         }
         motion.mul(speed);
         entity.motionX += motion.x;
         entity.motionY += motion.y;
         entity.motionZ += motion.z;
     }
    }

    private double calculateSpeed(double distance){
        distance = Math.min(distance,this.radius / 2);
        return 3.14 * this.radius * this.radius / (distance * distance) / 100;
    }

    protected boolean onHurt(){
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(this.posX+this.radius,this.posY+this.radius,this.posZ+this.radius,this.posX-this.radius,this.posY-this.radius,this.posZ-this.radius);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,axisAlignedBB, entity -> {
            if(entity == this || entity == this.shootingEntity){
                return false;
            }
            return entity.getDistance(this) <= this.radius;
        });
        boolean flag = false;
        for(Entity entity:entities){
            flag = true;
            if(entity instanceof EntityLivingBase){
                entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, 8.0F);
            }else if(entity instanceof EntityBlackHole && !this.isDead && !this.world.isRemote){
                EntityBlackHole blackHole = (EntityBlackHole) entity;
                this.setRadius((float) Math.sqrt(this.radius*this.radius + blackHole.radius*blackHole.radius));
                blackHole.setDead();
            }else {
                entity.setDead();
            }
        }
        return flag;
    }

    @Override
    protected void entityInit() {

    }
}
