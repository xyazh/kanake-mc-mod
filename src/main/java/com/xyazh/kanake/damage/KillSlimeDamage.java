package com.xyazh.kanake.damage;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class KillSlimeDamage extends DamageSource {
    private Entity attacker = null;
    public KillSlimeDamage() {
        super("killSlime");
        this.setDamageBypassesArmor();
    }

    public KillSlimeDamage setAttacker(Entity entity){
        this.attacker = entity;
        return this;
    }

    public Entity getTrueSource()
    {
        return this.attacker;
    }
}
