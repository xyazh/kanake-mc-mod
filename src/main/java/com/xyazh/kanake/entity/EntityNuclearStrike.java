package com.xyazh.kanake.entity;

import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.util.KillUtil;
import com.xyazh.kanake.particle.simple.particles.ExplosionFlashParticle;
import com.xyazh.kanake.particle.simple.particles.NuclearAshParticle;
import com.xyazh.kanake.particle.simple.particles.NuclearSmokeParticle;
import com.xyazh.kanake.particle.simple.particles.NuclearStrikeParticle;
import com.xyazh.kanake.particle.simple.particles.SkyStrikeSparkParticle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.potion.PotionEffect;
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
 * Nuclear Strike. Invisible ritual entity; visuals are procedural particles.
 * The caster is included in blast, knockback, fire and radiation damage.
 */
public class EntityNuclearStrike extends EntityShoot {
    public static final double CAST_RANGE = 384.0D;
    public static final double BLAST_RADIUS = 192.0D;
    public static final double AIRBURST_RADIUS = 224.0D;
    public static final double CORE_RADIUS = 40.0D;
    public static final double INNER_RADIUS = 96.0D;
    public static final double OUTER_RADIUS = 160.0D;
    public static final double CRATER_RADIUS = 88.0D;
    public static final int CRATER_DEPTH = 64;
    public static final double RADIATION_RADIUS = 160.0D;

    public static final int IMPACT_TICK = 120;
    public static final int CHARGE_END_TICK = 80;
    public static final int CRATER_LOAD_START_TICK = 90;
    public static final int RADIATION_INTERVAL = 40;
    public static final int RADIATION_TICKS = 600;
    // The upper hemisphere adds extra carving work. Keep the ritual alive long
    // enough for unusually large overhangs, but end it as soon as both the
    // crater and the radiation field are finished.
    public static final int TOTAL_TICKS = IMPACT_TICK + RADIATION_TICKS + 280;

    public static final float CORE_FRACTION = 0.60F;
    public static final float CORE_DAMAGE = 1000.0F;
    public static final float INNER_FRACTION = 0.30F;
    public static final float INNER_DAMAGE = 400.0F;
    public static final float OUTER_MAX_DAMAGE = 200.0F;
    public static final float OUTER_MIN_DAMAGE = 50.0F;
    public static final float FRINGE_MAX_DAMAGE = 50.0F;
    public static final float FRINGE_MIN_DAMAGE = 10.0F;
    public static final float RADIATION_DAMAGE = 6.0F;

    public static final int FIRE_RADIUS = 128;
    public static final int FIRE_LIMIT = 256;
    private static final int CRATER_BLOCK_BUDGET = 2048;
    private static final int CRATER_WORK_LIMIT = 8192;
    private static final int CRATER_TICKET_RADIUS_CHUNKS = 6;

    private static final DataParameter<Boolean> AIRBURST =
            EntityDataManager.createKey(EntityNuclearStrike.class, DataSerializers.BOOLEAN);

    private final List<ForgeChunkManager.Ticket> chunkTickets = new ArrayList<ForgeChunkManager.Ticket>();
    private final List<CraterColumn> craterColumns = new ArrayList<CraterColumn>();
    private final Map<Long, List<BlockPos>> pendingChanges = new HashMap<Long, List<BlockPos>>();

    private int craterColumnIndex;
    private int craterScanY = Integer.MIN_VALUE;
    private int craterFloorY;
    private int craterTopY;
    private int craterUpperTopY;
    private boolean craterLowerPhase;
    private boolean craterInitialized;
    private boolean initialChunkLoadRequested;
    private boolean craterChunksRequested;
    private boolean detonated;
    private boolean destructionDone;
    private boolean firePlaced;
    private boolean impactSoundPlayed;
    private boolean particleSpawned;
    private boolean impactFlashSpawned;

    public EntityNuclearStrike(World worldIn) {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
        this.setInvisible(true);
        this.noClip = true;
        this.speed = 0.0D;
        this.livingMaxAge = TOTAL_TICKS + 20;
    }

