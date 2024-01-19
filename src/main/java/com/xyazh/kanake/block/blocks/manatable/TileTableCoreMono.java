package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.entity.EntitySpawnParticle;
import com.xyazh.kanake.recipes.mono.MonoRecipeHelper;
import com.xyazh.kanake.recipes.mono.MonoRecipe;
import com.xyazh.kanake.recipes.mono.MonoWorkingRecipe;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class TileTableCoreMono extends TileTableCheckStructMono {
    public EntitySpawnParticle particle = null;
    public final MonoWorkingRecipe monoWorkingRecipe = new MonoWorkingRecipe(this);
    public boolean shouldFindRecipe = true;

    @Override
    public void update() {
        super.update();
        if (world.isRemote) {
            return;
        }
        if (!this.isInited) {
            return;
        }
        this.selectWorkStatus();
    }

    protected void selectWorkStatus(){
        if(this.monoWorkingRecipe.isWorking()){
            this.monoWorkingRecipe.doWork();
            this.workParticle(Integer.MAX_VALUE);
        }else if(this.shouldFindRecipe){
            this.findingRecipe();
        }
    }

    public void findingRecipe() {
        LinkedList<TileTableMono> subTiles = this.getSubTile();
        this.newRecipe(subTiles);
        this.shouldFindRecipe = false;
    }

    public void newRecipe(LinkedList<TileTableMono> subTiles) {
        LinkedList<TileTableMono> outTiles = new LinkedList<>();
        MonoRecipe recipe = MonoRecipeHelper.findRecipe(this.itemStacks[0],subTiles,this.world,this,outTiles);
        if (recipe == null) {
            return;
        }
        this.monoWorkingRecipe.clear();
        this.monoWorkingRecipe.load(this.getStackInSlot(0),outTiles,recipe.getOutItem(),recipe);
    }

    public void workParticle(int age) {
        if(this.particle != null){
            return;
        }
        TileTableMono workingTile = this.monoWorkingRecipe.getWorkingTile();
        if(workingTile == null){
            return;
        }
        int itemId = Item.getIdFromItem(workingTile.itemStacks[0].getItem());
        double x1, y1, z1, x2, y2, z2;
        x1 = workingTile.getPos().getX() + 0.5;
        y1 = workingTile.getPos().getY() + 1.5;
        z1 = workingTile.getPos().getZ() + 0.5;
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
        this.monoWorkingRecipe.readFromNBT(compound);
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound = this.monoWorkingRecipe.writeToNBT(compound);
        return compound;
    }
}
