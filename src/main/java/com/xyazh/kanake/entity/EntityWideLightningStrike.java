package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.particle.simple.particles.ExplosionFlashParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import com.xyazh.kanake.particle.simple.particles.WideLightningArrayParticle;
import com.xyazh.kanake.util.KillUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Super-tier spell "Wide-Area Lightning" (超位魔法——广域雷击).
 *
 * <p>A storm array forms at cloud height and switches the world to a
 * thunderstorm. After charging, a huge central strike lands together with
 * several nearby full-height bolts. The impact, creatures hit by a chain, and
 * one block branch per node schedule another lightning chain after 0.8 seconds,
 * up to sixteen generations.</p>
 */
public class EntityWideLightningStrike extends EntityShoot {
    public static final int ARRAY_BUILD_TICKS = 80;
    public static final int CHARGE_END_TICK = 200;
    /** The strike is immediate once charging completes; no falling pre-bolt. */
    public static final int FALL_TICKS = 0;
    public static final int IMPACT_TICK = CHARGE_END_TICK;
    public static final int CHAIN_DELAY_TICKS = 16;
    public static final int MAX_CHAIN_DEPTH = 16;
    public static final int MAX_CHAIN_PULSES = 512;
    public static final int CHAIN_BLOCKS_PER_NODE = 5;
    public static final double CHAIN_RADIUS = 32.0D;
    public static final double CHAIN_RADIUS_SQ = CHAIN_RADIUS * CHAIN_RADIUS;
    public static final int MAX_LIFE_TICKS = IMPACT_TICK
            + MAX_CHAIN_DEPTH * CHAIN_DELAY_TICKS + 120;
    public static final double ARRAY_RADIUS = 64.0D;
    public static final float MAGIC_DAMAGE = 20.0F;
    public static final float TRUE_DAMAGE = 5.0F;
    public static final float NOMINAL_DAMAGE = MAGIC_DAMAGE + TRUE_DAMAGE;
    public static final int FIRE_TICKS = 8 * 20;
    // Chain nodes can travel sixteen blocks per generation. Six chunks radius
    // covers roughly six generations in every direction; loaded entities are
    // the normal limiting factor, not unloaded terrain.
    private static final int CHUNK_TICKET_RADIUS = 6;
    private static final int INITIAL_AGE_PACKET = 100;
    private static final int CHAIN_VISUAL_PACKET = 101;
    public static final int CHAIN_VISUAL_LIFE = 50;
    private static final Map<Integer, WeatherSnapshot> ACTIVE_WEATHER =
            new HashMap<Integer, WeatherSnapshot>();

    public static void clearWeatherSnapshots() {
        ACTIVE_WEATHER.clear();
    }

    private final List<ForgeChunkManager.Ticket> chunkTickets =
            new ArrayList<ForgeChunkManager.Ticket>();
    private final Map<Long, List<BlockPos>> pendingChanges =
            new HashMap<Long, List<BlockPos>>();
    private final List<ChainPulse> chainPulses = new ArrayList<ChainPulse>();
    private final List<ClientChainVisual> clientChainVisuals =
            new ArrayList<ClientChainVisual>();

    private boolean initialChunkLoadRequested;
    private boolean weatherStarted;
    private boolean weatherRestored;
    private boolean weatherRefHeld;
    private int weatherDimension = Integer.MIN_VALUE;
    private boolean impactStarted;
    private boolean impactSoundPlayed;
    private int chainPulseCount;
    /** Client-only offset from the one-time start-tracking age sync. */
    private int ageOffset;
    @SideOnly(Side.CLIENT)
    private boolean arrayParticleSpawned;
    @SideOnly(Side.CLIENT)
    private boolean impactFlashSpawned;

