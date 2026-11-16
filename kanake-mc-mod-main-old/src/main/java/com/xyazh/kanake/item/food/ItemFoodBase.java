package com.xyazh.kanake.item.food;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.init.ModCreativeTab;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemFoodBase extends ItemFood implements IHasModel {

	public ItemFoodBase(String name,int amount, float saturation, boolean isWolfFood)
	{
		super(amount,saturation,isWolfFood);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModCreativeTab.KNK_CREATIVE);
		ModItems.ITEMS.add(this);
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull EntityLivingBase entityLiving) {
		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}

	@Override
	public void registerModels()
	{
		Kanake.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
