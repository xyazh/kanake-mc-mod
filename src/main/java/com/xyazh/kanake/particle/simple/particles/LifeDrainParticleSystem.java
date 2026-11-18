package com.xyazh.kanake.particle.simple.particles;

import com.xyazh.kanake.entity.EntityLifeDrain;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Client-side particle population manager for each life-drain focus.
 * It refills the orbiting mote cloud and the moving target streams as their
 * short-lived particles expire.
 */
@SideOnly(Side.CLIENT)
public final class LifeDrainParticleSystem {
    private static final int ORB_MOTE_COUNT = 30;
    private static final int STREAM_MOTE_COUNT = 18;
    private static final Map<Integer, EffectState> EFFECTS = new HashMap<Integer, EffectState>();

    private LifeDrainParticleSystem() {
    }

    public static void update(EntityLifeDrain source) {
        if (source == null || source.world == null || !source.world.isRemote || source.isDead) {
            if (source != null) {
                EFFECTS.remove(source.getEntityId());
            }
            return;
        }

        int sourceId = source.getEntityId();
        EffectState state = EFFECTS.get(sourceId);
        if (state == null) {
            state = new EffectState();
            EFFECTS.put(sourceId, state);
        }

        state.motes.removeIf(particle -> !particle.isAlive());
        while (state.motes.size() < ORB_MOTE_COUNT) {
            state.motes.add(new LifeDrainMoteParticle(source));
        }

        EntityLivingBase owner = source.getOwner();
        if (owner == null || !owner.isEntityAlive()) {
            clearStreams(state);
            return;
        }

        AxisAlignedBB searchBox = owner.getEntityBoundingBox().grow(EntityLifeDrain.RADIUS);
        List<EntityLivingBase> targets = source.world.getEntitiesWithinAABB(EntityLivingBase.class, searchBox);
        Set<Integer> activeTargets = new HashSet<Integer>();
        for (EntityLivingBase target : targets) {
            if (!EntityLifeDrain.isValidTarget(target, owner)
                    || target.getDistanceSq(owner) > EntityLifeDrain.RADIUS * EntityLifeDrain.RADIUS) {
                continue;
            }

            int targetId = target.getEntityId();
            activeTargets.add(targetId);
            List<LifeDrainStreamParticle> stream = state.streams.get(targetId);
            if (stream == null) {
                stream = new ArrayList<LifeDrainStreamParticle>();
                state.streams.put(targetId, stream);
            }
            stream.removeIf(particle -> !particle.isAlive());
            while (stream.size() < STREAM_MOTE_COUNT) {
                stream.add(new LifeDrainStreamParticle(source, target));
            }
        }

        Iterator<Map.Entry<Integer, List<LifeDrainStreamParticle>>> iterator = state.streams.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<LifeDrainStreamParticle>> entry = iterator.next();
            if (!activeTargets.contains(entry.getKey())) {
                for (LifeDrainStreamParticle particle : entry.getValue()) {
                    particle.setExpired();
                }
                iterator.remove();
            }
        }
    }

    private static void clearStreams(EffectState state) {
        for (List<LifeDrainStreamParticle> stream : state.streams.values()) {
            for (LifeDrainStreamParticle particle : stream) {
                particle.setExpired();
            }
        }
        state.streams.clear();
    }

    private static final class EffectState {
        private final List<LifeDrainMoteParticle> motes = new ArrayList<LifeDrainMoteParticle>();
        private final Map<Integer, List<LifeDrainStreamParticle>> streams =
                new HashMap<Integer, List<LifeDrainStreamParticle>>();
    }
}
