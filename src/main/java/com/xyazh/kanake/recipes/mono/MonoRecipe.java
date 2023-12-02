package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import com.xyazh.kanake.util.OreDictUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class MonoRecipe {
    public static HashSet<String> getCoreString(ItemStack coreItem){
        HashSet<String> coreItemString = new HashSet<>();
        if(coreItem.getItem().getRegistryName() != null){
            coreItemString.add(coreItem.getItem().getRegistryName().toString());
        }
        coreItemString.addAll(OreDictUtil.getOreDictStrings(coreItem));
        return coreItemString;
    }

    public static Set<Set<String>> getCheckItemString(Map<TileTableMono,Integer> monoMap){
        Set<TileTableMono> monos = new HashSet<>(monoMap.keySet());
        Set<Set<String>> itemStrings = new HashSet<>();
        for(TileTableMono te:monos){
            if(te.isEmpty()){
                monoMap.remove(te);
                continue;
            }
            ItemStack itemStack = te.getStackInSlot(0);
            Set<String> itemString = new NoEqSet<>();
            if(itemStack.getItem().getRegistryName() != null){
                itemString.add(itemStack.getItem().getRegistryName().toString());
            }
            itemString.addAll(OreDictUtil.getOreDictStrings(itemStack));
            itemStrings.add(itemString);
        }
        return itemStrings;
    }

    @Nullable
    public static Item findRecipe(ItemStack coreItem, HashMap<TileTableMono,Integer> tableMonos){
        Set<Set<String>> checkItemString = getCheckItemString(tableMonos);
        for(String s:getCoreString(coreItem)){
            if(MonoRecipes.RECIPES.containsKey(s)){
                for(RecipesInItems recipesInItems:MonoRecipes.RECIPES.get(s)){
                    if(recipesInItems.checkItems(checkItemString)){
                        return recipesInItems.getOutItem();
                    };
                }
            }
        }
        return null;
    }

    public static void addStringRecipes(String inputString) {
        String[] parts = inputString.split("\\|");
        if (parts.length != 3) {
            return;
        }
        String in, out;
        String[] items;
        in = parts[0].replaceAll("<ore:", "").replaceAll(">", "");
        in = in.replaceAll("<", "").replaceAll(">", "");
        items = parts[1].split(",");
        for (int i = 0; i < items.length; i++) {
            items[i] = items[i].replaceAll("<ore:", "").replaceAll(">", "");
            items[i] = items[i].replaceAll("<", "").replaceAll(">", "");
        }
        out = parts[2].replaceAll("<", "").replaceAll(">", "");
        RecipesInItems recipesInItems = new RecipesInItems(out);
        recipesInItems.addItems(Arrays.asList(items));
        if(MonoRecipes.RECIPES.containsKey(in)){
            MonoRecipes.RECIPES.get(in).add(recipesInItems);
        }else {
            HashSet<RecipesInItems> set = new HashSet<>();
            set.add(recipesInItems);
            MonoRecipes.RECIPES.put(in,set);
        }
    }
}
