package com.xyazh.kanake.entity;

import com.xyazh.kanake.Magic;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.util.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.UUID;

public class EntityEmptyMagic extends EntityShoot {
    public double speed = 0.0;
    public EntityLivingBase shootingEntity = null;
    protected BlockPos blockPos = null;
    public LinkedList<Integer> order = new LinkedList<>();
    public int stop = 0;

    private static final DataParameter<Float> SPEED = EntityDataManager.<Float>createKey(EntityEmptyMagic.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MOTION_X = EntityDataManager.<Float>createKey(EntityEmptyMagic.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MOTION_Y = EntityDataManager.<Float>createKey(EntityEmptyMagic.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> MOTION_Z = EntityDataManager.<Float>createKey(EntityEmptyMagic.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> STOP = EntityDataManager.<Integer>createKey(EntityEmptyMagic.class, DataSerializers.VARINT);

    public EntityEmptyMagic(World worldIn) {
        super(worldIn);
        this.dataManager.register(SPEED, (float) this.speed);
        this.dataManager.register(MOTION_X, (float) this.forward.x);
        this.dataManager.register(MOTION_Y, (float) this.forward.y);
        this.dataManager.register(MOTION_Z, (float) this.forward.z);
        this.dataManager.register(STOP, this.stop);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected boolean customMotion() {
        return true;
    }

    public void setOrder(LinkedList<Integer> order) {
        this.order = new LinkedList<>(order);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.execute();
        this.movingUpdate();
        this.spawnParticle();
    }

    public void movingUpdate(){
        this.motionX = this.forward.x * this.speed;
        this.motionY = this.forward.y * this.speed;
        this.motionZ = this.forward.z * this.speed;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    public Vec3d getMotion(){
        return new Vec3d(this.forward.x * this.speed, this.forward.y * this.speed, this.forward.z * this.speed);
    }

    public void execute() {
        if (this.order.size() <= 0) {
            return;
        }
        Vec3d dMotion, motion;
        switch (this.order.poll()) {
            case Magic.SPAWN:
                EntityEmptyMagic entity = this.copy();
                this.world.spawnEntity(entity);
                break;
            case Magic.SPEED_LOW:
                this.speed += 0.5;
                break;
            case Magic.SPEED_MEDIUM:
                this.speed += 1.0;
                break;
            case Magic.SPEED_HIGH:
                this.speed += 1.5;
                break;
            case Magic.RANDOM_SPEED_LOW:
                dMotion = Vec3d.random(0.5);
                motion = this.getMotion();
                motion.add(dMotion);
                this.speed = motion.normalizeAndLength();
                this.setForward(motion);
                break;
            case Magic.RANDOM_SPEED_MEDIUM:
                dMotion = Vec3d.random(1.0);
                motion = this.getMotion();
                motion.add(dMotion);
                this.speed = motion.normalizeAndLength();
                this.setForward(motion);
                break;
            case Magic.RANDOM_SPEED_HIGH:
                dMotion = Vec3d.random(1.5);
                motion = this.getMotion();
                motion.add(dMotion);
                this.speed = motion.normalizeAndLength();
                this.setForward(motion);
                break;
            case Magic.NOP_LOW:
                this.speed -= 0.5;
                break;
            case Magic.NOP_MEDIUM:
                this.speed -= 1.0;
                break;
            case Magic.NOP_HIGH:
                this.speed -= 1.5;
                break;
            case Magic.CONCURRENT:
                this.speed = 0.0;
        }
    }

    public EntityEmptyMagic copy() {
        EntityEmptyMagic entity = new EntityEmptyMagic(this.world);
        entity.order.addAll(this.order);
        entity.setPosition(this.posX, this.posY, this.posZ);
        return entity;
    }

    public void spawnParticle() {
        if (this.world.isRemote) {
            for (int i = 0; i <= 20; i++) {
                this.world.spawnParticle(ModParticles.MAGIC_PARTICLES, posX, posY, posZ, 0, 0, 0);
                this.world.spawnParticle(ModParticles.MAGIC_PARTICLES1, posX, posY, posZ, 0, 0, 0);
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
    }
}
