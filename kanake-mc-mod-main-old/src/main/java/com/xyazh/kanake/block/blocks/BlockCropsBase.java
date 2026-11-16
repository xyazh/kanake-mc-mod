package com.xyazh.kanake.block.blocks;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.item.ItemBlockBase;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockCropsBase extends BlockCrops  implements IHasModel
{
    public ItemBlockBase itemBlockBase;
    protected static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    public BlockCropsBase(String name)
    {
        super();
        setUnlocalizedName(name);
        setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
        this.itemBlockBase = this.getItemBlockBase();
        this.itemBlockBase.setRegistryName(name);
        ModItems.ITEMS.add(this.itemBlockBase);
        setHardness(0.0F);
        setResistance(0.0F);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos)
    {
        return CROPS_AABB[state.getValue(this.getAgeProperty())];
    }

    public ItemBlockBase getItemBlockBase(){
        return new ItemBlockBase(this);
    }

    public BlockCropsBase setOreDict(String[] oreDictName){
        this.itemBlockBase.setOreDict(oreDictName);
        return this;
    }

    @Nonnull
    @Override
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune)
    {
        return super.getItemDropped(state, rand, fortune);
    }

    public void reGrow(World worldIn, BlockPos pos, IBlockState state){
        worldIn.setBlockState(pos,state.getBlock().getDefaultState());
    }

    @Nonnull
    protected Item getSeed(){
        return Items.AIR;
    }

    @Nonnull
    protected Item getCrop(){
        return Items.AIR;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return state.getBlock() == Blocks.FARMLAND;
    }

    @Override
    public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        return true;
    }

    @Nonnull
    @Override
    public EnumPlantType getPlantType(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return super.getPlantType(world, pos);
    }

    @Override
    protected int getBonemealAgeIncrease(World worldIn)
    {
        return MathHelper.getInt(worldIn.rand, 2, 5);
    }

    @Override
    public void registerModels() {
        Kanake.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
