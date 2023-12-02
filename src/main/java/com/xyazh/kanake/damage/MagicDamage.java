package com.xyazh.kanake.damage;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class MagicDamage extends DamageSource {
    private Entity attacker = null;
    public MagicDamage() {
        super("magic_bullet");
        this.setDamageBypassesArmor();
        this.setMagicDamage();
    }

    public MagicDamage setAttacker(Entity entity){
        this.attacker = entity;
        return this;
    }

    public Entity getTrueSource()
    {
        return this.attacker;
    }
}
