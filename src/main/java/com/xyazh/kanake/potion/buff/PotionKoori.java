package com.xyazh.kanake.potion.buff;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;

public class PotionKoori extends BaseSimplePotion {
    private static final String SPEED_MODIFIER_ID = "a7f3d9f4-2a7b-4a1a-9e3c-1d4b5c6d7e8f";
    private static final String KNOCKBACK_MODIFIER_ID = "b8e4c0a5-3b8c-4b2b-8f4d-2e5c6d7f8a9b";

    public PotionKoori() {
        super(true, 0xaaaaff, "koori", 0);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, -1.0D, 2);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, KNOCKBACK_MODIFIER_ID, 1.0D, 0);
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        super.applyAttributesModifiersToEntity(entity, attributeMap, amplifier);
        PotionEffect effect = entity.getActivePotionEffect(this);
        if (!entity.world.isRemote && entity.world instanceof WorldServer && effect != null) {
            ((WorldServer) entity.world).getEntityTracker().sendToTracking(entity,
                    new SPacketEntityEffect(entity.getEntityId(), effect));
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entity, AbstractAttributeMap attributeMap, int amplifier) {
        super.removeAttributesModifiersFromEntity(entity, attributeMap, amplifier);
        if (!entity.world.isRemote && entity.world instanceof WorldServer) {
            ((WorldServer) entity.world).getEntityTracker().sendToTracking(entity,
                    new SPacketRemoveEntityEffect(entity.getEntityId(), this));
        }
    }

    public static Potion POTION_KOORI = new PotionKoori().setRegistryName(Kanake.MODID, "koori");
}
