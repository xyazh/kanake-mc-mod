package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MonoRecipeManager {
    public static final HashMap<ResourceLocation,MonoRecipe> RECIPES = new HashMap<>();
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
        for(MonoRecipe recipe:RECIPES.values()){
            if(recipe.matches(coreItem,itemStackList,world,te)){
                return recipe;
            }
        }
        return null;
    }

    public static void addStringRecipe(String id, String inputString) {
        ResourceLocation resourceLocation = new ResourceLocation(id);
        MonoRecipe recipe = MonoRecipe.factory(resourceLocation,inputString);
        if(recipe!=null){
            RECIPES.put(resourceLocation,recipe);
        }
    }

    public static void addStringRecipe(ResourceLocation id, String inputString) {
        MonoRecipe recipe = MonoRecipe.factory(id,inputString);
        if(recipe!=null){
            RECIPES.put(id,recipe);
        }
    }

    public static void addRecipe(String id, @Nonnull MonoRecipe recipe) {
        RECIPES.put(new ResourceLocation(id),recipe);
    }

    public static void addRecipe(ResourceLocation id, @Nonnull MonoRecipe recipe) {
        RECIPES.put(id,recipe);
    }
}
