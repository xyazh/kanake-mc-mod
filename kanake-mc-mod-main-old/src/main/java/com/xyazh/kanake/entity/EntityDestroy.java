package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.util.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
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
                Vec3d vec3d = Vec3d.random(this.radius);
                this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX+vec3d.x, this.posY+vec3d.y-1,this.posZ+vec3d.z, 0, 0, 0);
            }
        }
        this.doDestroy();
        this.onHurt();
        this.setRadius(this.radius-0.001f);
        if(this.radius < 0.25){
            this.setDead();
        }
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
                    Vec3d targetPos = new Vec3d(x+0.5,y+0.5,z+0.5);
                    Vec3d thisPos = Vec3d.fromEntityPos(this);
                    targetPos.sub(thisPos);
                    if(targetPos.length() <= this.radius){
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
            }else{
                entity.setDead();
            }
        }
        return flag;
    }



    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return true;
    }

    @Override
    protected void entityInit() {

    }

    public boolean isInLava()
    {
        return false;
    }

    protected void setOnFireFromLava()
    {
    }

    protected void dealFireDamage(int amount)
    {
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    public boolean isBurning()
    {
        return false;
    }

    public boolean handleWaterMovement()
    {
        this.inWater = false;
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }
}
