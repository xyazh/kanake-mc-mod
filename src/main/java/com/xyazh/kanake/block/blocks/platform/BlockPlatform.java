package com.xyazh.kanake.block.blocks.platform;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.BlockBase;
import com.xyazh.kanake.block.blocks.platform.render.RenderPlatform;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockPlatform extends BlockBase implements ITileEntityProvider {
    public BlockPlatform(String name) {
        super(name, Material.FIRE);
        setLightLevel(0.4F);
        setHardness(3.0F);
        setResistance(3.0F);
        setLightOpacity(0);
        GameRegistry.registerTileEntity(TilePlatform.class, new ResourceLocation(Kanake.MODID, name));
        if (!Kanake.proxy.isServer()) {
            this.bindTileRender();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void bindTileRender() {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                TilePlatform.class,
                new RenderPlatform());
    }

    @Override
    public void onEntityWalk(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn)
    {
        if(entityIn instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entityIn;
            player.setJumping(false);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn)
    {
        if(entityIn instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entityIn;
            player.setJumping(false);
        }
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TilePlatform();
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TilePlatform) {
            worldIn.removeTileEntity(pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return super.getCollisionBoundingBox(blockState,worldIn,pos);
    }

    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    public boolean isFullCube(@Nonnull IBlockState state) {
        return false;
    }

    public int quantityDropped(@Nonnull Random random) {
        return 0;
    }

    public int tickRate(@Nonnull World worldIn) {
        return 30;
    }

    public boolean requiresUpdates() {
        return false;
    }

    public boolean isCollidable() {
        return true;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
}
