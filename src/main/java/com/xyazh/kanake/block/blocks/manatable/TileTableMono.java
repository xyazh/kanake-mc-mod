package com.xyazh.kanake.block.blocks.manatable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class TileTableMono extends TileManaWithForeverEntity{
    public boolean working = false;
    public int workingTime = 0;
    public boolean inRecipes = false;
    public boolean isFail = false;
    public BlockPos coreBlockPos = new BlockPos(0,0,0);
    @Override
    public void update() {
        super.update();
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.working = compound.getBoolean("working");
        this.inRecipes = compound.getBoolean("inRecipes");
        this.workingTime = compound.getInteger("workingTime");
        int x,y,z;
        x = compound.getInteger("coreBlockPos.x");
        y = compound.getInteger("coreBlockPos.args");
        z = compound.getInteger("coreBlockPos.z");
        this.coreBlockPos = new BlockPos(x,y,z);
        this.isFail = compound.getBoolean("isFail");
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setBoolean("working",this.working);
        compound.setBoolean("inRecipes",this.inRecipes);
        compound.setInteger("workingTime",this.workingTime);
        compound.setInteger("coreBlockPos.x",this.coreBlockPos.getX());
        compound.setInteger("coreBlockPos.args",this.coreBlockPos.getX());
        compound.setInteger("coreBlockPos.z",this.coreBlockPos.getX());
        compound.setBoolean("isFail",this.isFail);
        return compound;
    }

    public void resetType(){
        this.isFail = false;
        this.working = false;
        this.inRecipes = false;
        this.workingTime = 0;
    }

    @Nonnull
    @Override
    //取出槽位中的Item
    public ItemStack removeStackFromSlot(int index) {
        if(this.working||this.inRecipes){
            this.isFail = true;
        }
        return super.removeStackFromSlot(index);
    }

    @Nonnull
    //添加槽位中的Item
    public ItemStack addStackFromSlot(int index,ItemStack itemStack){
        if(this.working){
            this.isFail = true;
        }
        return super.addStackFromSlot(index,itemStack);
    }

    @Override
    //是否可以取出物品
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return super.canExtractItem(index,stack,direction) && !this.working;
    }

    @Override
    //取出一定数量的物品
    public ItemStack decrStackSize(int index, int count) {
        if(this.working){
            this.isFail = true;
        }
        return super.decrStackSize(index,count);
    }

    @Override
    //设置槽位中的物品
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        if(this.working){
            this.isFail = true;
        }
        super.setInventorySlotContents(index,stack);
    }

    @Override
    public void clear() {
        if(this.working){
            this.isFail = true;
        }
        super.clear();
    }
}