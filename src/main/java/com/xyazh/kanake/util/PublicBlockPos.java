package com.xyazh.kanake.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class PublicBlockPos extends BlockPos {
    public int x,y,z;

    public PublicBlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public PublicBlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    public PublicBlockPos(Entity source) {
        super(source);
    }

    public PublicBlockPos(Vec3d vec) {
        super(vec);
    }

    public PublicBlockPos(Vec3i source) {
        super(source);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }
}
