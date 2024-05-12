package com.xyazh.kanake.block.blocks.rail;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class WSRailPoweredBase extends WSRailBase {
    public float acceleration = 0.2f;
    public WSRailPoweredBase(String name) {
        super(name);
        setLightLevel(7);
    }

    public WSRailPoweredBase(String name,float speed) {
        super(name, speed);
        setLightLevel(7);
    }

    public WSRailPoweredBase setAcceleration(float acceleration){
        this.acceleration = acceleration;
        return this;
    }


    public float getAcceleration(@Nonnull World world, @Nonnull EntityMinecart cart, @Nonnull BlockPos pos){
        return this.acceleration;
    }

    @Override
    public boolean isFlexibleRail(@Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        return false;
    }

}
