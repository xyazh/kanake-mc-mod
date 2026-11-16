package com.xyazh.kanake.proxy;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.ClientParticles;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientProxy extends ProxyBase {
    public static final List<KeyBinding> KEY_BINDINGS = new ArrayList<KeyBinding>();


	public boolean isServer()
	{
		return false;
	}

	public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), id));
	}

	public void registerItemRenderer(Item item, String name, int meta)
	{
		ModelResourceLocation resourceLocation = new ModelResourceLocation(String.format("%s:%s", Kanake.MODID, name), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, meta, resourceLocation);
	}

	@Override
	public void registerParticles() {
		ClientParticles.register();
	}
}
