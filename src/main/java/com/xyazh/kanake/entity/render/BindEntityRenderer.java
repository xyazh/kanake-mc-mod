package com.xyazh.kanake.entity.render;

import com.xyazh.kanake.entity.*;
import com.xyazh.kanake.util.Reference;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public class BindEntityRenderer {
    @SubscribeEvent
    public static void bindEntityRenderer(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityWSSoul.class, RenderWSSoul::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityWSSoulBullet.class, RenderWSButtleForSoul::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEye.class, RenderEye::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTreeMan.class, RenderTreeMan::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityForeverItem.class, RenderForeverItem::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityShield.class,RenderShield::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBall.class, RenderBall::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBiimu.class, RenderBiimu::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEnderBolt.class,
                manager -> new RenderInvisibleEntity<EntityEnderBolt>(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityLifeDrain.class,
                manager -> new RenderInvisibleEntity<EntityLifeDrain>(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntitySkyStrikeProjectile.class,
                manager -> new RenderInvisibleEntity<EntitySkyStrikeProjectile>(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntitySkyStrike.class,
                manager -> new RenderInvisibleEntity<EntitySkyStrike>(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicCannonCharge.class,
                manager -> new RenderInvisibleEntity<EntityMagicCannonCharge>(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicCannonBeam.class,
                manager -> new RenderInvisibleEntity<EntityMagicCannonBeam>(manager));
    }

}