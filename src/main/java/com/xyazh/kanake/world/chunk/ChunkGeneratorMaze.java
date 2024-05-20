package com.xyazh.kanake.world.chunk;

import com.xyazh.kanake.util.PerlinNoise2d;
import com.xyazh.kanake.util.Vec3d;
import com.xyazh.kanake.world.tree.BigTreeGenerateManager;
import com.xyazh.kanake.world.tree.BigTreeGenerateTemplate;
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
import java.util.*;

public class ChunkGeneratorMaze implements IChunkGenerator {
    private final World world;
    private final Random posRand1 = new Random();
    private final Random posRand2 = new Random();
    private final Random posRand3 = new Random();
    private final Random posRand4 = new Random();
    private final Random posRand5 = new Random();
    private final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    private final IBlockState AIR = Blocks.AIR.getDefaultState();
    private final IBlockState STONE = Blocks.STONE.getDefaultState();
    private final IBlockState STONE_BRICK_1 = Blocks.STONEBRICK.getDefaultState();
    private final IBlockState STONE_BRICK_2 = Blocks.STONEBRICK.getStateFromMeta(1);
    private final IBlockState STONE_BRICK_3 = Blocks.STONEBRICK.getStateFromMeta(2);
    protected final Random rand;
    private final Long seed;
    protected BigTreeGenerateManager bigTreeGenerateManager;
    protected PerlinNoise2d perlinNoise2d1;
    protected PerlinNoise2d perlinNoise2d2;

    public ChunkGeneratorMaze(World worldIn, long seed) {
        this.world = worldIn;
        this.seed = seed;
        this.rand = new Random(seed);
        this.bigTreeGenerateManager = new BigTreeGenerateManager(seed);
        this.perlinNoise2d1 = new PerlinNoise2d(Objects.hash(seed, 11L), 0.25, 8);
        this.perlinNoise2d2 = new PerlinNoise2d(Objects.hash(seed, 13L), 0.125, 16);
    }

    public void updateRandom1(int x, int y, int z) {
        this.posRand1.setSeed(this.hashCombine(x, y, z));
    }

    public void updateRandom2(int x, int y, int z) {
        this.posRand2.setSeed(this.hashCombine(x, y, z, 111546435L));
    }

    public void updateRandom3(int x, int y, int z) {
        this.posRand3.setSeed(this.hashCombine(x, y, z, 3234846615L));
    }

    public void updateRandom4(int x, int y, int z) {
        this.posRand4.setSeed(this.hashCombine(x, y, z, 100280245065L));
    }

    public void updateRandom5(int x, int y, int z) {
        this.posRand5.setSeed(this.hashCombine(x, y, z, 4849845L));
    }

    protected boolean randFuc2IB(int x, int y, int z) {
        this.updateRandom1(x, y, z);
        int b = this.posRand1.nextInt(1000);
        return b % 2 == 0;
    }

    private int hashCombine(int x, int y, int z) {
        return Objects.hash(x, y, z, this.seed);
    }

    private int hashCombine(int x, int y, int z, long offset) {
        return Objects.hash(x, y, z, offset, this.seed);
    }

