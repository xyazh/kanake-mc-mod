package com.xyazh.kanake.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityEye extends EntityMob {
    public EntityEye(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void setDead() {
        this.dead = false;
        this.isDead = false;
    }

    protected void outOfWorld() {
    }
}
