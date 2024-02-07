package com.xyazh.kanake.block.blocks.crystaler;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.block.blocks.manatable.BlockShowcase;
import com.xyazh.kanake.block.blocks.manatable.TileManaWithForeverEntity;
import com.xyazh.kanake.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockCrystaler extends BlockShowcase {
    public BlockCrystaler(String name, Material material, Class<? extends TileCrystaler> tileEntityClass) {
        super(name, material, tileEntityClass);
    }

    public int quantityDropped(@Nonnull Random random)
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World player, List<String> tooltip, @Nonnull ITooltipFlag advanced){
        tooltip.add(I18n.format("tile.crystaler.desc"));
        tooltip.add(I18n.format("tile.crystaler.desc.1"));
        tooltip.add(I18n.format("tile.crystaler.desc.2"));
        tooltip.add(I18n.format("tile.crystaler.desc.3"));
        tooltip.add(I18n.format("tile.crystaler.desc.4"));
        tooltip.add(I18n.format("tile.crystaler.desc.5"));
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

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    protected boolean canSilkHarvest()
    {
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(@Nonnull IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, @Nonnull EnumFacing side) {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        return !iblockstate.isFullCube() && iblockstate.getBlock() != this;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileCrystaler();
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity1 = worldIn.getTileEntity(pos);
        if(!(tileEntity1 instanceof TileCrystaler)){
            return false;
        }
        TileCrystaler tileEntity = (TileCrystaler) tileEntity1;
        boolean isTileEmpty,isPlayerEmpty;
        isTileEmpty = tileEntity.isEmpty();
        isPlayerEmpty = PlayerUtil.isMainHandEmpty(playerIn);
        if(!isTileEmpty){
            ItemStack toPlayerStack = tileEntity.removeStackFromSlot(0);
            if(!worldIn.isRemote){
                EntityItem entityItem = new EntityItem(worldIn);
                entityItem.setItem(toPlayerStack);
                entityItem.setPosition(playerIn.posX,playerIn.posY,playerIn.posZ);
                worldIn.spawnEntity(entityItem);
            }
        }else if(!isPlayerEmpty){
            tileEntity.addStackFromSlot(0,PlayerUtil.countMainHandItemStack(playerIn,1));
        }
        return true;
    }
}
