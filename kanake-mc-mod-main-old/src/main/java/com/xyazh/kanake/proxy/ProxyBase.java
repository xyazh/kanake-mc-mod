package com.xyazh.kanake.proxy;

import net.minecraft.item.Item;

public class ProxyBase {
	public boolean isServer()
	{
		return false;
	}

	public void registerItemRenderer(Item item, int meta, String id) {
		//Ignored
	}

	public void registerItemRenderer(Item item, String name, int meta) {
		//Ignored
	}
}
