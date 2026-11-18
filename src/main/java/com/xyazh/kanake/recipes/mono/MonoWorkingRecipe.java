package com.xyazh.kanake.recipes.mono;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.manatable.TileTableCoreMono;
import com.xyazh.kanake.block.blocks.manatable.TileTableMono;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    @Nullable
    public MonoRecipe recipe = null;


    protected TileTableCoreMono coreMono;
    private int invalidStructureTicks = 0;
    private static final int MAX_INVALID_STRUCTURE_TICKS = 100;

    public MonoWorkingRecipe(TileTableCoreMono coreMono){
        this.coreMono = coreMono;
    }

    public boolean isWorking(){
        return (!(isFail||isFinish))&&(!this.map.isEmpty());
    }

    protected void onFinish() {
        this.isFinish = true;
        this.invalidStructureTicks = 0;
        if (this.coreMono.particle != null) {
            this.coreMono.particle.setDead();
            this.coreMono.particle = null;
        }
        if (this.recipe != null) {
            this.recipe.onFinish(this);
        }
        this.coreMono.setInventorySlotContents(0, this.outItem.copy());
    }

    protected void onFail() {
        if (this.recipe != null) {
            this.recipe.onFail(this);
        }
        this.interrupt();
    }

    /**
     * 运行中的配方被破坏/中止：
     * 1. 产生不破坏方块的小爆炸；
     * 2. 清除台座核心物品；
     * 3. 终止配方，不再产出，也不返还已经抽取的输入，避免刷物品。
     */
    public void interrupt() {
        if (this.isFinish || this.isFail) {
            return;
        }
        World world = this.coreMono.getWorld();
        if (world != null && !world.isRemote) {
            world.createExplosion(null,
                    this.coreMono.getPos().getX() + 0.5,
                    this.coreMono.getPos().getY() + 1.0,
                    this.coreMono.getPos().getZ() + 0.5,
                    0.8F, false);
        }
        this.isFail = true;
        this.workingSlot = null;
        this.invalidStructureTicks = 0;
        if (this.coreMono.particle != null) {
            this.coreMono.particle.setDead();
            this.coreMono.particle = null;
        }
        this.coreMono.removeStackFromSlot(0);
        this.coreMono.markDirty();
        this.coreMono.shouldFindRecipe = false;
    }

    private boolean hasInvalidInput() {
        if (this.coreMono.getWorld() == null) {
            return true;
        }
        for (RecipeSlot slot : this.map.values()) {
            if (slot.time <= 0) {
                // 已经抽取/消耗的槽位，不再要求台座上还有物品
                continue;
            }
            TileTableMono tile = this.getSlotTile(slot);
            if (tile == null || !this.checkItemStack(tile.getStackInSlot(0), slot.itemStack)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack getWorkingItemStack() {
        if (this.workingSlot == null) {
            return ItemStack.EMPTY;
        }
        return this.workingSlot.itemStack;
    }

    public void doWork() {
        if (this.isFinish || this.isFail) {
            return;
        }
        if (this.hasInvalidInput()) {
            // 运行期间任何尚未抽取的输入被取走/替换，立即判定失败
            this.onFail();
            return;
        }
        if (!this.checkItemStack(this.coreMono.getStackInSlot(0), this.coreItem)) {
            // 核心物品被取走/替换：立即中止，避免无消耗产出
            this.onFail();
            return;
        }
        if (!this.coreMono.finishStruct()) {
            // 结构失效/尚未完成：先暂停，避免一次临时状态直接毁掉整个配方。
            if (++this.invalidStructureTicks < MAX_INVALID_STRUCTURE_TICKS) {
                return;
            }
            this.onFail();
            return;
        }
        this.invalidStructureTicks = 0;
        if (this.workingSlot == null) {
            this.workingSlot = this.getWorkingSlot();
        }
        if (this.workingSlot == null) {
            this.onFinish();
        } else {
            TileTableMono mono = this.getWorkingTile();
            if (mono == null) {
                this.onFail();
                return;
            }
            if (this.coreMono.particle != null) {
                this.coreMono.particle.age = this.workingSlot.time;
            }
            if (!(0 < --this.workingSlot.time)) {
                // 工作槽完成时抽取/消耗该台座上的物品
                this.workingSlot.working = false;
                mono.removeStackFromSlot(0);
                mono.markDirty();
                if (this.coreMono.particle != null) {
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
        this.recipe = recipe;
        for (int i = 0; i < outTiles.size(); i++) {
            TileTableMono tileTableMono = outTiles.get(i);
            ItemStack input = tileTableMono.getStackInSlot(0);
            if (input.isEmpty()) {
                continue;
            }
            RecipeSlot slot = new RecipeSlot(i);
            slot.pos = tileTableMono.getPos();
            // 配方开始时不抽取，保留在台座上；等该槽工作完成后再逐个消耗。
            slot.itemStack = input.copy();
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
        this.invalidStructureTicks = 0;
    }

    protected boolean checkItemStack(ItemStack itemStack1, ItemStack itemStack2) {
        return ItemStack.areItemStacksEqual(itemStack1,itemStack2);
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
        String recipeID = subCompound.getString("recipeID");
        if(!recipeID.equals("")){
            ResourceLocation resourceLocation = new ResourceLocation(recipeID);
            this.recipe = MonoRecipeManager.RECIPES.get(resourceLocation);
        }
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
        if(this.recipe!=null){
            subCompound.setString("recipeID",this.recipe.id.toString());
        }
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
