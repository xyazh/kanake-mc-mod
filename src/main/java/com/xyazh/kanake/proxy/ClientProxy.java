package com.xyazh.kanake.proxy;

import com.xyazh.kanake.Kanake;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends ProxyBase {
    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<KeyBinding>();


	public boolean isServer()
	{
		return false;
	}

	public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

	public void registerItemRenderer(Item item, String name, int meta)
	{
		ModelResourceLocation resourceLocation = new ModelResourceLocation(String.format("%s:models/item/%s", Kanake.MODID,name));
		ModelLoader.setCustomModelResourceLocation(item, meta, resourceLocation);
	}
}
