package com.xyazh.kanake.world.tree;

import com.xyazh.kanake.util.Vec3d;

import java.util.*;

public class BigTreeGenerateTemplate {
    protected final float PI = 3.1415926f;
    protected final Random rand;
    protected Vec3d rootPos;
    protected final long seed;
    public LinkedList<Branches> branches = new LinkedList<>();
    protected static final LinkedHashMap<Long, BigTreeGenerateTemplate> TEMPLATES = new LinkedHashMap<Long, BigTreeGenerateTemplate>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, BigTreeGenerateTemplate> eldest) {
            return size() > 5;
        }
    };

    public static BigTreeGenerateTemplate getTemplate(long seed, Vec3d rootPos) {
        return TEMPLATES.computeIfAbsent(seed, k -> new BigTreeGenerateTemplate(seed, rootPos));
    }

    public BigTreeGenerateTemplate(long seed, Vec3d rootPos) {
        this.seed = seed;
        this.rand = new Random(seed);
        this.rootPos = rootPos;
        this.drawBranch(rootPos, 4, 96, -90, 0, 5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigTreeGenerateTemplate that = (BigTreeGenerateTemplate) o;
        return seed == that.seed && rootPos.equals(that.rootPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootPos, seed);
    }

    protected void drawBranch(Vec3d v0, float radius, float length, float pitch, float yaw, int maxDepth) {
        if (maxDepth <= 0) {
            return;
        }
        if (radius < 2 && this.rand.nextFloat() < 0.3) {
            return;
        }
        if (radius < 0.5) {
            return;
        }
        if (v0.y > 255) {
            return;
        }
        Vec3d v1 = Vec3d.fromPitchYaw(pitch, yaw);
        v1.mul(length);
        v1.add(v0);
        this.branches.add(new Branches(v0, v1, radius));
        int count = this.rand.nextInt(2) + 2;
        for (int i = 0; i < count; i++) {
            this.drawBranch(v1, radius * (0.7f + this.rand.nextFloat() / 10), length * (0.4f + this.rand.nextFloat() / 2), pitch - this.rand.nextFloat() * 45, this.rand.nextFloat() * 360, maxDepth - 1);
        }
    }

    public boolean contain(Vec3d v) {
        for (Branches b : this.branches) {
            if (b.contain(v)) {
                return true;
            }
        }
        return false;
    }
}
