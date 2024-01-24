package com.xyazh.kanake.block.blocks;

import com.xyazh.kanake.api.IManaStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class TileManaStorageBase extends TileBase implements ITickable, IManaStorage {
    protected double maxManaStored;
    protected double manaStored = 0;
    public boolean shouldSync = false;

    public TileManaStorageBase(){
        super();
        this.maxManaStored = 200;
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.manaStored = compound.getDouble("manaStored");
        this.shouldSync = compound.getBoolean("shouldSync");
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("manaStored", this.manaStored);
        compound.setBoolean("shouldSync", this.shouldSync);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("manaStored", this.manaStored);
        return new SPacketUpdateTileEntity(this.pos, 1, compound);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager manager, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        this.manaStored = compound.getDouble("manaStored");
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
            this.world.notifyBlockUpdate(this.pos,state,state, Constants.BlockFlags.SEND_TO_CLIENTS);
        }
    }

    @Override
    public void update() {
        this.trySync();
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
