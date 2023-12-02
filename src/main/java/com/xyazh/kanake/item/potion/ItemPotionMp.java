package com.xyazh.kanake.item.potion;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemPotionMp extends ItemPotionBase{
    protected double mp;
    public ItemPotionMp(String name,double mp) {
        super(name);
        this.mp = mp;
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        if(entityplayer == null){
            return stack;
        }
        ManaData.add(entityplayer,this.mp);
        if(!entityplayer.isCreative()){
            stack.shrink(1);
            if (stack.isEmpty())
            {
                if(this.mp>=40){
                    return new ItemStack(Items.GLASS_BOTTLE);
                }
                return new ItemStack(ModItems.BOTTLE_SMALL);
            }
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return super.hasEffect(stack);
    }
}
