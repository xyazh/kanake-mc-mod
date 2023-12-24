package com.xyazh.kanake.block.blocks.platform;

import com.xyazh.kanake.block.blocks.clean.TileClean;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;


import javax.annotation.Nonnull;

public class TilePlatform extends TileClean implements ITickable{
    public int age = 4;

    public TilePlatform(){
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
