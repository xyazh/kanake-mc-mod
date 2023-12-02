package com.xyazh.kanake.item.bauble;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.data.ManaAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ItemManaRingHigh extends ItemBaubleBase{
    public final UUID attributeUUID1 = UUID.fromString("f335713f-ae61-4604-940c-475b08b7de4a");
    public final UUID attributeUUID2 = UUID.fromString("a27c8133-2afc-4661-ba2b-e383a2474539");
    public final AttributeModifier AM1 = new AttributeModifier(attributeUUID1, Kanake.MODID+":maxMana", 250, Constants.AttributeModifierOperation.ADD);
    public final AttributeModifier AM2 = new AttributeModifier(attributeUUID2, Kanake.MODID+":maxMana", 250, Constants.AttributeModifierOperation.ADD);
    public ItemManaRingHigh(String name) {
        super(name);
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(ManaAttribute.MANA_ATTRIBUTE);
        if(!attr.hasModifier(AM1)){
            attr.applyModifier(AM1);
        }else if(!attr.hasModifier(AM2)){
            attr.applyModifier(AM2);
        }
        super.onEquipped(itemstack, player);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(ManaAttribute.MANA_ATTRIBUTE);
        if(attr.hasModifier(AM1)){
            attr.removeModifier(AM1);
        }else if(attr.hasModifier(AM2)){
            attr.removeModifier(AM2);
        }
        super.onUnequipped(itemstack, player);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
