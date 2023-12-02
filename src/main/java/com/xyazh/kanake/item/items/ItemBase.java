package com.xyazh.kanake.item.items;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class ItemBase extends Item implements IHasModel {
	protected boolean glitters = false;
	public String[] oreDictName = {};
	String name;

	public ItemBase(String name)
	{
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
		ModItems.ITEMS.add(this);
	}

	public ItemBase setOreDict(String[] oreDictName){
		this.oreDictName = oreDictName;
		return this;
	}


	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return stack.isItemEnchanted() || glitters;
	}

	@Override
	public void registerModels() 
	{
		Kanake.proxy.registerItemRenderer(this, 0, "inventory");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {

	}
}
