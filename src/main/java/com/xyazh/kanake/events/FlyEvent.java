package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.item.bauble.ItemFlyNecklace;
import com.xyazh.kanake.network.FlyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.HashSet;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class FlyEvent {
    private static boolean SPACE_KEY_DOWN = false;
    public static final HashMap<EntityPlayer,Boolean> PLAYER_MAP = new HashMap<>();

    @SubscribeEvent
    public static void flyPlayer(TickEvent.PlayerTickEvent event){
        if(!PLAYER_MAP.containsKey(event.player)){
            return;
        }
        if(PLAYER_MAP.get(event.player)){
            if((!event.player.isCreative())&&ManaData.get(event.player)<=0){
                return;
            }
            boolean flag = false;
            if(Kanake.HAS_BAUBLES){
                if(baubles.api.BaublesApi.isBaubleEquipped(event.player, ModItems.FLY_NECKLACE)>=0){
                    flag = true;
                }
            }
            if(!flag){
                if(event.player.getHeldItemMainhand().getItem() instanceof ItemFlyNecklace){
                    flag = true;
                }else if(event.player.getHeldItemOffhand().getItem() instanceof ItemFlyNecklace){
                    flag = true;
                }
            }
            if(flag){
                event.player.motionY += 0.1;
                event.player.motionY = Math.min(1.5,event.player.motionY);
                if(!event.player.isCreative()){
                    if(Kanake.rand.nextFloat() <= 0.05){
                        if(event.player instanceof EntityPlayerMP){
                            ManaData.subSync((EntityPlayerMP) event.player, Kanake.rand.nextFloat() + 0.5);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        for(ItemStack itemStack:entity.getHeldEquipment()){
            if (itemStack.getItem() instanceof ItemFlyNecklace){
                event.setCanceled(true);
                return;
            }
        }
        if(!Kanake.HAS_BAUBLES){
            return;
        }
        if(entity instanceof EntityPlayer){
            if(baubles.api.BaublesApi.isBaubleEquipped((EntityPlayer) entity, ModItems.FLY_NECKLACE)>=0){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onSpace(TickEvent.ClientTickEvent event){
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if(player == null){
            return;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            if(!SPACE_KEY_DOWN){
                FlyPacket packet = new FlyPacket();
                packet.keyDown = true;
                PLAYER_MAP.put(player, true);
                Kanake.network.sendToServer(packet);
            }
            SPACE_KEY_DOWN = true;
        }else {
            if(SPACE_KEY_DOWN){
                FlyPacket packet = new FlyPacket();
                packet.keyDown = false;
                PLAYER_MAP.put(player, false);
                Kanake.network.sendToServer(packet);
            }
            SPACE_KEY_DOWN = false;
        }
    }
}
