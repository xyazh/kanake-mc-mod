package com.xyazh.kanake.world.chunk;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ChunkGeneratorMaze implements IChunkGenerator {
    private final World world;
    private final Random rand;
    private final Random posRand = new Random();
    private final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

    public ChunkGeneratorMaze(World worldIn, long seed) {
        this.world = worldIn;
        this.rand = new Random(seed);
    }

    protected boolean randFuc2IB(int x, int y, int z) {
        int seed = this.hashCombine(x, y, z);
        this.posRand.setSeed(seed);
        int b = this.posRand.nextInt(1000);
        return b % 2 == 0;
    }

    private int hashCombine(int x, int y, int z) {
        return Objects.hash(x, y, z);
    }

    protected boolean isKabe(int x, int y, int z) {
        boolean t = true;
        if ((x % 2 == 0) && (z % 2 == 0)) {
            t = false;
        } else if (x % 2 == 0) {
            if (this.rand.nextDouble() < 0.1) {
                t = false;
            } else {
                int p1, p2;
                p1 = x;
                p2 = z - 1;
                t = this.randFuc2IB(p1, y, p2);
            }
        } else if (z % 2 == 0) {
            if (this.rand.nextDouble() < 0.1) {
                t = false;
            } else {
                int p1, p2;
                p1 = x - 1;
                p2 = z;
                t = !this.randFuc2IB(p1, y, p2);
            }
        }
        return t;
    }

    public void prepareHeights(int x, int z, ChunkPrimer primer) {
        int layer = 8;
        for (int y = 0; y < layer; y++) {
            if (this.isKabe(x, y, z)) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 16; k++) {
                            primer.setBlockState(i, j + (y<<4), k, BEDROCK);
                        }
                    }
                }
            } else {
                if((y!=0)&&(this.rand.nextDouble()<0.02)){
                    continue;
                }
                for (int i = 0; i < 16; i++) {
                    for (int k = 0; k < 16; k++) {
                        primer.setBlockState(i, y<<4, k, BEDROCK);
                    }
                }
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                primer.setBlockState(i, layer<<4, k, BEDROCK);
            }
        }
    }

    public void buildSurfaces(int x, int z, ChunkPrimer primer) {

    }

    @Nonnull
    public Chunk generateChunk(int x, int z) {
        this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        this.prepareHeights(x, z, chunkprimer);
        this.buildSurfaces(x, z, chunkprimer);

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
        Biome[] abiome = this.world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i) {
            abyte[i] = (byte) Biome.getIdForBiome(abiome[i]);
        }
        chunk.resetRelightChecks();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {

    }

    @Override
    public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {
        return false;
    }

    @Nonnull
    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(@Nonnull EnumCreatureType creatureType, @Nonnull BlockPos pos) {
        Biome biome = this.world.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }


    @Nullable
    @Override
    public BlockPos getNearestStructurePos(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public void recreateStructures(@Nonnull Chunk chunkIn, int x, int z) {

    }

    @Override
    public boolean isInsideStructure(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos pos) {
        return false;
    }
}
