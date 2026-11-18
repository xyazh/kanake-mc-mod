package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.util.PlayerUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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
            return false;
        }
        TileManaWithForeverEntity tileEntity = (TileManaWithForeverEntity) tileEntity1;
        if(tileEntity instanceof TileTableMono && ((TileTableMono) tileEntity).tryFuse(playerIn, hand)){
            return true;
        }
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

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileTableMono) {
                TileTableCoreMono core = ((TileTableMono) tileEntity).getCoreMono();
                if (core != null && core.monoWorkingRecipe.isWorking()) {
                    // 运行中的结构被破坏：小爆炸、清除核心、终止配方，不返还已抽取输入。
                    core.monoWorkingRecipe.interrupt();
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag advanced){
        tooltip.add(I18n.format("tile.t_mono.desc"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Nonnull
    @Override
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.QUARTZ_BLOCK);
    }

}
