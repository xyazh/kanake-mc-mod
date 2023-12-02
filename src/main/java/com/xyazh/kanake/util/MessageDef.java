package com.xyazh.kanake.util;

import net.minecraft.item.ItemStack;

public class MessageDef {
    //GENERAL:
    public static final String OUT_OF_RANGE = "space_based_weapon.msg.out_of_range";
    public static final String IN_COOLDOWN = "space_based_weapon.skill.msg.cool_down";
    public static final String NOT_CASTABLE_MAINHAND = "space_based_weapon.skill.msg.not_castable_mainhand";
    public static final String NOT_CASTABLE_OFFHAND = "space_based_weapon.skill.msg.not_castable_offhand";

    public static String getSkillCastKey(ItemStack stack, int index)
    {
        //remove"item."
        return String.format("msg.%s.cast.%d", stack.getUnlocalizedName().substring(5), index);
    }
}
