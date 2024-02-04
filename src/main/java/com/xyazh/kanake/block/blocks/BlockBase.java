package com.xyazh.kanake.block.blocks;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.item.ItemBlockBase;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class BlockBase extends Block implements IHasModel
{
	public ItemBlockBase itemBlockBase;
	public String[] oreDictName = {};

	public BlockBase(String name, Material material)
	{
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		ModBlocks.BLOCKS.add(this);
		this.itemBlockBase = this.getItemBlockBase();
		this.itemBlockBase.setRegistryName(name);
		ModItems.ITEMS.add(this.itemBlockBase);
		setHardness(5.0F);
		setResistance(15.0F);
		setLightOpacity(1);
	}

	public ItemBlockBase getItemBlockBase(){
		return new ItemBlockBase(this);
	}

	public BlockBase setOreDict(String[] oreDictName){
		this.itemBlockBase.setOreDict(oreDictName);
		return this;
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return super.getItemDropped(state, rand, fortune);
	}

	@Override
	public int quantityDropped(Random rand) {
		return super.quantityDropped(rand);
	}
	
	@Override
	public void registerModels() {
		Kanake.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}
}
