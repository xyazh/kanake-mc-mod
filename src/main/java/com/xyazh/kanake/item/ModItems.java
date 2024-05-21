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
import com.xyazh.kanake.magic.Magic;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Item TEST_ITEM = new ItemTestItem("test").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    //public static final Item TEST_ITEM2 = new ItemTestItem2("test2").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE = new ItemBase("rune").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_EMPTY = new ItemBase("rune_empty").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item RUNE_EMPTY_MAGIC = new ItemEmptyMagicRune("rune_empty_magic").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
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
    public static final Item RUNE_LAUNCH = new ItemLaunchRune("rune_launch").setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item HARMONIUM_CRYSTAL = new ItemBase("harmonium_crystal")
            .setOreDict(new String[]{"gemCrystal", "gemQuartz", "gemHarmoniumCrystal"})
            .setCreativeTab(ModCreativeTab.KNK_CREATIVE);
    public static final Item PURE_HARMONIUM_CRYSTAL =  new ItemBase("harmonium_crystal_pure")
            .setOreDict(new String[]{"gemPureCrystal", "gemPureQuartz", "gemPureHarmoniumCrystal"})
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

    public static final ItemEmblem EMBLEM_SPAWN = new ItemEmblem(Magic.SPAWN.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SPEED_LOW = new ItemEmblem(Magic.SPEED_LOW.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SPEED_MEDIUM = new ItemEmblem(Magic.SPEED_MEDIUM.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SPEED_HIGH = new ItemEmblem(Magic.SPEED_HIGH.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_RANDOM_SPEED_LOW = new ItemEmblem(Magic.RANDOM_SPEED_LOW.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_RANDOM_SPEED_MEDIUM = new ItemEmblem(Magic.RANDOM_SPEED_MEDIUM.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_RANDOM_SPEED_HIGH = new ItemEmblem(Magic.RANDOM_SPEED_HIGH.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_NOP_LOW = new ItemEmblem(Magic.NOP_LOW.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_NOP_MEDIUM = new ItemEmblem(Magic.NOP_MEDIUM.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_NOP_HIGH = new ItemEmblem(Magic.NOP_HIGH.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_CONCURRENT = new ItemEmblem(Magic.CONCURRENT.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_EXPLODE_SMALL = new ItemEmblem(Magic.EXPLODE_SMALL.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_EXPLODE_MEDIUM = new ItemEmblem(Magic.EXPLODE_MEDIUM.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_EXPLODE_BIG = new ItemEmblem(Magic.EXPLODE_BIG.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_EXPLODE_SMALL_DEAD = new ItemEmblem(Magic.EXPLODE_SMALL_DEAD.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_EXPLODE_MEDIUM_DEAD = new ItemEmblem(Magic.EXPLODE_MEDIUM_DEAD.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_EXPLODE_BIG_DEAD = new ItemEmblem(Magic.EXPLODE_BIG_DEAD.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_DEAD = new ItemEmblem(Magic.DEAD.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_HEAT = new ItemEmblem(Magic.HEAT.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_COOL = new ItemEmblem(Magic.COOL.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_CALLBACK = new ItemEmblem(Magic.CALLBACK.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_STOP = new ItemEmblem(Magic.STOP.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_NO_GRAVITY = new ItemEmblem(Magic.GRAVITY.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SET_SUB = new ItemEmblem(Magic.SET_SUB.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SET_NOT_SUB = new ItemEmblem(Magic.SET_NOT_SUB.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SPAWN_CROSS = new ItemEmblem(Magic.SPAWN_CROSS.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SUB_JMP = new ItemEmblem(Magic.SUB_JMP.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SUB_JMP_10 = new ItemEmblem(Magic.SUB_JMP_10.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_JMP = new ItemEmblem(Magic.JMP.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_JMP_10 = new ItemEmblem(Magic.JMP_10.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_NOT_STOP = new ItemEmblem(Magic.NOT_STOP.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_CLEAR = new ItemEmblem(Magic.CLEAR.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_OPPOSITE_VELOCITY = new ItemEmblem(Magic.OPPOSITE_VELOCITY.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SERIES_2 = new ItemEmblem(Magic.SERIES_2.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SERIES_3 = new ItemEmblem(Magic.SERIES_3.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SERIES_5 = new ItemEmblem(Magic.SERIES_5.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_RIDE = new ItemEmblem(Magic.RIDE.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_ADD_GRAVITY = new ItemEmblem(Magic.ADD_GRAVITY.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_SUB_GRAVITY = new ItemEmblem(Magic.SUB_GRAVITY.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
    public static final ItemEmblem EMBLEM_KEEP_EXPLODE = new ItemEmblem(Magic.KEEP_EXPLODE.order)
            .setCreativeTab(ModCreativeTab.KNK_EMBLEM);
}
