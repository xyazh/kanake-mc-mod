package com.xyazh.kanake.recipes.mono;

import java.util.HashMap;
import java.util.HashSet;

public class MonoRecipes {
    public static final HashMap<String, HashSet<RecipesInItems>> RECIPES = new HashMap<>();

    public static void addMonoRecipes() {
        MonoRecipe.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardFire>,<ore:shardFire>,<ore:shardFire>,<ore:shardFire>|" +
                "<kanake:harmonium_crystal_fire>");
        MonoRecipe.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardIce>,<ore:shardIce>,<ore:shardIce>,<ore:shardIce>|" +
                "<kanake:harmonium_crystal_ice>");
        MonoRecipe.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardMagic>,<ore:shardMagic>,<ore:shardMagic>,<ore:shardMagic>|" +
                "<kanake:harmonium_crystal_magic>");
        MonoRecipe.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<ore:shardEnder>,<ore:shardEnder>,<ore:shardEnder>,<ore:shardEnder>|" +
                "<kanake:harmonium_crystal_end>");
        MonoRecipe.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<minecraft:spider_eye>,<minecraft:spider_eye>,<minecraft:spider_eye>,<minecraft:spider_eye>|" +
                "<kanake:harmonium_crystal_poison>");
        MonoRecipe.addStringRecipes("<ore:gemHarmoniumCrystal>|" +
                "<minecraft:poisonous_potato>,<minecraft:poisonous_potato>,<minecraft:poisonous_potato>,<minecraft:poisonous_potato>|" +
                "<kanake:harmonium_crystal_poison>");

        MonoRecipe.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>," +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>," +
                "<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>,<kanake:harmonium_crystal_fire>|" +
                "<kanake:slime_core_fire>");
        MonoRecipe.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>," +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>," +
                "<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>,<kanake:harmonium_crystal_ice>|" +
                "<kanake:slime_core_ice>");
        MonoRecipe.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>," +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>," +
                "<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>,<kanake:harmonium_crystal_magic>|" +
                "<kanake:slime_core_magic>");
        MonoRecipe.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>," +
                "<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>," +
                "<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>,<kanake:harmonium_crystal_poison>|" +
                "<kanake:slime_core_poison>");
        MonoRecipe.addStringRecipes("<ore:slimeCore>|" +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>," +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>," +
                "<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>,<kanake:harmonium_crystal_end>|" +
                "<kanake:slime_core_end>");

        MonoRecipe.addStringRecipes("<kanake:rune_empty>|" +
                "<kanake:slime_core_fire>,<kanake:slime_core_fire>,<kanake:slime_core_fire>,<kanake:slime_core_fire>|" +
                "<kanake:rune_fire_ball>");
        MonoRecipe.addStringRecipes("<kanake:rune_empty>|" +
                "<kanake:slime_core_ice>,<kanake:slime_core_ice>,<kanake:slime_core_ice>,<kanake:slime_core_ice>|" +
                "<kanake:rune_ice_bolt>");
        MonoRecipe.addStringRecipes("<kanake:rune_empty>|" +
                "<kanake:slime_core_magic>,<kanake:slime_core_magic>,<kanake:slime_core_magic>,<kanake:slime_core_magic>|" +
                "<kanake:rune_magic>");
        MonoRecipe.addStringRecipes("<kanake:rune_empty>|" +
                "<kanake:slime_core_end>,<kanake:slime_core_end>,<kanake:slime_core_end>,<kanake:slime_core_end>|" +
                "<kanake:rune_tp>");

        MonoRecipe.addStringRecipes("<ore:netherStar>|" +
                "<ore:gemEmerald>,<ore:gemEmerald>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:heart>|" +
                "<minecraft:totem_of_undying>");
        MonoRecipe.addStringRecipes("<ore:netherStar>|" +
                "<ore:gemEmerald>,<ore:gemEmerald>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<forge:heart>|" +
                "<minecraft:totem_of_undying>");
        MonoRecipe.addStringRecipes("<ore:netherStar>|" +
                "<ore:gemEmerald>,<ore:gemEmerald>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:humanHeart>|" +
                "<minecraft:totem_of_undying>");
        MonoRecipe.addStringRecipes("<ore:netherStar>|" +
                "<ore:gemEmerald>,<ore:gemEmerald>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:ingotGold>,<ore:heartHuman>|" +
                "<minecraft:totem_of_undying>");

        MonoRecipe.addStringRecipes("<ore:ingotIron>|" +
                "<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>," +
                "<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>,<kanake:ws_kakera>|" +
                "<kanake:ws_ingot>");
        MonoRecipe.addStringRecipes("<kanake:ws_ingot>|<ore:dustMagic>|<kanake:ws_magic_ingot>");
    }
}
