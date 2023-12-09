package com.xyazh.kanake.block.blocks.test;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.TileBase;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

public class TileTest extends TileEntity implements ITickable{
    protected int age = 20;

    public TileTest(){
        super();
    }

    @Nonnull
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
    }

    @Override
    public void update() {
        if(this.age--<0){
            world.getBlockState(pos).getBlock().breakBlock(world,pos,world.getBlockState(pos));
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
