package com.xyazh.kanake.block.blocks.manastorage;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.BlockBase;
import com.xyazh.kanake.block.blocks.teleportation.TileTeleportation;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockManaStorage extends BlockBase implements ITileEntityProvider {
    public BlockManaStorage(String name, Material material) {
        super(name, material);
        this.setLightLevel(1F);
        this.setTickRandomly(true);
        GameRegistry.registerTileEntity(TileManaStorage.class, new ResourceLocation(Kanake.MODID, name));
        if (!Kanake.proxy.isServer()) {
            this.bindTileRender();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void bindTileRender() {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                TileManaStorage.class,
                new com.xyazh.kanake.block.blocks.manastorage.render.RenderTileManaStorage());
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean isFullCube(@Nonnull IBlockState state)
    {
        return false;
    }

    public boolean isOpaqueCube(@Nonnull IBlockState state)
    {
        return false;
    }

    public int tickRate(@Nonnull World worldIn)
    {
        return 2;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@Nonnull IBlockState stateIn, World worldIn, BlockPos pos, @Nonnull Random rand)
    {
        worldIn.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 0.0D, 0.0D, 1.0D);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileManaStorage();
    }
}
