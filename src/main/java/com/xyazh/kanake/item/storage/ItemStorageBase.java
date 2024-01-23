package com.xyazh.kanake.item.storage;

import com.xyazh.kanake.api.IManaStorage;
import com.xyazh.kanake.item.items.ItemBase;
import com.xyazh.kanake.item.tools.ItemToolBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;

public class ItemStorageBase extends ItemBase {
    public ItemStorageBase(String name,int maxEnergy) {
        super(name);
        this.maxStackSize = 1;
        this.setMaxDamage(maxEnergy);
    }

    @Override
    public boolean isDamaged(@Nonnull ItemStack stack) {
        return false;
    }

    public boolean showDurabilityBar(@Nonnull ItemStack stack)
    {
        return true;
    }

    public int getMaxEnergy(ItemStack itemStack){
        return itemStack.getMaxDamage();
    }

    public int getEnergy(ItemStack itemStack){
        return itemStack.getMaxDamage() - itemStack.getItemDamage();
    }

    public void setEnergy(ItemStack itemStack,int energy){
        itemStack.setItemDamage(itemStack.getMaxDamage() - energy);
    }

    public int addEnergy(ItemStack itemStack,int n){
        int energy = this.getEnergy(itemStack);
        int can = this.getMaxEnergy(itemStack) - energy;
        int real = Math.min(can,n);
        this.setEnergy(itemStack,energy+real);
        return n-real;
    }

    public int subEnergy(ItemStack itemStack,int n){
        int energy = this.getEnergy(itemStack);
        int r = energy - n;
        this.setEnergy(itemStack, Math.max(r, 0));
        return r>=0?0:Math.abs(r);
    }

    public int remain(ItemStack itemStack){
        return this.getMaxEnergy(itemStack) - this.getEnergy(itemStack);
    }

    public void setFull(ItemStack itemStack){
        this.setEnergy(itemStack,this.getMaxEnergy(itemStack));
    }

    @SideOnly(Side.CLIENT)
    public String getEnergyNameFormat(){
        return "";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("tooltip.mana.storage") + ":" + this.getEnergy(stack) + " / " + this.getMaxEnergy(stack));
    }
}
