package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.damage.MagicDamage;
import com.xyazh.kanake.damage.MagicDamageUtil;
import com.xyazh.kanake.magic.Magic;
import com.xyazh.kanake.magic.command.Command;
import com.xyazh.kanake.magic.command.CommandNoInherit;
import com.xyazh.kanake.magic.effect.MagicEffectTask;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import com.xyazh.kanake.magic.mana.IManaSource;
import com.xyazh.kanake.magic.mana.ManaSourceRef;
import com.xyazh.kanake.magic.mana.ManaSourceResolver;
import com.xyazh.kanake.network.EntityDataPacket;
import com.xyazh.kanake.particle.simple.particles.CrossCloudParticle;
import com.xyazh.kanake.particle.simple.particles.ExplosionFlashParticle;
import com.xyazh.kanake.particle.simple.particles.GlyphAuraParticle;
import com.xyazh.kanake.particle.simple.particles.IgniteFlameParticle;
import com.xyazh.kanake.particle.simple.particles.OrbitTrailParticle;
import com.xyazh.kanake.particle.simple.particles.RuneCircleParticle;
import com.xyazh.kanake.particle.simple.particles.RuneSparkParticle;
import com.xyazh.kanake.particle.simple.particles.ShockwaveParticle;
import com.xyazh.kanake.potion.buff.PotionKoori;
import com.xyazh.kanake.util.Vec3d;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityEmptyMagic extends EntityShoot {
    private static final double BASE_MANA_UPKEEP = 0.1;
    private static final float[][] ICE_PALETTE = {
            {0.0f, 0.6f, 1.0f},
            {0.0f, 0.4f, 1.0f},
            {0.4f, 0.8f, 1.0f},
            {0.9f, 0.9f, 1.0f}
    };
    private static final float[][] MAGIC_PALETTE = {
            {0.8f, 0.6f, 0.8f},
            {0.8f, 0.4f, 0.8f},
            {0.8f, 0.2f, 0.6f},
            {0.8f, 0.1f, 0.6f},
            {0.8f, 0.0f, 0.8f},
            {1.0f, 0.0f, 1.0f}
    };
    private static final float[][] SPARK_PALETTE = {
            {1.0f, 0.6f, 0.0f},
            {1.0f, 0.4f, 0.0f},
            {1.0f, 0.8f, 0.4f},
            {1.0f, 0.6f, 0.6f},
            {1.0f, 0.0f, 0.0f}
    };

    public static class OrderTask {
        public final GlyphOrder order;
        public final int repeat;

        public OrderTask(GlyphOrder order, int repeat) {
            this.order = order;
            this.repeat = Math.max(1, repeat);
        }
    }

    protected final ArrayDeque<OrderTask> ORDER_QUEUE = new ArrayDeque<>();
    public final LinkedList<GlyphOrder> order = new LinkedList<>();
    public final LinkedList<GlyphOrder> finishOrder = new LinkedList<>();
    public final List<MagicEffectTask> effects = new ArrayList<>();
    public final List<EntityEmptyMagic> children = new ArrayList<>();
    public final List<UUID> childUUIDs = new ArrayList<>();

    public float gdy = 0.0f;
    public float g = -0.0048f;
    public int temperature = 0;
    public int cooldown = 0;
    public boolean isSubMagic = false;
    public boolean settingDead = false;
    public boolean isStop = false;
    public boolean keepExplode = false;
    public int lookAtType = 0;
    public boolean hidden = false;
    public boolean hiddenInherit = false;
    public int color = -1;
    public boolean orbit = false;
    public double orbitRadius = 2.0;
    public double orbitSpeed = 0.05;
    public int orbitMode = 0;
    private double orbitAnchorX;
    private double orbitAnchorY;
    private double orbitAnchorZ;
    private boolean orbitAnchorSet = false;
    private double auraParticleBudget = 0;
    private double orbitParticleBudget = 0;
    private double sparkParticleBudget = 0;
    public EntityEmptyMagic parent = null;
    public UUID parentUUID = null;
    public ManaSourceRef manaSource = null;
    public boolean manaStopped = false;
    public boolean terminated = false;
    public boolean finishExecuted = false;
    public int repeatNext = 0;
    public int burstRemaining = 0;
    public double continuousManaCost = 0;
    protected EntityLivingBase target = null;

    private int manaSourceCheckCooldown = 0;
    private int childResolveCooldown = 0;
    private boolean deadParticleSpawned = false;

    public EntityEmptyMagic(World worldIn) {
        super(worldIn);
        this.speed = 0.0;
    }

    public void findTarget() {
        AxisAlignedBB aabb = new AxisAlignedBB(
                this.posX - 8, this.posY - 8, this.posZ - 8,
                this.posX + 8, this.posY + 8, this.posZ + 8);
        EntityLivingBase found = null;
        for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb,
                e -> !e.equals(this.shootingEntity))) {
            if (found == null) {
                found = entity;
                continue;
            }
            if (this.getDistance(entity) < this.getDistance(found)) {
                found = entity;
            }
        }
        this.target = found;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected boolean customMotion() {
        return true;
    }

    public void setOrders(LinkedList<GlyphOrder> normal, LinkedList<GlyphOrder> finish) {
        this.order.clear();
        this.finishOrder.clear();
        if (normal != null) {
            for (GlyphOrder glyph : normal) {
                if (glyph != null && Magic.ORDER_MAP.containsKey(glyph.commandId)) {
                    this.order.add(glyph.copy());
                }
            }
        }
        if (finish != null) {
            for (GlyphOrder glyph : finish) {
                if (glyph != null && Magic.ORDER_MAP.containsKey(glyph.commandId)) {
                    this.finishOrder.add(glyph.copy());
                }
            }
        }
        this.cooldown = 0;
    }

    public void setManaSource(ManaSourceRef ref) {
        this.manaSource = ref;
    }

    public ManaSourceRef getManaSource() {
        return this.manaSource;
    }

    protected boolean checkManaSource() {
        if (this.world.isRemote || this.manaSource == null) {
            return true;
        }
        if (this.manaSourceCheckCooldown-- > 0) {
            return true;
        }
        this.manaSourceCheckCooldown = 10;
        IManaSource source = ManaSourceResolver.resolve(this.world, this.manaSource);
        return source != null && source.isValid();
    }

    public boolean consumeMana(double amount) {
        if (this.world.isRemote || amount <= 0 || this.manaSource == null) {
            return true;
        }
        if (this.manaStopped) {
            return false;
        }
        IManaSource source = ManaSourceResolver.resolve(this.world, this.manaSource);
        if (source == null || !source.isValid()) {
            this.stopForMana();
            return false;
        }
        if (source.getMana() < amount) {
            this.stopForMana();
            return false;
        }
        if (!source.consume(amount)) {
            this.stopForMana();
            return false;
        }
        return true;
    }

    public void stopForMana() {
        if (this.manaStopped) {
            return;
        }
        this.manaStopped = true;
        this.terminated = true;
        this.isStop = true;
        this.orbit = false;
        this.continuousManaCost = 0;
        this.order.clear();
        this.finishOrder.clear();
        this.effects.clear();
        this.ORDER_QUEUE.clear();
        this.cooldown = Integer.MAX_VALUE;
        if (!this.world.isRemote) {
            this.sendStopPacket();
        }
        this.setDead();
    }

    public void terminateAll() {
        this.terminated = true;
        this.isStop = true;
        this.orbit = false;
        this.continuousManaCost = 0;
        this.order.clear();
        this.finishOrder.clear();
        this.effects.clear();
        this.ORDER_QUEUE.clear();
        this.cooldown = Integer.MAX_VALUE;
        if (!this.world.isRemote) {
            this.sendStopPacket();
        }
    }

    public void clientFinishEffects() {
        this.isStop = true;
        this.orbit = false;
        this.continuousManaCost = 0;
        this.order.clear();
        this.finishOrder.clear();
        this.effects.clear();
        this.ORDER_QUEUE.clear();
        this.setDeadParticle();
    }

    public void clearClientStop() {
        this.manaStopped = true;
        this.terminated = true;
        this.isStop = true;
        this.orbit = false;
        this.continuousManaCost = 0;
        this.order.clear();
        this.finishOrder.clear();
        this.effects.clear();
        this.ORDER_QUEUE.clear();
    }

    @Override
    public void onUpdate() {
        if (!this.world.isRemote) {
            if (!this.checkManaSource()) {
                this.stopForMana();
                return;
            }
            if (!this.consumeMana(BASE_MANA_UPKEEP)) {
                return;
            }
            if (this.continuousManaCost > 0 && !this.consumeMana(this.continuousManaCost)) {
                return;
            }
            this.tickEffects(false);
        } else {
            this.tickEffects(true);
        }
        super.onUpdate();
        if (this.isDead) {
            return;
        }
        this.execute();
        if (this.isDead) {
            return;
        }
        this.movingUpdate();
        this.tryPollOrder();
        if (this.isDead) {
            return;
        }
        this.updateChildren();
        if (!this.world.isRemote && (this.onHurt() || this.collided
                || this.world.collidesWithAnyBlock(this.getEntityBoundingBox()))) {
            this.setDead();
        }
    }

    public void execute() {
        if (this.manaStopped) {
            return;
        }
        OrderTask task = this.ORDER_QUEUE.poll();
        if (task == null || task.order == null) {
            return;
        }
        Command command = Magic.ORDER_MAP.get(task.order.commandId);
        if (command == null) {
            return;
        }
        for (int i = 0; i < task.repeat; i++) {
            if (i > 0 && !this.world.isRemote && !this.consumeMana(command.getManaCost(this, task.order))) {
                return;
            }
            command.execute(this, task.order);
            if (this.isDead || this.manaStopped || this.terminated) {
                return;
            }
        }
    }

    public void tryPollOrder() {
        if (this.world.isRemote || this.isDead || this.manaStopped || this.terminated) {
            return;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        if (this.order.isEmpty()) {
            return;
        }
        GlyphOrder glyph = this.order.poll();
        Command command = Magic.ORDER_MAP.get(glyph.commandId);
        if (command == null) {
            return;
        }
        if (!this.consumeMana(command.getManaCost(this, glyph))) {
            return;
        }
        if (command == Magic.CONCURRENT) {
            this.burstRemaining += 2;
            this.cooldown = 0;
            return;
        }
        if (command == Magic.CLEAR) {
            this.order.clear();
            this.cooldown = 0;
            this.sendClearPacket();
            return;
        }
        int repeat = this.repeatNext;
        this.repeatNext = 0;
        this.ORDER_QUEUE.add(new OrderTask(glyph, repeat));
        this.cooldown = Math.max(0, command.getInterval(this, glyph) - 1);
        if (this.burstRemaining > 0) {
            this.cooldown = 0;
            this.burstRemaining--;
        }
        if (command.need_sync) {
            this.sendOrderPacket(glyph, repeat);
        }
    }

    public void movingUpdate() {
        if (this.orbit) {
            if (!this.world.isRemote) {
                this.orbitUpdate();
            }
            return;
        }
        double speed = this.speed;
        if (this.isStop) {
            speed = 0.0;
        }
        if (this.lookAtType != 0) {
            if (this.target == null) {
                this.findTarget();
            } else {
                Vec3d vec3d = Vec3d.fromEntityPos(this.target);
                vec3d.sub(this.posX, this.posY, this.posZ);
                switch (this.lookAtType) {
                    case 1:
                        vec3d.normalize();
                        vec3d.mul(0.1);
                        this.addSpeed(vec3d);
                        break;
                    case 2:
                        vec3d.normalize();
                        this.setForward(vec3d);
                        break;
                    default:
                        break;
                }
                this.motionX = this.forward.x * speed;
                this.motionY = this.forward.y * speed;
                this.motionZ = this.forward.z * speed;
                if (!this.target.isEntityAlive()) {
                    this.target = null;
                }
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                return;
            }
        }
        this.gdy += this.g;
        this.motionX = this.forward.x * speed;
        this.motionY = this.forward.y * speed + this.gdy;
        this.motionZ = this.forward.z * speed;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
    }

    protected void orbitUpdate() {
        EntityEmptyMagic owner = this.getParentRaw();
        if (owner != null) {
            this.orbitAnchorX = owner.posX;
            this.orbitAnchorY = owner.posY;
            this.orbitAnchorZ = owner.posZ;
            this.orbitAnchorSet = true;
        }
        if (!this.orbitAnchorSet) {
            this.orbit = false;
            this.continuousManaCost = 0;
            return;
        }
        List<EntityEmptyMagic> siblings = owner == null ? new ArrayList<>() : owner.getLivingChildren();
        List<EntityEmptyMagic> orbiting = new ArrayList<>();
        for (EntityEmptyMagic sibling : siblings) {
            if (sibling.orbit) {
                orbiting.add(sibling);
            }
        }
        int count = Math.max(1, orbiting.size());
        int index = orbiting.indexOf(this);
        if (index < 0) {
            index = 0;
        }
        double direction = this.orbitMode == 1 ? -1.0
                : (this.orbitMode == 2 ? (index % 2 == 0 ? 1.0 : -1.0) : 1.0);
        // 只围绕直接母实体运动；孙实体使用它自己的 parent，不会围绕根实体。
        // 初始角度始终均匀分布，方向只影响旋转方向。
        double angle = direction * this.world.getTotalWorldTime() * this.orbitSpeed
                + Math.PI * 2 * index / count;
        double oldX = this.posX;
        double oldY = this.posY;
        double oldZ = this.posZ;
        double newX = this.orbitAnchorX + Math.cos(angle) * this.orbitRadius;
        double newY = this.orbitAnchorY;
        double newZ = this.orbitAnchorZ + Math.sin(angle) * this.orbitRadius;
        this.motionX = newX - oldX;
        this.motionY = newY - oldY;
        this.motionZ = newZ - oldZ;
        this.prevPosX = oldX;
        this.prevPosY = oldY;
        this.prevPosZ = oldZ;
        this.setPosition(newX, newY, newZ);
        this.sendPositionSync();
    }

    public Vec3d getMotion() {
        return new Vec3d(this.forward.x * this.speed, this.forward.y * this.speed, this.forward.z * this.speed);
    }

    /**
     * 获取直接母实体，允许返回已经死亡但仍有对象引用的母实体。
     * 环绕需要这个引用来记录母体最后的位置。
     */
    public EntityEmptyMagic getParentRaw() {
        if (this.parent != null) {
            return this.parent;
        }
        if (this.parentUUID != null) {
            for (Entity entity : this.world.loadedEntityList) {
                if (entity instanceof EntityEmptyMagic && this.parentUUID.equals(entity.getUniqueID())) {
                    this.parent = (EntityEmptyMagic) entity;
                    return this.parent;
                }
            }
        }
        return null;
    }

    public EntityEmptyMagic getParent() {
        EntityEmptyMagic owner = this.getParentRaw();
        return owner != null && !owner.isDead ? owner : null;
    }

    public void setParent(EntityEmptyMagic parent) {
        this.parent = parent;
        this.parentUUID = parent == null ? null : parent.getUniqueID();
    }

    public void addChild(EntityEmptyMagic child) {
        if (child == null) {
            return;
        }
        if (!this.children.contains(child)) {
            this.children.add(child);
        }
        if (!this.childUUIDs.contains(child.getUniqueID())) {
            this.childUUIDs.add(child.getUniqueID());
        }
        child.parent = this;
        child.parentUUID = this.getUniqueID();
    }

    public void removeChild(EntityEmptyMagic child) {
        if (child == null) {
            return;
        }
        this.children.remove(child);
        this.childUUIDs.remove(child.getUniqueID());
        if (child.parent == this) {
            child.parent = null;
        }
    }

    private void ensureChildren() {
        if (!this.children.isEmpty() || this.childUUIDs.isEmpty()) {
            return;
        }
        if (this.childResolveCooldown-- > 0) {
            return;
        }
        this.childResolveCooldown = 20;
        for (Entity entity : this.world.loadedEntityList) {
            if (entity instanceof EntityEmptyMagic) {
                EntityEmptyMagic magic = (EntityEmptyMagic) entity;
                if (!magic.isDead && this.childUUIDs.contains(magic.getUniqueID())) {
                    this.children.add(magic);
                    magic.parent = this;
                }
            }
        }
    }

    public List<EntityEmptyMagic> getLivingChildren() {
        this.ensureChildren();
        this.children.removeIf(child -> child == null || child.isDead);
        return this.children;
    }

    private void updateChildren() {
        this.ensureChildren();
        this.children.removeIf(child -> child == null || child.isDead);
    }

    public EntityEmptyMagic copyForChild() {
        EntityEmptyMagic child = new EntityEmptyMagic(this.world);
        child.order.addAll(this.copyOrdersForChild(this.order));
        for (GlyphOrder glyph : this.finishOrder) {
            child.finishOrder.add(glyph.copy());
        }
        child.temperature = this.temperature;
        child.color = this.color;
        child.hidden = this.hidden && this.hiddenInherit;
        child.hiddenInherit = this.hiddenInherit;
        child.keepExplode = this.keepExplode;
        child.lookAtType = this.lookAtType;
        child.g = this.g;
        child.gdy = 0;
        child.continuousManaCost = this.continuousManaCost;
        child.isSubMagic = true;
        child.cooldown = 5;
        child.setLivingMaxAge(this.livingMaxAge);
        child.shootingEntity = this.shootingEntity;
        child.shootingEntityUUID = this.shootingEntityUUID;
        child.shootingEntityPos = this.shootingEntityPos;
        child.manaSource = this.manaSource == null ? null : this.manaSource.copy();
        child.parent = this;
        child.parentUUID = this.getUniqueID();
        child.setForward(this.forward);
        child.speed = this.speed;
        child.rotationYaw = this.rotationYaw;
        child.rotationPitch = this.rotationPitch;
        child.setPosition(this.posX, this.posY, this.posZ);
        child.prevPosX = child.posX;
        child.prevPosY = child.posY;
        child.prevPosZ = child.posZ;
        return child;
    }

    private LinkedList<GlyphOrder> copyOrdersForChild(LinkedList<GlyphOrder> source) {
        LinkedList<GlyphOrder> result = new LinkedList<>();
        int skip = 0;
        for (GlyphOrder glyph : source) {
            if (skip > 0) {
                skip--;
                continue;
            }
            result.add(glyph.copy());
            Command command = Magic.ORDER_MAP.get(glyph.commandId);
            if (command instanceof CommandNoInherit) {
                skip = ((CommandNoInherit) command).getCount(glyph);
            }
        }
        return result;
    }

    @Override
    public void setDead() {
        if (this.settingDead) {
            return;
        }
        this.settingDead = true;
        if (this.world.isRemote) {
            this.setDeadParticle();
        } else {
            if (!this.terminated && !this.manaStopped && !this.finishExecuted) {
                this.executeFinishOrders();
            }
            this.order.clear();
            this.finishOrder.clear();
            this.effects.clear();
            this.ORDER_QUEUE.clear();
        }
        EntityEmptyMagic owner = this.getParent();
        if (owner != null) {
            owner.removeChild(this);
        }
        // 保留 children/childUUIDs：母体死亡后，存活的子实体仍需要用它
        // 来维持同组环绕分布；死亡本身不会递归影响子实体。
        super.setDead();
    }

    public void executeFinishOrders() {
        if (this.finishExecuted) {
            return;
        }
        this.finishExecuted = true;
        LinkedList<GlyphOrder> list = new LinkedList<>(this.finishOrder);
        this.finishOrder.clear();
        LinkedList<GlyphOrder> executedSync = new LinkedList<>();
        for (GlyphOrder glyph : list) {
            Command command = Magic.ORDER_MAP.get(glyph.commandId);
            if (command == null) {
                continue;
            }
            if (!this.world.isRemote && !this.consumeMana(command.getManaCost(this, glyph))) {
                this.sendFinishPacket(executedSync);
                return;
            }
            if (command.need_sync) {
                executedSync.add(glyph);
            }
            command.execute(this, glyph);
            if (this.isDead || this.manaStopped || this.terminated) {
                this.sendFinishPacket(executedSync);
                return;
            }
        }
        this.sendFinishPacket(executedSync);
    }

    private void sendFinishPacket(List<GlyphOrder> orders) {
        EntityDataPacket packet = EntityDataPacket.getPacket(this);
        if (packet == null) {
            return;
        }
        LinkedList<GlyphOrder> syncOrders = new LinkedList<>();
        for (GlyphOrder glyph : orders) {
            Command command = Magic.ORDER_MAP.get(glyph.commandId);
            if (command != null && command.need_sync) {
                syncOrders.add(glyph);
            }
        }
        if (syncOrders.isEmpty()) {
            return;
        }
        packet.buffer.writeInt(3);
        packet.buffer.writeInt(syncOrders.size());
        for (GlyphOrder glyph : syncOrders) {
            packet.buffer.writeInt(glyph.commandId);
            packet.buffer.writeInt(glyph.uid);
            boolean hasData = glyph.data != null && !glyph.data.hasNoTags();
            packet.buffer.writeBoolean(hasData);
            if (hasData) {
                ((PacketBuffer) packet.buffer).writeCompoundTag(glyph.data);
            }
        }
        Kanake.network.sendToAllTracking(packet, this);
    }

    public void addEffect(MagicEffectTask task) {
        if (task != null) {
            this.effects.add(task);
        }
    }

    private void tickEffects(boolean client) {
        Iterator<MagicEffectTask> iterator = this.effects.iterator();
        while (iterator.hasNext()) {
            MagicEffectTask task = iterator.next();
            if (!client) {
                if (!this.consumeMana(task.manaCostPerTick)) {
                    return;
                }
                if (task.type == MagicEffectTask.TYPE_DEATH_CROSS) {
                    this.applyDeathCrossDamage(task);
                }
            }
            task.ticksLeft--;
            if (task.ticksLeft <= 0) {
                iterator.remove();
            }
        }
    }

    private void applyDeathCrossDamage(MagicEffectTask task) {
        double w = task.halfWidth;
        double l = task.halfLength;
        AxisAlignedBB vertical = new AxisAlignedBB(
                this.posX - w, this.posY - l, this.posZ - w,
                this.posX + w, this.posY + l, this.posZ + w
        );
        Vec3d forward = this.getForwardVec();
        double rx = -forward.z;
        double rz = forward.x;
        double horizontalLength = Math.sqrt(rx * rx + rz * rz);
        if (horizontalLength < 0.0001) {
            rx = 1;
            rz = 0;
            horizontalLength = 1;
        }
        rx /= horizontalLength;
        rz /= horizontalLength;
        AxisAlignedBB horizontal;
        if (Math.abs(rx) >= Math.abs(rz)) {
            horizontal = new AxisAlignedBB(
                    this.posX - l, this.posY - w, this.posZ - w,
                    this.posX + l, this.posY + w, this.posZ + w
            );
        } else {
            horizontal = new AxisAlignedBB(
                    this.posX - w, this.posY - w, this.posZ - l,
                    this.posX + w, this.posY + w, this.posZ + l
            );
        }
        Set<EntityLivingBase> targets = new HashSet<>();
        targets.addAll(this.world.getEntitiesWithinAABB(EntityLivingBase.class, vertical));
        targets.addAll(this.world.getEntitiesWithinAABB(EntityLivingBase.class, horizontal));
        for (EntityLivingBase target : targets) {
            if (target.getEntityId() == this.getEntityId() || target == this.shootingEntity || !target.isEntityAlive()) {
                continue;
            }
            DamageSource damageSource = DamageSource.causeIndirectDamage(this, this.shootingEntity);
            damageSource.setMagicDamage();
            MagicDamageUtil.attackEntity(target, damageSource,
                    (float) task.damage, (float) task.damage);
        }
    }

    protected boolean onHurt() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                this.posX - 0.4, this.posY - 0.4, this.posZ - 0.4,
                this.posX + 0.4, this.posY + 0.4, this.posZ + 0.4);
        List<EntityLivingBase> entityLivingBases = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisAlignedBB);
        boolean flag = false;
        for (EntityLivingBase entityLivingBase : entityLivingBases) {
            if (entityLivingBase.equals(this.shootingEntity) || entityLivingBase.getEntityId() == this.getEntityId()) {
                continue;
            }
            flag = true;
            DamageSource damageSource = DamageSource.causeIndirectDamage(this, this.shootingEntity);
            damageSource.setMagicDamage();
            int dTemperature = Math.min(Math.abs(this.temperature), 50);
            float damage = 4 + 4 * (dTemperature / 50f);
            if (this.temperature <= -50) {
                MagicDamageUtil.attackEntity(entityLivingBase, damageSource, damage, 8.0F);
                entityLivingBase.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI, dTemperature));
            } else if (this.temperature < 50) {
                MagicDamage magicDamage = new MagicDamage();
                magicDamage.setAttacker(this.shootingEntity);
                MagicDamageUtil.attackEntity(entityLivingBase, magicDamage, damage, 8.0F);
            } else {
                damageSource.setFireDamage();
                MagicDamageUtil.attackEntity(entityLivingBase, damageSource, damage, 8.0F);
                entityLivingBase.setFire(dTemperature / 20);
            }
        }
        return flag;
    }

    private float[] getPaletteColor(float[][] palette) {
        return palette[this.world.rand.nextInt(palette.length)];
    }

    public float[] getParticleColor() {
        if (this.color >= 0) {
            return new float[]{
                    ((this.color >> 16) & 255) / 255.0f,
                    ((this.color >> 8) & 255) / 255.0f,
                    (this.color & 255) / 255.0f
            };
        }
        if (this.temperature <= -50) {
            return this.getPaletteColor(ICE_PALETTE);
        }
        if (this.temperature < 50) {
            return this.getPaletteColor(MAGIC_PALETTE);
        }
        return this.getPaletteColor(SPARK_PALETTE);
    }

    public float[] getIgniteColor() {
        if (this.color >= 0) {
            return this.getParticleColor();
        }
        return this.getPaletteColor(SPARK_PALETTE);
    }

    /**
     * 由客户端渲染帧调用，而不是在 20Hz 的实体 update 中生成。
     * deltaTicks 是当前帧相对上一帧经过的 tick 比例，保证粒子密度平滑。
     */
    public void spawnParticleFrame(float partialTicks, float deltaTicks) {
        if (!this.world.isRemote || this.hidden || this.isDead || deltaTicks <= 0) {
            this.resetParticleBudgets();
            return;
        }
        double x;
        double y;
        double z;
        if (this.ticksExisted <= 0) {
            x = this.posX;
            y = this.posY;
            z = this.posZ;
        } else {
            x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
            y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
            z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
        }

        this.auraParticleBudget += 4.0 * deltaTicks;
        while (this.auraParticleBudget >= 1.0) {
            this.auraParticleBudget -= 1.0;
            float[] color = this.getParticleColor();
            new GlyphAuraParticle(this.world, x, y, z,
                    color[0], color[1], color[2], 0.7f + this.world.rand.nextFloat() * 0.5f);
        }

        if (this.orbit) {
            this.orbitParticleBudget += deltaTicks;
            while (this.orbitParticleBudget >= 1.0) {
                this.orbitParticleBudget -= 1.0;
                float[] color = this.getParticleColor();
                new OrbitTrailParticle(this.world, x, y, z,
                        color[0], color[1], color[2], 0.6f + this.world.rand.nextFloat() * 0.3f);
            }
        } else {
            this.orbitParticleBudget = 0;
        }

        if (this.speed > 0.01 || this.orbit) {
            this.sparkParticleBudget += 0.5 * deltaTicks;
            while (this.sparkParticleBudget >= 1.0) {
                this.sparkParticleBudget -= 1.0;
                float[] color = this.getParticleColor();
                new RuneSparkParticle(this.world, x, y, z,
                        color[0], color[1], color[2], 0.5f + this.world.rand.nextFloat() * 0.4f);
            }
        } else {
            this.sparkParticleBudget = 0;
        }

        for (MagicEffectTask task : new ArrayList<>(this.effects)) {
            if (task.type != MagicEffectTask.TYPE_DEATH_CROSS) {
                continue;
            }
            int particlesPerTick = (3 + (int) (task.halfLength / 4.0)) * 4;
            task.clientParticleBudget += particlesPerTick * deltaTicks;
            while (task.clientParticleBudget >= 1.0) {
                task.clientParticleBudget -= 1.0;
                this.spawnOneCrossParticle(task, x, y, z);
            }
        }
    }

    private void resetParticleBudgets() {
        this.auraParticleBudget = 0;
        this.orbitParticleBudget = 0;
        this.sparkParticleBudget = 0;
        for (MagicEffectTask task : this.effects) {
            task.clientParticleBudget = 0;
        }
    }

    private void spawnOneCrossParticle(MagicEffectTask task, double centerX, double centerY, double centerZ) {
        Vec3d forward = this.getForwardVec();
        double rx = -forward.z;
        double rz = forward.x;
        double horizontalLength = Math.sqrt(rx * rx + rz * rz);
        if (horizontalLength < 0.0001) {
            rx = 1;
            rz = 0;
            horizontalLength = 1;
        }
        rx /= horizontalLength;
        rz /= horizontalLength;
        double[][] directions = {
                {0, 1, 0}, {0, -1, 0}, {rx, 0, rz}, {-rx, 0, -rz}
        };
        double[] dir = directions[this.world.rand.nextInt(directions.length)];
        double ax = dir[0];
        double ay = dir[1];
        double az = dir[2];
        double refX = 0;
        double refY = 0;
        double refZ = 1;
        if (Math.abs(az) > 0.9) {
            refX = 1;
            refZ = 0;
        }
        double ux = ay * refZ - az * refY;
        double uy = az * refX - ax * refZ;
        double uz = ax * refY - ay * refX;
        double ul = Math.sqrt(ux * ux + uy * uy + uz * uz);
        ux /= ul;
        uy /= ul;
        uz /= ul;
        double vx = ay * uz - az * uy;
        double vy = az * ux - ax * uz;
        double vz = ax * uy - ay * ux;

        double halfLength = Math.max(0.001, task.halfLength);
        double distance = this.world.rand.nextDouble() * halfLength;
        double taper = 1.0 - distance / halfLength;
        double thickness = task.halfWidth * taper + 0.03;
        double ox = (this.world.rand.nextDouble() - 0.5) * 2 * thickness;
        double oy = (this.world.rand.nextDouble() - 0.5) * 2 * thickness;
        double x = centerX + ax * distance + ux * ox + vx * oy;
        double y = centerY + ay * distance + uy * ox + vy * oy;
        double z = centerZ + az * distance + uz * ox + vz * oy;
        float[] color = this.getParticleColor();
        new CrossCloudParticle(this.world, x, y, z,
                ax, ay, az, color[0], color[1], color[2],
                0.85f + this.world.rand.nextFloat() * 0.45f);
    }

    public void setDeadParticle() {
        if (this.deadParticleSpawned || !this.world.isRemote || this.hidden) {
            return;
        }
        this.deadParticleSpawned = true;
        float[] color = this.getParticleColor();
        new ExplosionFlashParticle(this.world, this.posX, this.posY, this.posZ,
                color[0], color[1], color[2], 2.0f);
        for (int i = 0; i < 24; i++) {
            new RuneSparkParticle(this.world, this.posX, this.posY, this.posZ,
                    color[0], color[1], color[2], 0.7f + this.world.rand.nextFloat() * 0.8f);
        }
    }

    public void spawnCastParticles() {
        if (!this.world.isRemote) {
            return;
        }
        float[] color = this.getParticleColor();
        new RuneCircleParticle(this.world, this.posX, this.posY, this.posZ,
                1.4, color[0], color[1], color[2]);
        new ExplosionFlashParticle(this.world, this.posX, this.posY, this.posZ,
                color[0], color[1], color[2], 1.0f);
        for (int i = 0; i < 10; i++) {
            new RuneSparkParticle(this.world, this.posX, this.posY, this.posZ,
                    color[0], color[1], color[2], 0.5f + this.world.rand.nextFloat() * 0.5f);
        }
    }

    public void spawnColorFlashParticles() {
        if (!this.world.isRemote) {
            return;
        }
        float[] color = this.getParticleColor();
        for (int i = 0; i < 18; i++) {
            new RuneSparkParticle(this.world, this.posX, this.posY, this.posZ,
                    color[0], color[1], color[2], 0.6f + this.world.rand.nextFloat() * 0.6f);
        }
    }

    public void spawnExplosionParticles(float power) {
        this.spawnExplosionParticlesAt(this.posX, this.posY, this.posZ, power);
    }

    public void spawnExplosionParticlesAt(double x, double y, double z, float power) {
        if (!this.world.isRemote) {
            return;
        }
        float[] color = this.getParticleColor();
        new ShockwaveParticle(this.world, x, y, z, Math.max(1.0, power), color[0], color[1], color[2]);
        new ExplosionFlashParticle(this.world, x, y, z, color[0], color[1], color[2], Math.max(1.0f, power));
        int sparks = 6 + (int) (power * 2);
        for (int i = 0; i < sparks; i++) {
            new RuneSparkParticle(this.world, x, y, z, color[0], color[1], color[2],
                    0.6f + this.world.rand.nextFloat() * 0.8f);
        }
    }

    public void spawnIgniteParticles(double radius) {
        if (!this.world.isRemote) {
            return;
        }
        for (int i = 0; i < 18; i++) {
            float[] color = this.getIgniteColor();
            new IgniteFlameParticle(this.world,
                    this.posX + (this.world.rand.nextDouble() - 0.5) * radius * 2,
                    this.posY + (this.world.rand.nextDouble() - 0.5) * radius,
                    this.posZ + (this.world.rand.nextDouble() - 0.5) * radius * 2,
                    color[0], color[1], color[2], 0.7f + this.world.rand.nextFloat() * 0.5f);
        }
    }

    private void sendOrderPacket(GlyphOrder glyph, int repeat) {
        EntityDataPacket packet = EntityDataPacket.getPacket(this);
        if (packet == null) {
            return;
        }
        packet.buffer.writeInt(0);
        packet.buffer.writeInt(glyph.commandId);
        packet.buffer.writeInt(glyph.uid);
        packet.buffer.writeInt(repeat);
        boolean hasData = glyph.data != null && !glyph.data.hasNoTags();
        packet.buffer.writeBoolean(hasData);
        if (hasData) {
            ((PacketBuffer) packet.buffer).writeCompoundTag(glyph.data);
        }
        Kanake.network.sendToAllTracking(packet, this);
    }

    private void sendStopPacket() {
        EntityDataPacket packet = EntityDataPacket.getPacket(this);
        if (packet == null) {
            return;
        }
        packet.buffer.writeInt(4);
        Kanake.network.sendToAllTracking(packet, this);
    }

    private void sendClearPacket() {
        EntityDataPacket packet = EntityDataPacket.getPacket(this);
        if (packet == null) {
            return;
        }
        packet.buffer.writeInt(5);
        Kanake.network.sendToAllTracking(packet, this);
    }

    private void sendPositionSync() {
        EntityDataPacket packet = EntityDataPacket.getPacket(this);
        if (packet == null) {
            return;
        }
        packet.buffer.writeInt(2);
        packet.buffer.writeDouble(this.posX);
        packet.buffer.writeDouble(this.posY);
        packet.buffer.writeDouble(this.posZ);
        packet.buffer.writeFloat(this.rotationYaw);
        packet.buffer.writeFloat(this.rotationPitch);
        Kanake.network.sendToAllTracking(packet, this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        boolean result = super.attackEntityFrom(source, amount);
        if (this.isDead && !this.settingDead) {
            this.setDead();
        }
        return result;
    }

    private NBTTagList writeOrders(List<GlyphOrder> orders) {
        NBTTagList list = new NBTTagList();
        for (GlyphOrder glyph : orders) {
            list.appendTag(glyph.writeToNBT());
        }
        return list;
    }

    private void readOrders(NBTTagList list, List<GlyphOrder> target) {
        target.clear();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            target.add(GlyphOrder.readFromNBT(list.getCompoundTagAt(i)));
        }
    }

    private NBTTagList writeUUIDs(List<UUID> uuids) {
        NBTTagList list = new NBTTagList();
        for (UUID uuid : uuids) {
            list.appendTag(new NBTTagString(uuid.toString()));
        }
        return list;
    }

    private void readUUIDs(NBTTagList list, List<UUID> target) {
        target.clear();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            try {
                target.add(UUID.fromString(list.getStringTagAt(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.readOrders(compound.getTagList("order", Constants.NBT.TAG_COMPOUND), this.order);
        this.readOrders(compound.getTagList("finishOrder", Constants.NBT.TAG_COMPOUND), this.finishOrder);
        this.gdy = compound.getFloat("gdy");
        this.g = compound.getFloat("g");
        this.temperature = compound.getInteger("temperature");
        this.cooldown = compound.getInteger("cooldown");
        this.isSubMagic = compound.getBoolean("isSubMagic");
        this.isStop = compound.getBoolean("isStop");
        this.keepExplode = compound.getBoolean("keepExplode");
        this.lookAtType = compound.getInteger("lookAtType");
        this.hidden = compound.getBoolean("hidden");
        this.hiddenInherit = compound.getBoolean("hiddenInherit");
        this.color = compound.hasKey("color") ? compound.getInteger("color") : -1;
        this.orbit = compound.getBoolean("orbit");
        this.orbitRadius = compound.hasKey("orbitRadius") ? compound.getDouble("orbitRadius") : 2.0;
        this.orbitSpeed = compound.hasKey("orbitSpeed") ? compound.getDouble("orbitSpeed") : 0.05;
        this.orbitMode = compound.getInteger("orbitMode");
        this.orbitAnchorX = compound.getDouble("orbitAnchorX");
        this.orbitAnchorY = compound.getDouble("orbitAnchorY");
        this.orbitAnchorZ = compound.getDouble("orbitAnchorZ");
        this.orbitAnchorSet = compound.getBoolean("orbitAnchorSet");
        this.terminated = compound.getBoolean("terminated");
        this.manaStopped = compound.getBoolean("manaStopped");
        this.finishExecuted = compound.getBoolean("finishExecuted");
        this.repeatNext = compound.getInteger("repeatNext");
        this.burstRemaining = compound.getInteger("burstRemaining");
        this.continuousManaCost = compound.getDouble("continuousManaCost");
        if (compound.hasUniqueId("parentUUID")) {
            this.parentUUID = compound.getUniqueId("parentUUID");
        } else {
            this.parentUUID = null;
        }
        this.parent = null;
        this.readUUIDs(compound.getTagList("childUUIDs", Constants.NBT.TAG_STRING), this.childUUIDs);
        this.children.clear();
        if (compound.hasKey("manaSource")) {
            this.manaSource = ManaSourceRef.readFromNBT(compound.getCompoundTag("manaSource"));
        } else {
            this.manaSource = null;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("order", this.writeOrders(this.order));
        compound.setTag("finishOrder", this.writeOrders(this.finishOrder));
        compound.setFloat("gdy", this.gdy);
        compound.setFloat("g", this.g);
        compound.setInteger("temperature", this.temperature);
        compound.setInteger("cooldown", this.cooldown);
        compound.setBoolean("isSubMagic", this.isSubMagic);
        compound.setBoolean("isStop", this.isStop);
        compound.setBoolean("keepExplode", this.keepExplode);
        compound.setInteger("lookAtType", this.lookAtType);
        compound.setBoolean("hidden", this.hidden);
        compound.setBoolean("hiddenInherit", this.hiddenInherit);
        compound.setInteger("color", this.color);
        compound.setBoolean("orbit", this.orbit);
        compound.setDouble("orbitRadius", this.orbitRadius);
        compound.setDouble("orbitSpeed", this.orbitSpeed);
        compound.setInteger("orbitMode", this.orbitMode);
        compound.setDouble("orbitAnchorX", this.orbitAnchorX);
        compound.setDouble("orbitAnchorY", this.orbitAnchorY);
        compound.setDouble("orbitAnchorZ", this.orbitAnchorZ);
        compound.setBoolean("orbitAnchorSet", this.orbitAnchorSet);
        compound.setBoolean("terminated", this.terminated);
        compound.setBoolean("manaStopped", this.manaStopped);
        compound.setBoolean("finishExecuted", this.finishExecuted);
        compound.setInteger("repeatNext", this.repeatNext);
        compound.setInteger("burstRemaining", this.burstRemaining);
        compound.setDouble("continuousManaCost", this.continuousManaCost);
        if (this.parentUUID != null) {
            compound.setUniqueId("parentUUID", this.parentUUID);
        }
        compound.setTag("childUUIDs", this.writeUUIDs(this.childUUIDs));
        if (this.manaSource != null) {
            compound.setTag("manaSource", this.manaSource.writeToNBT());
        }
    }

    @Override
    public int readData(ByteBuf buf) {
        int type = super.readData(buf);
        if (type == 0) {
            int commandId = buf.readInt();
            int uid = buf.readInt();
            int repeat = buf.readInt();
            NBTTagCompound data = new NBTTagCompound();
            if (buf.readBoolean()) {
                try {
                    NBTTagCompound read = ((PacketBuffer) buf).readCompoundTag();
                    if (read != null) {
                        data = read;
                    }
                } catch (IOException ignored) {
                }
            }
            this.ORDER_QUEUE.add(new OrderTask(new GlyphOrder(commandId, uid, data), repeat));
        } else if (type == 2) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            float yaw = buf.readFloat();
            float pitch = buf.readFloat();
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.setPositionAndRotation(x, y, z, yaw, pitch);
        } else if (type == 3) {
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                int commandId = buf.readInt();
                int uid = buf.readInt();
                NBTTagCompound data = new NBTTagCompound();
                if (buf.readBoolean()) {
                    try {
                        NBTTagCompound read = ((PacketBuffer) buf).readCompoundTag();
                        if (read != null) {
                            data = read;
                        }
                    } catch (IOException ignored) {
                    }
                }
                Command command = Magic.ORDER_MAP.get(commandId);
                if (command != null) {
                    command.execute(this, new GlyphOrder(commandId, uid, data));
                }
            }
        } else if (type == 4) {
            this.clearClientStop();
        } else if (type == 5) {
            this.ORDER_QUEUE.clear();
        }
        return type;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeFloat(this.gdy);
        buffer.writeFloat(this.g);
        buffer.writeInt(this.temperature);
        buffer.writeInt(this.cooldown);
        buffer.writeBoolean(this.isSubMagic);
        buffer.writeBoolean(this.keepExplode);
        buffer.writeBoolean(this.isStop);
        buffer.writeInt(this.lookAtType);
        buffer.writeBoolean(this.hidden);
        buffer.writeBoolean(this.hiddenInherit);
        buffer.writeInt(this.color);
        buffer.writeBoolean(this.orbit);
        buffer.writeDouble(this.orbitRadius);
        buffer.writeDouble(this.orbitSpeed);
        buffer.writeInt(this.orbitMode);
        buffer.writeBoolean(this.parentUUID != null);
        if (this.parentUUID != null) {
            buffer.writeLong(this.parentUUID.getMostSignificantBits());
            buffer.writeLong(this.parentUUID.getLeastSignificantBits());
        }
        buffer.writeBoolean(this.terminated);
        buffer.writeBoolean(this.manaStopped);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        super.readSpawnData(buffer);
        this.gdy = buffer.readFloat();
        this.g = buffer.readFloat();
        this.temperature = buffer.readInt();
        this.cooldown = buffer.readInt();
        this.isSubMagic = buffer.readBoolean();
        this.keepExplode = buffer.readBoolean();
        this.isStop = buffer.readBoolean();
        this.lookAtType = buffer.readInt();
        this.hidden = buffer.readBoolean();
        this.hiddenInherit = buffer.readBoolean();
        this.color = buffer.readInt();
        this.orbit = buffer.readBoolean();
        this.orbitRadius = buffer.readDouble();
        this.orbitSpeed = buffer.readDouble();
        this.orbitMode = buffer.readInt();
        if (buffer.readBoolean()) {
            long most = buffer.readLong();
            long least = buffer.readLong();
            this.parentUUID = new UUID(most, least);
        } else {
            this.parentUUID = null;
        }
        this.terminated = buffer.readBoolean();
        this.manaStopped = buffer.readBoolean();
    }

    @Override
    public boolean isImmuneToExplosions() {
        return this.keepExplode;
    }
}
