package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.particle.simple.particles.ExplosionFlashParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import com.xyazh.kanake.particle.simple.particles.SuperBlackHoleArrayParticle;
import com.xyazh.kanake.util.KillUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Super-tier spell "Black Hole" (超位魔法——黑洞).
 *
 * <p>The entity itself is invisible. {@link SuperBlackHoleArrayParticle} and
 * the matching render helper own every visible piece: the ground array, the
 * feeding beam and the dark event horizon with a bright rim. The class below
 * only owns the
 * timeline and the server-side world interaction.</p>
 *
 * <p>Timeline (20 ticks per second):</p>
 * <ul>
 *     <li>0-80: the ground array is constructed.</li>
 *     <li>20-240: the event horizon appears above the array and grows at a
 *     constant rate until its radius is exactly 64 blocks.</li>
 *     <li>240-1440: the event horizon is held at maximum size for sixty
 *     seconds.</li>
 *     <li>1440+: the event horizon shrinks at a constant speed. Swallowing
 *     blocks or entities no longer pauses the shrinkage.</li>
 *     <li>When the radius reaches zero the array flares out and the entity is
 *     removed shortly afterwards.</li>
 * </ul>
 *
 * <p>Blocks are removed from the centre of the sphere outwards. The full
 * sphere is split into one-block-thick radial buckets, so blocks are only
 * destroyed when the horizon has reached them; the server work is spread over
 * many ticks instead of creating one large lag spike.</p>
 */
public class EntitySuperBlackHole extends EntityShoot {
    /** Final radius of the event horizon. */
    public static final double MAX_RADIUS = 64.0D;
    /** Radius of the ground array. */
    public static final double ARRAY_RADIUS = 72.0D;
    /** Vertical offset of the visible array above the cast point. */
    public static final double ARRAY_FLOAT_HEIGHT = 1.0D;
    /** Height of the event horizon centre above the cast point. */
    public static final double CENTER_HEIGHT = 32.0D;

    public static final int ARRAY_BUILD_TICKS = 80;
    public static final int FORM_START_TICK = 20;
    public static final int FORM_END_TICK = 240;
    /** Hold the fully formed 64-block horizon for sixty seconds. */
    public static final int HOLD_END_TICK = FORM_END_TICK + 20 * 60;
    public static final double SHRINK_PER_TICK = 0.16D;
    public static final int SHRINK_TICKS = (int) Math.ceil(MAX_RADIUS / SHRINK_PER_TICK);
    /** Client-side deterministic completion tick; the server uses the same timeline. */
    public static final int ESTIMATED_FINISH_TICK = HOLD_END_TICK + SHRINK_TICKS;
    public static final int ARRAY_DISSIPATE_TICKS = 40;
    public static final int FINISH_FADE_TICKS = 40;
    /** Safety limit only; normal completion is driven by the shrinking radius. */
    public static final int MAX_LIFE_TICKS = 20 * 240;

    /** Radius of the square of chunks kept loaded around the cast point. */
    private static final int CHUNK_TICKET_RADIUS = 6;
    /**
     * Removed blocks per tick. The sixty-second hold is long enough for a
     * smaller budget, which keeps block-update packets and chunk rebuilds from
     * causing visible hitching on the client.
     */
    private static final int DESTROY_BLOCK_BUDGET = 2048;
    private static final int DESTROY_WORK_LIMIT = 8192;
    private static final int[] BUCKET_BY_DISTANCE_SQ = new int[4097];

    static {
        for (int bucket = 0; bucket <= 64; ++bucket) {
            int start = bucket == 0 ? 0 : (bucket - 1) * (bucket - 1) + 1;
            int end = bucket * bucket;
            for (int distanceSq = start; distanceSq <= end; ++distanceSq) {
                BUCKET_BY_DISTANCE_SQ[distanceSq] = bucket;
            }
        }
    }