    protected boolean isKabe(int x, int y, int z) {
        boolean t = true;
        this.updateRandom2(x, y, z);
        if ((x % 2 == 0) && (z % 2 == 0)) {
            t = false;
        } else if (x % 2 == 0) {
            if (this.posRand2.nextDouble() < 0.1) {
                t = false;
            } else {
                int p1, p2;
                p1 = x;
                p2 = z - 1;
                t = this.randFuc2IB(p1, y, p2);
            }
        } else if (z % 2 == 0) {
            if (this.posRand2.nextDouble() < 0.1) {
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

    private void buildBase(int x, int z, ChunkPrimer primer) {
        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                primer.setBlockState(i, 0, k, BEDROCK);
            }
        }
    }

    private boolean buildLayerBase(int x, int y, int z, ChunkPrimer primer) {
        boolean result = false;
        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                IBlockState state = STONE_BRICK_1;
                this.updateRandom4(x, y, z);
                if (this.posRand4.nextDouble() >= 0.01) {
                    this.updateRandom3(x + i, y, z + k);
                    if (this.posRand3.nextDouble() < 0.05) {
                        state = STONE_BRICK_2;
                    } else if (this.posRand3.nextDouble() < 0.1) {
                        state = STONE_BRICK_3;
                    }
                    primer.setBlockState(i, y, k, state);
                }
                this.updateRandom4(x, y + 15, z);
                if (this.posRand4.nextDouble() >= 0.01) {
                    this.updateRandom3(x + i, y + 15, z + k);
                    if (this.posRand3.nextDouble() < 0.05) {
                        state = STONE_BRICK_2;
                    } else if (this.posRand3.nextDouble() < 0.1) {
                        state = STONE_BRICK_3;
                    }
                    primer.setBlockState(i, y + 15, k, state);
                } else {
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean buildLayer(int x, int l, int z, ChunkPrimer primer) {
        int y = (l << 4) + 1;
        int posX = x << 4;
        int posZ = z << 4;
        boolean result = false;
        if (this.isKabe(x, l, z)) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        IBlockState state = STONE_BRICK_1;
                        this.updateRandom3(posX + i, y + j, posZ + k);
                        if (this.posRand3.nextDouble() < 0.05) {
                            state = STONE_BRICK_2;
                        } else if (this.posRand3.nextDouble() < 0.05) {
                            state = STONE_BRICK_3;
                        }
                        primer.setBlockState(i, y + j, k, state);
                    }
                }
            }
        } else {
            result = this.buildLayerBase(posX, y, posZ, primer);
        }
        return result;
    }

    private boolean buildMaze(int x, int z, int layer, ChunkPrimer primer) {
        boolean result = false;
        for (int l = 0; l < layer; l++) {
            result = this.buildLayer(x, l, z, primer);
        }
        return result;
    }

    private void buildSurface(int x, int z, int layer, ChunkPrimer primer) {
        int posX = x << 4;
        int posZ = z << 4;
        int y = (layer << 4) + 1;
        for (int i = 0; i < 16; i++) {
            for (int k = 0; k < 16; k++) {
                double height = 24 + this.perlinNoise2d1.noise(posX + i, posZ + k) + this.perlinNoise2d2.noise(posX + i, posZ + k);
                if (height > 0) {
                    for (int j = y; j <= y + height; j++) {
                        primer.setBlockState(i, j, k, STONE);
                    }
                }
            }
        }

    }

    private void buildTreeCell(int x, int y, int z, ChunkPrimer primer, int m, int n, Collection<BigTreeGenerateTemplate> trees) {
        for (BigTreeGenerateTemplate tree : trees) {
            if (tree.contain(new Vec3d(x + 0.5, y + 0.5, z + 0.5))) {
                primer.setBlockState(m, y + 1, n, STONE);
            }
        }
    }

    private void buildTree(int x, int z, ChunkPrimer primer) {
        Collection<BigTreeGenerateTemplate> trees = this.bigTreeGenerateManager.getTrees(x, z, 0);
        if (trees.size() <= 0) {
            return;
        }
        int posX = x << 4;
        int posZ = z << 4;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 16; k++) {
                    this.buildTreeCell(posX + i, j, posZ + k, primer, i, k, trees);
                }
            }
        }
    }

    public void prepareHeights(int x, int z, ChunkPrimer primer) {
        int layer = 4;
        this.buildBase(x, z, primer);
        boolean hasNotTopBase = this.buildMaze(x, z, layer, primer);
        if (!hasNotTopBase) {
            this.buildSurface(x, z, layer, primer);
        }
        this.buildTree(x, z, primer);
    }

    public void buildSurfaces(int x, int z, ChunkPrimer primer) {

    }

    @Nonnull
    public Chunk generateChunk(int x, int z) {
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
