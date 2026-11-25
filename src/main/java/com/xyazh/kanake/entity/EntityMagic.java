package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.particle.simple.particles.MagicParticle;
import com.xyazh.kanake.particle.simple.particles.MagicParticle1;
import com.xyazh.kanake.particle.simple.particles.TestParticle2;
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
                SimpleParticle particle = new TestParticle2(world, posX, posY, posZ, 0, 0, 0);
            }
        }
    }

    @Override
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        int count = this.consumeParticleBudget(11.0, deltaTicks);
        if (count <= 0) {
            return;
        }
        double x = this.getParticleX(partialTicks);
        double y = this.getParticleY(partialTicks);
        double z = this.getParticleZ(partialTicks);
        for (int i = 0; i < count; i++) {
            new MagicParticle(this.world, x, y, z, 0, 0, 0);
            new MagicParticle1(this.world, x, y, z, 0, 0, 0);
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
            MagicDamageUtil.attackEntity(entityLivingBase, damageSource, 8.0F, 8.0F);
        }
        return flag;
    }
}
