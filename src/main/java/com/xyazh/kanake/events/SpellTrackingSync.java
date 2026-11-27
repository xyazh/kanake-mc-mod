package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.entity.EntityStar;
import com.xyazh.kanake.entity.EntitySuperBlackHole;
import com.xyazh.kanake.entity.EntityWideLightningStrike;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Sends the current spell age once when a player starts tracking one of the
 * long-lived ritual entities. No dynamic size/temperature state is sent.
 */
@Mod.EventBusSubscriber(modid = Kanake.MODID)
public final class SpellTrackingSync {
    private SpellTrackingSync() {
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() != null && !event.getWorld().isRemote) {
            EntityWideLightningStrike.clearWeatherSnapshots();
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        if (target == null || target.world.isRemote
                || !(player instanceof EntityPlayerMP)) {
            return;
        }
        final EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
        Runnable sync = new Runnable() {
            @Override
            public void run() {
                if (target.isDead || target.world.isRemote) {
                    return;
                }
                if (target instanceof EntitySuperBlackHole) {
                    ((EntitySuperBlackHole) target).syncInitialAge(serverPlayer);
                } else if (target instanceof EntityStar) {
                    ((EntityStar) target).syncInitialAge(serverPlayer);
                } else if (target instanceof EntityWideLightningStrike) {
                    ((EntityWideLightningStrike) target).syncInitialAge(serverPlayer);
                }
            }
        };
        MinecraftServer server = FMLCommonHandler.instance()
                .getMinecraftServerInstance();
        if (server != null) {
            // Let the normal spawn packet reach the client first so the age
            // packet cannot race ahead of the entity creation.
            server.addScheduledTask(sync);
        } else {
            sync.run();
        }
    }
}
