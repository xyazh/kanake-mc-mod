package com.xyazh.kanake.block;

import com.xyazh.kanake.block.blocks.*;
import com.xyazh.kanake.block.blocks.clean.BlockClean;
import com.xyazh.kanake.block.blocks.crystaler.BlockCrystaler;
import com.xyazh.kanake.block.blocks.crystaler.TileCrystaler;
import com.xyazh.kanake.block.blocks.manastorage.BlockInfManaStorage;
import com.xyazh.kanake.block.blocks.manastorage.BlockManaStorage;
import com.xyazh.kanake.block.blocks.manastove.BlockManaStove;
import com.xyazh.kanake.block.blocks.manatable.*;
import com.xyazh.kanake.block.blocks.rail.WSRailBase;
import com.xyazh.kanake.block.blocks.rail.WSRailPoweredBase;
import com.xyazh.kanake.block.blocks.teleportation.BlockTeleportation;
import com.xyazh.kanake.block.blocks.unstableteleportation.BlockUnstableTeleportation;
import com.xyazh.kanake.init.ModCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
	public static final List<Block> BLOCKS = new ArrayList<Block>();

	public static final Block N_FIRE = new BlockNFire("n_fire");
	public static final Block N_ICE = new BlockNIce("n_ice");
	public static final Block TP = new BlockTeleportation("tp");
	public static final Block UN_YP = new BlockUnstableTeleportation("un_yp")
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block CLEAN = new BlockClean("clean")
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);;

	public static final Block HARMONIUM_CRYSTAL_ORE =  new BlockHarmoniumCrystalOre("harmonium_crystal_ore")
			.setOreDict(new String[]{"oreHarmoniumCrystal"});

	public static final Block MANA_TABLE_SHOW = new BlockShowcase("mono_table", Material.ROCK, TileManaWithForeverEntity.class)
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block T_MONO = new BlockTableMono("t_mono", Material.ROCK, TileTableMono.class)
			.setOreDict(new String[]{"blockQuartz"})
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block MONO = new BlockTableCoreMono("mono", Material.ROCK, TileTableCoreMono.class)
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block CRYSTALER = new BlockCrystaler("crystaler", Material.ROCK, TileCrystaler.class)
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block COMPRESSED1X_QUARTZ_BLOCK = new BlockBase("compressed1x_quartz_block", Material.ROCK)
			.setOreDict(new String[]{"compressed1xQuartzBlock"})
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block MANA_STORAGE = new BlockManaStorage("mana_storage", Material.ROCK)
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block INF_MANA_STORAGE = new BlockInfManaStorage("inf_mana_storage", Material.ROCK)
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block MANA_STOVE = new BlockManaStove("mana_stove", Material.ROCK)
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block PURE_HANA = new BlockPureHana("pure_hana")
			.setCreativeTab(ModCreativeTab.KNK_CREATIVE);

	public static final Block WS_WOOD_RAIL = new WSRailBase("wooden_rail")
			.setISoundType(SoundType.WOOD)
			.setRailMaxSpeed(0.3f)
			.setCreativeTab(CreativeTabs.TRANSPORTATION);
	public static final Block WS_WS_RAIL = new WSRailBase("ws_rail")
			.setISoundType(SoundType.METAL)
			.setRailMaxSpeed(1.5f)
			.setCreativeTab(CreativeTabs.TRANSPORTATION);
	public static final Block WS_WS_POWERED_RAIL = new WSRailPoweredBase("ws_powered_rail")
			.setISoundType(SoundType.METAL)
			.setRailMaxSpeed(1.5f)
			.setCreativeTab(CreativeTabs.TRANSPORTATION);
	public static final Block WS_MAGIC_RAIL = new WSRailBase("ws_magic_rail")
			.setISoundType(SoundType.METAL)
			.setRailMaxSpeed(4.5f)
			.setCreativeTab(CreativeTabs.TRANSPORTATION);
	public static final Block WS_MAGIC_POWERED_RAIL = new WSRailPoweredBase("ws_magic_powered_rail")
			.setISoundType(SoundType.METAL)
			.setRailMaxSpeed(4.5f)
			.setCreativeTab(CreativeTabs.TRANSPORTATION);
}
