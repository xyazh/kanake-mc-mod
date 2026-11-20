package com.xyazh.kanake.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityEye extends EntityMob {
    private static final float MAX_H = 1024.0F;

    public EntityEye(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // setHealth() clamps to getMaxHealth(), so the attribute must match MAX_H.
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAX_H);
    }

    @Override
    public void onUpdate() {
        this.dead = false;
        this.isDead = false;
        this.deathTime = 0;
        // getHealth() is final, so repair the backing value if something wrote to it.
        if (this.getHealth() != MAX_H) {
            this.setHealth(MAX_H);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount){
        super.setHealth(MAX_H);
        return true;
    }



    @Override
    public void setDead() {
        this.dead = false;
        this.isDead = false;
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(MAX_H);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.setHealth(MAX_H);
    }

    @Override
    public void heal(float healAmount)
    {
        super.setHealth(MAX_H);
    }

    protected void outOfWorld() {
    }
}
