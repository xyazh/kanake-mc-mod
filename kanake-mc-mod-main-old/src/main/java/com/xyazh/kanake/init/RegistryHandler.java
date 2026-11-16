package com.xyazh.kanake.init;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.item.ItemBlockBase;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.item.items.ItemBase;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber
public class RegistryHandler {
	public static WorldType TYPE_ONE;
	public static final int WILDCARD_VALUE = Short.MAX_VALUE;


	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
		for(Item item :  ModItems.ITEMS){
			if(item instanceof ItemBase){
				ItemBase itemBase = (ItemBase) item;
				for(String s:itemBase.oreDictName){
					OreDictionary.registerOre(s,itemBase);
				}
			}else if(item instanceof ItemBlockBase){
				ItemBlockBase itemBlockBase = (ItemBlockBase) item;
				for(String s:itemBlockBase.oreDictName){
					OreDictionary.registerOre(s,itemBlockBase);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
	}

	@SubscribeEvent
	public static void onEnchantmentRegister(RegistryEvent.Register<Enchantment> event)
	{

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event)
	{
		for(Item item : ModItems.ITEMS)
		{
			if (item instanceof IHasModel)
			{
				((IHasModel)item).registerModels();
			}
		}

		for(Block block : ModBlocks.BLOCKS)
		{
			if (block instanceof IHasModel)
			{
				((IHasModel)block).registerModels();
			}
		}
	}

	public static void preInitRegistries(FMLPreInitializationEvent event)
	{
		//ModEntityInit.registerEntities();
	}

	public static void postInitReg()
	{
	}

	public static void serverRegistries(FMLServerStartingEvent event)
    {
    }
}
