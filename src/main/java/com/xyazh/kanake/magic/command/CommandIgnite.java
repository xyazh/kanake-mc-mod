package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class CommandIgnite extends OrderCommand {
    public CommandIgnite(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        int enhance = GlyphData.getInt(glyphOrder.data, GlyphData.ENHANCE, 0);
        double radius = 3.0 + 0.5 * enhance;
        int seconds = 4 + enhance;

        if (magic.world.isRemote) {
            magic.spawnIgniteParticles(radius);
            return;
        }

        AxisAlignedBB aabb = new AxisAlignedBB(
                magic.posX - radius, magic.posY - radius, magic.posZ - radius,
                magic.posX + radius, magic.posY + radius, magic.posZ + radius
        );
        List<EntityLivingBase> list = magic.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        for (EntityLivingBase target : list) {
            if (target.getEntityId() == magic.getEntityId() || target == magic.shootingEntity) {
                continue;
            }
            target.setFire(seconds * 20);
        }
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        return 5 + 2 * GlyphData.getInt(order.data, GlyphData.ENHANCE, 0);
    }

    @Override
    public boolean canUpgrade(int crystalType) {
        return crystalType == 1;
    }

    @Override
    public void applyUpgrade(NBTTagCompound data, int crystalType) {
        if (crystalType == 1) {
            data.setInteger(GlyphData.ENHANCE, data.getInteger(GlyphData.ENHANCE) + 1);
        }
    }
}
