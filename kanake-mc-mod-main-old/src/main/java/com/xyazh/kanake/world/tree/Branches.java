package com.xyazh.kanake.world.tree;

import com.xyazh.kanake.util.Vec3d;

public class Branches {
    private final Vec3d tempVec = new Vec3d();
    public final Vec3d startPos;
    public final float radius;
    public final Vec3d endPos;

    public Branches(Vec3d startPos, Vec3d endPos, float radius) {
        this.startPos = startPos;
        this.radius = radius;
        this.endPos = endPos;
    }

    public boolean contain(double x, double y, double z) {
        tempVec.set(x, y, z);
        if (tempVec.isBetween(startPos, endPos)) {
            double distance = tempVec.distanceToLine(startPos, endPos);
            return distance <= radius;
        }
        return false;
    }
}
