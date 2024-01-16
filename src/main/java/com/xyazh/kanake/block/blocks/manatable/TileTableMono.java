package com.xyazh.kanake.block.blocks.manatable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileTableMono extends TileManaWithForeverEntity{
    public BlockPos coreBlockPos = new BlockPos(0,0,0);

    @Nullable
    public TileTableCoreMono getCoreMono(){
        if(this instanceof TileTableCoreMono){
            return (TileTableCoreMono) this;
        }
        TileEntity te = this.world.getTileEntity(this.coreBlockPos);
        if(te instanceof TileTableCoreMono){
            return (TileTableCoreMono) te;
        }
        return null;
    }

    @Override
    public void onInvChanges() {
        super.onInvChanges();
        TileTableCoreMono coreMono = this.getCoreMono();
        if(coreMono == null){
            return;
        }
        coreMono.shouldFindRecipe = true;
    }

    @Override
    public void update() {
        super.update();
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        int x,y,z;
        x = compound.getInteger("coreBlockPos.x");
        y = compound.getInteger("coreBlockPos.args");
        z = compound.getInteger("coreBlockPos.z");
        this.coreBlockPos = new BlockPos(x,y,z);
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("coreBlockPos.x",this.coreBlockPos.getX());
        compound.setInteger("coreBlockPos.args",this.coreBlockPos.getX());
        compound.setInteger("coreBlockPos.z",this.coreBlockPos.getX());
        return compound;
    }
}