    @Override
    public void entityInit() {
        this.dataManager.register(AIRBURST, Boolean.FALSE);
    }

    public void setAirburst(boolean airburst) {
        this.dataManager.set(AIRBURST, airburst);
    }

    public boolean isAirburst() {
        return this.dataManager.get(AIRBURST);
    }

    public double getSpellAge() {
        return Math.max(this.ticksExisted, 0);
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

    public void prepareInitialChunkLoad() {
        if (this.world.isRemote || this.initialChunkLoadRequested) {
            return;
        }
        this.initialChunkLoadRequested = true;
        NuclearStrikeChunkLoader.forceSquare(this.world, this.posX, this.posZ, 1, this.chunkTickets);
    }

    /**
     * At 384 blocks the server usually has not loaded the target chunk when the
     * look ray is tested. If the ray misses and its endpoint lies below the
     * freshly loaded surface, treat it as a ground burst; otherwise keep the
     * intended airburst.
     */
    public void refineTargetAfterChunkLoad() {
        if (this.world.isRemote || !this.isAirburst()) {
            return;
        }
        int x = MathHelper.floor(this.posX);
        int z = MathHelper.floor(this.posZ);
        BlockPos surface = this.world.getHeight(new BlockPos(x, 0, z));
        int surfaceY = surface.getY();
        if (surfaceY > 0 && this.posY <= surfaceY + 1.5D) {
            this.setPosition(this.posX, surfaceY + 0.10D, this.posZ);
            this.setAirburst(false);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead || this.world.isRemote) {
            return;
        }

        int age = this.ticksExisted;
        if (!this.initialChunkLoadRequested) {
            this.prepareInitialChunkLoad();
        }

        if (!this.isAirburst() && !this.craterChunksRequested && age >= CRATER_LOAD_START_TICK) {
            this.craterChunksRequested = true;
            NuclearStrikeChunkLoader.forceSquare(this.world, this.posX, this.posZ,
                    CRATER_TICKET_RADIUS_CHUNKS, this.chunkTickets);
        }

        if (!this.detonated && age >= IMPACT_TICK) {
            this.detonate();
        }

        if (this.detonated) {
            if (!this.destructionDone) {
                this.tickDestruction();
            }
            if (!this.impactSoundPlayed) {
                this.impactSoundPlayed = true;
                this.world.playSound(null, this.posX, this.posY, this.posZ,
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 8.0F, 0.45F);
                this.world.playSound(null, this.posX, this.posY, this.posZ,
                        SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 6.0F, 0.55F);
            }
            int sinceImpact = age - IMPACT_TICK;
            if (sinceImpact >= RADIATION_INTERVAL && sinceImpact <= RADIATION_TICKS
                    && sinceImpact % RADIATION_INTERVAL == 0) {
                this.applyRadiation();
            }
        }

        if (age >= TOTAL_TICKS
                || (this.destructionDone && age >= IMPACT_TICK + RADIATION_TICKS)) {
            this.releaseChunkTickets();
            this.setDead();
        }
    }

    private void detonate() {
        if (this.detonated) {
            return;
        }
        this.detonated = true;
        this.applyBlast();
        if (this.isAirburst()) {
            this.destructionDone = true;
            return;
        }
        this.buildCraterColumns();
    }

    private void applyBlast() {
        double maxRadius = this.isAirburst() ? AIRBURST_RADIUS : BLAST_RADIUS;
        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - maxRadius, 0.0D, this.posZ - maxRadius,
                this.posX + maxRadius, this.world.getActualHeight(), this.posZ + maxRadius);
        List<Entity> entities = this.world.getEntitiesWithinAABB(Entity.class, area);
        MagicDamage source = new MagicDamage("nuclear_strike");
        source.setAttacker(this.shootingEntity);
        source.setDamageIsAbsolute();

        for (Entity target : entities) {
            if (target == this || target.isDead || !this.isValidBlastTarget(target)) {
                continue;
            }
            double dx = target.posX - this.posX;
            double dy = target.posY + target.height * 0.5D - (this.posY + 1.0D);
            double dz = target.posZ - this.posZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance > maxRadius) {
                continue;
            }

            float directFraction = 0.0F;
            float absoluteDamage;
            if (distance <= CORE_RADIUS) {
                directFraction = CORE_FRACTION;
                absoluteDamage = CORE_DAMAGE;
            } else if (distance <= INNER_RADIUS) {
                directFraction = INNER_FRACTION;
                absoluteDamage = INNER_DAMAGE;
            } else if (distance <= OUTER_RADIUS) {
                double t = (distance - INNER_RADIUS) / (OUTER_RADIUS - INNER_RADIUS);
                absoluteDamage = (float) (OUTER_MAX_DAMAGE
                        + (OUTER_MIN_DAMAGE - OUTER_MAX_DAMAGE) * t);
            } else if (distance <= BLAST_RADIUS) {
                double t = (distance - OUTER_RADIUS) / (BLAST_RADIUS - OUTER_RADIUS);
                absoluteDamage = (float) (FRINGE_MAX_DAMAGE
                        + (FRINGE_MIN_DAMAGE - FRINGE_MAX_DAMAGE) * t);
            } else if (this.isAirburst() && distance <= AIRBURST_RADIUS) {
                double t = (distance - BLAST_RADIUS) / (AIRBURST_RADIUS - BLAST_RADIUS);
                absoluteDamage = (float) Math.max(2.0D, 10.0D * (1.0D - t));
            } else {
                continue;
            }

            if (!(target instanceof EntityLivingBase)) {
                target.attackEntityFrom(source, absoluteDamage);
                this.applyKnockback(target, dx, dz, distance);
                continue;
            }

            EntityLivingBase living = (EntityLivingBase) target;
            if (directFraction > 0.0F) {
                float directLoss = living.getMaxHealth() * directFraction;
                float remaining = living.getHealth() - directLoss;
                if (remaining <= 0.0F) {
                    if (!KillUtil.setHealthDirect(living, 0.0F)) {
                        living.setHealth(0.0F);
                    }
                    living.onDeath(source);
                    continue;
                }
                if (!KillUtil.setHealthDirect(living, remaining)) {
                    living.setHealth(remaining);
                }
            }

            // Absolute damage bypasses armour, enchantment reduction and the
            // ordinary hurt cooldown, matching the other super-tier scrolls.
            living.hurtResistantTime = 0;
            MagicDamageUtil.attackEntity(living, source, absoluteDamage, absoluteDamage);
            if (living.isEntityAlive()) {
                this.applyKnockback(living, dx, dz, distance);
                if (distance <= FIRE_RADIUS) {
                    living.setFire(8 * 20);
                }
            }
        }
    }

    private void applyKnockback(Entity target, double dx, double dz, double distance) {
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal < 0.0001D) {
            horizontal = 1.0D;
            dx = 1.0D;
            dz = 0.0D;
        }
        double strength;
        double lift;
        if (distance <= CORE_RADIUS) {
            strength = 1.8D;
            lift = 1.0D;
        } else if (distance <= INNER_RADIUS) {
            strength = 1.2D;
            lift = 0.6D;
        } else if (distance <= BLAST_RADIUS) {
            strength = 0.6D;
            lift = 0.3D;
        } else {
            strength = 0.2D;
            lift = 0.1D;
        }
        target.motionX += dx / horizontal * strength;
        target.motionY += lift;
        target.motionZ += dz / horizontal * strength;
        target.velocityChanged = true;
    }

    private boolean isValidBlastTarget(Entity target) {
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            return !player.isCreative() && !player.isSpectator();
        }
        return true;
    }

    private void applyRadiation() {
        AxisAlignedBB area = new AxisAlignedBB(
                this.posX - RADIATION_RADIUS, 0.0D, this.posZ - RADIATION_RADIUS,
                this.posX + RADIATION_RADIUS, this.world.getActualHeight(),
                this.posZ + RADIATION_RADIUS);
        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        MagicDamage source = new MagicDamage("nuclear_fallout");
        source.setAttacker(this.shootingEntity);
        source.setDamageIsAbsolute();

        for (EntityLivingBase target : entities) {
            if (target.isDead) {
                continue;
            }
            if (target instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) target;
                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
            }
            double dx = target.posX - this.posX;
            double dy = target.posY + target.height * 0.5D - this.posY;
            double dz = target.posZ - this.posZ;
            if (dx * dx + dy * dy + dz * dz > RADIATION_RADIUS * RADIATION_RADIUS) {
                continue;
            }
            target.hurtResistantTime = 0;
            MagicDamageUtil.attackEntity(target, source, RADIATION_DAMAGE, RADIATION_DAMAGE);
            target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 120, 0, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 120, 1, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 0, false, true));
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 0, false, true));
        }
    }

    private void buildCraterColumns() {
        int centerX = MathHelper.floor(this.posX);
        int centerZ = MathHelper.floor(this.posZ);
        int radius = MathHelper.ceil(CRATER_RADIUS);
        double radiusSq = CRATER_RADIUS * CRATER_RADIUS;
        for (int x = centerX - radius; x <= centerX + radius; ++x) {
            for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                double dx = x + 0.5D - this.posX;
                double dz = z + 0.5D - this.posZ;
                double distanceSq = dx * dx + dz * dz;
                if (distanceSq <= radiusSq) {
                    this.craterColumns.add(new CraterColumn(x, z, distanceSq));
                }
            }
        }
        // Centre first. The carve follows the expanding shockwave instead of
        // eating the crater from one arbitrary side.
        Collections.sort(this.craterColumns, new Comparator<CraterColumn>() {
            @Override
            public int compare(CraterColumn a, CraterColumn b) {
                return Double.compare(a.distanceSq, b.distanceSq);
            }
        });
        this.craterColumnIndex = 0;
        this.craterScanY = Integer.MIN_VALUE;
        this.craterInitialized = false;
        this.destructionDone = this.craterColumns.isEmpty();
        if (this.destructionDone) {
            this.placeScorchedFire();
        }
    }

    private void tickDestruction() {
        if (this.destructionDone || this.craterColumns.isEmpty()) {
            this.destructionDone = true;
            this.placeScorchedFire();
            return;
        }

        int budget = CRATER_BLOCK_BUDGET;
        int work = 0;
        while (budget > 0 && work < CRATER_WORK_LIMIT
                && this.craterColumnIndex < this.craterColumns.size()) {
            CraterColumn column = this.craterColumns.get(this.craterColumnIndex);
            if (!this.craterInitialized) {
                this.craterInitialized = true;
                int actualHeight = this.world.getActualHeight();
                int topY = Math.max(0, Math.min(actualHeight - 1,
                        MathHelper.floor(this.posY)));
                double depth = CRATER_DEPTH
                        * Math.sqrt(Math.max(0.0D, 1.0D - column.distanceSq
                        / (CRATER_RADIUS * CRATER_RADIUS)));
                int floorY = MathHelper.floor(this.posY - depth);
                this.craterTopY = topY;
                this.craterFloorY = Math.max(0, Math.min(topY, floorY));
                this.craterUpperTopY = Math.min(actualHeight - 1,
                        MathHelper.floor(this.posY + depth));
                this.craterLowerPhase = this.craterUpperTopY <= this.craterTopY;
                this.craterScanY = this.craterLowerPhase
                        ? this.craterTopY : this.craterUpperTopY;
            }

            if (!this.craterLowerPhase) {
                if (this.craterScanY > this.craterTopY) {
                    int y = this.craterScanY--;
                    ++work;
                    if (y >= 0 && y < this.world.getActualHeight()) {
                        BlockPos upperPos = new BlockPos(column.x, y, column.z);
                        IBlockState upperState = this.world.getBlockState(upperPos);
                        if (!upperState.getBlock().isAir(upperState, this.world, upperPos)
                                && upperState.getBlock().getBlockHardness(
                                upperState, this.world, upperPos) >= 0.0F) {
                            this.world.setBlockState(upperPos,
                                    Blocks.AIR.getDefaultState(), 0);
                            this.queueBlockChange(upperPos);
                            --budget;
                        }
                    }
                    if (this.craterScanY <= this.craterTopY) {
                        this.craterLowerPhase = true;
                        this.craterScanY = this.craterTopY;
                    }
                    continue;
                }
                this.craterLowerPhase = true;
                this.craterScanY = this.craterTopY;
            }

            if (this.craterScanY < this.craterFloorY) {
                this.decorateCraterColumn(column);
                ++this.craterColumnIndex;
                this.craterInitialized = false;
                this.craterScanY = Integer.MIN_VALUE;
                continue;
            }

            int y = this.craterScanY--;
            ++work;
            if (y < 0 || y >= this.world.getActualHeight()) {
                continue;
            }
            BlockPos pos = new BlockPos(column.x, y, column.z);
            IBlockState state = this.world.getBlockState(pos);
            if (state.getBlock().isAir(state, this.world, pos)) {
                continue;
            }
            if (state.getBlock().getBlockHardness(state, this.world, pos) < 0.0F) {
                continue;
            }
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
            this.queueBlockChange(pos);
            --budget;
        }

        this.flushBlockChanges();
        if (this.craterColumnIndex >= this.craterColumns.size()) {
            this.destructionDone = true;
            this.placeScorchedFire();
        }
    }

    private void decorateCraterColumn(CraterColumn column) {
        if (this.craterFloorY <= 0 || column.distanceSq > 56.0D * 56.0D) {
            return;
        }
        BlockPos floor = new BlockPos(column.x, this.craterFloorY, column.z);
        IBlockState oldState = this.world.getBlockState(floor);
        if (oldState.getBlock().getBlockHardness(oldState, this.world, floor) < 0.0F) {
            return;
        }
        IBlockState newState = null;
        if (column.distanceSq <= 28.0D * 28.0D) {
            newState = Blocks.MAGMA.getDefaultState();
        } else if (this.rand.nextFloat() < 0.18F) {
            newState = Blocks.OBSIDIAN.getDefaultState();
        }
        if (newState != null) {
            this.world.setBlockState(floor, newState, 0);
            this.queueBlockChange(floor);
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

    private void placeScorchedFire() {
        if (this.firePlaced || this.isAirburst()) {
            return;
        }
        this.firePlaced = true;
        int placed = 0;
        for (int attempt = 0; attempt < FIRE_LIMIT * 3 && placed < FIRE_LIMIT; ++attempt) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = Math.sqrt(this.rand.nextDouble()) * FIRE_RADIUS;
            int x = MathHelper.floor(this.posX + Math.cos(angle) * radius);
            int z = MathHelper.floor(this.posZ + Math.sin(angle) * radius);
            BlockPos heightPos = this.world.getHeight(new BlockPos(x, 0, z));
            int y = heightPos.getY();
            if (y <= 0 || y >= this.world.getActualHeight() - 1) {
                continue;
            }
            BlockPos firePos = new BlockPos(x, y, z);
            if (!this.world.isAirBlock(firePos)) {
                continue;
            }
            if (!this.world.getBlockState(firePos.down()).isFullBlock()) {
                continue;
            }
            this.world.setBlockState(firePos, Blocks.FIRE.getDefaultState(), 2);
            ++placed;
        }
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
        if (!this.particleSpawned) {
            this.particleSpawned = true;
            new NuclearStrikeParticle(this);
        }

        int age = this.ticksExisted;
        if (age >= IMPACT_TICK && !this.impactFlashSpawned) {
            this.impactFlashSpawned = true;
            new ExplosionFlashParticle(this.world, this.posX, this.posY + 2.0D,
                    this.posZ, 0.62F, 0.78F, 1.0F, 150.0F);
        }

        if (age < IMPACT_TICK) {
            int count = this.consumeParticleBudget(2.0D, deltaTicks);
            for (int i = 0; i < count; ++i) {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double radius = Math.sqrt(this.rand.nextDouble())
                        * (this.isAirburst() ? 60.0D : EntityNuclearStrike.CRATER_RADIUS);
                double px = this.posX + Math.cos(angle) * radius;
                double pz = this.posZ + Math.sin(angle) * radius;
                double py = this.posY + 0.3D + this.rand.nextDouble() * 4.0D;
                new SkyStrikeSparkParticle(this.world, px, py, pz,
                        (this.posX - px) * 0.012D,
                        (this.posY - py) * 0.012D,
                        (this.posZ - pz) * 0.012D,
                        1.0F, 0.28F + this.rand.nextFloat() * 0.25F, 0.08F,
                        0.8F + this.rand.nextFloat(), 12 + this.rand.nextInt(12));
            }
            return;
        }

        int sinceImpact = age - IMPACT_TICK;
        if (sinceImpact < 40) {
            int count = this.consumeParticleBudget(80.0D, deltaTicks);
            for (int i = 0; i < count; ++i) {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double radius = 4.0D + this.rand.nextDouble() * 36.0D;
                double px = this.posX + Math.cos(angle) * radius;
                double pz = this.posZ + Math.sin(angle) * radius;
                double py = this.posY + 1.0D + this.rand.nextDouble() * 22.0D;
                double speed = 0.18D + this.rand.nextDouble() * 0.65D;
                new SkyStrikeSparkParticle(this.world, px, py, pz,
                        Math.cos(angle) * speed,
                        0.10D + this.rand.nextDouble() * 0.45D,
                        Math.sin(angle) * speed,
                        1.0F,
                        0.42F + this.rand.nextFloat() * 0.35F,
                        this.rand.nextFloat() * 0.18F,
                        1.2F + this.rand.nextFloat() * 2.4F,
                        14 + this.rand.nextInt(20));
            }
        }
        this.spawnMushroomSmoke(sinceImpact, deltaTicks);
        if (sinceImpact < RADIATION_TICKS) {
            int count = this.consumeParticleBudget(2.0D, deltaTicks);
            for (int i = 0; i < count; ++i) {
                double angle = this.rand.nextDouble() * Math.PI * 2.0D;
                double radius = Math.sqrt(this.rand.nextDouble()) * RADIATION_RADIUS;
                double px = this.posX + Math.cos(angle) * radius;
                double pz = this.posZ + Math.sin(angle) * radius;
                double py = this.posY + 16.0D + this.rand.nextDouble() * 70.0D;
                new NuclearAshParticle(this.world, px, py, pz,
                        (this.rand.nextDouble() - 0.5D) * 0.04D,
                        -0.035D - this.rand.nextDouble() * 0.04D,
                        (this.rand.nextDouble() - 0.5D) * 0.04D,
                        0.6F + this.rand.nextFloat() * 1.0F,
                        80 + this.rand.nextInt(80));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnMushroomSmoke(int sinceImpact, float deltaTicks) {
        if (sinceImpact < 12 || sinceImpact > 380) {
            return;
        }
        double rise = smoothstep(clamp((sinceImpact - 12) / 230.0D));
        double available = Math.max(18.0D,
                this.world.getActualHeight() - this.posY - 8.0D);
        // Slightly smaller mushroom silhouette, but denser and brighter smoke.
        double stemHeight = Math.min(100.0D, available) * rise;
        double capRadius = 70.0D * (0.18D + 0.82D * rise);
        double capY = this.posY + stemHeight;
        // The cloud grows from a small column into a broad cap. Later puffs are
        // deliberately larger so the growing cloud does not become sparse.
        double cloudScale = 1.0D + 1.1D * rise;

        // Stem: many vanilla smoke puffs rising from the blast column.
        int stemCount = this.consumeParticleBudget(30.0D, deltaTicks);
        for (int i = 0; i < stemCount; ++i) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = 5.0D + 13.0D * rise + this.rand.nextDouble() * 9.0D;
            double px = this.posX + Math.cos(angle) * radius;
            double pz = this.posZ + Math.sin(angle) * radius;
            double py = this.posY + 1.0D + this.rand.nextDouble() * stemHeight * 0.86D;
            float shade = 0.38F + this.rand.nextFloat() * 0.18F;
            float stemSize = (float) ((2.2F + this.rand.nextFloat() * 3.0F) * cloudScale);
            NuclearSmokeParticle.spawn(this.world, px, py, pz,
                    Math.cos(angle) * (0.018D + this.rand.nextDouble() * 0.045D),
                    0.045D + this.rand.nextDouble() * 0.075D,
                    Math.sin(angle) * (0.018D + this.rand.nextDouble() * 0.045D),
                    stemSize,
                    30 + this.rand.nextInt(41),
                    shade * 1.05F, shade * 0.82F, shade * 0.70F);
        }

        // Cap: a dense torus and dome built entirely from small vanilla smoke
        // puffs. The old procedural cloud geometry is intentionally gone.
        int capCount = this.consumeParticleBudget(90.0D, deltaTicks);
        for (int i = 0; i < capCount; ++i) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            boolean dome = this.rand.nextDouble() < 0.24D;
            double major = dome ? capRadius * this.rand.nextDouble() * 0.55D
                    : capRadius * (0.50D + this.rand.nextDouble() * 0.50D);
            double vertical = dome
                    ? capRadius * (0.08D + this.rand.nextDouble() * 0.18D)
                    : (this.rand.nextDouble() - 0.5D) * (12.0D + 10.0D * rise);
            double px = this.posX + Math.cos(angle) * major;
            double pz = this.posZ + Math.sin(angle) * major;
            double py = capY + vertical;
            float shade = 0.34F + this.rand.nextFloat() * 0.22F;
            float red = shade * 1.08F;
            float green = shade * 0.82F;
            float blue = shade * 0.72F;
            if (this.rand.nextFloat() < 0.20F) {
                red = 0.84F + this.rand.nextFloat() * 0.16F;
                green = 0.36F + this.rand.nextFloat() * 0.16F;
                blue = 0.10F + this.rand.nextFloat() * 0.12F;
            }
            float capSize = (float) ((3.2F + this.rand.nextFloat() * 4.8F) * cloudScale);
            NuclearSmokeParticle.spawn(this.world, px, py, pz,
                    Math.cos(angle) * (0.025D + this.rand.nextDouble() * 0.055D),
                    0.010D + this.rand.nextDouble() * 0.035D,
                    Math.sin(angle) * (0.025D + this.rand.nextDouble() * 0.055D),
                    capSize,
                    35 + this.rand.nextInt(51),
                    red, green, blue);
        }

        // Base surge: a low expanding ring of dust around ground zero.
        int baseCount = this.consumeParticleBudget(22.0D, deltaTicks);
        for (int i = 0; i < baseCount; ++i) {
            double angle = this.rand.nextDouble() * Math.PI * 2.0D;
            double radius = Math.sqrt(this.rand.nextDouble()) * 90.0D * rise;
            double px = this.posX + Math.cos(angle) * radius;
            double pz = this.posZ + Math.sin(angle) * radius;
            double py = this.posY + 0.4D + this.rand.nextDouble() * 2.5D;
            float baseSize = (float) ((2.2F + this.rand.nextFloat() * 3.8F) * cloudScale);
            NuclearSmokeParticle.spawn(this.world, px, py, pz,
                    Math.cos(angle) * (0.09D + this.rand.nextDouble() * 0.13D),
                    0.008D + this.rand.nextDouble() * 0.025D,
                    Math.sin(angle) * (0.09D + this.rand.nextDouble() * 0.13D),
                    baseSize,
                    25 + this.rand.nextInt(31),
                    0.46F + this.rand.nextFloat() * 0.14F,
                    0.42F + this.rand.nextFloat() * 0.12F,
                    0.38F + this.rand.nextFloat() * 0.10F);
        }
    }

    private static double clamp(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static double smoothstep(double value) {
        double x = clamp(value);
        return x * x * (3.0D - 2.0D * x);
    }

    private static final class CraterColumn {
        private final int x;
        private final int z;
        private final double distanceSq;

        private CraterColumn(int x, int z, double distanceSq) {
            this.x = x;
            this.z = z;
            this.distanceSq = distanceSq;
        }
    }
}
