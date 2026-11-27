package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.particle.simple.particles.NuclearSmokeParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import com.xyazh.kanake.particle.simple.particles.StarArrayParticle;
import com.xyazh.kanake.util.TemperatureColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
 * Super-tier spell "Star" (超位魔法——恒星).
 *
 * <p>A containment array is built on the ground and a star ignites above it.
 * The star starts dark red, heats through the visible spectrum, first sets the
 * surrounding terrain on fire and later fires random rays that vaporize
 * non-stone blocks or melt stone through magma and lava stages. Living
 * creatures inside the heat field take {@code temperature / distance^2} fire
 * damage every second and are set on fire.</p>
 *
 * <p>After five minutes of burning the star collapses and fades away. There is
 * deliberately no supernova explosion. All visuals are procedural; see
 * {@link StarArrayParticle} and the render helper.</p>
 */
public class EntityStar extends EntityShoot {
    /** Approximately the same size as the fully grown stellar sphere. */
    public static final double ARRAY_RADIUS = 20.0D;
    public static final double ARRAY_FLOAT_HEIGHT = 1.0D;
    public static final double STAR_CENTER_HEIGHT = 28.0D;

    public static final int ARRAY_BUILD_TICKS = 100;
    public static final int STAR_APPEAR_TICK = 20;
    public static final int IGNITION_START_TICK = 100;
    public static final int BURN_TICKS = 20 * 300; // exactly five minutes
    public static final int BURN_END_TICK = IGNITION_START_TICK + BURN_TICKS;
    /** Time used for the final collapse and fade-out. */
    public static final int DISSIPATE_TICKS = 100;
    public static final int MAX_LIFE_TICKS = BURN_END_TICK + DISSIPATE_TICKS + 20;

    public static final double IGNITION_RADIUS_MAX = 96.0D;
    public static final double MELT_RADIUS_MAX = 64.0D;
    public static final double DAMAGE_RADIUS_MAX = 112.0D;

    public static final double MIN_TEMPERATURE = 6000.0D;
    public static final double MAX_TEMPERATURE = 160000.0D;

    private static final int CHUNK_TICKET_RADIUS = 6;
    private static final int MELT_START_OFFSET = 20 * 24;
    private static final int MELT_RAYS_PER_TICK = 16;

    private final List<ForgeChunkManager.Ticket> chunkTickets =
            new ArrayList<ForgeChunkManager.Ticket>();
    private final Map<Long, List<BlockPos>> pendingChanges =
            new HashMap<Long, List<BlockPos>>();
    private static final int INITIAL_AGE_PACKET = 100;
    private boolean initialChunkLoadRequested;
    private int spellAge;
    /** Client-only offset from the one-time start-tracking age sync. */
    private int ageOffset;
    @SideOnly(Side.CLIENT)
    private boolean arrayParticleSpawned;

