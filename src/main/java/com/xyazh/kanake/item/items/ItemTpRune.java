package com.xyazh.kanake.item.items;

import com.sun.javafx.geom.Vec3d;
import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.block.blocks.teleportation.TileTeleportation;
import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityMagic;
import com.xyazh.kanake.util.TpHelper;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ItemTpRune extends ItemBase {
    public ItemTpRune(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!world.isRemote) {
            NBTTagCompound compound = itemStack.getTagCompound();
            if (compound == null) {
                compound = new NBTTagCompound();
                itemStack.setTagCompound(compound);
            }
            if (compound.hasKey("tp_data")) {
                if (!this.tp(compound, player)) {
                    compound.removeTag("tp_data");
                    return new ActionResult<>(EnumActionResult.FAIL, itemStack);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        ItemStack itemStack = player.getHeldItem(hand);
        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            itemStack.setTagCompound(compound);
        }
        if (!compound.hasKey("tp_data")) {
            int fx=0,fy=0,fz=0,x,y,z;
            if(facing.equals(EnumFacing.UP)){
                fy += 1;
            }else if(facing.equals(EnumFacing.DOWN)){
                fy -= 1;
            }else if(facing.equals(EnumFacing.EAST)){
                fx += 1;
            }else if(facing.equals(EnumFacing.SOUTH)){
                fz += 1;
            }else if(facing.equals(EnumFacing.WEST)){
                fx -= 1;
            }else if(facing.equals(EnumFacing.NORTH)){
                fz -= 1;
            }
            x = pos.getX() + fx;
            y = pos.getY() + fy;
            z = pos.getZ() + fz;
            if (!this.setTPBlock(compound, player, new BlockPos(x,y,z))) {
                compound.removeTag("tp_data");
                return EnumActionResult.FAIL;
            }
        }else{
            if (!this.tp(compound, player)) {
                compound.removeTag("tp_data");
                return EnumActionResult.FAIL;
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public boolean setTPBlock(NBTTagCompound compound, Entity entity, BlockPos pos) {
        Map<DimensionType, IntSortedSet> dimensionTypes = DimensionManager.getRegisteredDimensions();
        int dim = entity.world.provider.getDimension();
        String dimName = null;
        for (DimensionType dimensionType : dimensionTypes.keySet()) {
            if (dimensionType.getId() == dim) {
                dimName = dimensionType.getName();
                break;
            }
        }
        if (dimName == null) {
            return false;
        }
        NBTTagCompound tpData = new NBTTagCompound();
        tpData.setString("dim_name", dimName);
        tpData.setInteger("pos.x", pos.getX());
        tpData.setInteger("pos.args", pos.getY());
        tpData.setInteger("pos.z", pos.getZ());
        compound.setTag("tp_data", tpData);
        if(entity.world.isAirBlock(pos)||entity.world.getBlockState(pos).getMaterial().isReplaceable()){
            entity.world.setBlockState(pos, ModBlocks.TP.getDefaultState());
            return true;
        }else{
            return false;
        }
    }

    public boolean tp(NBTTagCompound compound, Entity entity) {
        NBTTagCompound tpData = compound.getCompoundTag("tp_data");
        Map<DimensionType, IntSortedSet> dimensionTypes = DimensionManager.getRegisteredDimensions();
        int dim = 0;
        String dimName = tpData.getString("dim_name");
        boolean flag = true;
        for (DimensionType dimensionType : dimensionTypes.keySet()) {
            if (dimensionType.getName().equals(dimName)) {
                dim = dimensionType.getId();
                flag = false;
                break;
            }
        }
        if (flag) {
            return false;
        }
        MinecraftServer server = entity.getServer();
        if (server == null) {
            return false;
        }
        int selfDim = entity.world.provider.getDimension();
        World world = server.getWorld(dim);
        BlockPos pos = new BlockPos(
                tpData.getInteger("pos.x"),
                tpData.getInteger("pos.args"),
                tpData.getInteger("pos.z"));
        if (!world.getBlockState(pos).getBlock().equals(ModBlocks.TP)) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileTeleportation)) {
            return false;
        }
        TileTeleportation tileTeleportation = (TileTeleportation) te;
        if (dim == selfDim) {
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                boolean flag1 = false;
                double mana = ManaData.get(player);
                if(player.isCreative()){
                    flag1 = true;
                }else if(mana>=50){
                    flag1 = true;
                    ManaData.subSync(player,50);
                }
                if(flag1){
                    entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity.rotationYaw, 0.0F);
                    player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity.rotationYaw, 0.0F);
                    entity.motionX = 0.0D;
                    entity.motionY = 0.0D;
                    entity.motionZ = 0.0D;
                }
            }else{
                entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity.rotationYaw, 0.0F);
                entity.motionX = 0.0D;
                entity.motionY = 0.0D;
                entity.motionZ = 0.0D;
            }
        } else {
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                boolean flag1 = false;
                double mana = ManaData.get(player);
                if(player.isCreative()){
                    flag1 = true;
                }else if(mana>=100){
                    flag1 = true;
                    ManaData.subSync(player,100);
                }
                if(flag1){
                TpHelper.changeDimension(entity, dim);
                entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity.rotationYaw, 0.0F);
                player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity.rotationYaw, 0.0F);
                entity.motionX = 0.0D;
                entity.motionY = 0.0D;
                entity.motionZ = 0.0D;
                }
            }else {
                TpHelper.changeDimension(entity, dim);
                entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity.rotationYaw, 0.0F);
                entity.motionX = 0.0D;
                entity.motionY = 0.0D;
                entity.motionZ = 0.0D;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound == null || !compound.hasKey("tp_data")) {
            tooltip.add(I18n.format("item.rune_tp.desc.0"));
        } else {
            tooltip.add(I18n.format("item.rune_tp.desc.1"));
            NBTTagCompound tpData = compound.getCompoundTag("tp_data");
            tooltip.add(tpData.getString("dim_name"));
            tooltip.add(String.format("%d,%d,%d",
                    tpData.getInteger("pos.x"),
                    tpData.getInteger("pos.args"),
                    tpData.getInteger("pos.z")));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        return !(compound == null || !compound.hasKey("tp_data"));
    }
}
