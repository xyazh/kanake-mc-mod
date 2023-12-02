package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class EntityMagicWitch extends EntityWitch {
    public EntityMagicWitch(World worldIn) {
        super(worldIn);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
    }


    @Nullable
    public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance diff, @Nullable IEntityLivingData livingData) {
        return super.onInitialSpawn(diff,livingData);
    }


    protected void initEntityAI() {
        super.initEntityAI();
    }

    public void attackEntityWithRangedAttack(@Nonnull EntityLivingBase target, float distanceFactor)
    {
            double dx = target.posX + target.motionX - this.posX;
            double dz = target.posZ + target.motionZ - this.posZ;
            if(!this.world.isRemote){
                EntityShoot entity = null;
                switch (this.rand.nextInt(5)){
                    case 0:
                        entity = new EntityKoori(this.world);
                        break;
                    case 1:
                        entity = new EntityMagic(this.world);
                        break;
                    case 2:
                        entity = new EntityFireCurse(this.world);
                        break;
                    case 3:
                        entity = new EntityIceFrost(this.world);
                        break;
                    case 4:
                        entity = new EntityFireBall(this.world);
                        break;
                }
                    Vec3d m = Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYaw);
                    entity.entityShoot(this,m);
                    entity.setPosition(this.posX,this.posY+1.5,this.posZ);
                    world.spawnEntity(entity);
                }
            this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
    }

    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source){
        super.dropLoot(wasRecentlyHit,lootingModifier,source);
        Random random = Kanake.rand;
        this.dropItem(ModItems.WS_WS_KAKERA,1+random.nextInt(2));
        this.dropItem(ModItems.WS_POLLUTE_SALT,1+random.nextInt(2));
    }
}
