package com.xyazh.kanake.potion.buff;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.events.KooriEvent;
import com.xyazh.kanake.network.KooriEntityPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.Potion;

import javax.annotation.Nonnull;

public class PotionKoori extends BaseSimplePotion {
    public PotionKoori() {
        super(true,0xaaaaff,"koori",0);
    }

    @Override
    public void removeAttributesModifiersFromEntity(@Nonnull EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier){
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
        KooriEvent.entitySet.add(entityLivingBaseIn);
    }

    @Override
    public void applyAttributesModifiersToEntity(@Nonnull EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        KooriEvent.entitySet.add(entityLivingBaseIn);
        KooriEntityPacket kooriEntityPacket = new KooriEntityPacket();
        kooriEntityPacket.type = true;
        kooriEntityPacket.id = entityLivingBaseIn.getUniqueID();
        Kanake.network.sendToAll(kooriEntityPacket);
    }

    public static Potion POTION_KOORI = new PotionKoori().setRegistryName(Kanake.MODID, "koori");
}
