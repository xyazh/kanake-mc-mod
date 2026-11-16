package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.particle.simple.SimpleParticle;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GlyphAuraParticle extends SimpleParticle {

    public GlyphAuraParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                             float red, float green, float blue, float size) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        this.canCollide = false;
        this.disLight = true;
        this.setRBGColorF(red, green, blue);
        this.particleMaxAge = 12 + this.rand.nextInt(10);
        this.particleScale = size;
        Vec3d motion = new Vec3d(this.rand.nextDouble() - 0.5, this.rand.nextDouble() - 0.5, this.rand.nextDouble() - 0.5);
        motion.normalize();
        motion.mul(0.04 + this.rand.nextDouble() * 0.04);
        this.motionX = motion.x;
        this.motionY = motion.y + 0.01;
        this.motionZ = motion.z;
        this.setPosition(
                xCoordIn + (this.rand.nextDouble() - 0.5) * 0.5,
                yCoordIn + (this.rand.nextDouble() - 0.5) * 0.5,
                zCoordIn + (this.rand.nextDouble() - 0.5) * 0.5
        );
    }

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
            return;
        }
        this.particleScale *= 0.96f;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionX *= 0.92;
        this.motionY *= 0.92;
        this.motionZ *= 0.92;
        this.move(this.motionX, this.motionY, this.motionZ);
    }
}
