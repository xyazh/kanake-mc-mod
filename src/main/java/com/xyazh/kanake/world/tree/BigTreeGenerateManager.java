package com.xyazh.kanake.world.tree;

import com.xyazh.kanake.util.Vec3d;

import java.util.*;

public class BigTreeGenerateManager {
    public final long seed;
    public final Random rand = new Random();
    public final int maxRange;
    public final int weight;
    public final int maxHeight;
    public final int minHeight;
    public BigTreeGenerateManager(long seed,int maxRange,int weight,int maxHeight,int minHeight){
        this.seed = seed;
        this.maxRange = maxRange;
        this.weight = weight;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }

    public BigTreeGenerateManager(long seed){
        this(seed, 4, 5000, 255, 0);
    }

    public void updateRandom(int x, int z) {
        this.rand.setSeed(Objects.hash(x, z, 13871L, this.seed));
    }

    public Collection<BigTreeGenerateTemplate> getTrees(int chunkX, int chunkZ ,int y){
        HashSet<BigTreeGenerateTemplate> trees = new HashSet<>();
        for(int i=-this.maxRange;i<=this.maxRange;i++){
            for(int j=-this.maxRange;j<=this.maxRange;j++){
                int inChunkX = chunkX + i;
                int inChunkZ = chunkZ + j;
                this.updateRandom(inChunkX, inChunkZ);
                if(this.rand.nextInt(100000) > this.weight){
                    continue;
                }
                int x = (inChunkX << 4) + 7;
                int z = (inChunkZ << 4) + 7;
                Vec3d rootPos = new Vec3d(x, y, z);
                trees.add(BigTreeGenerateTemplate.getTemplate(Objects.hash(rootPos, this.seed), rootPos));
            }
        }
        return trees;
    }
}
