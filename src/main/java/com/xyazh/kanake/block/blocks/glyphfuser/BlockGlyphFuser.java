package com.xyazh.kanake.block.blocks.glyphfuser;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.BlockBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockGlyphFuser extends BlockBase implements ITileEntityProvider {
    public BlockGlyphFuser(String name, Material material) {
        super(name, material);
        this.setLightLevel(0.4f);
        GameRegistry.registerTileEntity(TileGlyphFuser.class, new ResourceLocation(Kanake.MODID, name));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileGlyphFuser();
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state,
                                    @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand,
                                    @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileGlyphFuser) {
            return ((TileGlyphFuser) tile).onActivate(playerIn, hand);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag advanced) {
        tooltip.add(I18n.format("tile.glyph_fuser.desc"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileGlyphFuser) {
                ItemStack glyph = ((TileGlyphFuser) tile).getGlyphStack();
                if (!glyph.isEmpty()) {
                    EntityItem entityItem = new EntityItem(worldIn,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, glyph.copy());
                    worldIn.spawnEntity(entityItem);
                }
                ((TileGlyphFuser) tile).setGlyphStack(ItemStack.EMPTY);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
