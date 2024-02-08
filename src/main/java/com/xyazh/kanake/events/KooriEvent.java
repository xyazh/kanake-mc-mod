package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.hud.HUDKoori;
import com.xyazh.kanake.network.KooriEntityPacket;
import com.xyazh.kanake.potion.buff.PotionKoori;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class KooriEvent {
    public static Set<EntityLivingBase> entitySet = new HashSet<>();
    public static Set<EntityLivingBase> entityRemoveSet = new HashSet<>();
    public static Set<UUID> clientEntitySet = new HashSet<>();

    @SubscribeEvent
    public static void onFireHurt(LivingHurtEvent event){
        EntityLivingBase entityLivingBase = event.getEntityLiving();
        if(entityLivingBase.world.isRemote){
            return;
        }
        if(!entitySet.contains(entityLivingBase)){
            return;
        }
        if(event.getSource().isFireDamage()){
            entityRemoveSet.add(entityLivingBase);
            event.setAmount(event.getAmount()*2);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event){
        for(EntityLivingBase entityLivingBase:entitySet){
            if(entityLivingBase == null){
                continue;
            }
            PotionEffect effect = entityLivingBase.getActivePotionEffect(PotionKoori.POTION_KOORI);
            if(effect == null){
                entityRemoveSet.add(entityLivingBase);
                continue;
            }
            entityLivingBase.extinguish();
            entityLivingBase.updateBlocked = true;
            entityLivingBase.onEntityUpdate();
        }
        for(EntityLivingBase entityLivingBase:entityRemoveSet){
            entityLivingBase.updateBlocked = false;
            KooriEntityPacket kooriEntityPacket = new KooriEntityPacket();
            kooriEntityPacket.type = false;
            kooriEntityPacket.id = entityLivingBase.getUniqueID();
            Kanake.network.sendToAll(kooriEntityPacket);
            entityLivingBase.removePotionEffect(PotionKoori.POTION_KOORI);
        }
        entitySet.removeAll(entityRemoveSet);
        entityRemoveSet.clear();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if(mc.player == null){
            return;
        }
        PotionEffect effect = mc.player.getActivePotionEffect(PotionKoori.POTION_KOORI);
        if(effect != null) {
            mc.player.updateBlocked = true;
        }else {
            mc.player.updateBlocked = false;
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
    public static void kooriRender(net.minecraftforge.client.event.RenderLivingEvent.Pre<EntityLivingBase> event){
        EntityLivingBase entity = event.getEntity();
        UUID uuid = entity.getUniqueID();
        if(clientEntitySet.contains(uuid)) {
            net.minecraft.client.renderer.GlStateManager.color(0.6F, 0.6F, 1.0F, 1.0F);
        }
    }
}
