package com.xyazh.kanake.item.bauble;

import com.xyazh.kanake.item.items.ItemBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemBaubleBase extends ItemBase implements baubles.api.IBauble {
    public static final int AMULET=0;
    public static final int RING=1;
    public static final int BELT=2;
    public static final int TRINKET=3;
    public static final int HEAD=4;
    public static final int BODY=5;
    public static final int CHARM=6;

    private int baubleType = RING;

    public ItemBaubleBase(String name) {
        super(name);
    }

    public void setBaubleType(int type){
        this.baubleType = type;
    }

    @Override
    public baubles.api.BaubleType getBaubleType(ItemStack itemStack) {
        switch (this.baubleType){
            case AMULET:
                return baubles.api.BaubleType.AMULET;
            case RING:
                return baubles.api.BaubleType.RING;
            case BELT:
                return baubles.api.BaubleType.BELT;
            case TRINKET:
                return baubles.api.BaubleType.TRINKET;
            case HEAD:
                return baubles.api.BaubleType.HEAD;
            case BODY:
                return baubles.api.BaubleType.BODY;
            case CHARM:
                return baubles.api.BaubleType.CHARM;
        }
        return baubles.api.BaubleType.RING;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        if(Loader.isModLoaded("baubles")){
            baubles.api.IBauble.super.onEquipped(itemstack, player);
        }
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        if(Loader.isModLoaded("baubles")){
            baubles.api.IBauble.super.onUnequipped(itemstack, player);
        }
    }
}
