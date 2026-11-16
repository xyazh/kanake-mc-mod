package com.xyazh.kanake.world.tree;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class BigTreeGenerateTemplate {
    protected final float PI = 3.1415926f;
    protected final Random rand;
    protected Vec3d rootPos;
    protected final long seed;
    public final HashMap<ChunkPos,HashSet<Branches>> chunks = new HashMap<>();
    private static final int MAX_CACHED_TEMPLATES = 2048;
    protected static final Map<Long, BigTreeGenerateTemplate> TEMPLATES = Collections.synchronizedMap(
            new LinkedHashMap<Long, BigTreeGenerateTemplate>(1024, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, BigTreeGenerateTemplate> eldest) {
                    return size() > MAX_CACHED_TEMPLATES;
                }
            });

    public static BigTreeGenerateTemplate getTemplate(long seed, Vec3d rootPos,float pitch,float yaw) {
        // The old cache used Objects.hash(...) (a 32-bit int) as the key, so different trees
        // could collide and silently share one template. Use a 64-bit key instead.
        long key = cacheKey(seed, rootPos);
        BigTreeGenerateTemplate template = TEMPLATES.get(key);
        if (template == null) {
            template = new BigTreeGenerateTemplate(seed, rootPos, pitch, yaw);
            TEMPLATES.put(key, template);
        }
        return template;
    }

    private static long cacheKey(long seed, Vec3d rootPos) {
        long h = seed;
        h = h * 0x9E3779B97F4A7C15L + Double.doubleToLongBits(rootPos.x);
        h = h * 0x9E3779B97F4A7C15L + Double.doubleToLongBits(rootPos.y);
        h = h * 0x9E3779B97F4A7C15L + Double.doubleToLongBits(rootPos.z);
        return h;
    }

    public BigTreeGenerateTemplate(long seed, Vec3d rootPos,float pitch,float yaw) {
        this.seed = seed;
        this.rand = new Random(seed);
        this.rootPos = rootPos;
        this.drawBranch(rootPos, 4, 108, pitch, yaw, 5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigTreeGenerateTemplate that = (BigTreeGenerateTemplate) o;
        return seed == that.seed && rootPos.equals(that.rootPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rootPos, seed);
    }

    protected void drawBranch(Vec3d v0, float radius, float length, float pitch, float yaw, int maxDepth) {
        if (maxDepth <= 0) {
            return;
        }
        if (radius < 2 && this.rand.nextFloat() < 0.3) {
            return;
        }
        if (radius < 0.5) {
            return;
        }
        if (v0.y > 255) {
            return;
        }
        // pitch below -90 would flip the branch and make it grow sideways/downwards.
        pitch = Math.max(-90.0F, Math.min(90.0F, pitch));
        radius = Math.min(radius, 8.0F);
        length = Math.min(length, 128.0F);
        Vec3d v1 = Vec3d.fromPitchYaw(pitch, yaw);
        v1.mul(length);
        v1.add(v0);
        // Clip the branch at the world height limit instead of writing outside the chunk.
        if (v1.y > 255.0D) {
            double dy = v1.y - v0.y;
            if (dy <= 0.0D) {
                return;
            }
            double t = (255.0D - v0.y) / dy;
            if (t <= 0.0D) {
                return;
            }
            v1.set(v0.x + (v1.x - v0.x) * t, 255.0D, v0.z + (v1.z - v0.z) * t);
        }
        Branches branches = new Branches(v0, v1, radius, maxDepth <= 2);
        this.calculateChunksForBranch(branches);
        int count = this.rand.nextInt(2) + 2;
        for (int i = 0; i < count; i++) {
            this.drawBranch(v1, radius * (0.7f + this.rand.nextFloat() / 10), length * (0.5f + this.rand.nextFloat() / 2.5f), pitch - this.rand.nextFloat() * 45, this.rand.nextFloat() * 360, maxDepth - 1);
        }
    }

    public boolean containChunk(int x, int z) {
        return this.chunks.containsKey(new ChunkPos(x, z));
    }

    public void calculateChunksForBranch(Branches branch) {
        Vec3d start = branch.startPos;
        Vec3d end = branch.endPos;
        double dx = end.x - start.x;
        double dz = end.z - start.z;
        // Walk along the segment instead of registering every chunk in its bounding box.
        // The old AABB version could register hundreds of unrelated chunks per branch.
        int radiusChunks = (int) Math.ceil(branch.radius / 16.0D) + 1;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dz)) * 2.0D));
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / (double) steps;
            int chunkX = (int) Math.floor(start.x + dx * t) >> 4;
            int chunkZ = (int) Math.floor(start.z + dz * t) >> 4;
            for (int ox = -radiusChunks; ox <= radiusChunks; ox++) {
                for (int oz = -radiusChunks; oz <= radiusChunks; oz++) {
                    this.addBranch(chunkX + ox, chunkZ + oz, branch);
                }
            }
        }
    }

    private void addBranch(int chunkX, int chunkZ, Branches branch) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        HashSet<Branches> branches = this.chunks.get(pos);
        if (branches == null) {
            branches = new HashSet<>();
            this.chunks.put(pos, branches);
        }
        branches.add(branch);
    }

    public boolean contain(double x,double y,double z) {
        HashSet<Branches> branches = this.chunks.get(new ChunkPos((int) Math.floor(x) >> 4, (int) Math.floor(z) >> 4));
        if(branches == null){
            return false;
        }
        for (Branches b : branches) {
            if (b.contain(x,y,z)) {
                return true;
            }
        }
        return false;
    }
}
