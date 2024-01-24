package com.xyazh.kanake.block.blocks.manastove;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.BlockBase;
import com.xyazh.kanake.block.blocks.crystaler.TileCrystaler;
import com.xyazh.kanake.block.blocks.manastorage.TileManaStorage;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.PlayerUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockManaStove extends BlockBase implements ITileEntityProvider {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockManaStove(String name, Material material) {
        super(name, material);
        this.setLightLevel(1F);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        GameRegistry.registerTileEntity(TileManaStove.class, new ResourceLocation(Kanake.MODID, name));
        if (!Kanake.proxy.isServer()) {
            this.bindTileRender();
        }
    }

    public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    @Nonnull
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public void onBlockPlacedBy(World worldIn, @Nonnull BlockPos pos, IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Nonnull
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    @Nonnull
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    @Nonnull
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @SideOnly(Side.CLIENT)
    protected void bindTileRender() {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                TileManaStove.class,
                new com.xyazh.kanake.block.blocks.manastove.render.RenderTileManaStove());
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
        return 30;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof TileManaStove){
            TileManaStove manaStove = (TileManaStove) te;
            if(manaStove.workingTime > 0){
                for(int i=0;i<10;i++){
                    worldIn.spawnParticle(ModParticles.WANA_PARTICLES,
                            pos.getX()+0.25+rand.nextFloat()/2,pos.getY()+0.1875,pos.getZ()+0.25+rand.nextFloat()/2,
                            0,0,0);
                }
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileManaStove();
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity1 = worldIn.getTileEntity(pos);
        if(!(tileEntity1 instanceof TileManaStove)){
            return false;
        }
        TileManaStove tileEntity = (TileManaStove) tileEntity1;
        ItemStack playerHeldItem;
        playerHeldItem =  playerIn.getHeldItem(EnumHand.MAIN_HAND);
        if(playerHeldItem.isEmpty()){
            playerIn.setHeldItem(EnumHand.MAIN_HAND,tileEntity.removeStackFromSlot(0));
            return true;
        }
        ItemStack itemStack = playerIn.getHeldItem(EnumHand.MAIN_HAND);
        if(!tileEntity.canInsertItem(0,itemStack,facing)){
            return false;
        }
        itemStack = tileEntity.addStackFromSlot(0,itemStack);
        playerIn.setHeldItem(EnumHand.MAIN_HAND,itemStack);
        return true;
    }
}
