package com.xyazh.kanake.util;

public class Vec3dFinal extends Vec3d {
    public static final Vec3dFinal ZERO = new Vec3dFinal(0, 0, 0);
    public static final Vec3dFinal ONE = new Vec3dFinal(1, 1, 1);
    public static final Vec3dFinal UP = new Vec3dFinal(0, 1, 0);
    public static final Vec3dFinal DOWN = new Vec3dFinal(0, -1, 0);
    public static final Vec3dFinal NORTH = new Vec3dFinal(0, 0, -1);
    public static final Vec3dFinal SOUTH = new Vec3dFinal(0, 0, 1);
    public static final Vec3dFinal WEST = new Vec3dFinal(-1, 0, 0);
    public static final Vec3dFinal EAST = new Vec3dFinal(1, 0, 0);

    public final double x;
    public final double y;
    public final double z;

    public Vec3dFinal(double x, double y, double z) {
        super(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public final void set(Vec3d v) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void set(net.minecraft.util.math.Vec3d v) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void set(double x, double y, double z) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void mul(double scale) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void sub(Vec3d t1, Vec3d t2) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void sub(Vec3d t1) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void add(Vec3d t1, Vec3d t2) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void add(Vec3d t1) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void normalize() {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final double normalizeAndLength() {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void cross(Vec3d v1, Vec3d v2) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void cross(Vec3d v) {
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void rand(){
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }

    @Override
    public final void rand(double scale){
        throw new UnsupportedOperationException("Cannot modify final Vec3d");
    }
}