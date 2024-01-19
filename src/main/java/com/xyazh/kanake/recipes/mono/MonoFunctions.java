package com.xyazh.kanake.recipes.mono;

import com.google.common.base.Predicate;
import com.xyazh.kanake.block.blocks.manatable.TileTableCoreMono;
import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;

public class MonoFunctions {
    public static final HashMap<String, Predicate<? super MonoWorkingRecipe>> MONO_FUCNTIONS = new HashMap<>();
    public static void addFunc(){
        MONO_FUCNTIONS.put("no_ops",(MonoWorkingRecipe workingRecipe)->true);
        MONO_FUCNTIONS.put("damage_item",MonoFunctions::damageItem);
        MONO_FUCNTIONS.put("lightning",MonoFunctions::lightning);
        MONO_FUCNTIONS.put("explosion",MonoFunctions::explosion);
    }

    public static boolean damageItem(MonoWorkingRecipe workingRecipe){
        workingRecipe.coreMono.removeStackFromSlot(0);
        return true;
    }

    public static boolean lightning(MonoWorkingRecipe workingRecipe){
        World world = workingRecipe.coreMono.getWorld();
        BlockPos pos = workingRecipe.coreMono.getPos();
        world.addWeatherEffect(new EntityLightningBolt(world, pos.getX()+0.5, pos.getX()+1, pos.getZ()+0.5, false));
        return true;
    }

    public static boolean explosion(MonoWorkingRecipe workingRecipe){
        World world = workingRecipe.coreMono.getWorld();
        BlockPos pos = workingRecipe.coreMono.getPos();
        world.createExplosion(null, pos.getX(), pos.getY()+1, pos.getZ(),0,true);
        return true;
    }
}
