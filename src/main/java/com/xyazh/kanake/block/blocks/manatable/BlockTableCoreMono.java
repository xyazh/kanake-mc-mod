package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.block.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockTableCoreMono extends BlockTableMono{
    public BlockTableCoreMono(String name, Material material, Class<? extends TileManaWithForeverEntity> tileEntityClass) {
        super(name, material, tileEntityClass);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileTableCoreMono();
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(this);
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity1 = worldIn.getTileEntity(pos);
        if(tileEntity1 instanceof TileTableCoreMono){
            TileTableCoreMono tileEntity = (TileTableCoreMono) tileEntity1;
            if(tileEntity.isOutTheChunk&&worldIn.isRemote){
                playerIn.sendMessage(new TextComponentString(I18n.format("message.mono.out_chunk")));
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World player, List<String> tooltip, @Nonnull ITooltipFlag advanced){
        tooltip.add(I18n.format("tile.mono.desc"));
    }
}
