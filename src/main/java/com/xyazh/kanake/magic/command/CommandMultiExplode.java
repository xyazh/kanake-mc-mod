package com.xyazh.kanake.magic.command;

import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class CommandMultiExplode extends OrderCommand {
    public CommandMultiExplode(int order, boolean needSync) {
        super(order, needSync);
    }

    @Override
    public void execute(Entity entity, GlyphOrder glyphOrder) {
        if (!(entity instanceof EntityEmptyMagic)) {
            return;
        }
        EntityEmptyMagic magic = (EntityEmptyMagic) entity;
        int enhance = GlyphData.getInt(glyphOrder.data, GlyphData.ENHANCE, 0);
        int count = 3 + enhance;
        double radius = 2.0 + 0.5 * enhance;
        float power = 1.5f + 0.5f * enhance;

        for (int i = 0; i < count; i++) {
            double x = magic.posX + (magic.world.rand.nextDouble() - 0.5) * 2 * radius;
            double y = magic.posY + (magic.world.rand.nextDouble() - 0.5) * radius;
            double z = magic.posZ + (magic.world.rand.nextDouble() - 0.5) * 2 * radius;
            if (magic.world.isRemote) {
                magic.spawnExplosionParticlesAt(x, y, z, power);
            } else {
                magic.world.createExplosion(magic, x, y, z, power, true);
            }
        }
    }

    @Override
    public int getInterval(EntityEmptyMagic magic, GlyphOrder order) {
        return 1;
    }

    @Override
    public double getManaCost(EntityEmptyMagic magic, GlyphOrder order) {
        int enhance = GlyphData.getInt(order.data, GlyphData.ENHANCE, 0);
        return 8.0 * (3 + enhance);
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
