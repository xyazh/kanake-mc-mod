package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.block.blocks.TileBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;


public class TileManaTableBase extends TileBase implements ITickable {
    protected ItemStack[] itemStacks = {ItemStack.EMPTY};

    public void onInvChanges(){

    }

    @Override
    public void update() {
    }

    //掉落物品
    public void drop(){
        if(!this.isEmpty() && !this.world.isRemote){
            EntityItem entityItem = new EntityItem(this.world);
            entityItem.setItem(this.removeStackFromSlot(0));
            entityItem.setPosition(this.pos.getX()+0.5,this.pos.getY()+0.5,this.pos.getZ()+0.5);
            this.world.spawnEntity(entityItem);
        }
    }

    @Nonnull
    @Override
    //取出槽位中的Item
    public ItemStack removeStackFromSlot(int index) {
        this.onInvChanges();
        ItemStack itemStack = this.itemStacks[0];
        this.itemStacks[0] = ItemStack.EMPTY;
        return itemStack;
    }

    @Nonnull
    //添加槽位中的Item
    public ItemStack addStackFromSlot(int index,ItemStack itemStack){
        this.onInvChanges();
        if(itemStacks[0].equals(ItemStack.EMPTY)){
            int count = itemStack.getCount();
            int maxCount = this.getInventoryStackLimit();
            if(count <= maxCount){
                itemStacks[0] = itemStack;
                return ItemStack.EMPTY;
            }
            itemStacks[0] = itemStack.copy();
            itemStacks[0].setCount(maxCount);
            itemStack.setCount(count - maxCount);
            return itemStack;
        }
        return itemStack;
    }

    @Nonnull
    //获取某个方向面向的槽位
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return new int[]{0};
    }

    @Override
    //是否可以放入物品
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return this.isEmpty();
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
        return this.itemStacks[0].equals(ItemStack.EMPTY);
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
        if(this.isEmpty()){
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = this.itemStacks[0];
        int thisCount = itemStack.getCount();
        if(thisCount <= count){
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
        this.itemStacks[0].setCount(1);
    }

    @Override
    //获取槽位中的物品最大堆叠
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    //是否可供玩家使用
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return true;
    }

    @Override
    //此插槽对物品是否可用
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public void clear() {
        this.onInvChanges();
        this.itemStacks[0] = ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public String getName() {
        return "mana_table";
    }

    public void readItemStacks(@Nonnull NBTTagCompound compound){
        NonNullList<ItemStack> itemStackList = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, itemStackList);
        itemStacks[0]=itemStackList.get(0);
    }

    public NBTTagCompound writeItemStacks(@Nonnull NBTTagCompound compound){
        NonNullList<ItemStack> itemStackList = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
        itemStackList.set(0,itemStacks[0]);
        ItemStackHelper.saveAllItems(compound, itemStackList);
        return compound;
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.readItemStacks(compound);
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound = writeItemStacks(compound);
        return compound;
    }
}
