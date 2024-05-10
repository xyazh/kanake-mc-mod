package com.xyazh.kanake.particle;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.particle.particles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.EnumParticleTypes;

import java.util.HashMap;
import java.util.Map;

public class ModParticles {
    private static final ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
    private static int next_id = -1;
    private static final Map<Integer, IParticleFactory> idParticles = new HashMap<>();
    public static final Map<Integer, EnumParticleTypes> myParticlesInt = new HashMap<>();
    public static final Map<String, EnumParticleTypes> myParticlesString = new HashMap<>();

    public static final EnumParticleTypes TEST_PARTICLES = register(new TestParticle.Factory(), "test_particle", true);
    public static final EnumParticleTypes TEST_PARTICLES1 = register(new TestParticle1.Factory(), "test1_particle", true);
    public static final EnumParticleTypes TEST_PARTICLES2 = register(new TestParticle2.Factory(), "test2_particle", true);
    public static final EnumParticleTypes SPARK_PARTICLES = register(new SparkParticle.Factory(), "spark_particle", true);
    public static final EnumParticleTypes SPARK_PARTICLES1 = register(new SparkParticle1.Factory(), "spark1_particle", true);
    public static final EnumParticleTypes ICE_PARTICLES = register(new IceParticle.Factory(), "ice_particle", true);
    public static final EnumParticleTypes ICE_PARTICLES1 = register(new IceParticle1.Factory(), "ice1_particle", true);
    public static final EnumParticleTypes WANA_PARTICLES = register(new WanaParticle.Factory(), "wana_particle", false);
    public static final EnumParticleTypes WANA_PARTICLES1 = register(new WanaParticle1.Factory(), "wana1_particle", false);
    public static final EnumParticleTypes MANA_PARTICLES = register(new ManaParticle.Factory(), "mana_particle", false);
    public static final EnumParticleTypes MANA_PARTICLES1 = register(new ManaParticle1.Factory(), "mana_particle1", false);
    public static final EnumParticleTypes MAGIC_PARTICLES = register(new MagicParticle.Factory(), "magic_particle", true);
    public static final EnumParticleTypes MAGIC_PARTICLES1 = register(new MagicParticle1.Factory(), "magic1_particle", true);
    public static final EnumParticleTypes TP_PARTICLES1 = register(new TpParticle1.Factory(), "tp1_particle", false);
    public static final EnumParticleTypes KAKERA_PARTICLES = register(new KakeraParticle.Factory(), "kakera_particle", true);
    public static final EnumParticleTypes BIIMU_PARTICLES = register(new BiimuParticle.Factory(), "biimu_particle", true);
    public static final EnumParticleTypes HEAL_PARTICLES = register(new HealParticles.HealParticlesFactory(),"heal_particle", false);
    public static final EnumParticleTypes HONOO_PARTICLES = register(new HorooParticle.Factory(),"heal_particle", false);

    public static int nextParticlesID() {
        if(next_id < 0){
            for(EnumParticleTypes particleTypes:EnumParticleTypes.values()){
                next_id = Math.max(particleTypes.getParticleID(),next_id);
            }
        }
        next_id += 1;
        return next_id;
    }

    public static void appendAllParticlesToMyMap(){
        for(EnumParticleTypes particleTypes:EnumParticleTypes.values()){
            myParticlesInt.put(particleTypes.getParticleID(),particleTypes);
            myParticlesString.put(particleTypes.getParticleName(),particleTypes);
        }
    }

    public static void registerParticles() {
        for (int key : idParticles.keySet()) {
            IParticleFactory vaule = idParticles.get(key);
            particleManager.registerParticle(key, vaule);
        }
    }

    public static EnumParticleTypes register(IParticleFactory particleFactory, String name, boolean shouldIgnoreRangeIn) {
        int id = nextParticlesID();
        idParticles.put(id, particleFactory);
        Kanake.logger.info("Particles:{}|{}", id, name);
        EnumParticleTypes particleTypes = EnumParticleTypesHelper.addParticleType(name, id, shouldIgnoreRangeIn);
        ModParticles.myParticlesInt.put(id,particleTypes);
        ModParticles.myParticlesString.put(name,particleTypes);
        return particleTypes;
    }}
