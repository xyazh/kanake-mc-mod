package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.Magic;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class Command {
    public int order;
    public boolean need_sync;

    public Command(int order, boolean need_sync) {
        this.order = order;
        this.need_sync = need_sync;
        Magic.ORDER_MAP.put(order, this);
    }

    public void execute(Entity entity) {
    }

    /**
     * 带纹章参数执行。旧纹章只需要实现 execute(Entity)，新纹章覆盖本方法。
     */
    public void execute(Entity entity, GlyphOrder order) {
        this.execute(entity);
    }

    /**
     * 本条纹章执行后，距离下一条纹章开始执行的 tick 数。
     */
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 4;
    }

    /**
     * 本条纹章执行一次消耗的魔力。
     */
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 1.0;
    }

    public boolean canUpgrade(int crystalType) {
        return false;
    }

    public void applyUpgrade(NBTTagCompound data, int crystalType) {
    }

    public boolean canApplyDye() {
        return false;
    }

    public void applyDye(NBTTagCompound data, int rgb) {
    }
}
