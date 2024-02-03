package com.xyazh.kanake.block.blocks.clean;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.api.IManaStorage;
import com.xyazh.kanake.damage.CleanDamage;
import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class TileClean extends TileEntity implements ITickable {
    public static final int DAMAGE = 10;
    public int range = 64;
    public double renderY = 0;
    public double innerRotate = 0;
    public double lastRenderY = 0;
    public double lastRotate = 0;
    public int age = 0;
    protected AxisAlignedBB atkAABB = null;
    protected AxisAlignedBB extractAABB = null;
    protected final Vec3d vecPos = new Vec3d(0, 0, 0);
    protected IManaStorage storageTile = null;
    protected EntityPlayerMP storageEntity = null;

    public TileClean() {
        super();
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public boolean shouldRenderCircle() {
        return age > 0;
    }

    private void timeIt(double circleHeight) {
        this.lastRenderY = this.renderY;
        this.renderY = circleHeight - this.pos.getY();
        this.lastRotate = this.innerRotate;
        this.innerRotate += 1;
    }

    protected void findStorageTile() {
        for (int i = -4; i <= 4; i++) {
            for (int j = -4; j <= 4; j++) {
                for (int k = -4; k <= 4; k++) {
                    BlockPos pos = this.pos.add(i, j, k);
                    TileEntity te = this.world.getTileEntity(pos);
                    if (te instanceof IManaStorage) {
                        this.storageTile = (IManaStorage) te;
                        if (!this.storageTile.isManaEmpty() && this.storageTile.canExtract(this)) {
                            return;
                        } else {
                            this.storageTile = null;
                        }
                    }
                }
            }
        }
    }

    protected void findStorageEntity() {
        if (this.extractAABB == null) {
            double ax, ay, az, ax1, ay1, az1;
            ax = this.pos.getX() + 4;
            ay = this.pos.getY() + 4;
            az = this.pos.getZ() + 4;
            ax1 = this.pos.getX() - 4;
            ay1 = this.pos.getY() - 4;
            az1 = this.pos.getZ() - 4;
            this.extractAABB = new AxisAlignedBB(ax, ay, az, ax1, ay1, az1);
        }
        for (EntityPlayerMP playerMP : world.getPlayers(EntityPlayerMP.class, (playerMP) -> !ManaData.isEmpty(playerMP) && this.extractAABB.contains(new net.minecraft.util.math.Vec3d(playerMP.posX, playerMP.posY, playerMP.posZ)))) {
            this.storageEntity = playerMP;
            return;
        }
    }

    protected void findStorage() {
        this.findStorageTile();
        if (this.storageTile == null) {
            this.findStorageEntity();
        }
    }

    protected void work() {
        if (this.age == 1) {
            this.setNotify();
        }
        boolean flag = false;
        if (this.age -- > 0) {
            this.damageEntities();
            flag = true;
        }
        if(flag){
            return;
        }
        if (this.storageTile instanceof TileEntity) {
            TileEntity te = (TileEntity) this.storageTile;
            if (te == this.world.getTileEntity(te.getPos()) && this.storageTile.canExtract(this)) {
                double mana = this.storageTile.extractMana(1000);
                this.age = (int) mana / 5;
                flag = true;
                if (this.age > 0) {
                    this.setNotify();
                }
                if (this.storageTile instanceof TileEntity) {
                    this.spawnParticle(this.age,
                            te.getPos().getX() + 0.5,
                            te.getPos().getY() + 0.5,
                            te.getPos().getZ() + 0.5);
                }
                if (this.storageTile.isManaEmpty() || !this.storageTile.canExtract(this)) {
                    this.storageTile = null;
                }
            }
        }
        if(flag){
            return;
        }
        if (this.storageEntity != null) {
            double mana = ManaData.subSync(this.storageEntity, 1000);
            this.age = (int) mana / 5;
            if (this.age > 0) {
                this.setNotify();
            }
            this.spawnParticle(this.age,
                    this.storageEntity.posX,
                    this.storageEntity.posY + 1,
                    this.storageEntity.posZ);
            if (ManaData.isEmpty(this.storageEntity)) {
                this.storageEntity = null;
            }
        }
        if(this.storageTile==null&&this.storageEntity ==null){
            this.findStorage();
        }
    }

    public void spawnParticle(int n, double x, double y, double z) {
        int itemId = Item.getIdFromItem(ModItems.TEST_ITEM);
        ModParticles.remoteSpawnParticle(
                this.world, n, ModParticles.MANA_PARTICLES,
                x, y, z,
                this.pos.getX() + 0.5, this.pos.getY() + 1.5, this.pos.getZ() + 0.5,
                itemId);
    }

    @Override
    public void update() {
        double circleHeight = world.getWorldInfo().getTerrainType().getCloudHeight();
        circleHeight = Math.min(128, circleHeight);
        this.timeIt(circleHeight);
        if (this.world.isRemote) {
            if (this.shouldRenderCircle()) {
                for (int i = 0; i < 3; i++) {
                    double radius = Kanake.rand.nextDouble() * range;
                    double theta = 2 * Math.PI * Kanake.rand.nextDouble();
                    double rx = radius * Math.cos(theta);
                    double rz = radius * Math.sin(theta);
                    this.world.spawnParticle(ModParticles.BIIMU_PARTICLES, this.pos.getX() + rx, circleHeight, this.pos.getZ() + rz, 0, -4, 0);
                }
            }
        } else {
            this.work();
        }
    }

    public void damageEntities() {
        if (this.atkAABB == null) {
            double ax, ay, az, ax1, ay1, az1;
            ax = this.pos.getX() + this.range;
            ay = 4096;
            az = this.pos.getZ() + this.range;
            ax1 = this.pos.getX() - this.range;
            ay1 = -16;
            az1 = this.pos.getZ() - this.range;
            this.atkAABB = new AxisAlignedBB(ax, ay, az, ax1, ay1, az1);
        }
        this.vecPos.x = this.pos.getX();
        this.vecPos.z = this.pos.getZ();
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, atkAABB, (e) -> {
            if (e instanceof EntityPlayer) {
                return false;
            }
            Vec3d p1 = new Vec3d(e.posX, 0, e.posZ);
            p1.sub(this.vecPos);
            return p1.length() <= this.range;
        })) {
            if (Kanake.rand.nextInt(100) < 10) {
                DamageSource damageSource = CleanDamage.CLEAN_DAMAGE;
                float health = entity.getHealth();
                entity.attackEntityFrom(damageSource, DAMAGE);
                if (entity.getHealth() - health < DAMAGE) {
                    entity.setHealth(Math.max(health - DAMAGE, 0));
                }
            }
        }
    }

    public void setNotify() {
        IBlockState state = this.world.getBlockState(this.pos);
        this.world.notifyBlockUpdate(this.pos, state, state, Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.age = compound.getInteger("age");
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("age", this.age);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("age", this.age);
        return new SPacketUpdateTileEntity(this.pos, 1, compound);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager manager, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        this.age = compound.getInteger("age");
    }
}
