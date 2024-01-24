package com.xyazh.kanake.item.storage;

import com.xyazh.kanake.api.IItemManaStorage;
import com.xyazh.kanake.item.items.ItemBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemStorageBase extends ItemBase implements IItemManaStorage {
    public ItemStorageBase(String name,int maxEnergy) {
        super(name);
        this.maxStackSize = 1;
        this.setMaxDamage(maxEnergy);
    }

    @Override
    public boolean isDamaged(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(@Nonnull ItemStack stack)
    {
        return true;
    }

    public int getMaxMana(ItemStack itemStack){
        return itemStack.getMaxDamage();
    }

    public int getMana(ItemStack itemStack){
        return itemStack.getMaxDamage() - itemStack.getItemDamage();
    }

    public void setMana(ItemStack itemStack, int energy){
        itemStack.setItemDamage(itemStack.getMaxDamage() - energy);
    }

    public int addMana(ItemStack itemStack, int n){
        int energy = this.getMana(itemStack);
        int can = this.getMaxMana(itemStack) - energy;
        int real = Math.min(can,n);
        this.setMana(itemStack,energy+real);
        return n-real;
    }

    public int subMana(ItemStack itemStack, int n){
        int energy = this.getMana(itemStack);
        int r = energy - n;
        this.setMana(itemStack, Math.max(r, 0));
        return r>=0?0:Math.abs(r);
    }

    public int remain(ItemStack itemStack){
        return this.getMaxMana(itemStack) - this.getMana(itemStack);
    }

    public void setFull(ItemStack itemStack){
        this.setMana(itemStack,this.getMaxMana(itemStack));
    }

    @SideOnly(Side.CLIENT)
    public String getEnergyNameFormat() {
        return I18n.format("tooltip.mana.storage");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("tooltip.mana.storage") + ":" + this.getMana(stack) + " / " + this.getMaxMana(stack));
    }
}
