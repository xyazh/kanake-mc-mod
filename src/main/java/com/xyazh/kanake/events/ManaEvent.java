package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.data.ManaAttribute;
import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.network.ManaPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class ManaEvent {
    @SubscribeEvent
    public static void mana(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if(entity.world.isRemote){
            return;
        }
        if(!(entity instanceof EntityPlayer)){
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        NBTTagCompound compound = player.getEntityData();
        double mana = player.getAttributeMap().getAttributeInstance(ManaAttribute.MANA_ATTRIBUTE).getAttributeValue();
        if(compound.hasKey(Kanake.MODID + ":mana")){
            mana = compound.getDouble(Kanake.MODID + ":mana");
        }else{
            compound.setDouble(Kanake.MODID + ":mana",mana);
        }
        if(player instanceof EntityPlayerMP){
            ManaPacket manaPacket = new ManaPacket();
            manaPacket.mana = mana;
            Kanake.network.sendTo(manaPacket,(EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public static void entityInit(EntityEvent.EntityConstructing event) {
        final Entity e = event.getEntity();
        if (e instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e;
            player.getAttributeMap().registerAttribute(ManaAttribute.MANA_ATTRIBUTE);
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
        if(mc.player.isCreative()){
            return;
        }
        double n,maxN;
        n = ManaData.get(mc.player);
        maxN = ManaData.getMax(mc.player);
        if(com.xyazh.kanake.gui.ManaHUD.hud == null){
            com.xyazh.kanake.gui.ManaHUD.hud = new com.xyazh.kanake.gui.ManaHUD();
        }
        com.xyazh.kanake.gui.ManaHUD.hud.renderHUD(n,maxN);
    }

    @SubscribeEvent
    public static void playerAutoHealMana(TickEvent.PlayerTickEvent event){
        if(event.player.world.isRemote){
            return;
        }
        if(event.phase.equals(TickEvent.Phase.START)){
            if(Kanake.rand.nextFloat() <= 0.001){
                if(!ManaData.isFull(event.player)&&(event.player instanceof EntityPlayerMP)){
                    ManaData.addSync((EntityPlayerMP) event.player, Kanake.rand.nextFloat() + 0.5);
                }
            };
        }
    }

    @SubscribeEvent
    public static void playerSleepHealMana(PlayerWakeUpEvent event){
        EntityPlayer player = event.getEntityPlayer();
        ManaData.setFull(player);
    }
}
