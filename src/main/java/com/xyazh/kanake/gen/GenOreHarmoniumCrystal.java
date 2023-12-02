package com.xyazh.kanake.gen;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.util.PublicBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class GenOreHarmoniumCrystal implements IWorldGenerator {
    private final PublicBlockPos blockPos = new PublicBlockPos(0,0,0);
    private final IBlockState blockState = ModBlocks.HARMONIUM_CRYSTAL_ORE.getDefaultState();
    private final WorldGenMinable worldGenMinable = new WorldGenMinable(blockState,7);
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        for(int n=0;n<30;n++){
            blockPos.x = (chunkX << 4) + random.nextInt(16);
            blockPos.z = (chunkZ << 4) + random.nextInt(16);
            blockPos.y = random.nextInt(118)+2;
            worldGenMinable.generate(world,random,blockPos);
        }
    }
}
