package com.xyazh.kanake.item.bauble;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.data.ManaAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class ItemManaRingLow extends ItemBaubleBase{
    public final UUID attributeUUID1 = UUID.fromString("3c8b5cd4-d4eb-e2fe-c7c6-c4d4f194824e");
    public final UUID attributeUUID2 = UUID.fromString("ee7ddd96-3368-48d4-89da-12d26af670b8");
    public final AttributeModifier AM1 = new AttributeModifier(attributeUUID1, Kanake.MODID+":maxMana", 20, Constants.AttributeModifierOperation.ADD);
    public final AttributeModifier AM2 = new AttributeModifier(attributeUUID2, Kanake.MODID+":maxMana", 20, Constants.AttributeModifierOperation.ADD);
    public ItemManaRingLow(String name) {
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
}
