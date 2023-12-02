package com.xyazh.kanake.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class OreDictUtil {
    public static Set<String> getOreDictStrings(@Nonnull ItemStack itemStack){
        Set<String> stringSet = new HashSet<>();
        if(ItemStack.EMPTY.equals(itemStack)){
            return stringSet;
        }
        if(itemStack.isEmpty()){
            return stringSet;
        }
        int[] ids = OreDictionary.getOreIDs(itemStack);
        for (int id : ids) {
            stringSet.add(OreDictionary.getOreName(id));
        }
        return stringSet;
    }

    public static Set<String> getOreDictStrings(@Nonnull Item item){
        return getOreDictStrings(new ItemStack(item));
    }

    public static Set<String> getOreDictStrings(@Nonnull Block block){
        return getOreDictStrings(new ItemStack(block));
    }

    public static boolean hasOreDict(ItemStack itemStack,String s){
        if(itemStack.equals(ItemStack.EMPTY)){
            return false;
        }
        for(String s1:getOreDictStrings(itemStack)){
            if(s1.equals(s)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasOreDict(Item item,String s){
        if(item.equals(Items.AIR)){
            return false;
        }
        for(String s1:getOreDictStrings(item)){
            if(s1.equals(s)){
                return true;
            }
        }
        return false;
    }

    public static boolean hasOreDict(Block block,String s){
        if(block.equals(Blocks.AIR)){
            return false;
        }
        for(String s1:getOreDictStrings(block)){
            if(s1.equals(s)){
                return true;
            }
        }
        return false;
    }

    public static boolean equalsOreDict(Item item1,Item item2){
        if(Objects.equals(item1,item2)){
            return true;
        }
        Set<String> od1 = getOreDictStrings(item1);
        Set<String> od2 = getOreDictStrings(item2);
        od2.retainAll(od1);
        return !od2.isEmpty();
    }
}
