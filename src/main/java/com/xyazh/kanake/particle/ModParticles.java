package com.xyazh.kanake.particle;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.SpawnParticlesPacket;
import com.xyazh.kanake.particle.particle.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ModParticles {
    private static final ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
    private static int next_id = new Random().nextInt(90000) + 10000;
    private static final Map<Integer, IParticleFactory> idParticles = new HashMap<>();
    public static final Map<Integer, EnumParticleTypes> myParticles = new HashMap<>();

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

    public static int nextPerticlesID() {
        while (true) {
            try {
                Particle particle = particleManager.spawnEffectParticle(next_id, 0, 0, 0, 0, 0, 0);
                if (particle == null) {
                    particleManager.clearEffects(null);
                    return next_id;
                }
                particle.setMaxAge(0);
                particle.setExpired();
            } catch (Exception ignored) {

            } finally {
                next_id += 1;
            }
        }
    }

    public static void registerParticles() {
        for (int key : idParticles.keySet()) {
            IParticleFactory vaule = idParticles.get(key);
            particleManager.registerParticle(key, vaule);
        }
    }

    public static EnumParticleTypes register(IParticleFactory particleFactory, String name, boolean shouldIgnoreRangeIn) {
        int id = nextPerticlesID();
        idParticles.put(id, particleFactory);
        Kanake.logger.info("Perticles:{}|{}", id, name);
        EnumParticleTypes particleTypes = EnumParticleTypesHelper.addParticleType(name, id, shouldIgnoreRangeIn);
        ModParticles.myParticles.put(id,particleTypes);
        return particleTypes;
    }

    public static void remoteSpawnParticle(World world,EnumParticleTypes particleTypes,double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed,int n){
        SpawnParticlesPacket spawnParticlesPacket = new SpawnParticlesPacket();
        spawnParticlesPacket.id = particleTypes.getParticleID();
        spawnParticlesPacket.x = xCoord;
        spawnParticlesPacket.y = yCoord;
        spawnParticlesPacket.z = zCoord;
        spawnParticlesPacket.speedX = xSpeed;
        spawnParticlesPacket.speedY = ySpeed;
        spawnParticlesPacket.speedZ = zSpeed;
        spawnParticlesPacket.n = n;
        AxisAlignedBB aabb = new AxisAlignedBB(xCoord+32,yCoord+32,zCoord+32,xCoord-32,yCoord-32,zCoord-32);
        List<EntityPlayerMP> playerMPS = world.getPlayers(EntityPlayerMP.class,(EntityPlayerMP e)-> aabb.contains(new Vec3d(e.posX,e.posY,e.posZ)));
        for(EntityPlayerMP entityPlayerMP:playerMPS){
            Kanake.network.sendTo(spawnParticlesPacket,entityPlayerMP);
        }
    }

    public static void remoteSpawnParticle(World world,EnumParticleTypes particleTypes,double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed,int n,int d){
        SpawnParticlesPacket spawnParticlesPacket = new SpawnParticlesPacket();
        spawnParticlesPacket.id = particleTypes.getParticleID();
        spawnParticlesPacket.x = xCoord;
        spawnParticlesPacket.y = yCoord;
        spawnParticlesPacket.z = zCoord;
        spawnParticlesPacket.speedX = xSpeed;
        spawnParticlesPacket.speedY = ySpeed;
        spawnParticlesPacket.speedZ = zSpeed;
        spawnParticlesPacket.n = n;
        spawnParticlesPacket.txId = d;
        AxisAlignedBB aabb = new AxisAlignedBB(xCoord+32,yCoord+32,zCoord+32,xCoord-32,yCoord-32,zCoord-32);
        List<EntityPlayerMP> playerMPS = world.getPlayers(EntityPlayerMP.class,(EntityPlayerMP e)-> aabb.contains(new Vec3d(e.posX,e.posY,e.posZ)));
        for(EntityPlayerMP entityPlayerMP:playerMPS){
            Kanake.network.sendTo(spawnParticlesPacket,entityPlayerMP);
        }
    }
}
