package com.xyazh.kanake.util;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ClearUtil {
    private static final PublicBlockPos pos = new PublicBlockPos(0,0,0);
    public static void clearPillar(World world, int x, int z){
        for(int j=0;j<256;j++){
            pos.x = x;
            pos.y = j;
            pos.z = z;
            world.setBlockToAir(pos);
        }
    }

    public static void clearPillarExceptBedrock(World world, int x, int z){
        for(int j=0;j<256;j++){
            pos.x = x;
            pos.y = j;
            pos.z = z;
            if(!Blocks.BEDROCK.equals(world.getBlockState(pos).getBlock())){
                world.setBlockToAir(pos);
            }
        }
    }


    public static void clearChunk(World world,ChunkPos chunkPos){
        int x = chunkPos.x << 4;
        int z = chunkPos.z << 4;
        for(int i = 0;i<16;i++){
            for(int k = 0;k<16;k++){
                clearPillar(world,x+i,z+k);
            }
        }
    }

    public static void clearChunkExceptBedrock(World world,ChunkPos chunkPos){
        int x = chunkPos.x << 4;
        int z = chunkPos.z << 4;
        for(int i = 0;i<16;i++){
            for(int k = 0;k<16;k++){
                clearPillarExceptBedrock(world,x+i,z+k);
            }
        }
    }

    public static void clearChunk(World world,int chunkX,int chunkZ){
        int x = chunkX << 4;
        int z = chunkZ << 4;
        for(int i = 0;i<16;i++){
            for(int k = 0;k<16;k++){
                clearPillar(world,x+i,z+k);
            }
        }
    }

    public static void clearChunkExceptBedrock(World world,int chunkX,int chunkZ){
        int x = chunkX << 4;
        int z = chunkZ << 4;
        for(int i = 0;i<16;i++){
            for(int k = 0;k<16;k++){
                clearPillarExceptBedrock(world,x+i,z+k);
            }
        }
    }
}
