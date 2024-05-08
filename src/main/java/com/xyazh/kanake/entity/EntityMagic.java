package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityMagic extends EntityShoot{
    public EntityMagic(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(!this.hasNoGravity()){
            dy += 0.0012;
        }
        if (world.isRemote) {
            for (int i = 0; i <= 10; i++) {
                this.world.spawnParticle(ModParticles.MAGIC_PARTICLES, posX, posY, posZ, 0, 0, 0);
                this.world.spawnParticle(ModParticles.MAGIC_PARTICLES1, posX, posY, posZ, 0, 0, 0);
            }
        }
        if(this.onHurt()){
            this.setDead();
            this.setDeadParticle();
        }
        if(this.collided){
            this.setDeadParticle();
            this.setDead();
        }
    }

    protected void setDeadParticle(){
        if (world.isRemote) {
            for (int i = 0; i <= 100; i++) {
                this.world.spawnParticle(ModParticles.TEST_PARTICLES2, posX, posY, posZ, 0, 0, 0);
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
            MagicDamage damageSource = new MagicDamage();
            damageSource.setAttacker(this.shootingEntity);
            entityLivingBase.attackEntityFrom(damageSource, 8);
        }
        return flag;
    }
}
