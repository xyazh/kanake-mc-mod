package com.xyazh.kanake.entity.render;

import com.xyazh.kanake.entity.EntityEye;
import com.xyazh.kanake.entity.EntityWSSoul;
import com.xyazh.kanake.entity.EntityWSSoulBullet;
import com.xyazh.kanake.util.Reference;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class BindEntityRenderer {
    @SubscribeEvent
    public static void bindEntityRenderer(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityWSSoul.class, RenderWSSoul::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityWSSoulBullet.class, RenderWSButtleForSoul::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEye.class, RenderEye::new);
    }

}