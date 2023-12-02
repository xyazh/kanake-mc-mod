package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.BlockBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockManaTableBase extends BlockBase implements ITileEntityProvider {
    Class<? extends TileManaTableBase> tileEntityClass;

    public BlockManaTableBase(String name, Material material, Class<? extends TileManaTableBase> tileEntityClass) {
        super(name, material);
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(Kanake.MODID, name));
        this.tileEntityClass = tileEntityClass;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return null;
    }

    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return true;
    }

    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity == null){
            super.breakBlock(worldIn,pos,state);
            return;
        }
        if(tileEntity.getClass().equals(this.tileEntityClass)){
            TileManaTableBase tileManaTableBase = (TileManaTableBase) tileEntity;
            tileManaTableBase.drop();
        }
        super.breakBlock(worldIn,pos,state);
    }
}
