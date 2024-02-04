package com.xyazh.kanake.item;


import com.xyazh.kanake.init.ModCreativeTab;
import com.xyazh.kanake.item.axe.ItemHarmoniumCrystalAxe;
import com.xyazh.kanake.item.bauble.ItemFlyNecklace;
import com.xyazh.kanake.item.bauble.ItemManaRingHigh;
import com.xyazh.kanake.item.bauble.ItemManaRingLow;
import com.xyazh.kanake.item.hoe.ItemHarmoniumCrystalHoe;
import com.xyazh.kanake.item.items.*;
import com.xyazh.kanake.item.pickaxe.ItemHarmoniumCrystalPickaxe;
import com.xyazh.kanake.item.potion.ItemPotionMp;
import com.xyazh.kanake.item.storage.ItemManaContainer;
import com.xyazh.kanake.item.swords.ItemHarmoniumCrystalSword;
import com.xyazh.kanake.item.wsitem.ItemWSMagicSword;
import com.xyazh.kanake.item.wsitem.ItemWSSword;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Item TEST_ITEM = new ItemTestItem("test").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE = new ItemBase("rune").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_EMPTY = new ItemBase("rune_empty").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_FIRE_BALL = new ItemFireBallRune("rune_fire_ball").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_FIRE_BALL_LOOK_AT = new ItemFireBallLookAtRune("rune_fire_ball_look_at").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_ICE_BOLT = new ItemIceBoltRune("rune_ice_bolt").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_FIRE_CURSE = new ItemFireCurseRune("rune_fire_curse").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_ICE_FROST = new ItemIceFrostRune("rune_ice_frost").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_EXPLOSION = new ItemExplosionRune("rune_explosion").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_EXPLOSION_LOOK_AT = new ItemExplosionLookAtRune("rune_explosion_look_at").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_MAGIC = new ItemMagicRune("rune_magic").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_TP = new ItemTpRune("rune_tp").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_BLOCKING_LOOK_AT = new ItemBlockingLookAtRune("rune_blocking_look_at").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL = new ItemBase("harmonium_crystal")
            .setOreDict(new String[]{"gemCrystal", "gemQuartz", "gemHarmoniumCrystal"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_AXE = new ItemHarmoniumCrystalAxe("harmonium_crystal_axe").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_HOE = new ItemHarmoniumCrystalHoe("harmonium_crystal_hoe").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_PICKAXE = new ItemHarmoniumCrystalPickaxe("harmonium_crystal_pickaxe").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_SHOVEL = new ItemHarmoniumCrystalSword("harmonium_crystal_shovel").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_SWORD = new ItemHarmoniumCrystalSword("harmonium_crystal_sword").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_FIRE = new ItemBase("harmonium_crystal_fire").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_ICE = new ItemBase("harmonium_crystal_ice").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_MAGIC = new ItemBase("harmonium_crystal_magic").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL_END = new ItemBase("harmonium_crystal_end").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item SLIME_CORE = new ItemBase("slime_core")
            .setOreDict(new String[]{"slimeCore","coreSlime"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item SLIME_CORE_FIRE = new ItemBase("slime_core_fire").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item SLIME_CORE_ICE = new ItemBase("slime_core_ice").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item SLIME_CORE_MAGIC = new ItemBase("slime_core_magic").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item SLIME_CORE_END = new ItemBase("slime_core_end").setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item POTION_SMALL_MP = new ItemPotionMp("potion_small_mp",10).setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item POTION_MP = new ItemPotionMp("potion_mp",40).setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item BOTTLE_SMALL = new ItemBase("bottle_small").setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item CRYSTAL_FIRE = new ItemBase("crystal_fire")
            .setOreDict(new String[]{"shardFire"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item CRYSTAL_ICE = new ItemBase("crystal_ice")
            .setOreDict(new String[]{"shardIce"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item CRYSTAL_MAGIC = new ItemBase("crystal_magic")
            .setOreDict(new String[]{"shardMagic"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item CRYSTAL_END = new ItemBase("crystal_end")
            .setOreDict(new String[]{"shardEnder"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item MONO_SP = new ItemMonoSP("mono_sp").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item MONO_SUB = new ItemBase("mono_sub").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item MONO_FRAME = new ItemBase("mono_frame").setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item MANA_CONTAINER_BIG = new ItemManaContainer("mana_container_big",1200)
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item MANA_CONTAINER = new ItemManaContainer("mana_container")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item MANA_GEM = new ItemManaContainer("mana_gem",20)
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item MANA_RING_LOW = new ItemManaRingLow("mana_ring_low")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item MANA_RING_HIGH = new ItemManaRingHigh("mana_ring_high")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item FLY_NECKLACE = new ItemFlyNecklace("fly_necklace")
        .setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item WS_WS_KAKERA = new ItemBase("ws_kakera")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_WS_INGOT = new ItemBase("ws_ingot")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_WS_NUGGET = new ItemBase("ws_nugget")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_WS_PLATE = new ItemBase("ws_plate")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_MAGIC_INGOT = new ItemBase("ws_magic_ingot")
            .setOreDict(new String[]{"ingotThaumium"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_MAGIC_NUGGET = new ItemBase("ws_magic_nugget")
            .setOreDict(new String[]{"nuggetThaumium"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_MAGIC_PLATE = new ItemBase("ws_magic_plate")
            .setOreDict(new String[]{"plateThaumium"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_POLLUTE_SALT = new ItemBase("pollute_salt")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item WS_SWORD = new ItemWSSword("ws_sword")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item WS_MAGIC_SWORD = new ItemWSMagicSword("ws_magic_sword")
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);

    public static final Item DUST_MAGIC= new ItemBase("dust_magic")
            .setOreDict(new String[]{"dustMagic"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
}
