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


public class EntityDestroy extends EntityBall {
    public EntityDestroy(World worldIn) {
        super(worldIn);
        this.speed = 0.1;
        this.noClip = true;
    }
    public boolean hasNoGravity()
    {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.world.isRemote){
            for(int i=0;i<3;i++){
                Vec3d vec3d = Vec3d.random();
                this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX+vec3d.x, this.posY+vec3d.y-1,this.posZ+vec3d.z, 0, 0, 0);
            }
        }
        this.doDestroy();
        this.onHurt();
    }

    public void doDestroy(){
        float thisX = (float) (this.posX-0.5);
        float thisY = (float) this.posY;
        float thisZ = (float) (this.posZ-0.5);
        int maxX = Math.round(thisX + this.radius);
        int minX = Math.round(thisX - this.radius);
        int maxY = Math.round(thisY + this.radius);
        int minY = Math.round(thisY - this.radius);
        int maxZ = Math.round(thisZ + this.radius);
        int minZ = Math.round(thisZ - this.radius);
        for(int x = minX; x <= maxX; x++){
            for(int y = minY; y <= maxY; y++){
                for(int z = minZ; z <= maxZ; z++){
                    BlockPos pos1 = new BlockPos(x,y,z);
                    IBlockState blockState = this.world.getBlockState(pos1);
                    Block block = blockState.getBlock();
                    float hardness = block.getBlockHardness(blockState,this.world, pos1);
                    if(1024 > hardness && hardness >= 0){
                        this.world.destroyBlock(pos1, false);
                    }
                }
            }
        }
    }

    protected boolean onHurt(){
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(this.posX+this.radius,this.posY+this.radius,this.posZ+this.radius,this.posX-this.radius,this.posY-this.radius,this.posZ-this.radius);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,axisAlignedBB, entity -> {
            if(entity == this){
                return false;
            }
            Vec3d targetPos = Vec3d.fromEntityPos(entity);
            Vec3d thisPos = Vec3d.fromEntityPos(this);
            targetPos.sub(thisPos);
            return targetPos.length() <= this.radius;
        });
        boolean flag = false;
        for(Entity entity:entities){
            flag = true;
            if(entity instanceof EntityLivingBase){
                entity.attackEntityFrom(DamageSource.OUT_OF_WORLD, 8.0F);
            }else{
                entity.setDead();
            }
        }
        return flag;
    }

    @Override
    protected void entityInit() {

    }
}
