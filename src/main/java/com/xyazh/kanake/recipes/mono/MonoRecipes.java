package com.xyazh.kanake.recipes.mono;

import java.util.HashMap;
import java.util.HashSet;

public class MonoRecipes {
    public static void addMonoRecipes() {
        //火之调和水晶
        MonoRecipeHelper.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardFire>,<ore:shardFire>,<ore:shardFire>,<ore:shardFire>|" +
                "<kanake:harmonium_crystal_fire>");
        //冰之调和水晶
        MonoRecipeHelper.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardIce>,<ore:shardIce>,<ore:shardIce>,<ore:shardIce>|" +
                "<kanake:harmonium_crystal_ice>");
        //蕴魔调和水晶
        MonoRecipeHelper.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardMagic>,<ore:shardMagic>,<ore:shardMagic>,<ore:shardMagic>|" +
                "<kanake:harmonium_crystal_magic>");
        //末影调和水晶
        MonoRecipeHelper.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardEnder>,<ore:shardEnder>,<ore:shardEnder>,<ore:shardEnder>|" +
                "<kanake:harmonium_crystal_end>");
        //毒之调和水晶
        MonoRecipeHelper.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<minecraft:spider_eye>,<minecraft:spider_eye>,<minecraft:spider_eye>,<minecraft:spider_eye>|" +
                "<kanake:harmonium_crystal_poison>");
        //毒之调和水晶
        MonoRecipeHelper.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<minecraft:poisonous_potato>,<minecraft:poisonous_potato>,<minecraft:poisonous_potato>,<minecraft:poisonous_potato>|" +
                "<kanake:harmonium_crystal_poison>");

        //史莱姆核心|火
        MonoRecipeHelper.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>," +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>," +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>|" +
                "<kanake:slime_core_fire>");
        //史莱姆核心|冰
        MonoRecipeHelper.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>," +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>," +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>|" +
                "<kanake:slime_core_ice>");
        //史莱姆核心|魔法
        MonoRecipeHelper.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>," +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>," +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>|" +
                "<kanake:slime_core_magic>");
        //史莱姆核心|毒
        MonoRecipeHelper.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>," +
                "<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>," +
                "<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>|" +
                "<kanake:slime_core_poison>");
        //史莱姆核心|末影
        MonoRecipeHelper.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>," +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>," +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>|" +
                "<kanake:slime_core_end>");

        //废怯铁锭
        MonoRecipeHelper.addStringRecipes("<ore:ingotIron>|" +
                "<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>," +
                "<kanake:ws_ingot>");
        //魔法铁锭
        MonoRecipeHelper.addStringRecipes("<kanake:ws_ingot>|<ore:dustMagic>|<kanake:ws_magic_ingot>");
    }
}
