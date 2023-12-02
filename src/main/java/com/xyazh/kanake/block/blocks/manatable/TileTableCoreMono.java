package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.entity.EntitySpawnParticle;
import com.xyazh.kanake.recipes.mono.MonoRecipe;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;

public class TileTableCoreMono extends TileTableCheckStructMono {
    public TileTableMono workingTile = null;
    public final LinkedList<TileTableMono> recipeTiles = new LinkedList<>();
    public Item outItem = Items.AIR;

    public EntitySpawnParticle particle = null;

    public int findAge = 40 + Kanake.rand.nextInt(5);

    @Override
    public void update() {
        super.update();
        if (world.isRemote) {
            return;
        }
        if (!this.isInited) {
            return;
        }
        this.work();
        if (this.workingTile != null) {
            this.workParticle(this.workingTile.workingTime);
        }
    }

    public void work() {
        if (this.working) {
            this.doRecipe();
        } else if(!this.isEmpty()){
            if(this.findAge--<0){
                this.findingRecipe();
                this.findAge = 40 + Kanake.rand.nextInt(5);
            }
        }
    }

    public void findingRecipe() {
        LinkedList<TileTableMono> subTiles = this.getSubTile();
        this.restoreRecipe(subTiles);
        this.newRecipe(subTiles);
    }

    public void restoreRecipe(LinkedList<TileTableMono> subTiles) {
        if (!(this.workingTile == null && this.recipeTiles.isEmpty())) {
            this.working = true;
            return;
        }
        for (TileTableMono tileTableMono : subTiles) {
            if (tileTableMono.working) {
                this.workingTile = tileTableMono;
            } else if (tileTableMono.inRecipes) {
                this.recipeTiles.add(tileTableMono);
            }
        }
    }

    public void newRecipe(LinkedList<TileTableMono> subTiles) {
        if (!(this.workingTile == null && this.recipeTiles.isEmpty())) {
            this.working = true;
            return;
        }
        HashMap<TileTableMono, Integer> tableMonos = new HashMap<>();
        for (TileTableMono tileTableMono : subTiles) {
            tableMonos.put(tileTableMono, 240);
        }
        Item out = MonoRecipe.findRecipe(this.itemStacks[0], tableMonos);
        if (out == null) {
            return;
        }
        this.outItem = out;
        for (TileTableMono tileTableMono : tableMonos.keySet()) {
            tileTableMono.resetType();
            tileTableMono.inRecipes = true;
            tileTableMono.workingTime = tableMonos.get(tileTableMono);
        }
    }

    public void doRecipe() {
        if (this.workingTile == null && this.recipeTiles.isEmpty()) {
            this.working = false;
            return;
        }
        if (this.workingTile == null) {
            this.workingTile = this.recipeTiles.removeFirst();
            this.workingTile.inRecipes = false;
            this.workingTile.working = true;
        } else {
            if (0 < --this.workingTile.workingTime) {
                if (this.workingTile.isFail || this.isFail) {
                    this.workingTile.isFail = false;
                    this.isFail = false;
                    this.onFail();
                }
            } else {
                this.workingTile.working = false;
                this.workingTile.removeStackFromSlot(0);
                if(this.particle!=null){
                    this.particle.setDead();
                    this.particle = null;
                }
                if (this.recipeTiles.isEmpty()) {
                    this.onFinish();
                } else {
                    this.workingTile = this.recipeTiles.removeFirst();
                    this.workingTile.inRecipes = false;
                    this.workingTile.working = true;
                }
            }
        }
    }

    @Override
    public void resetType() {
        super.resetType();
        this.outItem = Items.AIR;
        if (this.particle !=null) {
            this.particle.setDead();
            this.particle = null;
        }
        if (this.workingTile != null) {
            this.workingTile.resetType();
        }
        for (TileTableMono subTile : this.recipeTiles) {
            subTile.resetType();
        }
        this.workingTile = null;
        this.recipeTiles.clear();
    }

    public void onFinish() {
        this.working = false;
        ItemStack itemStack = new ItemStack(this.outItem);
        itemStack.setCount(1);
        this.setInventorySlotContents(0, itemStack);
        this.resetType();
    }

    public void onFail() {
        this.resetType();
        this.clear();
    }

    public void workParticle(int age) {
        if(this.particle != null){
            return;
        }
        int itemId = Item.getIdFromItem(this.workingTile.itemStacks[0].getItem());
        double x1, y1, z1, x2, y2, z2;
        x1 = this.workingTile.getPos().getX() + 0.5;
        y1 = this.workingTile.getPos().getY() + 1.5;
        z1 = this.workingTile.getPos().getZ() + 0.5;
        x2 = this.pos.getX() + 0.5;
        y2 = this.pos.getY() + 1.5;
        z2 = this.pos.getZ() + 0.5;
        EntitySpawnParticle particle = new EntitySpawnParticle(this.world, age, itemId, x1, y1, z1, x2, y2, z2);
        this.particle = particle;
        this.world.spawnEntity(particle);
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        String outItemStr = compound.getString("outItem");
        if(!outItemStr.equals("")){
            Item item = Item.getByNameOrId(outItemStr);
            if(item != null){
                this.outItem = item;
            }
        }
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if(this.outItem.equals(Items.AIR)){
            compound.setString("outItem","");
        }else if(this.outItem.getRegistryName() == null){
            compound.setString("outItem","");
        }else {
            compound.setString("outItem",this.outItem.getRegistryName().toString());
        }
        return compound;
    }
}
