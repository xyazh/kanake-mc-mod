package com.xyazh.kanake.particle;

import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;


public class EnumParticleTypesHelper{
    public static EnumParticleTypes addParticleType(String particleNameIn, int particleIDIn, boolean shouldIgnoreRangeIn,int argumentCountIn){
        return EnumHelper.addEnum(EnumParticleTypes.class,"EnumParticleTypes", new Class[]{String.class, int.class, boolean.class,int.class},particleNameIn,particleIDIn,shouldIgnoreRangeIn,argumentCountIn);
    }

    public static EnumParticleTypes addParticleType(String particleNameIn, int particleIDIn, boolean shouldIgnoreRangeIn){
        return EnumHelper.addEnum(EnumParticleTypes.class,"EnumParticleTypes", new Class[]{String.class, int.class, boolean.class},particleNameIn,particleIDIn,shouldIgnoreRangeIn);
    }
}


