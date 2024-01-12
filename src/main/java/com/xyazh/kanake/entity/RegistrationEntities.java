package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class RegistrationEntities {
    @SubscribeEvent
    public static void onEntityRegistation(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityFireBall.class)
                .id(new ResourceLocation(Kanake.MODID, "fire_ball"), 233)
                .name("fire_ball")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityKoori.class)
                .id(new ResourceLocation(Kanake.MODID, "koori"), 234)
                .name("koori")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityFireCurse.class)
                .id(new ResourceLocation(Kanake.MODID, "fire_curse"), 235)
                .name("fire_curse")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityIceFrost.class)
                .id(new ResourceLocation(Kanake.MODID, "ice_frost"), 236)
                .name("ice_frost")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityForeverItem.class)
                .id(new ResourceLocation(Kanake.MODID, "forever_item"), 237)
                .name("forever_item")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntitySpawnParticle.class)
                .id(new ResourceLocation(Kanake.MODID, "particle_entity"), 238)
                .name("particle_entity")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityFireBallLookAt.class)
                .id(new ResourceLocation(Kanake.MODID, "fire_ball_look_at"), 239)
                .name("fire_ball_look_at")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityExplosionLookAt.class)
                .id(new ResourceLocation(Kanake.MODID, "explosion_look_at"), 240)
                .name("explosion_look_at")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityExplosion.class)
                .id(new ResourceLocation(Kanake.MODID, "explosion"), 241)
                .name("explosion")
                .tracker(1024, 3, true)
                .build()
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityMagic.class)
                .id(new ResourceLocation(Kanake.MODID, "magic"), 242)
                .name("magic")
                .tracker(1024, 3, true)
                .build()
        );
        EntityRegistry.registerModEntity(new ResourceLocation(Kanake.MODID,"ws_knight"),
                EntityWSKnight.class,
                "ws_knight",
                243,
                Kanake.instance,
                50,
                3,
                true,
                0xcccccc, 0x7777ff
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityBlockingLookAt.class)
                .id(new ResourceLocation(Kanake.MODID, "blocking_look_at"), 244)
                .name("blocking_look_at")
                .tracker(1024, 3, true)
                .build()
        );
        EntityRegistry.registerModEntity(new ResourceLocation(Kanake.MODID,"magic_witch"),
                EntityMagicWitch.class,
                "magic_witch",
                245,
                Kanake.instance,
                50,
                3,
                true,
                0xcccccc, 0x9d08bf
        );
        EntityRegistry.registerModEntity(new ResourceLocation(Kanake.MODID,"ws_soul"),
                EntityWSSoul.class,
                "ws_soul",
                246,
                Kanake.instance,
                50,
                3,
                true,
                0xcccccc, 0xaaaaaa
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityWSSoulBullet.class)
                .id(new ResourceLocation(Kanake.MODID, "ws_soul_bullet"), 247)
                .name("ws_soul_bullet")
                .tracker(1024, 3, true)
                .build()
        );
        EntityRegistry.registerModEntity(new ResourceLocation(Kanake.MODID,"tree_man"),
                EntityTreeMan.class,
                "tree_man",
                248,
                Kanake.instance,
                50,
                3,
                true,
                0xcccccc, 0x00ff00
        );
        event.getRegistry().register(EntityEntryBuilder.create()
                .entity(EntityLaunch.class)
                .id(new ResourceLocation(Kanake.MODID, "launch"), 249)
                .name("launch")
                .tracker(1024, 3, true)
                .build()
        );
    }

    @SubscribeEvent
    public static void bindEntityRenderer(ModelRegistryEvent event) {
        //RenderingRegistry.registerEntityRenderingHandler(WSASoul.class, RenderWSASoul::new);
    }
}
