package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.entity.EntityForeverItem;
import net.minecraft.item.ItemStack;

public class TileManaWithForeverEntity extends TileManaTableBase implements ITileForeverEntity{
    public EntityForeverItem feItemEntity = null;

    public boolean checkItem(ItemStack itemStack){
        return itemStacks[0].equals(itemStack);
    }

    public double getFEY(){
        return 1;
    }

    public void setFeEntityData(EntityForeverItem feItemEntity){

    }

    @Override
    public boolean isDead() {
        return this.world.getTileEntity(this.pos) != this;
    }

    public void setFeEntity(){
        if(world.isRemote){
            return;
        }
        if(itemStacks[0].equals(ItemStack.EMPTY)){
            return;
        }
        if(this.feItemEntity == null || this.feItemEntity.isDead){
            this.feItemEntity = new EntityForeverItem(this.world,this);
            this.feItemEntity.setItem(itemStacks[0]);
            this.feItemEntity.setPosition(this.pos.getX()+0.5,this.pos.getY()+this.getFEY(),this.pos.getZ()+0.5);
            this.feItemEntity.setEntityInvulnerable(true);
            this.feItemEntity.setInfinitePickupDelay();
            this.setFeEntityData(this.feItemEntity);
            this.world.spawnEntity(this.feItemEntity);
        }
    }

    @Override
    public void update() {
        super.update();
        this.setFeEntity();
    }
}
