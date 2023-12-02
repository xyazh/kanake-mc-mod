package com.xyazh.kanake.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WSHelper {
    public static void rotateTowardsMovement(Entity projectile, float rotationSpeed)
    {
        double d0 = projectile.motionX;
        double d1 = projectile.motionY;
        double d2 = projectile.motionZ;
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
        projectile.rotationYaw = (float)(MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;

        for (projectile.rotationPitch = (float)(MathHelper.atan2((double)f, d1) * (180D / Math.PI)) - 90.0F; projectile.rotationPitch - projectile.prevRotationPitch < -180.0F; projectile.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (projectile.rotationPitch - projectile.prevRotationPitch >= 180.0F)
        {
            projectile.prevRotationPitch += 360.0F;
        }

        while (projectile.rotationYaw - projectile.prevRotationYaw < -180.0F)
        {
            projectile.prevRotationYaw -= 360.0F;
        }

        while (projectile.rotationYaw - projectile.prevRotationYaw >= 180.0F)
        {
            projectile.prevRotationYaw += 360.0F;
        }

        projectile.rotationPitch = projectile.prevRotationPitch + (projectile.rotationPitch - projectile.prevRotationPitch) * rotationSpeed;
        projectile.rotationYaw = projectile.prevRotationYaw + (projectile.rotationYaw - projectile.prevRotationYaw) * rotationSpeed;
    }
}
