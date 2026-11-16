package com.xyazh.kanake.block.blocks;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.item.ItemBlockBase;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.item.itemblock.ItemPureHanaSeed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockPureHana extends BlockCropsBase {
    public static final EnumPlantType ORE_CROP = EnumPlantType.getPlantType("ore_crop");
    public BlockPureHana(String name) {
        super(name);
    }
    @Override
    public ItemBlockBase getItemBlockBase() {
        return new ItemPureHanaSeed(this);
    }

    @Nonnull
    @Override
    protected Item getSeed() {
        return Items.AIR;
    }

    @Nonnull
    @Override
    protected Item getCrop() {
        return ModItems.PURE_HARMONIUM_CRYSTAL;
    }

    @Override
    public boolean canBlockStay(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        return this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == ModBlocks.HARMONIUM_CRYSTAL_ORE;
    }


    protected int getBonemealAgeIncrease(World worldIn) {
        return 1;
    }

    @Override
    public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return true;
    }

    @Override
    public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return (int) ((float) this.getAge(state) / (float) this.getMaxAge() * 8);
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!this.isMaxAge(state)){
            return false;
        }
        this.dropBlockAsItem(worldIn, pos, state, 0);
        this.reGrow(worldIn,pos,state);
        return true;
    }

    @Nonnull
    @Override
    public EnumPlantType getPlantType(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return ORE_CROP;
    }
}
