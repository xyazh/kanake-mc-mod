package com.xyazh.kanake.particle;

import com.xyazh.kanake.util.Vec3d;

public class ParticleTempData {
    public static final Vec3d[] AVE_3D = new Vec3d[] {
            new Vec3d(0,0,0),
            new Vec3d(0,0,0),
            new Vec3d(0,0,0),
            new Vec3d(0,0,0)
    };

    public static void rotateVector(Vec3d vec, float angle, Vec3d axis) {
        double cosTheta = Math.cos(Math.toRadians(angle));
        double sinTheta = Math.sin(Math.toRadians(angle));
        double x = (cosTheta + (1 - cosTheta) * axis.x * axis.x) * vec.x +
                ((1 - cosTheta) * axis.x * axis.y - sinTheta * axis.z) * vec.y +
                ((1 - cosTheta) * axis.x * axis.z + sinTheta * axis.y) * vec.z;
        double y = ((1 - cosTheta) * axis.x * axis.y + sinTheta * axis.z) * vec.x +
                (cosTheta + (1 - cosTheta) * axis.y * axis.y) * vec.y +
                ((1 - cosTheta) * axis.y * axis.z - sinTheta * axis.x) * vec.z;
        double z = ((1 - cosTheta) * axis.x * axis.z - sinTheta * axis.y) * vec.x +
                ((1 - cosTheta) * axis.y * axis.z + sinTheta * axis.x) * vec.y +
                (cosTheta + (1 - cosTheta) * axis.z * axis.z) * vec.z;
        vec.set(x,y,z);
    }

}
