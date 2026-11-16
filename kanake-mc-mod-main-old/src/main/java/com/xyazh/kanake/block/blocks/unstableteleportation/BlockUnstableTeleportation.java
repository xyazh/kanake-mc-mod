package com.xyazh.kanake.block.blocks.unstableteleportation;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.BlockBase;
import com.xyazh.kanake.block.blocks.unstableteleportation.render.RenderTileUnstableTeleportation;
import com.xyazh.kanake.util.TpHelper;
import com.xyazh.kanake.world.ModWorlds;
import com.xyazh.kanake.world.provider.ProviderMaze;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class BlockUnstableTeleportation extends BlockBase implements ITileEntityProvider {
    HashMap<Entity, Integer> teleportingEntities = new HashMap<>();

    public BlockUnstableTeleportation(String name) {
        super(name, Material.FIRE);
        setLightLevel(0.4F);
        setHardness(3.0F);
        setResistance(3.0F);
        setLightOpacity(0);
        GameRegistry.registerTileEntity(TileUnstableTeleportation.class, new ResourceLocation(Kanake.MODID, name));
        this.setTickRandomly(true);
        if (!Kanake.proxy.isServer()) {
            this.bindTileRender();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void bindTileRender() {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                TileUnstableTeleportation.class,
                new RenderTileUnstableTeleportation());
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileUnstableTeleportation();
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileUnstableTeleportation) {
            worldIn.removeTileEntity(pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    public void onEntityWalk(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn) {
        if(worldIn.isRemote){
            return;
        }
        if(this.teleportingEntities.containsKey(entityIn)){
            int i = this.teleportingEntities.get(entityIn);
            if(i < 100){
                this.teleportingEntities.put(entityIn, i + 2);
            }else {
                int dim = ModWorlds.getDimIdByName(ProviderMaze.providerName);
                TpHelper.changeDimension(entityIn,dim);
                if (entityIn instanceof EntityPlayerMP){
                    EntityPlayerMP player = (EntityPlayerMP) entityIn;
                    player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.rotationYaw, 0.0F);
                }
            }
        }else {
            this.teleportingEntities.put(entityIn, 2);
        }
    }
}
