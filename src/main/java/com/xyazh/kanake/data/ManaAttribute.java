package com.xyazh.kanake.data;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class ManaAttribute extends RangedAttribute {
    public static final ManaAttribute MANA_ATTRIBUTE =  new ManaAttribute();
    public ManaAttribute() {
        super(null, Kanake.MODID+":maxMana", 200, 0, 65535);
    }

    @Override
    public boolean getShouldWatch() {
        return true;
    }
}
