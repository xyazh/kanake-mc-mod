package com.xyazh.kanake.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;
import java.util.UUID;


public class TagUtil {
    public static void readIntListFromNBT(NBTTagCompound compound, String key, List<Integer> list) {
        NBTTagCompound compound1 = compound.getCompoundTag(key);
        if(!compound1.hasKey("size")){
            return;
        }
        int size = compound1.getInteger("size");
        list.clear();
        for(int i=0;i<size;i++){
            int value = compound1.getInteger(""+i);
            list.add(value);
        }
    }


    public static void writeIntListToNBT(NBTTagCompound compound, String key, List<Integer> list) {
        NBTTagCompound compound1 = new NBTTagCompound();
        compound1.setInteger("size", list.size());
        for(int i=0;i<list.size();i++){
            int value = list.get(i);
            compound1.setInteger(""+i, value);
        }
        compound.setTag(key, compound1);
    }
}

