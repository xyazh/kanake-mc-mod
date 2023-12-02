package com.xyazh.kanake.util;

import com.xyazh.kanake.world.ModWorlds;
import com.xyazh.kanake.world.provider.ProviderMaze;
import com.xyazh.kanake.world.teleporter.MyTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

public class TpHelper {
    public static void changeDimension(Entity entity,int dim){
        if(!entity.world.isRemote){
            MinecraftServer server = entity.getServer();
            if(server!=null){
                if(entity.dimension == dim){
                    entity.changeDimension(0, MyTeleporter.getTeleporterForDim(server,0));
                }else{
                    entity.changeDimension(dim,MyTeleporter.getTeleporterForDim(server,dim));
                }
            }
        }
    }
}