    public EntityStar(World worldIn) {
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

    public static double getStarCenterY(double baseY) {
        return baseY + STAR_CENTER_HEIGHT;
    }

    public static double getHeatingProgress(double age) {
        return clamp((age - IGNITION_START_TICK)
                / (double) (BURN_END_TICK - IGNITION_START_TICK));
    }

    public static double getTemperatureAt(double age) {
        if (age < IGNITION_START_TICK) {
            return 800.0D;
        }
        if (age <= BURN_END_TICK) {
            double progress = getHeatingProgress(age);
            return MIN_TEMPERATURE + (MAX_TEMPERATURE - MIN_TEMPERATURE)
                    * progress * progress;
        }
        return MAX_TEMPERATURE;
    }

    public static double getStarRadiusAt(double age) {
        if (age < STAR_APPEAR_TICK) {
            return 0.0D;
        }
        if (age < IGNITION_START_TICK) {
            double t = smoothstep(clamp((age - STAR_APPEAR_TICK)
                    / (double) (IGNITION_START_TICK - STAR_APPEAR_TICK)));
            return 9.5D * t;
        }
        if (age <= BURN_END_TICK) {
            return 9.5D + 8.5D * getHeatingProgress(age);
        }
        if (age < BURN_END_TICK + DISSIPATE_TICKS) {
            double dissolve = clamp((age - BURN_END_TICK)
                    / (double) DISSIPATE_TICKS);
            return 18.0D * Math.pow(1.0D - dissolve, 1.35D);
        }
        return 0.0D;
    }

    public static double getIgnitionRadiusAt(double age) {
        double progress = getHeatingProgress(age);
        if (age < IGNITION_START_TICK) {
            return 0.0D;
        }
        return 14.0D + (IGNITION_RADIUS_MAX - 14.0D) * Math.sqrt(progress);
    }

    public static double getMeltRadiusAt(double age) {
        double progress = getHeatingProgress(age);
        double local = clamp((progress - 0.08D) / 0.92D);
        return 6.0D + (MELT_RADIUS_MAX - 6.0D) * Math.sqrt(local);
    }

    public static double getDamageRadiusAt(double age) {
        double progress = getHeatingProgress(age);
        return 24.0D + (DAMAGE_RADIUS_MAX - 24.0D) * Math.sqrt(progress);
    }

    public boolean isBurning() {
        double age = this.getSpellAge();
        return age >= IGNITION_START_TICK && age < BURN_END_TICK;
    }

    public int getSpellAge() {
        return this.world != null && this.world.isRemote
                ? this.ticksExisted + this.ageOffset : this.spellAge;
    }

    /**
     * Send the current server age once when a player starts tracking. After
     * that, temperature, radius and every animation are computed locally.
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
        compound.setInteger("superStarAge", this.spellAge);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.spellAge = compound.getInteger("superStarAge");
        this.initialChunkLoadRequested = false;
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

        if (this.spellAge >= BURN_END_TICK + DISSIPATE_TICKS
                || this.spellAge >= MAX_LIFE_TICKS) {
            // The five-minute burn is over. The client fades the collapsing
            // star out; no supernova damage or crater is created.
            this.setDead();
            return;
        }

        if (this.spellAge >= IGNITION_START_TICK && this.spellAge < BURN_END_TICK) {
            if (this.spellAge % 20 == 0) {
                this.applyHeatDamage();
            }
            if (this.spellAge % 20 == 10) {
                this.igniteSurface();
            }
            if (this.spellAge >= IGNITION_START_TICK + MELT_START_OFFSET) {
                this.tickMeltRays();
            }
        }

        if (this.spellAge >= MAX_LIFE_TICKS) {
            this.setDead();
        }
    }

    private void applyHeatDamage() {
        double age = this.spellAge;
        double temperature = getTemperatureAt(age);
        double radius = getDamageRadiusAt(age);
        double centerY = getStarCenterY(this.posY);
        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - radius, centerY - radius, this.posZ - radius,
                this.posX + radius, centerY + radius, this.posZ + radius);
        List<EntityLivingBase> targets =
                this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        MagicDamage source = new MagicDamage("star_heat");
        source.setAttacker(this.shootingEntity);
        source.setDamageIsAbsolute();
        source.setFireDamage();
        for (EntityLivingBase target : targets) {
            if (!isValidTarget(target)) {
                continue;
            }
            double dx = target.posX - this.posX;
            double dy = target.posY + target.height * 0.5D - centerY;
            double dz = target.posZ - this.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;
            if (distanceSq > radius * radius) {
                continue;
            }
            // The requested heat law: temperature / distance^2. A tiny floor
            // on distance^2 prevents an infinite value at the exact centre.
            double damage = temperature / Math.max(1.0D, distanceSq);
            float applied = (float) Math.min(damage, 250000.0D);
            target.hurtResistantTime = 0;
            MagicDamageUtil.attackEntity(target, source, applied, applied);
            if (target.isEntityAlive()) {
                target.setFire(10 * 20);
            }
        }
    }

    private void igniteSurface() {
        double radius = getIgnitionRadiusAt(this.spellAge);
        if (radius <= 0.5D) {
            return;
        }
        int attempts = 10 + (int) (getHeatingProgress(this.spellAge) * 18.0D);
        for (int i = 0; i < attempts; ++i) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double r = Math.sqrt(this.rand.nextDouble()) * radius;
            int x = MathHelper.floor(this.posX + Math.cos(angle) * r);
            int z = MathHelper.floor(this.posZ + Math.sin(angle) * r);
            BlockPos heightPos = this.world.getHeight(new BlockPos(x, 0, z));
            int y = heightPos.getY();
            if (y <= 0 || y >= this.world.getActualHeight() - 1) {
                continue;
            }
            BlockPos firePos = new BlockPos(x, y, z);
            if (!this.world.isAirBlock(firePos)) {
                continue;
            }
            IBlockState below = this.world.getBlockState(firePos.down());
            if (!below.isFullBlock() || below.getMaterial().isLiquid()) {
                continue;
            }
            this.world.setBlockState(firePos, Blocks.FIRE.getDefaultState(), 2);
        }
    }

    /**
     * Emits sixteen rays from the star every tick. Six rays go along the fixed
     * axes (down, up, +X, -X, +Z, -Z); the other ten are uniform random
     * directions on the full sphere. Each ray affects the first block it hits:
     * <ul>
     *     <li>non-stone block: vaporized (air)</li>
     *     <li>stone-like block: becomes a magma block</li>
     *     <li>magma block: becomes source lava</li>
     *     <li>lava (source or flowing): vaporized</li>
     * </ul>
     */
    private void tickMeltRays() {
        double centerY = getStarCenterY(this.posY);
        Vec3d start = new Vec3d(this.posX, centerY, this.posZ);
        for (int i = 0; i < MELT_RAYS_PER_TICK; ++i) {
            double dx;
            double dy;
            double dz;
            if (i < 6) {
                // Fixed six-direction coverage: down, up, left, right,
                // front and back. The downward ray also guarantees that the
                // area directly under the star is never skipped.
                switch (i) {
                    case 0:
                        dx = 0.0D;
                        dy = -1.0D;
                        dz = 0.0D;
                        break;
                    case 1:
                        dx = 0.0D;
                        dy = 1.0D;
                        dz = 0.0D;
                        break;
                    case 2:
                        dx = 1.0D;
                        dy = 0.0D;
                        dz = 0.0D;
                        break;
                    case 3:
                        dx = -1.0D;
                        dy = 0.0D;
                        dz = 0.0D;
                        break;
                    case 4:
                        dx = 0.0D;
                        dy = 0.0D;
                        dz = 1.0D;
                        break;
                    default:
                        dx = 0.0D;
                        dy = 0.0D;
                        dz = -1.0D;
                        break;
                }
            } else {
                // Uniform random point on a sphere.
                dy = this.rand.nextDouble() * 2.0D - 1.0D;
                double azimuth = this.rand.nextDouble() * Math.PI * 2.0D;
                double horizontal = Math.sqrt(Math.max(0.0D, 1.0D - dy * dy));
                dx = Math.cos(azimuth) * horizontal;
                dz = Math.sin(azimuth) * horizontal;
            }
            Vec3d end = start.add(new Vec3d(
                    dx * MELT_RADIUS_MAX,
                    dy * MELT_RADIUS_MAX,
                    dz * MELT_RADIUS_MAX));
            RayTraceResult hit = this.world.rayTraceBlocks(start, end,
                    true, false, true);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK
                    && hit.getBlockPos() != null) {
                this.meltRayHit(hit.getBlockPos());
            }
        }
        this.flushBlockChanges();
    }

