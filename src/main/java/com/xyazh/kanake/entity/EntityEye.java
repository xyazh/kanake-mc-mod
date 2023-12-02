package com.xyazh.kanake.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class EntityEye extends EntityMob {
    public int age = 0;
    public float lastT = 0;

    public EntityEye(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.age += 1;
    }
}
