package com.xyazh.kanake.magic.mana;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * 法术实体记录的最初施法者。后续实体/方块实体施法时可以用 ENTITY/TILE 类型。
 */
public class ManaSourceRef {
    public static final int PLAYER = 0;
    public static final int ENTITY = 1;
    public static final int TILE = 2;

    public int type;
    public UUID uuid;
    public int dimension;
    public BlockPos pos;

    public ManaSourceRef(int type) {
        this.type = type;
    }

    public static ManaSourceRef player(EntityPlayer player) {
        ManaSourceRef ref = new ManaSourceRef(PLAYER);
        ref.uuid = player.getUniqueID();
        return ref;
    }

    public static ManaSourceRef entity(UUID uuid) {
        ManaSourceRef ref = new ManaSourceRef(ENTITY);
        ref.uuid = uuid;
        return ref;
    }

    public static ManaSourceRef tile(int dimension, BlockPos pos) {
        ManaSourceRef ref = new ManaSourceRef(TILE);
        ref.dimension = dimension;
        ref.pos = pos;
        return ref;
    }

    public ManaSourceRef copy() {
        ManaSourceRef ref = new ManaSourceRef(this.type);
        ref.uuid = this.uuid;
        ref.dimension = this.dimension;
        ref.pos = this.pos;
        return ref;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("type", this.type);
        if (this.uuid != null) {
            compound.setUniqueId("uuid", this.uuid);
        }
        compound.setInteger("dimension", this.dimension);
        if (this.pos != null) {
            compound.setLong("pos", this.pos.toLong());
        }
        return compound;
    }

    public static ManaSourceRef readFromNBT(NBTTagCompound compound) {
        ManaSourceRef ref = new ManaSourceRef(compound.getInteger("type"));
        if (compound.hasUniqueId("uuid")) {
            ref.uuid = compound.getUniqueId("uuid");
        }
        ref.dimension = compound.getInteger("dimension");
        if (compound.hasKey("pos")) {
            ref.pos = BlockPos.fromLong(compound.getLong("pos"));
        }
        return ref;
    }
}
