package com.xyazh.kanake.entity;

import com.xyazh.kanake.block.blocks.manatable.ITileForeverEntity;
import com.xyazh.kanake.block.blocks.manatable.TileManaWithForeverEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class EntityForeverItem extends EntityItem implements IEntityAdditionalSpawnData {
    public ITileForeverEntity tileEntity = null;
    public TileEntity te = null;
    public boolean isStatic = false;

    public EntityForeverItem(World worldIn) {
        super(worldIn);
    }

    public EntityForeverItem(World worldIn, ITileForeverEntity tileEntity) {
        super(worldIn);
        this.tileEntity = tileEntity;
    }

    public void setStatic(float hover,boolean isStatic){
        this.hoverStart = hover;
        this.isStatic  = isStatic;
    }

    public boolean shouldBob(){
        return !this.isStatic;
    }

    public boolean shouldRotate(){
        return !this.isStatic;
    }

    public void onCollideWithPlayer(@Nonnull EntityPlayer entityIn)
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
        }else if(this.tileEntity.isDead()){
            this.setDead();
        }
    }

    public void move(@Nonnull MoverType type, double x, double y, double z)
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
        return false;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.isStatic = compound.getBoolean("isStatic");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean("isStatic",this.isStatic);
        return compound;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(this.isStatic);
        buffer.writeFloat(this.hoverStart);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.isStatic = buffer.readBoolean();
        this.hoverStart = buffer.readFloat();
    }
}
