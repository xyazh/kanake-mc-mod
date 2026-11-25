package com.xyazh.kanake.particle.simple.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Minecraft's vanilla smoke particle, tinted for nuclear fallout. It keeps the
 * vanilla smoke texture, short puffy lifetime and rising motion. The mushroom
 * cloud is made from many of these small puffs instead of a few huge quads.
 */
@SideOnly(Side.CLIENT)
public class NuclearSmokeParticle extends ParticleSmokeNormal {
    public NuclearSmokeParticle(World worldIn, double x, double y, double z,
                                double motionX, double motionY, double motionZ,
                                float scale, int lifetime,
                                float red, float green, float blue) {
        super(worldIn, x, y, z, motionX, motionY, motionZ,
                Math.max(0.10F, scale));
        this.setRBGColorF(
                Math.max(0.0F, Math.min(1.0F, red)),
                Math.max(0.0F, Math.min(1.0F, green)),
                Math.max(0.0F, Math.min(1.0F, blue)));
        this.setMaxAge(Math.max(4, lifetime));
        this.canCollide = false;
    }

    /**
     * Adds a tinted vanilla smoke particle to Minecraft's normal particle
     * manager. These particles keep the vanilla smoke texture and are rendered
     * by ParticleManager rather than the mod's procedural particle buffer.
     */
    public static void spawn(World worldIn, double x, double y, double z,
                             double motionX, double motionY, double motionZ,
                             float scale, int lifetime,
                             float red, float green, float blue) {
        if (worldIn == null || !worldIn.isRemote) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.effectRenderer != null) {
            minecraft.effectRenderer.addEffect(new NuclearSmokeParticle(
                    worldIn, x, y, z, motionX, motionY, motionZ,
                    scale, lifetime, red, green, blue));
        }
    }
}
