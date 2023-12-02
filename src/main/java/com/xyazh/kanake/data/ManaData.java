package com.xyazh.kanake.data;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.PlayerManaPacket;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class ManaData {
    public static boolean isFull(EntityPlayer player){
        return ManaData.get(player)>= ManaData.getMax(player);
    }

    public static void setFull(EntityPlayer player){
        ManaData.set(player,ManaData.getMax(player));
    }

    public static double getMax(EntityPlayer player){
        return player.getAttributeMap().getAttributeInstance(ManaAttribute.MANA_ATTRIBUTE).getAttributeValue();
    }

    public static double remain(EntityPlayer player){
        return getMax(player)-get(player);
    }

    public static double get(EntityPlayer player){
        NBTTagCompound compound = player.getEntityData();
        return compound.getDouble(Kanake.MODID+":mana");
    }

    public static void set(EntityPlayer player,double n){
        NBTTagCompound compound = player.getEntityData();
        compound.setDouble(Kanake.MODID + ":mana",n);
    }

    public static double add(EntityPlayer player,double n){
        IAttributeInstance instance = player.getAttributeMap().getAttributeInstance(ManaAttribute.MANA_ATTRIBUTE);
        double mana = get(player);
        double can = instance.getAttributeValue() - mana;
        double real = Math.min(can,n);
        set(player,mana+real);
        return n-real;
    }

    public static double sub(EntityPlayer player,double n){
        double mana = get(player);
        double r = mana - n;
        set(player,r<0?0:r);
        return r>=0?0:Math.abs(r);
    }

    public static void setSync(EntityPlayerMP player,double n){
        NBTTagCompound compound = player.getEntityData();
        compound.setDouble(Kanake.MODID + ":mana",n);
        PlayerManaPacket packet = new PlayerManaPacket();
        packet.mana = n;
        Kanake.network.sendTo(packet,player);
    }

    public static double addSync(EntityPlayerMP player,double n){
        IAttributeInstance instance = player.getAttributeMap().getAttributeInstance(ManaAttribute.MANA_ATTRIBUTE);
        double mana = get(player);
        double can = instance.getAttributeValue() - mana;
        double real = Math.min(can,n);
        setSync(player,mana+real);
        return n-real;
    }

    public static double subSync(EntityPlayerMP player,double n){
        double mana = get(player);
        double r = mana - n;
        setSync(player,r<0?0:r);
        return r>=0?0:Math.abs(r);
    }
}
