package com.xyazh.kanake.world.gen;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import javax.annotation.Nonnull;
import java.util.Random;

public class WorldGenAreaIsland extends WorldGenerator {
    public boolean generate(@Nonnull World worldIn, Random rand, @Nonnull BlockPos position) {
        float f = (float) (rand.nextInt(3) + 4);

        for (int i = 0; f > 0.5F; --i) {
            for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); ++j) {
                for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); ++k) {
                    if ((float) (j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(j, i, k), Blocks.STONE.getDefaultState());
                    }
                }
            }

            f = (float) ((double) f - ((double) rand.nextInt(2) + 0.5D));
        }

        return true;
    }
}
