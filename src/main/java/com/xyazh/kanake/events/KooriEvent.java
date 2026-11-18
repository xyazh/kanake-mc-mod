package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.hud.HUDKoori;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.potion.buff.PotionKoori;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class KooriEvent {
    private static final String NBT_KOORI_AI = "kanake_koori_ai";
    private static final String NBT_KOORI_ORIGINAL_AI = "kanake_koori_original_ai";

    private static boolean isFrozen(EntityLivingBase entity) {
        return entity != null && entity.isPotionActive(PotionKoori.POTION_KOORI);
    }

    private static boolean isTestItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.getItem() == ModItems.TEST_ITEM;
    }

    private static boolean isHoldingTestItem(EntityPlayer player) {
        return player != null && (isTestItem(player.getHeldItemMainhand()) || isTestItem(player.getHeldItemOffhand()));
    }

    private static boolean isTestItemInteraction(PlayerInteractEvent event) {
        return isTestItem(event.getItemStack()) || isHoldingTestItem(event.getEntityPlayer());
    }

    @SubscribeEvent
    public static void onFireHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || !isFrozen(entity)) {
            return;
        }
        if (event.getSource().isFireDamage()) {
            event.setAmount(event.getAmount() * 2.0F);
            entity.removePotionEffect(PotionKoori.POTION_KOORI);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity.updateBlocked && entity instanceof EntityLivingBase && isFrozen((EntityLivingBase) entity)) {
            entity.updateBlocked = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (isFrozen(entity) && event.getTarget() != null && entity instanceof EntityLiving) {
            ((EntityLiving) entity).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!isFrozen(entity)) {
            updateKooriAI(entity);
            return;
        }
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        entity.moveStrafing = 0.0F;
        entity.moveVertical = 0.0F;
        entity.moveForward = 0.0F;
        entity.setJumping(false);
        entity.setSprinting(false);
        if (entity instanceof EntityLiving) {
            ((EntityLiving) entity).setAttackTarget(null);
        }
        updateKooriAI(entity);
    }

    private static void updateKooriAI(EntityLivingBase entity) {
        if (entity.world.isRemote || !(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving living = (EntityLiving) entity;
        NBTTagCompound data = living.getEntityData();
        boolean marked = data.getBoolean(NBT_KOORI_AI);
        if (isFrozen(living)) {
            if (!marked) {
                data.setBoolean(NBT_KOORI_AI, true);
                data.setBoolean(NBT_KOORI_ORIGINAL_AI, living.isAIDisabled());
            }
            if (!living.isAIDisabled()) {
                living.setNoAI(true);
            }
        } else if (marked) {
            living.setNoAI(data.getBoolean(NBT_KOORI_ORIGINAL_AI));
            data.removeTag(NBT_KOORI_AI);
            data.removeTag(NBT_KOORI_ORIGINAL_AI);
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (isFrozen(entity)) {
            entity.motionY = 0.0D;
            entity.isAirBorne = false;
        }
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (isFrozen(event.getEntityLiving()) && !isTestItem(event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        if (isFrozen(event.getEntityLiving()) && !isTestItem(event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (isFrozen(player) && !isHoldingTestItem(player)) {
            event.setCanceled(true);
        }
    }

    private static void cancelPlayerInteract(PlayerInteractEvent event) {
        if (isFrozen(event.getEntityPlayer()) && !isTestItemInteraction(event)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        cancelPlayerInteract(event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        cancelPlayerInteract(event);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        cancelPlayerInteract(event);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        cancelPlayerInteract(event);
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        cancelPlayerInteract(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isTestItemInteraction(event)) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isTestItemInteraction(event)) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (isTestItemInteraction(event)) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (isTestItemInteraction(event)) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemAttack(AttackEntityEvent event) {
        if (isHoldingTestItem(event.getEntityPlayer())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayer && isTestItem(event.getItem())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void allowTestItemUseTick(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntityLiving() instanceof EntityPlayer && isTestItem(event.getItem())) {
            event.setCanceled(false);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isFrozen(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onOverlayRender(net.minecraftforge.client.event.RenderGameOverlayEvent event) {
        if (event.getType() != net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if(mc.player == null){
            return;
        }
        PotionEffect effect = mc.player.getActivePotionEffect(PotionKoori.POTION_KOORI);
        if(effect != null) {
            HUDKoori hud = new HUDKoori();
            hud.renderHUD();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event) {
        if (!isFrozen(event.getEntityPlayer())) {
            return;
        }
        MovementInput input = event.getMovementInput();
        input.moveForward = 0.0F;
        input.moveStrafe = 0.0F;
        input.jump = false;
        input.sneak = false;
        input.forwardKeyDown = false;
        input.backKeyDown = false;
        input.leftKeyDown = false;
        input.rightKeyDown = false;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void kooriRenderPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (isFrozen(event.getEntity())) {
            GlStateManager.color(0.6F, 0.6F, 1.0F, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void kooriRenderPost(RenderLivingEvent.Post<EntityLivingBase> event) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