    private final List<ForgeChunkManager.Ticket> chunkTickets =
            new ArrayList<ForgeChunkManager.Ticket>();
    private final Map<Long, List<BlockPos>> pendingChanges =
            new HashMap<Long, List<BlockPos>>();

    private boolean initialChunkLoadRequested;
    private boolean bucketsBuilt;
    /** One array of packed local block offsets per integer radial distance. */
    private long[][] radialBuckets;
    private int[] radialCursor;
    private int currentBucket;
    private static final int INITIAL_AGE_PACKET = 100;
    private int spellAge;
    private int finishTick = -1;
    private double eventRadius;
    /** Client-only offset from the one-time start-tracking age sync. */
    private int ageOffset;
    @SideOnly(Side.CLIENT)
    private boolean arrayParticleSpawned;
    @SideOnly(Side.CLIENT)
    private boolean finishFlashSpawned;
    private boolean finishSoundPlayed;

    public EntitySuperBlackHole(World worldIn) {
        super(worldIn);
        this.setSize(0.2F, 0.2F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = MAX_LIFE_TICKS;
    }

    @Override
    public void entityInit() {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected boolean customMotion() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    public static double getHoleCenterY(double baseY) {
        return baseY + CENTER_HEIGHT;
    }

    /**
     * Interpolates the event horizon radius while it is growing. The server
     * uses the real radius for block removal; this helper is mainly useful to
     * client code before the first metadata packet arrives.
     */
    public static double getGrowthRadiusAt(double age) {
        double t = clamp((age - FORM_START_TICK)
                / (double) (FORM_END_TICK - FORM_START_TICK));
        return MAX_RADIUS * t;
    }

    /**
     * Deterministic visual radius for clients. Both sides use the same
     * timeline, so the radius is never sent over the network.
     */
    public static double getRadiusAtAge(double age) {
        if (age <= FORM_START_TICK) {
            return 0.0D;
        }
        if (age <= FORM_END_TICK) {
            return getGrowthRadiusAt(age);
        }
        if (age <= HOLD_END_TICK) {
            return MAX_RADIUS;
        }
        if (age <= ESTIMATED_FINISH_TICK) {
            return Math.max(0.0D,
                    MAX_RADIUS - (age - HOLD_END_TICK) * SHRINK_PER_TICK);
        }
        return 0.0D;
    }

    public int getSpellAge() {
        return this.world != null && this.world.isRemote
                ? this.ticksExisted + this.ageOffset : this.spellAge;
    }

    /**
     * Send the current server age once when a player starts tracking. Visual
     * size, temperature and animation remain entirely client-side afterwards.
     */
    public void syncInitialAge(EntityPlayerMP player) {
        if (this.world.isRemote || player == null) {
            return;
        }
        try {
            EntityDataPacket packet = EntityDataPacket.getPacket(this);
            if (packet == null) {
                return;
            }
            packet.buffer.writeInt(INITIAL_AGE_PACKET);
            packet.buffer.writeInt(this.spellAge);
            Kanake.network.sendTo(packet, player);
        } catch (Throwable ignored) {
            // A disconnected player no longer needs the visual phase.
        }
    }

    @Override
    public int readData(ByteBuf buf) {
        int type = buf.readInt();
        if (type == INITIAL_AGE_PACKET) {
            this.ageOffset = buf.readInt() - this.ticksExisted;
            return type;
        }
        return readData(type, buf);
    }

    public double getEventRadius() {
        if (this.world != null && this.world.isRemote) {
            return getRadiusAtAge(this.getSpellAge());
        }
        return this.eventRadius;
    }

    public int getFinishTick() {
        if (this.world != null && this.world.isRemote) {
            return this.getSpellAge() >= ESTIMATED_FINISH_TICK
                    ? ESTIMATED_FINISH_TICK : -1;
        }
        return this.finishTick;
    }

    public boolean isFinishing() {
        return this.getFinishTick() >= 0;
    }

    public void prepareInitialChunkLoad() {
        if (this.world.isRemote || this.initialChunkLoadRequested) {
            return;
        }
        this.initialChunkLoadRequested = true;
        NuclearStrikeChunkLoader.forceSquare(this.world, this.posX, this.posZ,
                CHUNK_TICKET_RADIUS, this.chunkTickets);
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("superBlackHoleAge", this.spellAge);
        compound.setDouble("superBlackHoleRadius", this.eventRadius);
        compound.setInteger("superBlackHoleFinishTick", this.finishTick);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.spellAge = compound.getInteger("superBlackHoleAge");
        this.eventRadius = compound.getDouble("superBlackHoleRadius");
        this.finishTick = compound.hasKey("superBlackHoleFinishTick")
                ? compound.getInteger("superBlackHoleFinishTick") : -1;
        this.initialChunkLoadRequested = false;
        this.bucketsBuilt = false;
        this.radialBuckets = null;
        this.radialCursor = null;
        this.currentBucket = 0;
        this.pendingChanges.clear();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead || this.world.isRemote) {
            return;
        }

        ++this.spellAge;
        if (!this.initialChunkLoadRequested) {
            this.prepareInitialChunkLoad();
        }
        if (!this.bucketsBuilt && this.spellAge >= FORM_START_TICK) {
            this.buildRadialBuckets();
        }

        if (this.finishTick >= 0) {
            if (this.spellAge >= this.finishTick + FINISH_FADE_TICKS) {
                this.setDead();
            }
            return;
        }

        if (this.spellAge <= FORM_START_TICK) {
            this.eventRadius = 0.0D;
        } else if (this.spellAge <= FORM_END_TICK) {
            this.eventRadius = getGrowthRadiusAt(this.spellAge);
        } else if (this.spellAge <= HOLD_END_TICK) {
            this.eventRadius = MAX_RADIUS;
        }

        if (this.eventRadius > 0.01D) {
            this.absorbBlocks();
            this.pullEntities();
        }

        if (this.spellAge > HOLD_END_TICK && this.eventRadius > 0.0D) {
            // The horizon now shrinks at a constant speed. Being fed blocks or
            // entities does not slow it down.
            this.eventRadius = Math.max(0.0D, this.eventRadius - SHRINK_PER_TICK);
        }

        if (this.eventRadius <= 0.04D && this.spellAge > HOLD_END_TICK) {
            this.finish();
        }

        if (this.spellAge >= MAX_LIFE_TICKS) {
            this.setDead();
        }
    }

    private void finish() {
        if (this.finishTick >= 0) {
            return;
        }
        this.eventRadius = 0.0D;
        this.finishTick = this.spellAge;
        this.flushBlockChanges();
        if (!this.finishSoundPlayed) {
            this.finishSoundPlayed = true;
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_ENDERMEN_DEATH, SoundCategory.HOSTILE,
                    8.0F, 0.55F);
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE,
                    6.0F, 0.42F);
        }
    }

    /**
     * Splits the complete 64-block sphere into radial buckets. Packing local
     * offsets into a long keeps the whole structure at roughly 9 MB instead of
     * creating millions of short-lived BlockPos objects.
     */
    private void buildRadialBuckets() {
        if (this.bucketsBuilt) {
            return;
        }
        this.bucketsBuilt = true;
        int centerX = MathHelper.floor(this.posX);
        int centerY = MathHelper.floor(getHoleCenterY(this.posY));
        int centerZ = MathHelper.floor(this.posZ);
        int maxRadius = MathHelper.floor(MAX_RADIUS);
        double maxRadiusSq = MAX_RADIUS * MAX_RADIUS;
        int[] counts = new int[maxRadius + 1];

        for (int dx = -maxRadius; dx <= maxRadius; ++dx) {
            for (int dy = -maxRadius; dy <= maxRadius; ++dy) {
                for (int dz = -maxRadius; dz <= maxRadius; ++dz) {
                    double distanceSq = dx * dx + dy * dy + dz * dz;
                    if (distanceSq <= maxRadiusSq) {
                        ++counts[bucketForDistanceSq(distanceSq)];
                    }
                }
            }
        }

        this.radialBuckets = new long[maxRadius + 1][];
        this.radialCursor = new int[maxRadius + 1];
        for (int i = 0; i <= maxRadius; ++i) {
            this.radialBuckets[i] = new long[counts[i]];
        }

        int[] fill = new int[maxRadius + 1];
        for (int dx = -maxRadius; dx <= maxRadius; ++dx) {
            for (int dy = -maxRadius; dy <= maxRadius; ++dy) {
                for (int dz = -maxRadius; dz <= maxRadius; ++dz) {
                    double distanceSq = dx * dx + dy * dy + dz * dz;
                    if (distanceSq > maxRadiusSq) {
                        continue;
                    }
                    int bucket = bucketForDistanceSq(distanceSq);
                    long packed = ((long) (dx + 64) << 16)
                            | ((long) (dy + 64) << 8)
                            | (long) (dz + 64);
                    this.radialBuckets[bucket][fill[bucket]++] = packed;
                }
            }
        }
        this.currentBucket = 0;
        // Keep the base coordinates for decoding. They are stable because the
        // ritual entity never moves.
        this.getEntityData().setInteger("superHoleCenterX", centerX);
        this.getEntityData().setInteger("superHoleCenterY", centerY);
        this.getEntityData().setInteger("superHoleCenterZ", centerZ);
    }

    private static int bucketForDistanceSq(double distanceSq) {
        int index = (int) distanceSq;
        if (index < 0) {
            return 0;
        }
        if (index >= BUCKET_BY_DISTANCE_SQ.length) {
            index = BUCKET_BY_DISTANCE_SQ.length - 1;
        }
        return BUCKET_BY_DISTANCE_SQ[index];
    }

    private void absorbBlocks() {
        if (!this.bucketsBuilt || this.radialBuckets == null) {
            return;
        }
        int maxBucket = Math.min(this.radialBuckets.length - 1,
                (int) Math.floor(this.eventRadius + 0.0001D));
        int budget = DESTROY_BLOCK_BUDGET;
        int work = 0;
        int centerX = this.getEntityData().getInteger("superHoleCenterX");
        int centerY = this.getEntityData().getInteger("superHoleCenterY");
        int centerZ = this.getEntityData().getInteger("superHoleCenterZ");
        int worldHeight = this.world.getActualHeight();

        while (budget > 0 && work < DESTROY_WORK_LIMIT
                && this.currentBucket <= maxBucket
                && this.currentBucket < this.radialBuckets.length) {
            long[] bucket = this.radialBuckets[this.currentBucket];
            int cursor = this.radialCursor[this.currentBucket];
            if (bucket == null || cursor >= bucket.length) {
                ++this.currentBucket;
                continue;
            }

            long packed = bucket[cursor++];
            this.radialCursor[this.currentBucket] = cursor;
            ++work;
            if (cursor >= bucket.length) {
                ++this.currentBucket;
            }

            int x = centerX + ((int) (packed >> 16) & 0xFF) - 64;
            int y = centerY + ((int) (packed >> 8) & 0xFF) - 64;
            int z = centerZ + ((int) packed & 0xFF) - 64;
            if (y < 0 || y >= worldHeight) {
                continue;
            }
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();
            if (block.isAir(state, this.world, pos)) {
                continue;
            }
            if (state.getBlockHardness(this.world, pos) < 0.0F) {
                // Bedrock, barriers and other unbreakable blocks survive.
                continue;
            }
            if (block.hasTileEntity(state)) {
                this.world.removeTileEntity(pos);
            }
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
            this.queueBlockChange(pos);
            --budget;
        }

        this.flushBlockChanges();
    }

    private void pullEntities() {
        double centerY = getHoleCenterY(this.posY);
        double pullRange = Math.max(40.0D, this.eventRadius * 2.2D);
        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - pullRange, centerY - pullRange, this.posZ - pullRange,
                this.posX + pullRange, centerY + pullRange, this.posZ + pullRange);
        List<Entity> targets = this.world.getEntitiesWithinAABB(Entity.class, area);
        for (Entity target : targets) {
            if (target == this || target == this.shootingEntity || target.isDead) {
                continue;
            }
            if (!isValidTarget(target)) {
                continue;
            }
            double dx = this.posX - target.posX;
            double dy = centerY - (target.posY + target.height * 0.5D);
            double dz = this.posZ - target.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;
            double distance = Math.sqrt(distanceSq);
            if (distance <= this.eventRadius + target.width * 0.5D) {
                this.swallow(target);
                continue;
            }
            if (distance < 0.0001D) {
                this.swallow(target);
                continue;
            }
            // Inverse-square acceleration, clamped so distant entities are
            // gently pulled instead of being teleported into the hole.
            double acceleration = Math.min(0.42D,
                    0.62D * this.eventRadius * this.eventRadius
                            / Math.max(16.0D, distanceSq));
            target.motionX += dx / distance * acceleration;
            target.motionY += dy / distance * acceleration;
            target.motionZ += dz / distance * acceleration;
            target.velocityChanged = true;
        }
    }

    private static boolean isValidTarget(Entity target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
    }

    private void swallow(Entity target) {
        if (target == null || target.isDead) {
            return;
        }
        target.hurtResistantTime = 0;
        try {
            // Positive infinity void damage, as specified. KillUtil is still
            // called afterwards so invulnerable or non-living entities are
            // removed from the world as well.
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.POSITIVE_INFINITY);
        } catch (Throwable ignored) {
            // A modded entity may not handle infinite damage. KillUtil below
            // is the authoritative removal path.
        }
        try {
            KillUtil.purge(this.world, target);
        } catch (Throwable ignored) {
        }
    }

    private void queueBlockChange(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long key = chunkKey(chunkX, chunkZ);
        List<BlockPos> list = this.pendingChanges.get(key);
        if (list == null) {
            list = new ArrayList<BlockPos>();
            this.pendingChanges.put(key, list);
        }
        list.add(pos);
    }

    private void flushBlockChanges() {
        if (this.pendingChanges.isEmpty() || !(this.world instanceof WorldServer)) {
            this.pendingChanges.clear();
            return;
        }
        WorldServer server = (WorldServer) this.world;
        List<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
        for (EntityPlayerMP player : server.getMinecraftServer().getPlayerList().getPlayers()) {
            if (player.dimension == server.provider.getDimension()) {
                players.add(player);
            }
        }
        for (Map.Entry<Long, List<BlockPos>> entry : this.pendingChanges.entrySet()) {
            long key = entry.getKey();
            int chunkX = (int) (key >> 32);
            int chunkZ = (int) key;
            List<BlockPos> positions = entry.getValue();
            short[] offsets = new short[positions.size()];
            for (int i = 0; i < positions.size(); ++i) {
                BlockPos pos = positions.get(i);
                offsets[i] = (short) (((pos.getX() & 15) << 12)
                        | ((pos.getZ() & 15) << 8) | (pos.getY() & 255));
            }
            Chunk chunk = server.getChunkFromChunkCoords(chunkX, chunkZ);
            SPacketMultiBlockChange packet = new SPacketMultiBlockChange(
                    offsets.length, offsets, chunk);
            for (EntityPlayerMP player : players) {
                player.connection.sendPacket(packet);
            }
            chunk.markDirty();
        }
        this.pendingChanges.clear();
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    private void releaseChunkTickets() {
        NuclearStrikeChunkLoader.releaseAll(this.chunkTickets);
    }

    @Override
    public void setDead() {
        if (!this.world.isRemote) {
            this.flushBlockChanges();
            this.releaseChunkTickets();
        }
        super.setDead();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.isDead) {
            return;
        }
        if (!this.arrayParticleSpawned) {
            this.arrayParticleSpawned = true;
            new SuperBlackHoleArrayParticle(this);
            this.world.playSound(this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_ENDERMEN_AMBIENT, SoundCategory.HOSTILE,
                    4.0F, 0.35F, false);
        }

        double age = this.getSpellAge() + partialTicks;
        double radius = getRadiusAtAge(age);
        int finish = this.getFinishTick();
        if (finish >= 0 && !this.finishFlashSpawned) {
            this.finishFlashSpawned = true;
            new ExplosionFlashParticle(this.world, this.posX,
                    getHoleCenterY(this.posY), this.posZ,
                    0.62F, 0.38F, 1.0F, 42.0F);
            int burstCount = this.consumeParticleBudget(90.0D, deltaTicks);
            for (int i = 0; i < burstCount; ++i) {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double speed = 0.15D + this.rand.nextDouble() * 0.65D;
                new SkyStrikeSparkParticle(this.world,
                        this.posX + Math.cos(angle) * (2.0D + this.rand.nextDouble() * 8.0D),
                        getHoleCenterY(this.posY) + (this.rand.nextDouble() - 0.5D) * 12.0D,
                        this.posZ + Math.sin(angle) * (2.0D + this.rand.nextDouble() * 8.0D),
                        Math.cos(angle) * speed, (this.rand.nextDouble() - 0.5D) * 0.3D,
                        Math.sin(angle) * speed,
                        0.82F, 0.56F, 1.0F,
                        1.4F + this.rand.nextFloat() * 2.8F,
                        16 + this.rand.nextInt(22));
            }
        }
        if (finish >= 0) {
            return;
        }

        double rate;
        if (age < FORM_START_TICK) {
            rate = 10.0D;
        } else if (age < FORM_END_TICK) {
            rate = 34.0D;
        } else if (age < HOLD_END_TICK) {
            rate = 18.0D;
        } else {
            rate = 24.0D;
        }
        int count = this.consumeParticleBudget(rate, deltaTicks);
        double centerY = getHoleCenterY(this.posY);
        for (int i = 0; i < count; ++i) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double spread;
            double px;
            double py;
            double pz;
            double vx;
            double vy;
            double vz;
            if (radius <= 0.05D) {
                spread = ARRAY_RADIUS * (0.08D + this.rand.nextDouble() * 0.88D);
                px = this.posX + Math.cos(angle) * spread;
                pz = this.posZ + Math.sin(angle) * spread;
                py = this.posY + ARRAY_FLOAT_HEIGHT + 0.2D
                        + this.rand.nextDouble() * 2.0D;
                vx = (this.posX - px) * 0.012D;
                vy = 0.06D + this.rand.nextDouble() * 0.10D;
                vz = (this.posZ - pz) * 0.012D;
            } else {
                spread = radius * (1.05D + this.rand.nextDouble() * 0.95D);
                double vertical = (this.rand.nextDouble() - 0.5D)
                        * radius * 1.4D;
                px = this.posX + Math.cos(angle) * spread;
                pz = this.posZ + Math.sin(angle) * spread;
                py = centerY + vertical;
                double inward = 0.06D + this.rand.nextDouble() * 0.22D;
                vx = -Math.cos(angle) * inward;
                vy = (centerY - py) * 0.008D
                        + (this.rand.nextDouble() - 0.5D) * 0.04D;
                vz = -Math.sin(angle) * inward;
            }
            float brightness = 0.72F + this.rand.nextFloat() * 0.28F;
            new SkyStrikeSparkParticle(this.world, px, py, pz, vx, vy, vz,
                    0.56F * brightness, 0.22F * brightness, 1.0F,
                    0.7F + this.rand.nextFloat() * 1.6F,
                    10 + this.rand.nextInt(16));
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}

