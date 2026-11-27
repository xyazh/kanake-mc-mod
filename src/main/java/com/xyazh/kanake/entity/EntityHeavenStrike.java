package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.particle.simple.particles.HeavenStrikeArrayParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import com.xyazh.kanake.util.KillUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Super-tier spell "Sky Strike" (天击).
 *
 * <p>The entity is invisible. All visuals are procedural and are owned by
 * {@link HeavenStrikeArrayParticle} / HeavenStrikeRenderHelper. This class only
 * owns the timeline, the damage and the terrain destruction.</p>
 *
 * <p>Timeline (20 ticks per second):</p>
 * <ul>
 *     <li>0-80: the huge purple array is constructed and grows from a small
 *     circle to its full radius.</li>
 *     <li>20-240: a purple, constantly rippling energy sphere gathers high
 *     above the cloud layer. Its radius grows to exactly 64 blocks.</li>
 *     <li>240-264: the array dissolves.</li>
 *     <li>240-280: the sphere is stretched into an ellipsoid and slams down.</li>
 *     <li>280+: a giant explosion destroys every block with hardness &gt;= 0
 *     inside a 96-block radius, damages and ignites entities. The damage is
 *     repeated once per second, each hit being 2/3 of the previous one.</li>
 * </ul>
 */
public class EntityHeavenStrike extends EntityShoot {
    /** Horizontal radius of the visible array and the destruction/damage area. */
    public static final double ARRAY_RADIUS = 96.0D;
    /**
     * The visible array plane floats this far above the locked point. The
     * individual layers are stacked even higher by the renderer; this offset
     * keeps the lowest ring out of grass, snow and other small ground cover.
     */
    public static final double ARRAY_FLOAT_HEIGHT = 1.00D;
    public static final double DAMAGE_RADIUS = 96.0D;
    public static final double DAMAGE_RADIUS_SQ = DAMAGE_RADIUS * DAMAGE_RADIUS;
    /** Radius of the charging energy sphere, kept at the original 64 blocks. */
    public static final double SPHERE_RADIUS = 64.0D;

    public static final int ARRAY_BUILD_TICKS = 80;
    public static final int SPHERE_START_TICK = 20;
    public static final int SPHERE_COMPLETE_TICK = 240;
    public static final int ARRAY_DISSIPATE_TICKS = 24;
    public static final int ARRAY_GONE_TICK = SPHERE_COMPLETE_TICK + ARRAY_DISSIPATE_TICKS;
    public static final int DESCENT_TICKS = 40;
    public static final int IMPACT_TICK = SPHERE_COMPLETE_TICK + DESCENT_TICKS;
    public static final int AFTERSHOCK_INTERVAL = 20;
    public static final int AFTERSHOCK_TICKS = 200;
    public static final int FADE_TICKS = 60;
    public static final int VISUAL_END_TICK = IMPACT_TICK + AFTERSHOCK_TICKS + FADE_TICKS;
    public static final int DESTRUCTION_START_DELAY = 40;
    /** Safety limit: a very slow server must never keep the ritual alive forever. */
    public static final int MAX_LIFE_TICKS = 20 * 180;

    public static final float MAGIC_DAMAGE = 768.0F;
    public static final float TRUE_DAMAGE = 256.0F;
    public static final float NOMINAL_DAMAGE = MAGIC_DAMAGE + TRUE_DAMAGE;
    /**
     * 10% of the nominal 1024 damage. The direct set-health write below always
     * removes the full 256 true damage, so this minimum is guaranteed even if
     * another mod caps ordinary magic damage.
     */
    public static final float MIN_TRUE_DAMAGE = NOMINAL_DAMAGE * MagicDamageUtil.MIN_DAMAGE_FRACTION;
    public static final double AFTERSHOCK_SCALE = 2.0D / 3.0D;
    public static final int FIRE_TICKS = 15 * 20;
    public static final int AFTERSHOCK_FIRE_TICKS = 6 * 20;

