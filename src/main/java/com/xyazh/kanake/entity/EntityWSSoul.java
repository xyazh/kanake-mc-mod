package com.xyazh.kanake.entity;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class EntityWSSoul extends EntityGhast {
    public EntityWSSoul(World worldIn) {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
    }
    protected void initEntityAI() {
        this.tasks.addTask(2, new AIRandomFly(this));
        this.tasks.addTask(2, new AILookAround(this));
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
        this.tasks.addTask(1,new AIAttack());

    }

    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source){
        super.dropLoot(wasRecentlyHit,lootingModifier,source);
        Random random = Kanake.rand;
        this.dropItem(ModItems.WS_POLLUTE_SALT,1+random.nextInt(2));
    }

    class AIAttack extends EntityAIBase {
        private int attackTimer;

        public AIAttack() {
            this.setMutexBits(3);
        }

        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = EntityWSSoul.this.getAttackTarget();

            if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
                return EntityWSSoul.this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
            } else {
                return false;
            }
        }
        public void updateTask(){
            ++this.attackTimer;
            if (this.attackTimer == 20) {
                EntityLivingBase entitylivingbase = EntityWSSoul.this.getAttackTarget();
                if (!EntityWSSoul.this.world.isRemote && entitylivingbase != null) {
                    EntityWSSoulBullet wsBulletForSoul = new EntityWSSoulBullet(EntityWSSoul.this.world);
                    wsBulletForSoul.shoot(entitylivingbase,EntityWSSoul.this);
                    EntityWSSoul.this.world.spawnEntity(wsBulletForSoul);
                    this.attackTimer = -40;
                }
            }
        }
    }

    public static class AIRandomFly extends EntityAIBase {
        private final EntityGhast parentEntity;

        public AIRandomFly(EntityGhast ghast) {
            this.parentEntity = ghast;
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();

            if (!entitymovehelper.isUpdating()) {
                return true;
            } else {
                double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        public boolean shouldContinueExecuting() {
            return false;
        }

        public void startExecuting() {
            Random random = this.parentEntity.getRNG();
            double d0 = this.parentEntity.posX + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = this.parentEntity.posY + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = this.parentEntity.posZ + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
        }
    }

    public static class AILookAround extends EntityAIBase {
        private final EntityGhast parentEntity;

        public AILookAround(EntityGhast ghast) {
            this.parentEntity = ghast;
            this.setMutexBits(2);
        }

        public boolean shouldExecute() {
            return true;
        }

        public void updateTask() {
            if (this.parentEntity.getAttackTarget() == null) {
                this.parentEntity.rotationYaw = -((float) MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ)) * (180F / (float) Math.PI);
                this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
            } else {
                EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
                double d0 = 64.0D;

                if (entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D) {
                    double d1 = entitylivingbase.posX - this.parentEntity.posX;
                    double d2 = entitylivingbase.posZ - this.parentEntity.posZ;
                    this.parentEntity.rotationYaw = -((float) MathHelper.atan2(d1, d2)) * (180F / (float) Math.PI);
                    this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
                }
            }
        }
    }
}