package com.xyazh.kanake.damage;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class CleanDamage extends DamageSource {
    public static final CleanDamage CLEAN_DAMAGE = new CleanDamage();

    public CleanDamage() {
        super("clean");
        this.setDamageBypassesArmor();
        this.setMagicDamage();
        this.setDamageIsAbsolute();
    }
}
