package com.xyazh.kanake.world.provider;

import com.xyazh.kanake.world.ModWorlds;
import com.xyazh.kanake.world.chunk.ChunkGeneratorMaze;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ProviderMaze extends WorldProvider {
    public static final String providerName = "kanake_test";

    public void init()
    {
        this.biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);
        this.doesWaterVaporize = false;
        this.nether = false;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
    {
        return new Vec3d(1, 1, 1);
    }

    protected void generateLightBrightnessTable()
    {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i)
        {
            float f1 = 1.0F - (float)i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 0.9F + 0.1F;
        }
    }

    @Nonnull
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkGeneratorMaze(this.world, this.world.getSeed());
    }

    public boolean isSurfaceWorld()
    {
        return true;
    }

    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return true;
    }

    public float calculateCelestialAngle(long worldTime, float partialTicks)
    {
        return 0.5F;
    }

    public boolean canRespawnHere()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }

    @Nonnull
    public WorldBorder createWorldBorder()
    {
        return new WorldBorder()
        {
            public double getCenterX()
            {
                return super.getCenterX() / 8.0D;
            }
            public double getCenterZ()
            {
                return super.getCenterZ() / 8.0D;
            }
        };
    }

    @Nonnull
    public DimensionType getDimensionType()
    {
        DimensionType type = ModWorlds.getDimTypeByName(providerName);
        if(type == null){
            type = DimensionType.OVERWORLD;
        }
        return type;
    }
}
