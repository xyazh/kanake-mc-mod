package com.xyazh.kanake.item.hoe;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.init.ModCreativeTab;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemHoeBase extends ItemHoe implements IHasModel {
	protected ToolMaterial toolMaterial;

	public ItemHoeBase(String name, ToolMaterial material)
	{
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModCreativeTab.KNK_CREATIVE);
		toolMaterial = material;
		ModItems.ITEMS.add(this);
	}

	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		return super.hitEntity(stack,target,attacker);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase living, int count) {
		super.onUsingTick(stack, living, count);
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
