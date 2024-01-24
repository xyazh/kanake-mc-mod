package com.xyazh.kanake.block;

import com.xyazh.kanake.block.blocks.BlockBase;
import com.xyazh.kanake.block.blocks.BlockHarmoniumCrystalOre;
import com.xyazh.kanake.block.blocks.BlockNIce;
import com.xyazh.kanake.block.blocks.BlockNFire;
import com.xyazh.kanake.block.blocks.clean.BlockClean;
import com.xyazh.kanake.block.blocks.crystaler.BlockCrystaler;
import com.xyazh.kanake.block.blocks.crystaler.TileCrystaler;
import com.xyazh.kanake.block.blocks.manastorage.BlockManaStorage;
import com.xyazh.kanake.block.blocks.manastove.BlockManaStove;
import com.xyazh.kanake.block.blocks.manatable.*;
import com.xyazh.kanake.block.blocks.teleportation.BlockTeleportation;
import com.xyazh.kanake.init.ModCreativeTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
	public static final List<Block> BLOCKS = new ArrayList<Block>();

	public static final Block N_FIRE = new BlockNFire("n_fire");
	public static final Block N_ICE = new BlockNIce("n_ice");
	public static final Block TP = new BlockTeleportation("tp");

	public static final Block CLEAN = new BlockClean("clean").setCreativeTab(ModCreativeTab.SBW_CREATIVE);;

	public static final Block HARMONIUM_CRYSTAL_ORE =  new BlockHarmoniumCrystalOre("harmonium_crystal_ore")
			.setOreDict(new String[]{"oreHarmoniumCrystal"});

	public static final Block MANA_TABLE_SHOW = new BlockShowcase("mono_table", Material.IRON, TileManaWithForeverEntity.class)
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);

	public static final Block T_MONO = new BlockTableMono("t_mono", Material.IRON, TileTableMono.class)
			.setOreDict(new String[]{"blockQuartz"})
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);

	public static final Block MONO = new BlockTableCoerMono("mono", Material.IRON, TileTableCoreMono.class)
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);

	public static final Block CRYSTALER = new BlockCrystaler("crystaler", Material.IRON, TileCrystaler.class)
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);

	public static final Block COMPRESSED1X_QUARTZ_BLOCK = new BlockBase("compressed1x_quartz_block", Material.IRON)
			.setOreDict(new String[]{"compressed1xQuartzBlock"})
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);

	public static final Block MANA_STORAGE = new BlockManaStorage("mana_storage", Material.ROCK)
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);

	public static final Block MANA_STOVE = new BlockManaStove("mana_stove", Material.ROCK)
			.setCreativeTab(ModCreativeTab.SBW_CREATIVE);
}
