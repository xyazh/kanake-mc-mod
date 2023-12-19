package com.xyazh.kanake.world.provider;

import com.xyazh.kanake.world.chunk.ChunkGeneratorArea;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ProviderArea extends WorldProviderSurface{
    public static final String providerName = "kanake_area";
    @Nonnull
    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkGeneratorArea(this.world, this.world.getWorldInfo().isMapFeaturesEnabled(), this.world.getSeed(), this.getSpawnCoordinate());
    }

    @Override
    public boolean isSurfaceWorld()
    {
        return true;
    }

    @Override
    public boolean canRespawnHere()
    {
        return true;
    }

    @Override
    public float getCloudHeight() {
        return -16f;
    }

    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int x, int z)
    {
        return false;
    }
}
