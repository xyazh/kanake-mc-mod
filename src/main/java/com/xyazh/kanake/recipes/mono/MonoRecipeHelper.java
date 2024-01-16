package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class MonoRecipeHelper {
    public static final HashSet<MonoRecipe> RECIPES = new HashSet<>();
    @Nullable
    public static MonoRecipe findRecipe(ItemStack coreItem, LinkedList<TileTableMono> tableMonos, World world, TileEntity te, LinkedList<TileTableMono> outMonos){
        LinkedList<ItemStack> itemStackList = new LinkedList<>();
        for(TileTableMono tableMono:tableMonos){
            ItemStack itemStack = tableMono.getStackInSlot(0);
            if(!itemStack.equals(ItemStack.EMPTY)){
                itemStackList.add(itemStack);
                outMonos.add(tableMono);
            }
        }
        for(MonoRecipe recipe:RECIPES){
            if(recipe.matches(coreItem,itemStackList,world,te)){
                return recipe;
            }
        }
        return null;
    }

    public static void addStringRecipes(String inputString) {
        MonoRecipe recipe = MonoRecipe.factory(inputString);
        if(recipe!=null){
            RECIPES.add(recipe);
        }
    }
}
