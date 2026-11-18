package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Prevents other mods from deleting or disarming the test item while a player
 * is holding it. Death drops are deliberately not touched: they still follow
 * the world's keepInventory/game rules.
 */
@Mod.EventBusSubscriber(modid = Kanake.MODID)
public final class TestItemProtection {
    private static final Map<UUID, HeldTestItem> HELD = new HashMap<UUID, HeldTestItem>();

    private TestItemProtection() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof EntityPlayerMP) || event.player.world.isRemote) {
            return;
        }
        if (event.phase != TickEvent.Phase.START && event.phase != TickEvent.Phase.END) {
            return;
        }
        protect((EntityPlayerMP) event.player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void ensureBeforeInteraction(PlayerInteractEvent event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            protect((EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void ensureBeforeAttack(AttackEntityEvent event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            protect((EntityPlayerMP) event.getEntityPlayer());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void ensureBeforeUseItem(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            protect((EntityPlayerMP) event.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onItemToss(ItemTossEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null || player.isDead || player.getHealth() <= 0.0F) {
            // Death drops use another path and should remain governed by
            // keepInventory and the rest of the normal death rules.
            return;
        }
        if (isTestItem(event.getEntityItem().getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            HELD.remove(event.getEntityLiving().getUniqueID());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        HELD.remove(event.getOriginal().getUniqueID());
        HELD.remove(event.getEntityPlayer().getUniqueID());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        HELD.remove(event.player.getUniqueID());
    }

    private static void protect(EntityPlayerMP player) {
        UUID id = player.getUniqueID();
        if (player.isDead || player.getHealth() <= 0.0F) {
            HELD.remove(id);
            return;
        }

        ItemStack main = player.getHeldItemMainhand();
        ItemStack off = player.getHeldItemOffhand();
        HeldTestItem state = HELD.get(id);

        if (state == null) {
            if (isTestItem(main)) {
                HELD.put(id, new HeldTestItem(EnumHand.MAIN_HAND, player.inventory.currentItem, main));
            } else if (isTestItem(off)) {
                HELD.put(id, new HeldTestItem(EnumHand.OFF_HAND, -1, off));
            }
            return;
        }

        if (state.hand == EnumHand.MAIN_HAND) {
            if (player.inventory.currentItem != state.hotbarSlot) {
                // The player intentionally changed hotbar slot.
                HELD.remove(id);
                return;
            }

            if (isTestItem(main)) {
                state.snapshot = main.copy();
                return;
            }

            ItemStack found = findInInventory(player, EnumHand.MAIN_HAND);
            setLocked(player, state, found.isEmpty() ? state.snapshot : found);
            return;
        }

        if (isTestItem(off)) {
            state.snapshot = off.copy();
            return;
        }
        if (isTestItem(main)) {
            HELD.put(id, new HeldTestItem(EnumHand.MAIN_HAND, player.inventory.currentItem, main));
            return;
        }

        ItemStack found = findInInventory(player, EnumHand.OFF_HAND);
        setLocked(player, state, found.isEmpty() ? state.snapshot : found);
    }

    private static ItemStack findInInventory(EntityPlayerMP player, EnumHand lockedHand) {
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            if (lockedHand == EnumHand.MAIN_HAND && i == player.inventory.currentItem) {
                continue;
            }
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (isTestItem(stack)) {
                player.inventory.mainInventory.set(i, ItemStack.EMPTY);
                return stack.copy();
            }
        }

        if (lockedHand != EnumHand.OFF_HAND) {
            ItemStack off = player.inventory.offHandInventory.get(0);
            if (isTestItem(off)) {
                player.inventory.offHandInventory.set(0, ItemStack.EMPTY);
                return off.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    private static void setLocked(EntityPlayerMP player, HeldTestItem state, ItemStack stack) {
        ItemStack previous = player.getHeldItem(state.hand);
        if (!previous.isEmpty() && !isTestItem(previous)) {
            player.setHeldItem(state.hand, ItemStack.EMPTY);
            storePrevious(player, state, previous);
        }

        ItemStack replacement = stack.copy();
        player.setHeldItem(state.hand, replacement);
        state.snapshot = replacement.copy();
        player.inventory.markDirty();
        try {
            player.inventoryContainer.detectAndSendChanges();
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable throwable) {
            Kanake.logger.error("TestItemProtection could not sync inventory for " + player.getName(), throwable);
        }
    }

    private static void storePrevious(EntityPlayerMP player, HeldTestItem state, ItemStack previous) {
        for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
            if (state.hand == EnumHand.MAIN_HAND && i == player.inventory.currentItem) {
                continue;
            }
            if (player.inventory.mainInventory.get(i).isEmpty()) {
                player.inventory.mainInventory.set(i, previous);
                return;
            }
        }

        if (state.hand == EnumHand.MAIN_HAND && player.inventory.offHandInventory.get(0).isEmpty()) {
            player.inventory.offHandInventory.set(0, previous);
            return;
        }

        player.dropItem(previous.copy(), false);
    }

    private static boolean isTestItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() == ModItems.TEST_ITEM;
    }

    private static final class HeldTestItem {
        private final EnumHand hand;
        private final int hotbarSlot;
        private ItemStack snapshot;

        private HeldTestItem(EnumHand hand, int hotbarSlot, ItemStack stack) {
            this.hand = hand;
            this.hotbarSlot = hotbarSlot;
            this.snapshot = stack.copy();
        }
    }
}
