package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.Kanake;
import net.minecraft.util.ResourceLocation;

public class MonoRecipes {
    public static void addMonoRecipes() {
        //火之调和水晶
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_harmonium_crystal_fire"),
                "<ore:gemHarmoniumCrystal>|" +
                "<ore:shardFire>,<ore:shardFire>,<ore:shardFire>,<ore:shardFire>|" +
                "<kanake:harmonium_crystal_fire>");
        //冰之调和水晶
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_harmonium_crystal_ice"),
                "<ore:gemHarmoniumCrystal>|" +
                "<ore:shardIce>,<ore:shardIce>,<ore:shardIce>,<ore:shardIce>|" +
                "<kanake:harmonium_crystal_ice>");
        //蕴魔调和水晶
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_harmonium_crystal_magic"),
                "<ore:gemHarmoniumCrystal>|" +
                "<ore:shardMagic>,<ore:shardMagic>,<ore:shardMagic>,<ore:shardMagic>|" +
                "<kanake:harmonium_crystal_magic>");
        //末影调和水晶
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_harmonium_crystal_end"),
                "<ore:gemHarmoniumCrystal>|" +
                "<ore:shardEnder>,<ore:shardEnder>,<ore:shardEnder>,<ore:shardEnder>|" +
                "<kanake:harmonium_crystal_end>");

        //史莱姆核心|火
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_slime_core_fire"),
                "<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>," +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>," +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>|" +
                "<kanake:slime_core_fire>");
        //史莱姆核心|冰
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_slime_core_ice"),
                "<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>," +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>," +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>|" +
                "<kanake:slime_core_ice>");
        //史莱姆核心|魔法
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_slime_core_magic"),
                "<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>," +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>," +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>|" +
                "<kanake:slime_core_magic>");
        //史莱姆核心|末影
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_slime_core_end"),
                "<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>," +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>," +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>|" +
                "<kanake:slime_core_end>");

        //废怯铁锭
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_ws_ingot"),
                "<ore:ingotIron>|" +
                "<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>," +
                "<kanake:ws_ingot>");
        //魔法铁锭
        MonoRecipeManager.addStringRecipe(new ResourceLocation(Kanake.MODID,"recipe_ws_magic_ingot"),
                "<kanake:ws_ingot>|<ore:dustMagic>|<kanake:ws_magic_ingot>");
    }
}
