package com.xyazh.kanake.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Random;


public class Vec3d {
    public static final Vec3d ZERO = new Vec3d(0, 0, 0);
    public static final Vec3d ONE = new Vec3d(1, 1, 1);
    public static final Vec3d UP = new Vec3d(0, 1, 0);
    public static final Vec3d DOWN = new Vec3d(0, -1, 0);
    public static final Vec3d NORTH = new Vec3d(0, 0, -1);
    public static final Vec3d SOUTH = new Vec3d(0, 0, 1);
    public static final Vec3d WEST = new Vec3d(-1, 0, 0);
    public static final Vec3d EAST = new Vec3d(1, 0, 0);

    private static final Random rand = new Random();
    public double x;
    public double y;
    public double z;

    public static Vec3d random() {
        Vec3d vec3d = new Vec3d();
        vec3d.rand();
        return vec3d;
    }

    public static Vec3d random(double scale) {
        Vec3d vec3d = new Vec3d();
        vec3d.rand(scale);
        return vec3d;
    }

    public static Vec3d fromEntityPos(Entity entity) {
        return new Vec3d(entity.posX, entity.posY, entity.posZ);
    }


    public static Vec3d fromPitchYaw(float pitch, float yaw) {
        float radianYaw = -yaw * 0.017453292F - (float)Math.PI;
        float radianPitch = -pitch * 0.017453292F;
        float cosYaw = MathHelper.cos(radianYaw);
        float sinYaw = MathHelper.sin(radianYaw);
        float cosPitch = MathHelper.cos(radianPitch);
        float sinPitch = MathHelper.sin(radianPitch);
        Vec3d result = new Vec3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
        result.normalize();
        return result;
    }


    public static double delivery(Vec3d t1, Vec3d t2) {
        double dx, dy, dz;
        dx = t1.x - t2.x;
        dy = t1.y - t2.y;
        dz = t1.z - t2.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public Vec3d() {
    }

    public Vec3d(net.minecraft.util.math.Vec3d vec3d) {
        this.x = vec3d.x;
        this.y = vec3d.y;
        this.z = vec3d.z;
    }

    public Vec3d(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vec3d v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void set(net.minecraft.util.math.Vec3d v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void mul(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    public void sub(Vec3d t1, Vec3d t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
    }

    public void sub(Vec3d t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
    }

    public void sub(double x, double y, double z){
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void add(Vec3d t1, Vec3d t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
    }

    public void add(Vec3d t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
    }

    public void add(double x, double y, double z){
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public void normalize() {
        double l = length();
        if (l == 0) {
            return;
        }
        this.x = this.x / l;
        this.y = this.y / l;
        this.z = this.z / l;
    }

    public double normalizeAndLength() {
        double l = length();
        if (l == 0) {
            return l;
        }
        this.x = this.x / l;
        this.y = this.y / l;
        this.z = this.z / l;
        return l;
    }

    public void cross(Vec3d v1, Vec3d v2) {
        double tmpX;
        double tmpY;

        tmpX = v1.y * v2.z - v1.z * v2.y;
        tmpY = v2.x * v1.z - v2.z * v1.x;
        this.z = v1.x * v2.y - v1.y * v2.x;
        this.x = tmpX;
        this.y = tmpY;
    }

    public void cross(Vec3d v) {
        double tx, ty;
        tx = this.y * v.z - this.z * v.y;
        ty = this.z * v.x - this.x * v.z;
        this.z = this.x * v.y - this.y * v.x;
        this.x = tx;
        this.y = ty;
    }

    public double dot(Vec3d v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }

    public boolean isBetween(Vec3d a, Vec3d b) {
        Vec3d ab = new Vec3d();
        ab.sub(b, a);
        Vec3d av = this.copy();
        av.sub(a);
        double dotProduct = av.dot(ab);
        double abSquared = ab.dot(ab);
        return dotProduct > 0 && dotProduct < abSquared;
    }

    public double distanceToLine(Vec3d a, Vec3d b) {
        Vec3d ab = new Vec3d();
        ab.sub(b, a);
        Vec3d av = this.copy();
        av.sub(a);
        av.cross(ab);
        double abLength = ab.length();
        return av.length() / abLength;
    }

    public double distance(Vec3d v) {
        return Math.sqrt((v.x - this.x) * (v.x - this.x) + (v.y - this.y) * (v.y - this.y) + (v.z - this.z) * (v.z - this.z));
    }

    @Override
    public int hashCode() {
        long bits = 7L;
        bits = 31L * bits + Double.doubleToLongBits(x);
        bits = 31L * bits + Double.doubleToLongBits(y);
        bits = 31L * bits + Double.doubleToLongBits(z);
        return (int) (bits ^ (bits >> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vec3d) {
            Vec3d v = (Vec3d) obj;
            return (x == v.x) && (y == v.y) && (z == v.z);
        }
        return false;
    }

    public void rand() {
        this.set(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
        this.normalize();
    }

    public void rand(double scale) {
        this.rand();
        this.mul(scale);
    }

    public Vec3d copy() {
        return new Vec3d(x, y, z);
    }

    @Override
    public String toString() {
        return "Vec3d(" + x + ", " + y + ", " + z + ")";
    }
}