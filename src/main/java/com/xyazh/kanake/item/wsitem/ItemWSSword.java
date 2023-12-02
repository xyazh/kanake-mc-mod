package com.xyazh.kanake.item.wsitem;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.item.swords.ItemSwordBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;


public class ItemWSSword extends ItemSwordBase {
    public ItemWSSword(String name) {
        super(name, ToolMaterial.IRON);
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        target.hurtResistantTime = 0;
        target.attackEntityFrom(new MagicDamage().setAttacker(attacker),8);
        return super.hitEntity(stack,target,attacker);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        /*添加自定义物品信息（tooltip），当鼠标指针移动到物品上就会显示出来*/
        tooltip.add(I18n.format("item.ws_sword.desc"));
    }
}