    private void meltRayHit(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        Block block = state.getBlock();
        if (block.isAir(state, this.world, pos)
                || state.getBlockHardness(this.world, pos) < 0.0F) {
            return;
        }

        if (block == Blocks.MAGMA) {
            // Magma block -> source lava.
            this.placeMeltedState(pos, Blocks.LAVA.getDefaultState());
            return;
        }
        if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            // Lava -> vaporized.
            this.placeMeltedState(pos, Blocks.AIR.getDefaultState());
            return;
        }
        if (state.getMaterial() == Material.ROCK) {
            // Stone family -> magma block.
            this.placeMeltedState(pos, Blocks.MAGMA.getDefaultState());
            return;
        }
        // Everything else is directly vaporized.
        this.placeMeltedState(pos, Blocks.AIR.getDefaultState());
    }

    private void placeMeltedState(BlockPos pos, IBlockState state) {
        Block oldBlock = this.world.getBlockState(pos).getBlock();
        if (oldBlock.hasTileEntity(this.world.getBlockState(pos))) {
            this.world.removeTileEntity(pos);
        }
        this.world.setBlockState(pos, state, 0);
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

    private static boolean isValidTarget(Entity target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
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
            new StarArrayParticle(this);
        }

        double age = this.getSpellAge() + partialTicks;
        double centerY = getStarCenterY(this.posY);
        if (age >= BURN_END_TICK) {
            // Collapse and fade: luminous matter streams inward instead of
            // detonating as a supernova.
            int count = this.consumeParticleBudget(22.0D, deltaTicks);
            for (int i = 0; i < count; ++i) {
                double theta = this.rand.nextDouble() * Math.PI;
                double phi = this.rand.nextDouble() * Math.PI * 2.0D;
                double shell = 2.0D + this.rand.nextDouble() * 12.0D;
                double dx = Math.sin(theta) * Math.cos(phi);
                double dy = Math.cos(theta);
                double dz = Math.sin(theta) * Math.sin(phi);
                float warm = 0.72F + this.rand.nextFloat() * 0.28F;
                new SkyStrikeSparkParticle(this.world,
                        this.posX + dx * shell,
                        centerY + dy * shell,
                        this.posZ + dz * shell,
                        -dx * (0.04D + this.rand.nextDouble() * 0.10D),
                        -dy * (0.04D + this.rand.nextDouble() * 0.10D)
                                + 0.015D,
                        -dz * (0.04D + this.rand.nextDouble() * 0.10D),
                        1.0F, 0.72F * warm, 0.28F * warm,
                        0.8F + this.rand.nextFloat() * 1.6F,
                        10 + this.rand.nextInt(14));
            }
            return;
        }

        double progress = clamp(getHeatingProgress(age));
        float[] color = TemperatureColor.spectrum(progress);
        double radius = getStarRadiusAt(age);
        double rate = age < IGNITION_START_TICK ? 12.0D : 17.0D;
        int count = this.consumeParticleBudget(rate, deltaTicks);
        for (int i = 0; i < count; ++i) {
            double theta = this.rand.nextDouble() * Math.PI;
            double phi = this.rand.nextDouble() * Math.PI * 2.0D;
            double shell = Math.max(0.6D, radius)
                    * (1.0D + this.rand.nextDouble() * 0.24D);
            double dx = Math.sin(theta) * Math.cos(phi);
            double dy = Math.cos(theta);
            double dz = Math.sin(theta) * Math.sin(phi);
            double outward = 0.015D + this.rand.nextDouble() * 0.075D;
            float b = 0.80F + this.rand.nextFloat() * 0.20F;
            new SkyStrikeSparkParticle(this.world,
                    this.posX + dx * shell,
                    centerY + dy * shell,
                    this.posZ + dz * shell,
                    dx * outward + (this.rand.nextDouble() - 0.5D) * 0.03D,
                    dy * outward + 0.02D + this.rand.nextDouble() * 0.05D,
                    dz * outward + (this.rand.nextDouble() - 0.5D) * 0.03D,
                    color[0] * b, color[1] * b, color[2] * b,
                    0.7F + this.rand.nextFloat() * 1.8F,
                    10 + this.rand.nextInt(16));
            if (radius > 1.0D && this.rand.nextFloat() < 0.28F) {
                this.spawnGroundHeatParticle(age, progress, centerY);
            }
        }

        if (age >= IGNITION_START_TICK && age < BURN_END_TICK
                && this.consumeParticleBudget(3.0D, deltaTicks) > 0) {
            this.spawnGroundHeatParticle(age, progress, centerY);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnGroundHeatParticle(double age, double progress, double centerY) {
        double meltRadius = Math.max(4.0D, getMeltRadiusAt(age));
        double angle = this.rand.nextDouble() * Math.PI * 2.0D;
        double r = Math.sqrt(this.rand.nextDouble()) * meltRadius;
        int x = MathHelper.floor(this.posX + Math.cos(angle) * r);
        int z = MathHelper.floor(this.posZ + Math.sin(angle) * r);
        double px = x + 0.5D;
        // Use the already-known cast surface instead of querying the chunk
        // height map for every ember on every render frame.
        double py = this.posY + 0.25D + this.rand.nextDouble() * 0.55D;
        double pz = z + 0.5D;
        float[] color = TemperatureColor.spectrum(progress);
        new SkyStrikeSparkParticle(this.world, px, py, pz,
                (this.rand.nextDouble() - 0.5D) * 0.045D,
                0.04D + this.rand.nextDouble() * 0.10D,
                (this.rand.nextDouble() - 0.5D) * 0.045D,
                color[0], color[1], color[2],
                0.8F + this.rand.nextFloat() * 1.7F,
                14 + this.rand.nextInt(18));
        if (this.rand.nextFloat() < 0.55F) {
            this.world.spawnParticle(EnumParticleTypes.FLAME,
                    px, py + 0.1D, pz, 0.0D, 0.04D, 0.0D);
        }
        if (progress > 0.18D && this.rand.nextFloat() < 0.35F) {
            this.world.spawnParticle(EnumParticleTypes.LAVA,
                    px, py + 0.1D, pz, 0.0D, 0.02D, 0.0D);
        }
        if (progress > 0.32D && this.rand.nextFloat() < 0.25F) {
            NuclearSmokeParticle.spawn(this.world, px, py + 0.4D, pz,
                    0.0D, 0.035D, 0.0D, 1.8F + this.rand.nextFloat() * 2.5F,
                    28 + this.rand.nextInt(28), 0.35F, 0.22F, 0.16F);
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }
}
