package com.xyazh.kanake.item.swords;

import com.xyazh.kanake.damage.KillSlimeDamage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemHarmoniumCrystalSword extends ItemSwordBase{
    public ItemHarmoniumCrystalSword(String name) {
        super(name, ToolMaterial.STONE);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
        if(!target.world.isRemote && (target instanceof EntitySlime)){
            target.attackEntityFrom(new KillSlimeDamage().setAttacker(attacker),Float.MAX_VALUE);
        }
        return super.hitEntity(stack, target, attacker);
    }

    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        tooltip.add(I18n.format("item.harmonium_crystal.desc"));
    }
}
