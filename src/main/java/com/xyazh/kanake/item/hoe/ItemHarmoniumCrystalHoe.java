package com.xyazh.kanake.item.hoe;

import com.xyazh.kanake.damage.KillSlimeDamage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHarmoniumCrystalHoe extends ItemHoeBase {
    public ItemHarmoniumCrystalHoe(String name) {
        super(name, ToolMaterial.STONE);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if(!target.world.isRemote && (target instanceof EntitySlime)){
            target.attackEntityFrom(new KillSlimeDamage().setAttacker(attacker),Float.MAX_VALUE);
        }
        return super.hitEntity(stack, target, attacker);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        /*添加自定义物品信息（tooltip），当鼠标指针移动到物品上就会显示出来*/
        tooltip.add(I18n.format("item.harmonium_crystal.desc"));
    }
}
