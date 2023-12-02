package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.util.PlayerUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockTableMono extends BlockManaTableBase{
    public BlockTableMono(String name, Material material, Class<? extends TileManaWithForeverEntity> tileEntityClass) {
        super(name, material, tileEntityClass);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileTableMono();
    }

    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity1 = worldIn.getTileEntity(pos);
        if(!(tileEntity1 instanceof TileManaWithForeverEntity)){
            return true;
        }
        TileManaWithForeverEntity tileEntity = (TileManaWithForeverEntity) tileEntity1;
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

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.QUARTZ_BLOCK);
    }

}