    public EntityWideLightningStrike(World worldIn) {
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

    public int getSpellAge() {
        return this.world != null && this.world.isRemote
                ? this.ticksExisted + this.ageOffset : this.ticksExisted;
    }

    /**
     * Send the current server age once when a player starts tracking, so a
     * late joiner sees the correct array/charge/impact phase.
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
            packet.buffer.writeInt(this.ticksExisted);
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
        if (type == CHAIN_VISUAL_PACKET) {
            double sx = buf.readDouble();
            double sy = buf.readDouble();
            double sz = buf.readDouble();
            double tx = buf.readDouble();
            double ty = buf.readDouble();
            double tz = buf.readDouble();
            int seed = buf.readInt();
            buf.readInt(); // server age, consumed for protocol stability
            ClientChainVisual visual = new ClientChainVisual(
                    sx, sy, sz, tx, ty, tz, seed, this.ticksExisted);
            if (this.clientChainVisuals.size() < 1024) {
                this.clientChainVisuals.add(visual);
            }
            return type;
        }
        return readData(type, buf);
    }

    public static double getArrayY(World world, double targetY) {
        double cloudY = targetY + 64.0D;
        if (world.provider.isSurfaceWorld()) {
            float cloudHeight = world.provider.getCloudHeight();
            if (!Float.isNaN(cloudHeight) && cloudHeight > 0.0F) {
                cloudY = cloudHeight + 1.0D;
            }
        }
        return Math.max(targetY + 64.0D, cloudY);
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
        compound.setBoolean("wideLightningImpact", this.impactStarted);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.impactStarted = compound.getBoolean("wideLightningImpact");
        this.initialChunkLoadRequested = false;
        this.pendingChanges.clear();
        this.chainPulses.clear();
        this.chainPulseCount = 0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead || this.world.isRemote) {
            return;
        }

        this.prepareInitialChunkLoad();
        if (!this.weatherStarted) {
            this.startThunderstorm();
        }

        int age = this.getSpellAge();
        if (age >= IMPACT_TICK && !this.impactStarted) {
            this.impact();
        }
        if (this.impactStarted) {
            this.processChainPulses(age);
        }

        this.flushBlockChanges();
        if (age >= MAX_LIFE_TICKS) {
            this.restoreWeather();
            this.setDead();
        }
    }

    private void startThunderstorm() {
        this.weatherStarted = true;
        if (!(this.world instanceof WorldServer)) {
            return;
        }
        WorldServer server = (WorldServer) this.world;
        this.weatherDimension = server.provider.getDimension();
        WeatherSnapshot snapshot = ACTIVE_WEATHER.get(this.weatherDimension);
        if (snapshot == null) {
            snapshot = new WeatherSnapshot(this.world.getWorldInfo());
            ACTIVE_WEATHER.put(this.weatherDimension, snapshot);
        } else {
            ++snapshot.users;
        }
        this.weatherRefHeld = true;

        WorldInfo info = this.world.getWorldInfo();
        info.setCleanWeatherTime(0);
        info.setRaining(true);
        info.setThundering(true);
        info.setRainTime(20 * 120);
        info.setThunderTime(20 * 120);
        this.sendWeatherState(true, true);
    }

    /** Restore the shared pre-spell weather once the last cast ends. */
    private void restoreWeather() {
        if (!this.weatherStarted || this.weatherRestored || this.world.isRemote
                || !this.weatherRefHeld) {
            return;
        }
        this.weatherRestored = true;
        this.weatherRefHeld = false;
        WeatherSnapshot snapshot = ACTIVE_WEATHER.get(this.weatherDimension);
        if (snapshot == null) {
            return;
        }
        --snapshot.users;
        if (snapshot.users > 0) {
            return;
        }
        ACTIVE_WEATHER.remove(this.weatherDimension);
        WorldInfo info = this.world.getWorldInfo();
        snapshot.restore(info);
        this.sendWeatherState(snapshot.raining, snapshot.thundering);
    }

    private void sendWeatherState(boolean raining, boolean thundering) {
        if (!(this.world instanceof WorldServer)) {
            return;
        }
        WorldServer server = (WorldServer) this.world;
        int dimension = server.provider.getDimension();
        server.getMinecraftServer().getPlayerList().sendPacketToAllPlayersInDimension(
                new SPacketChangeGameState(raining ? 1 : 2, 0.0F), dimension);
        server.getMinecraftServer().getPlayerList().sendPacketToAllPlayersInDimension(
                new SPacketChangeGameState(7, raining ? 1.0F : 0.0F), dimension);
        server.getMinecraftServer().getPlayerList().sendPacketToAllPlayersInDimension(
                new SPacketChangeGameState(8, thundering ? 1.0F : 0.0F), dimension);
    }

    private void impact() {
        this.impactStarted = true;
        int x = MathHelper.floor(this.posX);
        int z = MathHelper.floor(this.posZ);
        int y = MathHelper.floor(this.posY - 0.01D);
        y = MathHelper.clamp(y, 0, this.world.getActualHeight() - 1);

        BlockPos impactPos = new BlockPos(x, y, z);
        if (this.world.isAirBlock(impactPos)) {
            BlockPos top = this.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            if (top.getY() > 0) {
                impactPos = top;
            }
        }

        this.destroyBlockAt(impactPos);
        double sx = impactPos.getX() + 0.5D;
        double sy = impactPos.getY() + 0.5D;
        double sz = impactPos.getZ() + 0.5D;
        this.triggerChain(sx, sy, sz, 0, this.rand.nextLong());
        this.flushBlockChanges();

        if (!this.impactSoundPlayed) {
            this.impactSoundPlayed = true;
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER,
                    24.0F, 0.55F);
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE,
                    2.5F, 0.75F);
        }
    }

    private void processChainPulses(int age) {
        if (this.chainPulses.isEmpty()) {
            return;
        }
        // Collect first: triggerChain() schedules future pulses by appending to
        // this same list. Triggering while iterating would invalidate the
        // iterator and throw ConcurrentModificationException.
        List<ChainPulse> ready = new ArrayList<ChainPulse>();
        Iterator<ChainPulse> iterator = this.chainPulses.iterator();
        while (iterator.hasNext()) {
            ChainPulse pulse = iterator.next();
            if (pulse.dueTick <= age) {
                iterator.remove();
                ready.add(pulse);
            }
        }
        for (ChainPulse pulse : ready) {
            this.triggerChain(pulse.x, pulse.y, pulse.z, pulse.depth, pulse.seed);
        }
    }

    /**
     * Starts one chain node. A node always tries all valid creatures in a
     * thirty-two-block sphere and up to five random blocks. Creatures schedule
     * the next node after sixteen ticks; blocks only carry the
     * visual/destruction link.
     */
    private void triggerChain(double x, double y, double z, int depth, long seed) {
        if (depth > MAX_CHAIN_DEPTH || this.chainPulseCount >= MAX_CHAIN_PULSES) {
            return;
        }
        this.ensureChainChunksLoaded(x, z);
        ++this.chainPulseCount;

        boolean found = false;
        boolean foundBlock = false;
        AxisAlignedBB box = new AxisAlignedBB(x, y, z, x, y, z).grow(CHAIN_RADIUS);
        List<EntityLivingBase> entities =
                this.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        for (EntityLivingBase target : entities) {
            if (!this.isValidChainTarget(target)
                    || target.getDistanceSq(x, y, z) > CHAIN_RADIUS_SQ) {
                continue;
            }

            found = true;
            double tx = target.posX;
            double ty = target.posY + target.height * 0.5D;
            double tz = target.posZ;
            this.spawnChainBolt(x, y, z, tx, ty, tz, seed ^ target.getEntityId());

            // Schedule the next generation from the hit point before applying
            // damage. A chain must still visibly continue after the victim is
            // killed by the 20 + 5 damage burst.
            if (depth < MAX_CHAIN_DEPTH
                    && this.chainPulseCount < MAX_CHAIN_PULSES
                    && this.chainPulseCount + this.chainPulses.size()
                    < MAX_CHAIN_PULSES) {
                this.chainPulses.add(new ChainPulse(tx, ty, tz, depth + 1,
                        this.ticksExisted + CHAIN_DELAY_TICKS,
                        seed * 31L + target.getEntityId() * 17L));
            }

            this.hitTarget(target);
        }

        Set<BlockPos> selectedBlocks = new HashSet<BlockPos>();
        boolean scheduledBlockChild = false;
        for (int i = 0; i < CHAIN_BLOCKS_PER_NODE; ++i) {
            BlockPos target = this.pickRandomChainBlock(x, y, z);
            if (target == null || !selectedBlocks.add(target)) {
                continue;
            }
            found = true;
            foundBlock = true;
            double tx = target.getX() + 0.5D;
            double ty = target.getY() + 0.5D;
            double tz = target.getZ() + 0.5D;
            this.spawnChainBolt(x, y, z, tx, ty, tz, seed * 31L + i * 101L);
            this.destroyChainPath(x, y, z, target);
            if (!scheduledBlockChild && this.canScheduleChainChild(depth)) {
                this.chainPulses.add(new ChainPulse(tx, ty, tz, depth + 1,
                        this.ticksExisted + CHAIN_DELAY_TICKS,
                        seed * 31L + i * 271L + 4242L));
                scheduledBlockChild = true;
            }
        }

        // If there is neither a creature nor a random solid block, still emit a
        // visible child chain from the struck point. This keeps block impacts
        // from looking like a dead end in sparse test/void terrain.
        if (!foundBlock) {
            BlockPos below = new BlockPos(MathHelper.floor(x),
                    MathHelper.floor(y) - 1, MathHelper.floor(z));
            if (this.isValidChainBlock(below)) {
                this.spawnChainBolt(x, y, z,
                        below.getX() + 0.5D, below.getY() + 0.5D,
                        below.getZ() + 0.5D,
                        seed * 31L + 7001L);
                this.destroyChainPath(x, y, z, below);
                found = true;
                foundBlock = true;
                if (!scheduledBlockChild && this.canScheduleChainChild(depth)) {
                    this.chainPulses.add(new ChainPulse(
                            below.getX() + 0.5D, below.getY() + 0.5D,
                            below.getZ() + 0.5D, depth + 1,
                            this.ticksExisted + CHAIN_DELAY_TICKS,
                            seed * 31L + 7001L));
                }
            }
        }
        if (!found) {
            for (int i = 0; i < 2; ++i) {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double distance = 6.0D + this.rand.nextDouble() * 12.0D;
                double tx = x + Math.cos(angle) * distance;
                double ty = y + (this.rand.nextDouble() - 0.25D) * 8.0D;
                double tz = z + Math.sin(angle) * distance;
                this.spawnChainBolt(x, y, z, tx, ty, tz,
                        seed * 31L + i * 113L + 9001L);
            }
        }
    }

    /**
     * Schedules one child chain. Blocks schedule at most one child per node so
     * their branches still visibly continue without five-way exponential
     * growth; creatures use the same cap through their own scheduling block.
     */
    private boolean canScheduleChainChild(int depth) {
        return depth < MAX_CHAIN_DEPTH
                && this.chainPulseCount < MAX_CHAIN_PULSES
                && this.chainPulseCount + this.chainPulses.size()
                < MAX_CHAIN_PULSES;
    }

    /**
     * Chain generations may step outside the initial square ticket. Load only
     * the missing 5x5 chunks around the node synchronously before scanning for
     * creatures/blocks; the initial spell ticket still covers the common case.
     */
    private void ensureChainChunksLoaded(double x, double z) {
        if (!(this.world instanceof WorldServer)) {
            return;
        }
        WorldServer server = (WorldServer) this.world;
        int centerX = MathHelper.floor(x) >> 4;
        int centerZ = MathHelper.floor(z) >> 4;
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                int chunkX = centerX + dx;
                int chunkZ = centerZ + dz;
                BlockPos sample = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
                if (server.isBlockLoaded(sample)) {
                    continue;
                }
                try {
                    server.getChunkFromChunkCoords(chunkX, chunkZ);
                } catch (Throwable ignored) {
                    // Generation failures are rare; the chain simply skips
                    // unloaded targets instead of crashing the server tick.
                }
            }
        }
    }

    private boolean isValidChainTarget(EntityLivingBase target) {
        if (target == null || target == this.shootingEntity
                || !target.isEntityAlive() || target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
    }

    /**
     * Applies the spell's 20 magic damage and 5 direct health damage. The
     * hurt cooldown is reset so neither packet is swallowed. MagicDamageUtil
     * uses the nominal 25 damage for its 10% (2.5) guaranteed-health-loss
     * fallback, matching the other super-tier spells.
     */
    private void hitTarget(EntityLivingBase target) {
        target.hurtResistantTime = 0;
        MagicDamage source = new MagicDamage("wide_lightning");
        source.setAttacker(this.shootingEntity);
        source.setDamageIsAbsolute();
        source.setFireDamage();
        MagicDamageUtil.attackEntity(target, source, MAGIC_DAMAGE, NOMINAL_DAMAGE);
        if (!target.isEntityAlive()) {
            return;
        }

        // Reset again before the direct health write. The five points are true
        // set-health damage and are not allowed to disappear behind the normal
        // hurt cooldown set by the magic hit above.
        target.hurtResistantTime = 0;
        float health = target.getHealth();
        if (health <= TRUE_DAMAGE) {
            if (!KillUtil.setHealthDirect(target, 0.0F)) {
                target.setHealth(0.0F);
            }
            target.onDeath(source);
        } else {
            float remaining = health - TRUE_DAMAGE;
            if (!KillUtil.setHealthDirect(target, remaining)) {
                target.setHealth(remaining);
            }
        }
        if (target.isEntityAlive()) {
            target.setFire(FIRE_TICKS);
        }
    }

    private void spawnChainBolt(double sx, double sy, double sz,
                                double tx, double ty, double tz, long seed) {
        if (!(this.world instanceof WorldServer)) {
            return;
        }
        WorldServer server = (WorldServer) this.world;
        double maxDistanceSq = 384.0D * 384.0D;
        for (EntityPlayerMP player :
                server.getMinecraftServer().getPlayerList().getPlayers()) {
            if (player.dimension != server.provider.getDimension()) {
                continue;
            }
            boolean inRange = player.getDistanceSq(sx, sy, sz) <= maxDistanceSq
                    || player.getDistanceSq(tx, ty, tz) <= maxDistanceSq;
            if (!inRange) {
                continue;
            }
            EntityDataPacket packet = EntityDataPacket.getPacket(this);
            if (packet == null) {
                return;
            }
            packet.buffer.writeInt(CHAIN_VISUAL_PACKET);
            packet.buffer.writeDouble(sx);
            packet.buffer.writeDouble(sy);
            packet.buffer.writeDouble(sz);
            packet.buffer.writeDouble(tx);
            packet.buffer.writeDouble(ty);
            packet.buffer.writeDouble(tz);
            packet.buffer.writeInt((int) seed);
            packet.buffer.writeInt(this.getSpellAge());
            Kanake.network.sendTo(packet, player);
        }
    }

    @SideOnly(Side.CLIENT)
    private void pruneClientChainVisuals() {
        double age = this.ticksExisted;
        Iterator<ClientChainVisual> iterator = this.clientChainVisuals.iterator();
        while (iterator.hasNext()) {
            if (age - iterator.next().birthAge > CHAIN_VISUAL_LIFE + 4) {
                iterator.remove();
            }
        }
    }

    public List<ClientChainVisual> getClientChainVisuals() {
        return this.clientChainVisuals;
    }

    /**
     * Picks a random solid block within sixteen blocks of the node. Several
     * attempts are made because most of the volume around a creature is air;
     * the fallback probes the local surface column for a visible target.
     */
    private BlockPos pickRandomChainBlock(double sx, double sy, double sz) {
        int maxY = this.world.getActualHeight() - 1;
        for (int attempt = 0; attempt < 12; ++attempt) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = Math.sqrt(this.rand.nextDouble()) * CHAIN_RADIUS;
            int x = MathHelper.floor(sx + Math.cos(angle) * radius);
            int z = MathHelper.floor(sz + Math.sin(angle) * radius);

            int y = MathHelper.floor(sy + (this.rand.nextDouble() * 2.0D - 1.0D)
                    * CHAIN_RADIUS);
            y = MathHelper.clamp(y, 0, maxY);
            BlockPos pos = new BlockPos(x, y, z);
            if (this.isValidChainBlock(pos)
                    && pos.distanceSq(sx, sy, sz) <= CHAIN_RADIUS_SQ) {
                return pos;
            }

            int top = this.world.getHeight(new BlockPos(x, 0, z)).getY() - 1;
            if (top > 0) {
                pos = new BlockPos(x, Math.min(top, maxY), z);
                if (this.isValidChainBlock(pos)
                        && pos.distanceSq(sx, sy, sz) <= CHAIN_RADIUS_SQ) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean isValidChainBlock(BlockPos pos) {
        if (this.world.isAirBlock(pos)) {
            return false;
        }
        IBlockState state = this.world.getBlockState(pos);
        return state.getBlock().getBlockHardness(state, this.world, pos) >= 0.0F;
    }

    /** Destroys every solid, breakable block crossed by the visible chain. */
    private void destroyChainPath(double sx, double sy, double sz, BlockPos target) {
        double ex = target.getX() + 0.5D;
        double ey = target.getY() + 0.5D;
        double ez = target.getZ() + 0.5D;
        double dx = ex - sx;
        double dy = ey - sy;
        double dz = ez - sz;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int steps = Math.max(1, (int) Math.ceil(distance * 2.0D));
        steps = Math.min(160, steps);
        Set<BlockPos> visited = new HashSet<BlockPos>();
        for (int i = 0; i <= steps; ++i) {
            double t = i / (double) steps;
            BlockPos pos = new BlockPos(
                    MathHelper.floor(sx + dx * t),
                    MathHelper.floor(sy + dy * t),
                    MathHelper.floor(sz + dz * t));
            if (visited.add(pos)) {
                this.destroyBlockAt(pos);
            }
        }
    }

    private void destroyBlockAt(BlockPos pos) {
        if (pos.getY() < 0 || pos.getY() >= this.world.getActualHeight()) {
            return;
        }
        IBlockState state = this.world.getBlockState(pos);
        if (state.getBlock().isAir(state, this.world, pos)) {
            return;
        }
        if (state.getBlock().getBlockHardness(state, this.world, pos) < 0.0F) {
            return;
        }
        this.world.removeTileEntity(pos);
        this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
        this.queueBlockChange(pos);
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

    @Override
    public void setDead() {
        if (!this.world.isRemote) {
            this.restoreWeather();
            this.flushBlockChanges();
            NuclearStrikeChunkLoader.releaseAll(this.chunkTickets);
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
            new WideLightningArrayParticle(this);
        }
        this.pruneClientChainVisuals();

        double age = this.getSpellAge() + partialTicks;
        if (age >= IMPACT_TICK) {
            if (!this.impactFlashSpawned && age < IMPACT_TICK + 20.0D) {
                this.impactFlashSpawned = true;
                new ExplosionFlashParticle(this.world, this.posX, this.posY + 1.0D,
                        this.posZ, 0.55F, 0.82F, 1.0F, 90.0F);
            }
            double sinceImpact = age - IMPACT_TICK;
            if (sinceImpact < 100.0D) {
                double rate = 26.0D * (1.0D - sinceImpact / 100.0D);
                int count = this.consumeParticleBudget(rate, deltaTicks);
                for (int i = 0; i < count; ++i) {
                    double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                    double radius = this.rand.nextDouble() * 17.0D;
                    new SkyStrikeSparkParticle(this.world,
                            this.posX + Math.cos(angle) * radius,
                            this.posY + 0.3D + this.rand.nextDouble() * 8.0D,
                            this.posZ + Math.sin(angle) * radius,
                            this.rand.nextDouble() * 0.08D - 0.04D,
                            this.rand.nextDouble() * 0.12D,
                            this.rand.nextDouble() * 0.08D - 0.04D,
                            0.55F, 0.85F, 1.0F,
                            0.7F + this.rand.nextFloat() * 1.8F,
                            8 + this.rand.nextInt(12));
                }
            }
        }
    }

    private static final class WeatherSnapshot {
        private final boolean raining;
        private final boolean thundering;
        private final int rainTime;
        private final int thunderTime;
        private final int cleanWeatherTime;
        private int users = 1;

        private WeatherSnapshot(WorldInfo info) {
            this.raining = info.isRaining();
            this.thundering = info.isThundering();
            this.rainTime = info.getRainTime();
            this.thunderTime = info.getThunderTime();
            this.cleanWeatherTime = info.getCleanWeatherTime();
        }

        private void restore(WorldInfo info) {
            info.setCleanWeatherTime(this.cleanWeatherTime);
            info.setRaining(this.raining);
            info.setThundering(this.thundering);
            info.setRainTime(this.rainTime);
            info.setThunderTime(this.thunderTime);
        }
    }

    public static final class ClientChainVisual {
        public final double sourceX;
        public final double sourceY;
        public final double sourceZ;
        public final double targetX;
        public final double targetY;
        public final double targetZ;
        public final int seed;
        public final int birthAge;

        private ClientChainVisual(double sourceX, double sourceY, double sourceZ,
                                  double targetX, double targetY, double targetZ,
                                  int seed, int birthAge) {
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.sourceZ = sourceZ;
            this.targetX = targetX;
            this.targetY = targetY;
            this.targetZ = targetZ;
            this.seed = seed;
            this.birthAge = birthAge;
        }
    }

    private static final class ChainPulse {
        private final double x;
        private final double y;
        private final double z;
        private final int depth;
        private final int dueTick;
        private final long seed;

        private ChainPulse(double x, double y, double z, int depth,
                           int dueTick, long seed) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.depth = depth;
            this.dueTick = dueTick;
            this.seed = seed;
        }
    }
}
