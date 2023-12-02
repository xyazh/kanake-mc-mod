package com.xyazh.kanake.entity;

import com.xyazh.kanake.block.blocks.manatable.TileManaWithForeverEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityForeverItem extends EntityItem {
    public TileManaWithForeverEntity tileEntity = null;

    public EntityForeverItem(World worldIn) {
        super(worldIn);
    }

    public EntityForeverItem(World worldIn, TileManaWithForeverEntity tileEntity) {
        super(worldIn);
        this.tileEntity = tileEntity;
    }

    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.tileEntity == null){
            if(!this.world.isRemote){
                this.setDead();
            }
        }else if(!this.tileEntity.checkItem(this.getItem())){
            this.setDead();
        }
    }

    public void move(MoverType type, double x, double y, double z)
    {
    }

    public boolean isInLava()
    {
        return false;
    }

    protected void setOnFireFromLava()
    {
    }


    protected void dealFireDamage(int amount)
    {
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    public boolean isBurning()
    {
        return false;
    }

    public boolean handleWaterMovement()
    {
        this.inWater = false;
        return this.inWater;
    }
}
