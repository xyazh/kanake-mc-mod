package com.xyazh.kanake.damage;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class CleanDamage extends DamageSource {
    private Entity attacker = null;
    public CleanDamage() {
        super("clean");
        this.setDamageBypassesArmor();
        this.setMagicDamage();
        this.setDamageIsAbsolute();
    }

    public CleanDamage(String damageTypeIn) {
        super(damageTypeIn);
        this.setDamageBypassesArmor();
        this.setMagicDamage();
    }

    public CleanDamage setAttacker(Entity entity){
        this.attacker = entity;
        return this;
    }

    public Entity getTrueSource()
    {
        return this.attacker;
    }
}
