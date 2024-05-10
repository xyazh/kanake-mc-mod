package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.magic.Magic;
import com.xyazh.kanake.magic.command.Command;
import com.xyazh.kanake.magic.command.CommandJmp;
import com.xyazh.kanake.magic.command.CommandNop;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.network.IEntityDataParameter;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.potion.buff.PotionKoori;
import com.xyazh.kanake.util.TagUtil;
import com.xyazh.kanake.util.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class EntityEmptyMagic extends EntityShoot{
    protected final LinkedList<Integer> ORDER_QUEUE = new LinkedList<>();
    public LinkedList<Integer> order = new LinkedList<>();
    public LinkedList<Integer> callback = new LinkedList<>();
    public float gdy = 0.0f;
    public float g = -0.0048f;
    public int temperature = 0;
    public int lastOrderAge = 0;
    public boolean isSubMagic = false;
    public boolean settingDead = false;
    public boolean isStop = false;


    public EntityEmptyMagic(World worldIn) {
        super(worldIn);
        this.speed = 0.0;
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
        this.tryPollOrder();
        if (this.onHurt()) {
            this.setDeadParticle();
            this.setDead();
        }
        if (this.collided) {
            this.setDeadParticle();
            this.setDead();
        }
    }

    @Override
    public void setDead() {
        if(this.settingDead){
            return;
        }
        this.settingDead = true;
        for (int oid : this.callback) {
            Command command = Magic.ORDER_MAP.get(oid);
            if (command == null) {
                Kanake.logger.warn("Command not found: {}", oid);
                continue;
            }
            command.execute(this);
        }
        super.setDead();
    }

    public void movingUpdate() {
        double speed = this.speed;
        if (this.isStop) {
            speed = 0.0;
        }
        this.gdy += this.g;
        this.motionX = this.forward.x * speed;
        this.motionY = this.forward.y * speed + this.gdy;
        this.motionZ = this.forward.z * speed;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    public Vec3d getMotion() {
        return new Vec3d(this.forward.x * this.speed, this.forward.y * this.speed, this.forward.z * this.speed);
    }

    public void tryPollOrder() {
        if (this.world.isRemote) {
            return;
        }
        if (this.lastOrderAge-- > 0) {
            return;
        }
        this.lastOrderAge = 10;
        if (this.order.size() <= 0) {
            return;
        }
        int oid = this.order.poll();
        Command command = Magic.ORDER_MAP.get(oid);
        if (command == null) {
            Kanake.logger.warn("Command not found: {}", oid);
            return;
        }
        if (command == Magic.CONCURRENT) {
            this.lastOrderAge = -1;
            this.tryPollOrder();
            this.lastOrderAge = -1;
            this.tryPollOrder();
            return;
        } else if (command instanceof CommandNop) {
            CommandNop nop = (CommandNop) command;
            this.lastOrderAge = nop.time;
            return;
        } else if (command == Magic.CALLBACK) {
            if (this.order.size() > 0) {
                this.callback.add(this.order.poll());
            }
            return;
        }else if (command instanceof CommandJmp) {
            CommandJmp jmp = (CommandJmp) command;
            if(this.isSubMagic!=jmp.isSubMagic){
                return;
            }
            for(int i = 0; i < jmp.jmp; i++){
                if (this.order.size() <= 0) {
                    return;
                }
                this.order.poll();
            }
            return;
        }else if(command == Magic.CLEAR){
            this.order.clear();
            return;
        }
        this.ORDER_QUEUE.add(oid);
        if (!command.need_sync) {
            return;
        }
        EntityDataPacket packet = EntityDataPacket.getPacket(this);
        if (packet == null) {
            return;
        }
        packet.buffer.writeInt(0);
        packet.buffer.writeInt(oid);
        Kanake.network.sendToAll(packet);
    }

    public void execute() {
        if (this.ORDER_QUEUE.size() <= 0) {
            return;
        }
        int oid = this.ORDER_QUEUE.poll();
        Command command = Magic.ORDER_MAP.get(oid);
        if (command == null) {
            Kanake.logger.warn("Command not found: {}", oid);
            return;
        }
        command.execute(this);
    }

    public EntityEmptyMagic copy() {
        EntityEmptyMagic entity = new EntityEmptyMagic(this.world);
        entity.order.addAll(this.order);
        entity.callback.addAll(this.callback);
        entity.temperature = this.temperature;
        entity.lastOrderAge = this.lastOrderAge;
        entity.shootingEntity = this.shootingEntity;
        entity.setForward(this.forward);
        entity.setPosition(this.posX, this.posY, this.posZ);
        return entity;
    }

    protected boolean onHurt() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(posX + 0.4, posY + 0.4, posZ + 0.4, posX - 0.4, posY - 0.4, posZ - 0.4);
        List<EntityLivingBase> entityLivingBases = world.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB);
        boolean flag = false;
        for (EntityLivingBase entityLivingBase : entityLivingBases) {
            if (entityLivingBase.equals(this.shootingEntity)) {
                continue;
            }
            flag = true;
            DamageSource damageSource = DamageSource.causeIndirectDamage(this, this.shootingEntity);
            damageSource.setMagicDamage();
            int dTemperature = Math.min(Math.abs(this.temperature), 50);
            float damage = 4 + 4 * (dTemperature / 50f);
            if (this.temperature <= -50) {
                entityLivingBase.attackEntityFrom(damageSource, damage);
                entityLivingBase.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI, dTemperature));
            } else if (this.temperature < 50) {
                MagicDamage damageSource1 = new MagicDamage();
                damageSource1.setAttacker(this.shootingEntity);
                entityLivingBase.attackEntityFrom(damageSource, damage);
            } else {
                damageSource.setFireDamage();
                entityLivingBase.attackEntityFrom(damageSource, damage);
                entityLivingBase.setFire(dTemperature / 20);
            }
        }
        return flag;
    }

    public void spawnParticle() {
        if (this.world.isRemote) {
            EnumParticleTypes particle1, particle2;
            if (this.temperature <= -50) {
                particle1 = ModParticles.ICE_PARTICLES;
                particle2 = ModParticles.ICE_PARTICLES1;
            } else if (this.temperature < 50) {
                particle1 = ModParticles.MAGIC_PARTICLES;
                particle2 = ModParticles.MAGIC_PARTICLES1;
            } else {
                particle1 = ModParticles.SPARK_PARTICLES;
                particle2 = ModParticles.SPARK_PARTICLES1;
            }
            for (int i = 0; i < 10; i++) {
                this.world.spawnParticle(particle1, posX, posY, posZ, 0, 0, 0);
                this.world.spawnParticle(particle2, posX, posY, posZ, 0, 0, 0);
            }
        }
    }

    protected void setDeadParticle() {
        if (this.world.isRemote) {
            EnumParticleTypes particle;
            if (this.temperature <= -50) {
                particle = ModParticles.TEST_PARTICLES1;
            } else if (this.temperature < 50) {
                particle = ModParticles.TEST_PARTICLES2;
            } else {
                particle = ModParticles.TEST_PARTICLES;
            }
            for (int i = 0; i <= 200; i++) {
                this.world.spawnParticle(particle, posX, posY, posZ, 0, 0, 0);
            }
        }
    }


    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        TagUtil.readIntListFromNBT(compound, "order_queue", this.ORDER_QUEUE);
        TagUtil.readIntListFromNBT(compound, "order", this.order);
        TagUtil.readIntListFromNBT(compound, "callback", this.callback);
        this.temperature = compound.getInteger("temperature");
        this.lastOrderAge = compound.getInteger("lastOrderAge");
        this.isSubMagic = compound.getBoolean("isSubMagic");
        this.gdy = compound.getFloat("gdy");
        this.g = compound.getFloat("g");
        this.settingDead = compound.getBoolean("settingDead");
        this.isStop = compound.getBoolean("isStop");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        TagUtil.writeIntListToNBT(compound, "order_queue", this.ORDER_QUEUE);
        TagUtil.writeIntListToNBT(compound, "order", this.order);
        TagUtil.writeIntListToNBT(compound, "callback", this.callback);
        compound.setInteger("temperature", this.temperature);
        compound.setInteger("lastOrderAge", this.lastOrderAge);
        compound.setBoolean("isSubMagic", this.isSubMagic);
        compound.setFloat("gdy", this.gdy);
        compound.setFloat("g", this.g);
        compound.setBoolean("settingDead", this.settingDead);
        compound.setBoolean("isStop", this.isStop);
    }


    @Override
    public int readData(ByteBuf buf) {
        int type = super.readData(buf);
        if(type == 0){
            this.ORDER_QUEUE.add(buf.readInt());
        }
        return type;
    }

    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(this.gdy);
        buffer.writeFloat(this.g);
        buffer.writeInt(this.temperature);
        buffer.writeInt(this.lastOrderAge);
        buffer.writeBoolean(this.isSubMagic);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        this.gdy = buffer.readFloat();
        this.g = buffer.readFloat();
        this.temperature = buffer.readInt();
        this.lastOrderAge = buffer.readInt();
        this.isSubMagic = buffer.readBoolean();
    }
}
