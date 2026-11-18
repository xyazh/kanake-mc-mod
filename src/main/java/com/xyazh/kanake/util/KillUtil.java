package com.xyazh.kanake.util;

import com.xyazh.kanake.Kanake;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Server-side forced entity cleanup.
 *
 * <p>Entities are removed from the world directly instead of going through
 * {@link Entity#attackEntityFrom} / {@link Entity#onKillCommand()} /
 * {@link EntityLivingBase#onDeath}. Players use the normal death/respawn path,
 * but their health and death event are forced so ordinary invulnerability,
 * respawn protection, creative/spectator protection and totems do not stop it.</p>
 */
@Mod.EventBusSubscriber(modid = Kanake.MODID)
public final class KillUtil {
    private static final ThreadLocal<Boolean> PURGING = new ThreadLocal<Boolean>();
    private static final ThreadLocal<Set<UUID>> FORCED_PLAYER_DEATHS = new ThreadLocal<Set<UUID>>() {
        @Override
        protected Set<UUID> initialValue() {
            return new HashSet<UUID>();
        }
    };
    private static final DataParameter<Float> HEALTH_PARAMETER = findHealthParameter();
    private static final String NBT_ORIGINAL_MAX_HEALTH = "kanake_kill_original_max_health";
    private static final AttributeModifier ZERO_MAX_HEALTH = new AttributeModifier(
            UUID.fromString("5e0c1f52-6d3e-4f0a-9a5c-7f2d8b1c3e64"),
            "kanake_kill_zero_max_health", -1.0D, 2);

    private KillUtil() {
    }

    public static boolean purge(Entity target) {
        if (target == null) {
            return false;
        }
        return purge(target.world, target);
    }

    public static boolean purge(World world, Entity target) {
        if (!beginPurge()) {
            return false;
        }
        try {
            return purgeInternal(world, target);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("purge", target, throwable);
            return false;
        } finally {
            endPurge();
        }
    }

    public static int purgeAll(World world) {
        return purgeAll(world, entity -> true);
    }

    public static int purgeAll(World world, Predicate<? super Entity> filter) {
        if (filter == null || !isServerWorld(world) || !beginPurge()) {
            return 0;
        }
        int removed = 0;
        try {
            List<Entity> snapshot = new ArrayList<Entity>(world.loadedEntityList);
            for (Entity weatherEntity : world.weatherEffects) {
                if (!containsIdentity(snapshot, weatherEntity)) {
                    snapshot.add(weatherEntity);
                }
            }
            for (Entity target : snapshot) {
                if (target == null) {
                    continue;
                }
                try {
                    if (filter.test(target) && purgeInternal(world, target)) {
                        ++removed;
                    }
                } catch (VirtualMachineError | ThreadDeath fatal) {
                    throw fatal;
                } catch (Throwable throwable) {
                    logFailure("purgeAll", target, throwable);
                }
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("purgeAll world=" + world, null, throwable);
        } finally {
            endPurge();
        }
        return removed;
    }

    private static boolean purgeInternal(World world, Entity target) {
        if (target == null || !isServerWorld(world) || target.world != world) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            // Players are connection-owned. Removing them from the world list
            // leaves a ghost player, so use the normal death/respawn path and
            // force only the parts that ordinary death protections can stop.
            return forcePlayerDeath((EntityPlayer) target);
        }

        notifyDragonFight(target);
        detach(target);

        // Do not call setDead()/attackEntityFrom()/onDeath(): all of them are
        // virtual and can be overridden or cancelled. Mark the public field
        // directly and remove the entity from every world-side collection.
        target.isDead = true;
        forceRemoveFromWorld(world, target);

        if (world instanceof WorldServer) {
            WorldServer worldServer = (WorldServer) world;
            try {
                for (EntityPlayer trackingPlayer : worldServer.getEntityTracker().getTrackingPlayers(target)) {
                    if (trackingPlayer instanceof net.minecraft.entity.player.EntityPlayerMP) {
                        target.removeTrackingPlayer((net.minecraft.entity.player.EntityPlayerMP) trackingPlayer);
                    }
                }
            } catch (VirtualMachineError | ThreadDeath fatal) {
                throw fatal;
            } catch (Throwable throwable) {
                logFailure("removeTrackingPlayers", target, throwable);
            }
            try {
                worldServer.getEntityTracker().untrack(target);
            } catch (VirtualMachineError | ThreadDeath fatal) {
                throw fatal;
            } catch (Throwable throwable) {
                logFailure("untrack", target, throwable);
            }
        }

        target.isDead = true;
        return !containsIdentity(world.loadedEntityList, target) && !containsIdentity(world.weatherEffects, target);
    }

    private static boolean forcePlayerDeath(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return false;
        }
        EntityPlayerMP mp = (EntityPlayerMP) player;
        if (mp.isDead) {
            return true;
        }

        NBTTagCompound data = mp.getEntityData();
        if (!data.hasKey(NBT_ORIGINAL_MAX_HEALTH)) {
            double originalMaxHealth = mp.getMaxHealth();
            data.setDouble(NBT_ORIGINAL_MAX_HEALTH, originalMaxHealth > 0.0D ? originalMaxHealth : 20.0D);
        }

        // 1. Force max health to zero.
        setPlayerMaxHealth(mp, 0.0D);
        // 2. Write the health DataParameter directly, bypassing setHealth overrides.
        setRawHealth(mp, 0.0F);

        UUID id = mp.getUniqueID();
        Set<UUID> forced = FORCED_PLAYER_DEATHS.get();
        forced.add(id);
        try {
            // Direct onDeath bypasses attackEntityFrom, invulnerability,
            // respawn protection, creative/spectator checks and the totem.
            // Death drops still follow the normal game rules.
            mp.onDeath(DamageSource.OUT_OF_WORLD);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("player death", mp, throwable);
        } finally {
            forced.remove(id);
        }

        setPlayerMaxHealth(mp, 0.0D);
        setRawHealth(mp, 0.0F);
        detach(mp);
        removePlayerFromWorld(mp);
        return mp.getHealth() <= 0.0F;
    }

    private static void removePlayerFromWorld(EntityPlayerMP player) {
        World world = player.world;
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer worldServer = (WorldServer) world;
        player.isDead = true;

        try {
            worldServer.getEntityTracker().removePlayerFromTrackers(player);
            worldServer.getEntityTracker().untrack(player);
            worldServer.getPlayerChunkMap().removePlayer(player);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("player tracker/chunk-map removal", player, throwable);
        }

        try {
            if (player.connection != null) {
                // The tracker does not send destroy packets to the player's own
                // connection, so clear the client-side player entity explicitly.
                player.connection.sendPacket(new SPacketDestroyEntities(player.getEntityId()));
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("player client destroy sync", player, throwable);
        }

        try {
            removeIdentity(world.loadedEntityList, player);
            removeIdentity(world.playerEntities, player);
            BlockPos pos = player.getPosition();
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            chunk.removeEntity(player);
            world.onEntityRemoved(player);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("player world-list removal", player, throwable);
        }

        player.isDead = true;
        player.addedToChunk = false;
    }

    private static void setPlayerMaxHealth(EntityPlayerMP player, double value) {
        try {
            IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if (attribute != null) {
                attribute.removeModifier(ZERO_MAX_HEALTH);
                attribute.setBaseValue(value);
                if (value <= 0.0D) {
                    // Multiplicative -100% keeps max health at zero even if
                    // another mod adds health modifiers after the base value.
                    attribute.applyModifier(ZERO_MAX_HEALTH);
                }
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("max-health attribute write", player, throwable);
        }
    }

    private static void setRawHealth(EntityLivingBase entity, float value) {
        if (HEALTH_PARAMETER != null) {
            try {
                entity.getDataManager().set(HEALTH_PARAMETER, Float.valueOf(value));
                return;
            } catch (VirtualMachineError | ThreadDeath fatal) {
                throw fatal;
            } catch (Throwable throwable) {
                logFailure("raw health data write", entity, throwable);
            }
        }
        try {
            entity.setHealth(value);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("fallback health write", entity, throwable);
        }
    }

    private static DataParameter<Float> findHealthParameter() {
        try {
            for (Field field : EntityLivingBase.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) || field.getType() != DataParameter.class) {
                    continue;
                }
                Type genericType = field.getGenericType();
                if (!(genericType instanceof ParameterizedType)) {
                    continue;
                }
                Type[] arguments = ((ParameterizedType) genericType).getActualTypeArguments();
                if (arguments.length != 1 || arguments[0] != Float.class) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(null);
                if (value instanceof DataParameter) {
                    return (DataParameter<Float>) value;
                }
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("resolve health data parameter", null, throwable);
        }
        return null;
    }

    /**
     * Only active while {@link #forcePlayerDeath(EntityPlayer)} is invoking
     * onDeath. It removes a cancellation made by a higher-priority
     * LivingDeathEvent handler for that specific forced death.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onForcedPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        Set<UUID> forced = FORCED_PLAYER_DEATHS.get();
        if (forced.remove(event.getEntityLiving().getUniqueID())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(NBT_ORIGINAL_MAX_HEALTH)) {
            return;
        }

        double originalMaxHealth = data.getDouble(NBT_ORIGINAL_MAX_HEALTH);
        if (originalMaxHealth <= 0.0D) {
            originalMaxHealth = 20.0D;
        }
        setPlayerMaxHealth(player, originalMaxHealth);
        setRawHealth(player, player.getMaxHealth());
        data.removeTag(NBT_ORIGINAL_MAX_HEALTH);

        try {
            if (player.connection != null) {
                AttributeMap attributeMap = (AttributeMap) player.getAttributeMap();
                player.connection.sendPacket(new SPacketEntityProperties(player.getEntityId(), attributeMap.getWatchedAttributes()));
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("player max-health sync", player, throwable);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        IAttributeInstance oldMaxHealth = event.getOriginal().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (oldMaxHealth == null || !event.getOriginal().getEntityData().hasKey(NBT_ORIGINAL_MAX_HEALTH)) {
            return;
        }

        double originalMaxHealth = event.getOriginal().getEntityData().getDouble(NBT_ORIGINAL_MAX_HEALTH);
        if (originalMaxHealth <= 0.0D) {
            originalMaxHealth = 20.0D;
        }
        setPlayerMaxHealth(player, originalMaxHealth);
        setRawHealth(player, player.getMaxHealth());
    }

    private static void detach(Entity target) {
        try {
            if (target.isBeingRidden()) {
                target.removePassengers();
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("removePassengers", target, throwable);
        }

        try {
            if (target.isRiding()) {
                target.dismountRidingEntity();
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("dismountRidingEntity", target, throwable);
        }

        try {
            for (Entity passenger : new ArrayList<Entity>(target.getPassengers())) {
                try {
                    passenger.dismountRidingEntity();
                } catch (VirtualMachineError | ThreadDeath fatal) {
                    throw fatal;
                } catch (Throwable throwable) {
                    logFailure("dismount passenger", passenger, throwable);
                }
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("getPassengers", target, throwable);
        }

    }

    private static void notifyDragonFight(Entity target) {
        if (!(target instanceof EntityDragon)) {
            return;
        }
        EntityDragon dragon = (EntityDragon) target;
        if (dragon.isDead) {
            return;
        }
        try {
            DragonFightManager fightManager = dragon.getFightManager();
            if (fightManager != null) {
                fightManager.processDragonDeath(dragon);
            }
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("dragon fight cleanup", target, throwable);
        }
    }

    private static void forceRemoveFromWorld(World world, Entity target) {
        target.isDead = true;

        try {
            removeIdentity(world.loadedEntityList, target);
            removeIdentity(world.weatherEffects, target);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("forced loaded-list removal", target, throwable);
        }

        try {
            BlockPos pos = target.getPosition();
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            chunk.removeEntity(target);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("forced chunk removal", target, throwable);
        }

        try {
            world.onEntityRemoved(target);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("forced world-removal callback", target, throwable);
        }

        // Re-check after onEntityRemoved: a listener may try to re-add the
        // entity directly to the loaded list.
        try {
            removeIdentity(world.loadedEntityList, target);
            removeIdentity(world.weatherEffects, target);
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            logFailure("post-callback loaded-list removal", target, throwable);
        }

        target.isDead = true;
        target.addedToChunk = false;
    }

    private static boolean containsIdentity(List<? extends Entity> entities, Entity target) {
        for (Entity entity : entities) {
            if (entity == target) {
                return true;
            }
        }
        return false;
    }

    private static void removeIdentity(List<? extends Entity> entities, Entity target) {
        for (int i = entities.size() - 1; i >= 0; --i) {
            if (entities.get(i) == target) {
                entities.remove(i);
            }
        }
    }

    private static boolean isServerWorld(World world) {
        if (world == null || world.isRemote) {
            return false;
        }
        MinecraftServer server = world.getMinecraftServer();
        return server == null || server.isCallingFromMinecraftThread();
    }

    private static boolean beginPurge() {
        if (Boolean.TRUE.equals(PURGING.get())) {
            return false;
        }
        PURGING.set(Boolean.TRUE);
        return true;
    }

    private static void endPurge() {
        PURGING.remove();
    }

    private static void logFailure(String stage, Entity target, Throwable throwable) {
        String targetInfo = "null";
        if (target != null) {
            try {
                targetInfo = target.getClass().getName() + "#" + target.getEntityId();
            } catch (Throwable ignored) {
                targetInfo = target.getClass().getName();
            }
        }
        String message = "KillUtil failed during " + stage + " for " + targetInfo;
        if (Kanake.logger != null) {
            Kanake.logger.error(message, throwable);
        } else {
            System.err.println(message);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }
    }
}
