package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.manatable.TileTableCoreMono;
import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class MonoWorkingRecipe {
    @Nonnull
    protected ItemStack coreItem = ItemStack.EMPTY;
    @Nonnull
    protected ItemStack outItem = ItemStack.EMPTY;
    protected final HashMap<BlockPos, RecipeSlot> map = new HashMap<>();
    public boolean isFail = false;
    public boolean isFinish = false;
    private RecipeSlot workingSlot = null;
    @Nonnull
    public MonoFunction fucFinish = new MonoFunction("finish");
    @Nonnull
    public MonoFunction fucFail = new MonoFunction("fail");

    protected TileTableCoreMono coreMono;
    public MonoWorkingRecipe(TileTableCoreMono coreMono){
        this.coreMono = coreMono;
    }

    public boolean isWorking(){
        return (!(isFail||isFinish))&&(!this.map.isEmpty());
    }

    protected void onFinish(){
        this.isFinish = true;
        boolean success = this.fucFinish.callFuc(this);
        this.coreMono.setInventorySlotContents(0,this.outItem.copy());
    }

    protected void onFail(){
        this.isFail = true;
        boolean success = this.fucFail.callFuc(this);
    }

    public void doWork(){
        if(this.isFinish||this.isFail){
            return;
        }
        if(!(this.coreMono.isStructuralIntegrityS||this.coreMono.isStructuralIntegrityB)){
            this.onFail();
            return;
        }
        if(this.workingSlot==null){
            this.workingSlot = this.getWorkingSlot();
        }
        if(this.workingSlot==null){
            this.onFinish();
        }else {
            TileTableMono mono = this.getWorkingTile();
            if(mono==null){
                this.onFail();
                return;
            }else if(!this.checkItemStack(mono.getStackInSlot(0),this.workingSlot.itemStack)){
                this.onFail();
                return;
            }
            if(this.coreMono.particle!=null){
                this.coreMono.particle.age = this.workingSlot.time;
            }
            if (!(0 < --this.workingSlot.time)) {
                this.workingSlot.working = false;
                mono.removeStackFromSlot(0);
                if(this.coreMono.particle!=null){
                    this.coreMono.particle.setDead();
                    this.coreMono.particle = null;
                }
                this.workingSlot = null;
            }
        }
    }

    @Nullable
    public RecipeSlot getWorkingSlot(){
        if(this.map.isEmpty()){
            return null;
        }
        RecipeSlot slot = null;
        for(RecipeSlot i:this.map.values()){
            if(i.time<=0){
                continue;
            }
            slot = i;
            if(slot.working){
                return slot;
            }
        }
        if(slot!=null){
            slot.working = true;
            return slot;
        }
        return null;
    }

    @Nullable
    public TileTableMono getWorkingTile(){
        RecipeSlot slot = this.getWorkingSlot();
        if(slot == null){
            return null;
        }
        return this.getSlotTile(slot);
    }

    @Nullable
    public TileTableMono getSlotTile(RecipeSlot slot){
        BlockPos pos = slot.pos;
        TileEntity te = this.coreMono.getWorld().getTileEntity(pos);
        if(te instanceof TileTableMono){
            return (TileTableMono) te;
        }
        return null;
    }

    public void load(ItemStack coreItem, LinkedList<TileTableMono> outTiles, ItemStack outItem, MonoRecipe recipe) {
        this.clear();
        this.coreItem = coreItem;
        this.outItem = outItem;
        this.fucFinish = recipe.getFinishFuc(this.coreMono,outTiles);
        this.fucFail = recipe.getFailFuc(this.coreMono,outTiles);
        for (int i = 0; i < outTiles.size(); i++) {
            TileTableMono tileTableMono = outTiles.get(i);
            RecipeSlot slot = new RecipeSlot(i);
            slot.pos = tileTableMono.getPos();
            slot.itemStack = tileTableMono.getStackInSlot(0);
            slot.time = recipe.getTime(this.coreMono,outTiles,slot.itemStack);
            this.map.put(slot.pos, slot);
        }
    }

    public void clear() {
        this.coreItem = ItemStack.EMPTY;
        this.outItem = ItemStack.EMPTY;
        this.map.clear();
        this.isFail = false;
        this.isFinish = false;
        this.workingSlot = null;
        this.fucFinish = new MonoFunction("finish");
        this.fucFail = new MonoFunction("fail");
    }

    protected boolean checkItemStack(ItemStack itemStack1, ItemStack itemStack2) {
        if(itemStack1 == itemStack2){
            return true;
        }
        if (itemStack1.getItem() != itemStack2.getItem()) {
            return false;
        }
        NBTTagCompound compound1 = itemStack1.getTagCompound();
        NBTTagCompound compound2 = itemStack2.getTagCompound();
        return Objects.equals(compound1, compound2);
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound subCompound = compound.getCompoundTag("mono_working_recipe");
        NonNullList<ItemStack> itemStackList = NonNullList.withSize(2, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(subCompound, itemStackList);
        this.coreItem = itemStackList.get(0);
        this.outItem = itemStackList.get(1);
        int size = subCompound.getInteger("shot_size");
        for(int i=0;i<size;i++){
            RecipeSlot slot = new RecipeSlot(i);
            slot.readFromNBT(subCompound);
            this.map.put(slot.pos,slot);
        }
        this.isFail = subCompound.getBoolean("isFail");
        this.isFinish = subCompound.getBoolean("isFinish");
        this.fucFinish.readFromNBT(subCompound);
        this.fucFail.readFromNBT(subCompound);
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagCompound subCompound = new NBTTagCompound();
        NonNullList<ItemStack> itemStackList = NonNullList.withSize(2, ItemStack.EMPTY);
        itemStackList.set(0,this.coreItem);
        itemStackList.set(1,this.outItem);
        ItemStackHelper.saveAllItems(subCompound, itemStackList);
        subCompound.setInteger("shot_size",this.map.size());
        for(RecipeSlot slot:this.map.values()){
            subCompound = slot.writeToNBT(subCompound);
        }
        subCompound.setBoolean("isFail",this.isFail);
        subCompound.setBoolean("isFinish",this.isFinish);
        subCompound = this.fucFinish.writeToNBT(subCompound);
        subCompound = this.fucFail.writeToNBT(subCompound);
        compound.setTag("mono_working_recipe", subCompound);
        return compound;
    }

    protected static class RecipeSlot {
        @Nonnull
        public ItemStack itemStack = ItemStack.EMPTY;
        public int time = 0;
        public boolean working = false;
        @Nonnull
        public String id;
        public BlockPos pos;

        private RecipeSlot(@Nonnull String id) {
            this.id = id;
        }

        private RecipeSlot(int id) {
            this(String.valueOf(id));
        }

        public void readFromNBT(@Nonnull NBTTagCompound compound) {
            NBTTagCompound subCompound = compound.getCompoundTag(id);
            this.time = subCompound.getInteger("time");
            this.working = subCompound.getBoolean("working");
            NonNullList<ItemStack> itemStackList = NonNullList.withSize(1, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(subCompound, itemStackList);
            this.itemStack = itemStackList.get(0);
            int x,y,z;
            x = subCompound.getInteger("pos.x");
            y = subCompound.getInteger("pos.y");
            z = subCompound.getInteger("pos.z");
            this.pos = new BlockPos(x,y,z);
        }

        @Nonnull
        public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
            NBTTagCompound subCompound = new NBTTagCompound();
            subCompound.setInteger("time",this.time);
            subCompound.setBoolean("working",this.working);
            NonNullList<ItemStack> itemStackList = NonNullList.withSize(1, ItemStack.EMPTY);
            itemStackList.set(0,this.itemStack);
            ItemStackHelper.saveAllItems(subCompound, itemStackList);
            subCompound.setInteger("pos.x",this.pos.getX());
            subCompound.setInteger("pos.y",this.pos.getY());
            subCompound.setInteger("pos.z",this.pos.getZ());
            compound.setTag(this.id, subCompound);
            return compound;
        }
    }
}
