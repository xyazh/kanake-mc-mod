package com.xyazh.kanake.particle;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.SpawnParticlesPacket;
import com.xyazh.kanake.particle.particles.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModParticles {
    private static final ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
    private static int next_id = -1;
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
            myParticles.put(particleTypes.getParticleID(),particleTypes);
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
        ModParticles.myParticles.put(id,particleTypes);
        return particleTypes;
    }

    public static void remoteSpawnParticle(World world,int n,EnumParticleTypes particleTypes,double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed){
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

    public static void remoteSpawnParticle(World world,int n,EnumParticleTypes particleTypes,double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed,int d){
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
