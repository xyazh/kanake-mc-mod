package com.xyazh.kanake.util;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.SpawnParticlesPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class ParticleUtil {
    public static void remoteSpawnParticle(World world,int count, String particleName, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        SpawnParticlesPacket packet = new SpawnParticlesPacket(count,particleName,(float) x,(float) y,(float) z,(float) xSpeed,(float) ySpeed,(float) zSpeed,parameters);
        List<EntityPlayerMP> players = world.getPlayers(EntityPlayerMP.class, player -> true);
        for(EntityPlayerMP player : players)
        {
            Kanake.network.sendTo(packet,player);
        }
    }

    public static void remoteSpawnParticleWithAABB(World world, AxisAlignedBB aabb,int count, String particleName, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters){
        SpawnParticlesPacket packet = new SpawnParticlesPacket(count,particleName,(float) x,(float) y,(float) z,(float) xSpeed,(float) ySpeed,(float) zSpeed,parameters);
        List<EntityPlayerMP> players = world.getPlayers(EntityPlayerMP.class, player -> aabb.contains(player.getPositionVector()));
        for(EntityPlayerMP player : players)
        {
            Kanake.network.sendTo(packet,player);
        }
    }
}
