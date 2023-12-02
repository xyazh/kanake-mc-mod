package com.xyazh.kanake.recipes.brewing;

import com.xyazh.kanake.item.ModItems;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class MyBrewing {
    public static void addBrewingRecipes(){
        ItemStack itemStack = new ItemStack(Items.POTIONITEM);
        PotionUtils.addPotionToItemStack(itemStack, PotionTypes.WATER);
        BrewingRecipeRegistry.addRecipe(itemStack, "shardFire", new ItemStack(ModItems.POTION_MP));
        BrewingRecipeRegistry.addRecipe(itemStack, "shardIce", new ItemStack(ModItems.POTION_MP));
        BrewingRecipeRegistry.addRecipe(itemStack, "shardMagic", new ItemStack(ModItems.POTION_MP));
        BrewingRecipeRegistry.addRecipe(itemStack, "shardEnder", new ItemStack(ModItems.POTION_MP));
    }
}
