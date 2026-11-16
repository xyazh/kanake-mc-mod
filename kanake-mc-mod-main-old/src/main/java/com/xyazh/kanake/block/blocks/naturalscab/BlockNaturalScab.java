package com.xyazh.kanake.block.blocks.naturalscab;

import com.xyazh.kanake.block.blocks.manatable.BlockTableMono;
import com.xyazh.kanake.block.blocks.manatable.TileManaWithForeverEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockNaturalScab extends BlockTableMono {
    public BlockNaturalScab(String name, Material material, Class<? extends TileManaWithForeverEntity> tileEntityClass) {
        super(name, material, tileEntityClass);
        this.setTickRandomly(true);
    }

    @Override
    public int tickRate(@Nonnull World worldIn)
    {
        return 30;
    }

    public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand)
    {

    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileNaturalScab();
    }

    @Nonnull
    @Override
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune)
    {
        return Items.AIR;
    }
}