    /**
     * Scan down to y=0 as well. Only blocks whose hardness is -1 (bedrock,
     * barriers, ...) are skipped by the destruction loop, so a world without a
     * bedrock floor is carved completely.
     */
    public static final int EFFECT_MIN_Y = 0;
    // A 96-block radius fits inside +/-6 chunks from any chunk-local offset.
    public static final int CHUNK_TICKET_RADIUS = 6;
    private static final int DESTROY_BLOCK_BUDGET = 4096;
    private static final int DESTROY_WORK_LIMIT = 16384;

    private final List<ForgeChunkManager.Ticket> chunkTickets = new ArrayList<ForgeChunkManager.Ticket>();
    private final List<Column> columns = new ArrayList<Column>();
    private final Map<Long, List<BlockPos>> pendingChanges = new HashMap<Long, List<BlockPos>>();

    private boolean arrayParticleSpawned;
    private boolean initialChunkLoadRequested;
    private boolean craterChunksRequested;
    private boolean impactStarted;
    private boolean destructionStarted;
    private boolean destructionDone;
    private boolean soundPlayed;
    private int centerBX;
    private int centerBZ;
    private int minY;
    private int maxY;
    private int columnIndex;
    private int scanY;

    public EntityHeavenStrike(World worldIn) {
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
        return Math.max(this.ticksExisted, this.livingAge);
    }

    /**
     * Height of the sphere's centre. It is deliberately kept above the cloud
     * layer; custom geometry is not clipped by the world height, so dimensions
     * with a very high cloud layer still show the complete sphere.
     *
     * <p>Client-only because {@link net.minecraft.world.WorldProvider#getCloudHeight()}
     * is itself a client-side method in 1.12.</p>
     */
    @SideOnly(Side.CLIENT)
    public static double getSphereCenterY(World world, double targetY) {
        float cloudHeight = world.provider.getCloudHeight();
        double cloudBase;
        if (!Float.isNaN(cloudHeight) && cloudHeight > 0.0F) {
            cloudBase = cloudHeight + SPHERE_RADIUS;
        } else {
            cloudBase = world.getActualHeight() - SPHERE_RADIUS * 0.75D;
        }
        return Math.max(targetY + 96.0D, cloudBase);
    }

    /** Current sphere radius used by the client while the ball is charging. */
    public static double getSphereRadiusAt(double age) {
        double t = clamp((age - SPHERE_START_TICK)
                / (double) (SPHERE_COMPLETE_TICK - SPHERE_START_TICK));
        return SPHERE_RADIUS * (0.02D + 0.98D * smoothstep(t));
    }

    public void prepareInitialChunkLoad() {
        if (this.world.isRemote || this.initialChunkLoadRequested) {
            return;
        }
        this.initialChunkLoadRequested = true;
        NuclearStrikeChunkLoader.forceSquare(this.world, this.posX, this.posZ, 1, this.chunkTickets);
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("heavenStrikeImpact", this.impactStarted);
        compound.setBoolean("heavenStrikeDestroyed", this.destructionDone);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.impactStarted = compound.getBoolean("heavenStrikeImpact");
        this.destructionDone = compound.getBoolean("heavenStrikeDestroyed");
        // Rebuild the column list after a reload if the carving was unfinished.
        this.destructionStarted = false;
        this.initialChunkLoadRequested = false;
        this.craterChunksRequested = false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead || this.world.isRemote) {
            return;
        }

        int age = this.getSpellAge();
        if (!this.craterChunksRequested && age >= SPHERE_COMPLETE_TICK) {
            // Start loading the 64-block crater area while the sphere is still
            // descending, so the first destruction tick does not stall on
            // synchronous chunk generation.
            this.craterChunksRequested = true;
            NuclearStrikeChunkLoader.forceSquare(this.world, this.posX, this.posZ,
                    CHUNK_TICKET_RADIUS, this.chunkTickets);
        }
        if (age < IMPACT_TICK) {
            return;
        }

        if (!this.impactStarted) {
            this.impact();
        }

        int sinceImpact = age - IMPACT_TICK;
        if (!this.destructionStarted && sinceImpact >= DESTRUCTION_START_DELAY) {
            this.initDestruction();
        }
        if (this.destructionStarted && !this.destructionDone) {
            this.tickDestruction();
        }

