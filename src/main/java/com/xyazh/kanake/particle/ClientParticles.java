package com.xyazh.kanake.particle;

import com.xyazh.kanake.particle.particles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 客户端粒子工厂注册。
 * 单独放在 @SideOnly(Side.CLIENT) 类中，保证专用服务器永远不会加载
 * ParticleManager / 粒子工厂等客户端类型。
 */
@SideOnly(Side.CLIENT)
public final class ClientParticles {
    private ClientParticles() {
    }

    public static void register() {
        ParticleManager manager = Minecraft.getMinecraft().effectRenderer;
        manager.registerParticle(ModParticles.TEST_PARTICLES.getParticleID(), new TestParticle.Factory());
        manager.registerParticle(ModParticles.TEST_PARTICLES1.getParticleID(), new TestParticle1.Factory());
        manager.registerParticle(ModParticles.TEST_PARTICLES2.getParticleID(), new TestParticle2.Factory());
        manager.registerParticle(ModParticles.SPARK_PARTICLES.getParticleID(), new SparkParticle.Factory());
        manager.registerParticle(ModParticles.SPARK_PARTICLES1.getParticleID(), new SparkParticle1.Factory());
        manager.registerParticle(ModParticles.ICE_PARTICLES.getParticleID(), new IceParticle.Factory());
        manager.registerParticle(ModParticles.ICE_PARTICLES1.getParticleID(), new IceParticle1.Factory());
        manager.registerParticle(ModParticles.WANA_PARTICLES.getParticleID(), new WanaParticle.Factory());
        manager.registerParticle(ModParticles.WANA_PARTICLES1.getParticleID(), new WanaParticle1.Factory());
        manager.registerParticle(ModParticles.MANA_PARTICLES.getParticleID(), new ManaParticle.Factory());
        manager.registerParticle(ModParticles.MANA_PARTICLES1.getParticleID(), new ManaParticle1.Factory());
        manager.registerParticle(ModParticles.MAGIC_PARTICLES.getParticleID(), new MagicParticle.Factory());
        manager.registerParticle(ModParticles.MAGIC_PARTICLES1.getParticleID(), new MagicParticle1.Factory());
        manager.registerParticle(ModParticles.TP_PARTICLES1.getParticleID(), new TpParticle1.Factory());
        manager.registerParticle(ModParticles.KAKERA_PARTICLES.getParticleID(), new KakeraParticle.Factory());
        manager.registerParticle(ModParticles.BIIMU_PARTICLES.getParticleID(), new BiimuParticle.Factory());
        manager.registerParticle(ModParticles.HEAL_PARTICLES.getParticleID(), new HealParticles.HealParticlesFactory());
        manager.registerParticle(ModParticles.HONOO_PARTICLES.getParticleID(), new HorooParticle.Factory());
        manager.registerParticle(ModParticles.HEAL_BALL_PARTICLES.getParticleID(), new HealBallParticle.Factory());
    }
}
