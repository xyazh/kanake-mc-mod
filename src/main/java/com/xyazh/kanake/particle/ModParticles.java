package com.xyazh.kanake.particle;

import com.xyazh.kanake.Kanake;
import net.minecraft.util.EnumParticleTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * 双端安全的粒子注册表。
 * 这里只创建/登记 EnumParticleTypes 与名称映射，不引用任何客户端类
 * （ParticleManager / IParticleFactory 等由 ClientParticles 在客户端注册），
 * 这样专用服务器即使加载本类也不会崩溃。
 */
public class ModParticles {
    private static int next_id = -1;
    public static final Map<Integer, EnumParticleTypes> myParticlesInt = new HashMap<>();
    public static final Map<String, EnumParticleTypes> myParticlesString = new HashMap<>();

    public static final EnumParticleTypes TEST_PARTICLES = create("test_particle", true);
    public static final EnumParticleTypes TEST_PARTICLES1 = create("test1_particle", true);
    public static final EnumParticleTypes TEST_PARTICLES2 = create("test2_particle", true);
    public static final EnumParticleTypes SPARK_PARTICLES = create("spark_particle", true);
    public static final EnumParticleTypes SPARK_PARTICLES1 = create("spark1_particle", true);
    public static final EnumParticleTypes ICE_PARTICLES = create("ice_particle", true);
    public static final EnumParticleTypes ICE_PARTICLES1 = create("ice1_particle", true);
    public static final EnumParticleTypes WANA_PARTICLES = create("wana_particle", false);
    public static final EnumParticleTypes WANA_PARTICLES1 = create("wana1_particle", false);
    public static final EnumParticleTypes MANA_PARTICLES = create("mana_particle", false);
    public static final EnumParticleTypes MANA_PARTICLES1 = create("mana_particle1", false);
    public static final EnumParticleTypes MAGIC_PARTICLES = create("magic_particle", true);
    public static final EnumParticleTypes MAGIC_PARTICLES1 = create("magic1_particle", true);
    public static final EnumParticleTypes TP_PARTICLES1 = create("tp1_particle", false);
    public static final EnumParticleTypes KAKERA_PARTICLES = create("kakera_particle", true);
    public static final EnumParticleTypes BIIMU_PARTICLES = create("biimu_particle", true);
    public static final EnumParticleTypes HEAL_PARTICLES = create("heal_particle", false);
    public static final EnumParticleTypes HONOO_PARTICLES = create("honoo_particle", false);
    public static final EnumParticleTypes HEAL_BALL_PARTICLES = create("heal_ball_particle", false);

    public static int nextParticlesID() {
        if (next_id < 0) {
            for (EnumParticleTypes particleTypes : EnumParticleTypes.values()) {
                next_id = Math.max(particleTypes.getParticleID(), next_id);
            }
        }
        next_id += 1;
        return next_id;
    }

    public static void appendAllParticlesToMyMap() {
        for (EnumParticleTypes particleTypes : EnumParticleTypes.values()) {
            myParticlesInt.put(particleTypes.getParticleID(), particleTypes);
            myParticlesString.put(particleTypes.getParticleName(), particleTypes);
        }
    }

    public static EnumParticleTypes create(String name, boolean shouldIgnoreRangeIn) {
        int id = nextParticlesID();
        if (Kanake.logger != null) {
            Kanake.logger.info("Particles:{}|{}", id, name);
        }
        EnumParticleTypes particleTypes = EnumParticleTypesHelper.addParticleType(name, id, shouldIgnoreRangeIn);
        myParticlesInt.put(id, particleTypes);
        myParticlesString.put(name, particleTypes);
        return particleTypes;
    }
}
