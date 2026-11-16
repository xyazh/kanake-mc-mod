package com.xyazh.kanake.world.tree;

import com.xyazh.kanake.util.Vec3d;

public class Branches {
    public final Vec3d startPos;
    public final float radius;
    public final Vec3d endPos;
    /**
     * Marks branches near the tip of the tree so ChunkGeneratorMaze can grow leaves there.
     */
    public final boolean leaf;

    public Branches(Vec3d startPos, Vec3d endPos, float radius) {
        this(startPos, endPos, radius, false);
    }

    public Branches(Vec3d startPos, Vec3d endPos, float radius, boolean leaf) {
        this.startPos = startPos;
        this.radius = radius;
        this.endPos = endPos;
        this.leaf = leaf;
    }

    /**
     * Segment-capsule test. The old implementation kept a shared mutable Vec3d in this
     * object (template instances are reused across chunk generation and can race) and
     * excluded both end points, which left holes where branches connect.
     */
    public boolean contain(double x, double y, double z) {
        double abx = endPos.x - startPos.x;
        double aby = endPos.y - startPos.y;
        double abz = endPos.z - startPos.z;
        double abLenSq = abx * abx + aby * aby + abz * abz;
        double radiusSq = (double) radius * (double) radius;
        if (abLenSq <= 1.0E-9D) {
            double dx = x - startPos.x;
            double dy = y - startPos.y;
            double dz = z - startPos.z;
            return dx * dx + dy * dy + dz * dz <= radiusSq;
        }
        double apx = x - startPos.x;
        double apy = y - startPos.y;
        double apz = z - startPos.z;
        double t = (apx * abx + apy * aby + apz * abz) / abLenSq;
        if (t < 0.0D || t > 1.0D) {
            return false;
        }
        double dx = apx - abx * t;
        double dy = apy - aby * t;
        double dz = apz - abz * t;
        return dx * dx + dy * dy + dz * dz <= radiusSq;
    }

    public double getMinX() {
        return Math.min(startPos.x, endPos.x);
    }

    public double getMaxX() {
        return Math.max(startPos.x, endPos.x);
    }

    public double getMinY() {
        return Math.min(startPos.y, endPos.y);
    }

    public double getMaxY() {
        return Math.max(startPos.y, endPos.y);
    }

    public double getMinZ() {
        return Math.min(startPos.z, endPos.z);
    }

    public double getMaxZ() {
        return Math.max(startPos.z, endPos.z);
    }
}