        if (sinceImpact >= AFTERSHOCK_INTERVAL && sinceImpact <= AFTERSHOCK_TICKS
                && sinceImpact % AFTERSHOCK_INTERVAL == 0) {
            double scale = Math.pow(AFTERSHOCK_SCALE,
                    sinceImpact / AFTERSHOCK_INTERVAL);
            this.applyAftershock(scale);
        }

        if ((this.destructionDone && sinceImpact >= AFTERSHOCK_TICKS + FADE_TICKS)
                || age >= MAX_LIFE_TICKS) {
            this.setDead();
        }
    }

    private void impact() {
        if (this.impactStarted) {
            return;
        }
        this.impactStarted = true;

        // The future crater is normally loaded during the descent. This is a
        // fallback for unusual ticks where the age skipped the preload.
        if (!this.craterChunksRequested) {
            this.craterChunksRequested = true;
            NuclearStrikeChunkLoader.forceSquare(this.world, this.posX, this.posZ,
                    CHUNK_TICKET_RADIUS, this.chunkTickets);
        }
        this.applyImpactDamage();

        if (!this.soundPlayed) {
            this.soundPlayed = true;
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE,
                    12.0F, 0.35F);
            this.world.playSound(null, this.posX, this.posY, this.posZ,
                    SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER,
                    10.0F, 0.45F);
        }
    }


    private void applyImpactDamage() {
        this.applyAreaDamage(MAGIC_DAMAGE, TRUE_DAMAGE, NOMINAL_DAMAGE, FIRE_TICKS);
    }

    private void applyAftershock(double scale) {
        float magic = (float) (MAGIC_DAMAGE * scale);
        float trueDamage = (float) (TRUE_DAMAGE * scale);
        float nominal = (float) (NOMINAL_DAMAGE * scale);
        if (magic <= 0.0F && trueDamage <= 0.0F) {
            return;
        }
        this.applyAreaDamage(magic, trueDamage, nominal, AFTERSHOCK_FIRE_TICKS);
    }

    private void applyAreaDamage(float magic, float trueDamage,
                                 float nominal, int fireTicks) {
        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - DAMAGE_RADIUS, 0.0D, this.posZ - DAMAGE_RADIUS,
                this.posX + DAMAGE_RADIUS, this.world.getActualHeight(),
                this.posZ + DAMAGE_RADIUS);
        List<Entity> targets = this.world.getEntitiesWithinAABB(Entity.class, area);
        MagicDamage source = new MagicDamage("heaven_strike");
        source.setAttacker(this.shootingEntity);
        source.setDamageIsAbsolute();

        for (Entity target : targets) {
            if (!isValidTarget(target, this.shootingEntity)) {
                continue;
            }
            double dx = target.posX - this.posX;
            double dz = target.posZ - this.posZ;
            if (dx * dx + dz * dz > DAMAGE_RADIUS_SQ) {
                continue;
            }
            this.damageTarget(target, source, magic, trueDamage, nominal, fireTicks);
        }
    }

    private void damageTarget(Entity target, MagicDamage source,
                              float magic, float trueDamage,
                              float nominal, int fireTicks) {
        if (!(target instanceof EntityLivingBase)) {
            if (magic > 0.0F) {
                target.attackEntityFrom(source, magic);
            }
            return;
        }

        EntityLivingBase living = (EntityLivingBase) target;
        float health = living.getHealth();
        if (health <= 0.0F) {
            return;
        }

        // "sethealth" true damage: the synchronized health value is written
        // directly, bypassing resistance, caps and ordinary invulnerability.
        // This alone is 25% of the total nominal damage, i.e. always above the
        // required 10% (102.4) minimum.
        if (trueDamage >= health) {
            if (!KillUtil.setHealthDirect(living, 0.0F)) {
                living.setHealth(0.0F);
            }
            living.onDeath(source);
            return;
        }
        float remaining = health - trueDamage;
        if (!KillUtil.setHealthDirect(living, remaining)) {
            living.setHealth(remaining);
        }

        // The magic part uses the normal damage path and MagicDamageUtil, which
        // guarantees at least 10% of the nominal 1024 damage if a damage cap or
        // invulnerability would otherwise swallow the hit.
        if (magic > 0.0F) {
            living.hurtResistantTime = 0;
            MagicDamageUtil.attackEntity(living, source, magic, nominal);
        }
        if (living.isEntityAlive() && fireTicks > 0) {
            living.setFire(fireTicks);
        }
    }

    private static boolean isValidTarget(Entity target, EntityLivingBase owner) {
        if (target == null || target == owner || target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (player.isCreative() || player.isSpectator()) {
                return false;
            }
        }
        return true;
    }


    private void initDestruction() {
        if (this.destructionStarted) {
            return;
        }
        this.destructionStarted = true;
        this.centerBX = MathHelper.floor(this.posX);
        this.centerBZ = MathHelper.floor(this.posZ);
        this.minY = EFFECT_MIN_Y;
        this.maxY = Math.max(this.minY + 1, this.world.getActualHeight() - 1);

        int radius = MathHelper.ceil(DAMAGE_RADIUS);
        for (int x = this.centerBX - radius; x <= this.centerBX + radius; ++x) {
            for (int z = this.centerBZ - radius; z <= this.centerBZ + radius; ++z) {
                double dx = x + 0.5D - this.posX;
                double dz = z + 0.5D - this.posZ;
                double distanceSq = dx * dx + dz * dz;
                if (distanceSq <= DAMAGE_RADIUS_SQ) {
                    this.columns.add(new Column(x, z, distanceSq));
                }
            }
        }
        // Carve from the centre outwards so the hole follows the shockwave.
        Collections.sort(this.columns, new Comparator<Column>() {
            @Override
            public int compare(Column a, Column b) {
                return Double.compare(a.distanceSq, b.distanceSq);
            }
        });
        this.columnIndex = 0;
        this.scanY = this.maxY;
        this.destructionDone = this.columns.isEmpty();
    }

    private void tickDestruction() {
        if (this.destructionDone || this.columns.isEmpty()) {
            this.destructionDone = true;
            return;
        }

        int budget = DESTROY_BLOCK_BUDGET;
        int work = 0;
        while (budget > 0 && work < DESTROY_WORK_LIMIT
                && this.columnIndex < this.columns.size()) {
            Column column = this.columns.get(this.columnIndex);
            if (this.scanY < this.minY) {
                ++this.columnIndex;
                this.scanY = this.maxY;
                continue;
            }

            int y = this.scanY--;
            ++work;
            if (y < this.minY || y > this.maxY) {
                continue;
            }
            BlockPos pos = new BlockPos(column.x, y, column.z);
            IBlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();
            if (block.isAir(state, this.world, pos)) {
                continue;
            }
            if (state.getBlockHardness(this.world, pos) < 0.0F) {
                // Bedrock, barriers and every other hardness -1 block survive.
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
        if (this.columnIndex >= this.columns.size()) {
            this.destructionDone = true;
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
            new HeavenStrikeArrayParticle(this);
        }

        double age = this.getSpellAge() + partialTicks;
        if (age >= VISUAL_END_TICK) {
            return;
        }
        double rate;
        if (age >= IMPACT_TICK) {
            rate = age - IMPACT_TICK < 50.0D ? 90.0D : 10.0D;
        } else if (age >= SPHERE_COMPLETE_TICK) {
            rate = 72.0D;
        } else if (age >= SPHERE_START_TICK) {
            rate = 48.0D;
        } else {
            rate = 26.0D;
        }
        int count = this.consumeParticleBudget(rate, deltaTicks);
        for (int i = 0; i < count; ++i) {
            this.spawnPhaseSpark(age);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnPhaseSpark(double age) {
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;
        float size;
        int life;

        double sphereCenter = getSphereCenterY(this.world, this.posY);
        if (age < SPHERE_START_TICK) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double arrayScale = 0.05D + 0.95D * smoothstep(clamp(age
                    / (double) ARRAY_BUILD_TICKS));
            double radius = ARRAY_RADIUS * arrayScale
                    * (0.55D + this.rand.nextDouble() * 0.42D);
            x = this.posX + Math.cos(angle) * radius;
            y = this.posY + ARRAY_FLOAT_HEIGHT + 0.2D
                    + this.rand.nextDouble() * 2.2D;
            z = this.posZ + Math.sin(angle) * radius;
            vx = (this.posX - x) * 0.010D;
            vy = 0.06D + this.rand.nextDouble() * 0.10D;
            vz = (this.posZ - z) * 0.010D;
            size = 0.7F + this.rand.nextFloat() * 1.4F;
            life = 12 + this.rand.nextInt(14);
        } else if (age < SPHERE_COMPLETE_TICK) {
            double radius = getSphereRadiusAt(age);
            double theta = this.rand.nextDouble() * Math.PI;
            double phi = this.rand.nextDouble() * Math.PI * 2.0D;
            double shell = radius * (0.96D + this.rand.nextDouble() * 0.08D);
            x = this.posX + Math.sin(theta) * Math.cos(phi) * shell;
            y = sphereCenter + Math.cos(theta) * shell;
            z = this.posZ + Math.sin(theta) * Math.sin(phi) * shell;
            vx = Math.sin(theta) * Math.cos(phi) * 0.08D;
            vy = Math.cos(theta) * 0.08D;
            vz = Math.sin(theta) * Math.sin(phi) * 0.08D;
            size = 1.0F + this.rand.nextFloat() * 2.2F;
            life = 12 + this.rand.nextInt(18);
        } else if (age < IMPACT_TICK) {
            double fall = clamp((age - SPHERE_COMPLETE_TICK) / (double) DESCENT_TICKS);
            double radius = SPHERE_RADIUS * (1.0D - 0.22D * fall);
            double theta = this.rand.nextDouble() * Math.PI;
            double phi = this.rand.nextDouble() * Math.PI * 2.0D;
            x = this.posX + Math.sin(theta) * Math.cos(phi) * radius;
            y = sphereCenter - (sphereCenter - this.posY) * fall * fall
                    + Math.cos(theta) * radius * (1.0D + 0.6D * fall);
            z = this.posZ + Math.sin(theta) * Math.sin(phi) * radius;
            vx = Math.sin(theta) * Math.cos(phi) * 0.12D;
            vy = 0.12D + this.rand.nextDouble() * 0.20D;
            vz = Math.sin(theta) * Math.sin(phi) * 0.12D;
            size = 1.2F + this.rand.nextFloat() * 2.6F;
            life = 10 + this.rand.nextInt(16);
        } else {
            double since = age - IMPACT_TICK;
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = this.rand.nextDouble() * DAMAGE_RADIUS
                    * (since < 50.0D ? 1.0D : 1.25D);
            x = this.posX + Math.cos(angle) * radius;
            y = this.posY + 0.4D + this.rand.nextDouble()
                    * (since < 50.0D ? 28.0D : 8.0D);
            z = this.posZ + Math.sin(angle) * radius;
            double speed = since < 50.0D
                    ? 0.20D + this.rand.nextDouble() * 0.75D
                    : 0.02D + this.rand.nextDouble() * 0.10D;
            vx = Math.cos(angle) * speed;
            vy = 0.08D + this.rand.nextDouble() * 0.50D;
            vz = Math.sin(angle) * speed;
            size = since < 50.0D
                    ? 1.5F + this.rand.nextFloat() * 3.5F
                    : 0.6F + this.rand.nextFloat() * 1.4F;
            life = since < 50.0D ? 14 + this.rand.nextInt(22) : 10 + this.rand.nextInt(12);
        }

        float red = 0.48F + this.rand.nextFloat() * 0.34F;
        float green = 0.02F + this.rand.nextFloat() * 0.20F;
        float blue = 0.92F + this.rand.nextFloat() * 0.08F;
        if (this.rand.nextInt(7) == 0) {
            red = 0.88F;
            green = 0.72F + this.rand.nextFloat() * 0.18F;
            blue = 1.0F;
        }
        new SkyStrikeSparkParticle(this.world, x, y, z, vx, vy, vz,
                red, green, blue, size, life);
    }


    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }

    private static final class Column {
        private final int x;
        private final int z;
        private final double distanceSq;

        private Column(int x, int z, double distanceSq) {
            this.x = x;
            this.z = z;
            this.distanceSq = distanceSq;
        }
    }
}

