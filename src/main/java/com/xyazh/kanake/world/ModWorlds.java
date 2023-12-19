package com.xyazh.kanake.world;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.world.provider.ProviderArea;
import com.xyazh.kanake.world.provider.ProviderMaze;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;
import java.util.HashSet;

public class ModWorlds {
    public static final HashSet<Dim> dims = new HashSet<>();
    public static final HashMap<String,Integer> dimIdMap = new HashMap<>();
    public static final HashMap<String,DimensionType> dimMap = new HashMap<>();

    public static void worlds(){
        register(ProviderMaze.providerName, ProviderMaze.class,true);
        register(ProviderArea.providerName, ProviderArea.class,true);
    }

    public static int getDimIdByName(String name){
        if(dimIdMap.containsKey(name)){
            return dimIdMap.get(name);
        }
        return 0;
    }

    public static DimensionType getDimTypeByName(String name){
        if(dimMap.containsKey(name)){
            return dimMap.get(name);
        }
        return null;
    }

    public static void register(String name, Class<? extends WorldProvider> provider, boolean keepLoaded){
        register(name, Kanake.MODID,provider,keepLoaded);
    }

    public static void register(String name, String suffix, Class<? extends WorldProvider> provider, boolean keepLoaded){
        Dim dim = new Dim();
        dim.name = name;
        dim.suffix = suffix;
        dim.provider = provider;
        dim.keepLoaded = keepLoaded;
        dims.add(dim);
    }

    public static void registerAllDim(){
        worlds();
        int dimID = 119;
        for(Dim dim:dims){
            while (DimensionManager.isDimensionRegistered(dimID)){
                dimID+=1;
            }
            DimensionType dimensionType = DimensionType.register(dim.name,dim.suffix,dimID,dim.provider, dim.keepLoaded);
            DimensionManager.registerDimension(dimID, dimensionType);
            dimIdMap.put(dim.name,dimID);
            dimMap.put(dim.name,dimensionType);
        }
    }

    static class Dim{
        public String name;
        public String suffix;
        public Class<? extends WorldProvider> provider;
        public boolean keepLoaded;
    }
}
