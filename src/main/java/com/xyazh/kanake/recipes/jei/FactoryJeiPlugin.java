package com.xyazh.kanake.recipes.jei;

import javax.annotation.Nullable;

import com.xyazh.kanake.block.ModBlocks;
import net.minecraft.item.ItemStack;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class FactoryJeiPlugin implements IModPlugin {
	@Nullable
	public static IJeiHelpers jeiHelpers;

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		jeiHelpers = registry.getJeiHelpers();
	}

	@Override
	public void register(IModRegistry registry) {
		jeiHelpers = registry.getJeiHelpers();
		registry.addRecipeCatalyst(new ItemStack(ModBlocks.MONO), "minecraft.mono");
	}
}
