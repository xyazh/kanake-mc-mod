package com.xyazh.kanake.magic.glyph;

import net.minecraft.nbt.NBTTagCompound;

/**
 * 一次施法中某个槽位上的纹章。
 * uid 用于在复制给子实体后精确识别同一条纹章（例如“不继承”需要删除后续纹章）。
 */
public class GlyphOrder {
    public final int commandId;
    public final int uid;
    public final NBTTagCompound data;

    public GlyphOrder(int commandId, int uid, NBTTagCompound data) {
        this.commandId = commandId;
        this.uid = uid;
        this.data = data == null ? new NBTTagCompound() : data.copy();
    }

    public GlyphOrder copy() {
        return new GlyphOrder(this.commandId, this.uid, this.data);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("id", this.commandId);
        compound.setInteger("uid", this.uid);
        compound.setTag("data", this.data.copy());
        return compound;
    }

    public static GlyphOrder readFromNBT(NBTTagCompound compound) {
        return new GlyphOrder(
                compound.getInteger("id"),
                compound.getInteger("uid"),
                compound.getCompoundTag("data")
        );
    }
}
