package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandReleaseCast extends OrderCommand {
    public enum Mode {
        REMOTE,
        LAUNCH,
        PLACE,
        DOWN
    }

    private final Mode mode;

    public CommandReleaseCast(int order, boolean needSync, Mode mode) {
        super(order, needSync);
        this.mode = mode;
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic caster = (EntityEmptyMagic) entity;
        if (caster.world.isRemote) {
            caster.spawnCastParticles();
            return;
        }

        int enhance = GlyphData.getInt(glyphOrder.data, GlyphData.ENHANCE, 0);
        double distance = 4.0 + 2.0 * enhance;
        double speed = 0.5 + 0.25 * enhance;

        EntityEmptyMagic child = caster.copyForChild();
        child.isSubMagic = true;
        child.g = 0;
        child.gdy = 0;
        child.cooldown = 5;
        Vec3d forward = caster.getForwardVec();

        switch (this.mode) {
            case REMOTE:
                child.setPosition(
                        caster.posX + forward.x * distance,
                        caster.posY + forward.y * distance,
                        caster.posZ + forward.z * distance
                );
                child.speed = 0;
                break;
            case LAUNCH:
                child.setPosition(caster.posX, caster.posY, caster.posZ);
                child.setForward(forward);
                child.speed = speed;
                break;
            case PLACE:
                child.setPosition(caster.posX, caster.posY, caster.posZ);
                child.speed = 0;
                child.addLivingMaxAge(40 * enhance);
                break;
            case DOWN:
                child.setPosition(caster.posX, caster.posY, caster.posZ);
                child.setForward(new Vec3d(0, -1, 0));
                child.speed = speed;
                break;
            default:
                child.setPosition(caster.posX, caster.posY, caster.posZ);
                child.speed = 0;
                break;
        }

        child.continuousManaCost = (this.mode == Mode.LAUNCH || this.mode == Mode.DOWN) ? 0.5 : 0;
        child.prevPosX = child.posX;
        child.prevPosY = child.posY;
        child.prevPosZ = child.posZ;
        caster.addChild(child);
        caster.world.spawnEntity(child);
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        int quicken = GlyphData.getInt(order.data, GlyphData.QUICKEN, 0);
        int delay = GlyphData.getInt(order.data, GlyphData.DELAY, 0);
        return Math.max(1, 30 - 4 * quicken + 4 * delay);
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        int enhance = GlyphData.getInt(order.data, GlyphData.ENHANCE, 0);
        int quicken = GlyphData.getInt(order.data, GlyphData.QUICKEN, 0);
        int delay = GlyphData.getInt(order.data, GlyphData.DELAY, 0);
        double base = this.mode == Mode.REMOTE ? 20 + 5 * enhance : 12 + 3 * enhance;
        return base + 2 * quicken + 2 * delay;
    }

    @Override
    public double getSpellSpeed(GlyphOrder order) {
        if (this.mode == Mode.LAUNCH || this.mode == Mode.DOWN) {
            return 0.5 + 0.25 * GlyphData.getInt(order.data, GlyphData.ENHANCE, 0);
        }
        return -1.0;
    }

    @Override
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1 || crystalType == 2 || crystalType == 3;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        String key = crystalType == 1 ? GlyphData.ENHANCE : (crystalType == 2 ? GlyphData.QUICKEN : GlyphData.DELAY);
        data.setInteger(key, data.getInteger(key) + 1);
    }
}
