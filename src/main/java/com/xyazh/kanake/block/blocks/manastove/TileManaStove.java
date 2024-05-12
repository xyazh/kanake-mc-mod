package com.xyazh.kanake.block.blocks.manastove;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.api.IManaStorage;
import com.xyazh.kanake.block.blocks.manatable.TileManaWithForeverEntity;
import com.xyazh.kanake.entity.EntityForeverItem;
import com.xyazh.kanake.particle.ModParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class TileManaStove extends TileManaWithForeverEntity implements IManaStorage {
    public int workingTime = 0;
    protected double maxManaStored;
    protected double manaStored = 0;
    public boolean shouldSync = false;

    public TileManaStove() {
        super();
        this.maxManaStored = 200;
    }

    @Override
    public double getFEY() {
        return 0;
    }

    @Override
    public void setFeEntityData(EntityForeverItem feItemEntity) {
        super.setFeEntityData(feItemEntity);
        feItemEntity.setStatic(0,true);
    }

    @Override
    public void update() {
        super.update();
        this.trySync();
        if (world.isRemote) {
            if (this.workingTime > 0) {
                for(int i=0;i<2;i++){
                    this.world.spawnParticle(ModParticles.HONOO_PARTICLES,
                            pos.getX() + 0.25 + Kanake.rand.nextFloat() / 2, pos.getY() + 0.1875, pos.getZ() + 0.25 + Kanake.rand.nextFloat() / 2,
                            0, 0, 0);
                }
            }
        }
        for(int i=0;i<4;i++){
            if (this.workingTime-- > 0) {
                this.receiveMana(0.015);
            }else {
                break;
            }
        }
        if (this.workingTime <= 0 && !this.isEmpty() && !this.isFull()) {
            ItemStack itemStack = this.decrStackSize(0, 1);
            this.workingTime = TileEntityFurnace.getItemBurnTime(itemStack);
        }
    }


    @Nonnull
    @Override
    //取出槽位中的Item
    public ItemStack removeStackFromSlot(int index) {
        this.onInvChanges();
        if (itemStacks[0].isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = this.itemStacks[0];
        this.itemStacks[0] = ItemStack.EMPTY;
        return itemStack;
    }

    @Nonnull
    //添加槽位中的Item
    public ItemStack addStackFromSlot(int index, ItemStack itemStack) {
        this.onInvChanges();
        if (this.isEmpty()) {
            itemStacks[0] = itemStack;
            return ItemStack.EMPTY;
        }
        if (!itemStacks[0].isStackable()) {
            return itemStack;
        }
        if (ItemStack.areItemStackTagsEqual(itemStack, itemStacks[0])) {
            int maxSize = itemStacks[0].getMaxStackSize();
            int size1 = itemStacks[0].getCount();
            int size2 = itemStack.getCount();
            size1 += size2;
            size2 = Math.max(0, size1 - maxSize);
            size1 = Math.min(maxSize, size1);
            itemStacks[0].setCount(size1);
            if (size2 <= 0) {
                return ItemStack.EMPTY;
            }
            itemStack.setCount(size2);
        }
        return itemStack;
    }

    @Override
    //是否可以放入物品
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStack, @Nonnull EnumFacing direction) {
        int burnTime = TileEntityFurnace.getItemBurnTime(itemStack);
        if (burnTime <= 0) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        if (!itemStacks[0].isStackable()) {
            return false;
        }
        if (ItemStack.areItemStackTagsEqual(itemStack, itemStacks[0])) {
            int maxSize = itemStacks[0].getMaxStackSize();
            int size1 = itemStacks[0].getCount();
            return maxSize > size1;
        }
        return false;
    }

    @Override
    //是否可以取出物品
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return !this.isEmpty();
    }

    @Override
    //获取槽位数量
    public int getSizeInventory() {
        return this.itemStacks.length;
    }

    @Override
    //是否是空的
    public boolean isEmpty() {
        return this.itemStacks[0].isEmpty();
    }

    @Nonnull
    @Override
    //获取槽位中的物品
    public ItemStack getStackInSlot(int index) {
        return this.itemStacks[0];
    }

    @Nonnull
    @Override
    //取出一定数量的物品
    public ItemStack decrStackSize(int index, int count) {
        this.onInvChanges();
        if (this.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = this.itemStacks[0];
        int thisCount = itemStack.getCount();
        if (thisCount <= count) {
            return this.removeStackFromSlot(0);
        }
        itemStack.setCount(thisCount - count);
        ItemStack newItemStack = itemStack.copy();
        newItemStack.setCount(count);
        return newItemStack;
    }

    @Override
    //设置槽位中的物品
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        this.onInvChanges();
        this.itemStacks[0] = stack;
    }

    @Override
    //获取槽位中的物品最大堆叠
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.manaStored = compound.getDouble("manaStored");
        this.shouldSync = compound.getBoolean("shouldSyncSpeed");
        this.workingTime = compound.getInteger("workingTime");
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("manaStored", this.manaStored);
        compound.setBoolean("shouldSyncSpeed", this.shouldSync);
        compound.setInteger("workingTime", this.workingTime);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("manaStored", this.manaStored);
        compound.setInteger("workingTime", this.workingTime);
        return new SPacketUpdateTileEntity(this.pos, 1, compound);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager manager, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        this.manaStored = compound.getDouble("manaStored");
        this.workingTime = compound.getInteger("workingTime");
    }

    public void setShouldSync() {
        if (this.world.isRemote) {
            return;
        }
        this.shouldSync = true;
    }

    public void trySync() {
        if (this.world.isRemote) {
            return;
        }
        if (this.shouldSync) {
            this.shouldSync = false;
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS);
        }
    }

    @Override
    public boolean isFull() {
        return this.manaStored >= this.maxManaStored;
    }

    @Override
    public void setFull() {
        this.manaStored = this.maxManaStored;
        this.setShouldSync();
    }

    @Override
    public boolean isManaEmpty() {
        return this.getManaStored()<=0;
    }

    @Override
    public void clearMana() {
        this.manaStored = 0;
        this.setShouldSync();
    }

    @Override
    public double remain() {
        return this.maxManaStored - this.manaStored;
    }

    @Override
    public boolean canWork(Object object) {
        return true;
    }

    @Override
    public boolean canExtract(Object object) {
        return true;
    }

    @Override
    public double receiveMana(double n) {
        this.manaStored += n;
        if (this.manaStored > this.maxManaStored) {
            n = this.manaStored - this.maxManaStored;
            this.manaStored = this.maxManaStored;
            this.setShouldSync();
            return n;
        }
        this.setShouldSync();
        return 0;
    }

    @Override
    public double extractMana(double n) {
        if (n < this.manaStored) {
            this.manaStored -= n;
            this.setShouldSync();
            return n;
        }
        n = this.manaStored;
        this.manaStored = 0;
        this.setShouldSync();
        return n;
    }

    @Override
    public void setManaStored(double n) {
        this.manaStored = n;
        this.setShouldSync();
    }

    @Override
    public double getManaStored() {
        return this.manaStored;
    }

    @Override
    public double getMaxManaStored() {
        return this.maxManaStored;
    }
}
