package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.block.blocks.manastorage.TileManaStorage;
import com.xyazh.kanake.entity.EntitySpawnParticle;
import com.xyazh.kanake.recipes.mono.MonoRecipeManager;
import com.xyazh.kanake.recipes.mono.MonoRecipe;
import com.xyazh.kanake.recipes.mono.MonoWorkingRecipe;
import com.xyazh.kanake.util.PublicBlockPos;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

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

    protected void selectWorkStatus() {
        if (this.monoWorkingRecipe.isWorking()) {
            this.monoWorkingRecipe.doWork();
            if (this.monoWorkingRecipe.isWorking()) {
                this.workParticle(Integer.MAX_VALUE);
            }
        } else if (this.shouldFindRecipe) {
            this.findingRecipe();
        }
    }

    @Override
    protected void onStructureChanged(boolean valid) {
        if(valid){
            // 结构恢复/仍然有效时重新尝试匹配，避免 shouldFindRecipe 被一次性消耗后永不重试
            this.shouldFindRecipe = true;
        }
    }

    public void findingRecipe() {
        if(!this.finishStruct()){
            // 结构尚未检查完成或已失效：保留 shouldFindRecipe，等结构恢复后再试
            return;
        }
        LinkedList<TileTableMono> subTiles = this.getSubTile();
        if(subTiles.isEmpty()){
            // 台座方块实体尚未准备好，下个 tick/下轮结构检测再试
            return;
        }
        this.newRecipe(subTiles);
        this.shouldFindRecipe = false;
    }

    public boolean newRecipe(LinkedList<TileTableMono> subTiles) {
        LinkedList<TileTableMono> outTiles = new LinkedList<>();
        MonoRecipe recipe = MonoRecipeManager.findRecipe(this.itemStacks[0], subTiles, this.world, this, outTiles);
        if (recipe == null) {
            return false;
        }
        this.monoWorkingRecipe.clear();
        this.monoWorkingRecipe.load(this.getStackInSlot(0), outTiles, recipe.getOutItem(), recipe);
        return true;
    }

    public void workParticle(int age) {
        if (this.particle != null) {
            return;
        }
        TileTableMono workingTile = this.monoWorkingRecipe.getWorkingTile();
        if (workingTile == null) {
            return;
        }
        ItemStack workingItem = this.monoWorkingRecipe.getWorkingItemStack();
        if (workingItem.isEmpty()) {
            return;
        }
        int itemId = Item.getIdFromItem(workingItem.getItem());
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

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.monoWorkingRecipe.readFromNBT(compound);
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound = this.monoWorkingRecipe.writeToNBT(compound);
        return compound;
    }
}
