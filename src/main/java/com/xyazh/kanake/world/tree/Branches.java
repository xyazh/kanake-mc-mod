package com.xyazh.kanake.world.tree;

import com.xyazh.kanake.util.Vec3d;

public class Branches {
    public final Vec3d startPos;
    public final float radius;
    public final Vec3d endPos;

    public Branches(Vec3d startPos, Vec3d endPos, float radius) {
        this.startPos = startPos;
        this.radius = radius;
        this.endPos = endPos;
    }

    public boolean contain(Vec3d v) {
        if (v.isBetween(startPos, endPos)) {
            double distance = v.distanceToLine(startPos, endPos);
            return distance <= radius;
        }
        return false;
    }
}
