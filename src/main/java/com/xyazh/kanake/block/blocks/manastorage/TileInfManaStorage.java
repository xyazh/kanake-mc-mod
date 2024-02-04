package com.xyazh.kanake.block.blocks.manastorage;

import com.xyazh.kanake.api.IManaStorage;
import com.xyazh.kanake.block.blocks.TileBase;
import com.xyazh.kanake.block.blocks.TileManaStorageBase;

public class TileInfManaStorage extends TileBase implements IManaStorage {
    public TileInfManaStorage(){
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public void setFull() {
    }

    @Override
    public boolean isManaEmpty() {
        return false;
    }

    @Override
    public void clearMana() {
    }

    @Override
    public double remain() {
        return 0;
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
        return n;
    }

    @Override
    public double extractMana(double n) {
        return n;
    }

    @Override
    public void setManaStored(double n) {
    }

    @Override
    public double getManaStored() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getMaxManaStored() {
        return Double.POSITIVE_INFINITY;
    }
}
