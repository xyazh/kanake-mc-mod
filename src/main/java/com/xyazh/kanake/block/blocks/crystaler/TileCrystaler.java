package com.xyazh.kanake.block.blocks.crystaler;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.manatable.TileManaWithForeverEntity;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.world.provider.ProviderMaze;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.*;

import javax.annotation.Nonnull;

public class TileCrystaler extends TileManaWithForeverEntity {
    private boolean shouldInit = true;
    private Item item = null;
    private int color = 0xffffff;
    private final int maxWorkTime = 2400;
    private int workTime = maxWorkTime;

    protected double getFEY() {
        return 0;
    }

    private void init() {
        if (this.shouldInit) {
            this.shouldInit = false;
            if (this.world.provider instanceof WorldProviderHell) {
                this.item = ModItems.CRYSTAL_FIRE;
                this.color = 0xfa5a32;
            } else if (this.world.provider instanceof WorldProviderEnd) {
                this.item = ModItems.CRYSTAL_END;
                this.color = 0x61caca;
            } else if (this.world.provider instanceof ProviderMaze) {
                this.item = ModItems.CRYSTAL_MAGIC;
                this.color = 0xf03cde;
            } else {
                Biome biome = this.world.getBiome(this.pos);
                if (biome instanceof BiomeEnd) {
                    this.item = ModItems.CRYSTAL_END;
                    this.color = 0x61caca;
                } else if (biome instanceof BiomeHell) {
                    this.item = ModItems.CRYSTAL_FIRE;
                    this.color = 0xfa5a32;
                } else if (biome instanceof BiomeOcean) {
                    this.item = ModItems.CRYSTAL_ICE;
                    this.color = 0x3c96f0;
                } else if (biome instanceof BiomeSnow) {
                    this.item = ModItems.CRYSTAL_ICE;
                    this.color = 0x3c96f0;
                }
            }
        }
    }

    @Override
    public void update() {
        super.update();
        init();
        if (!this.isEmpty() || this.item == null) {
            return;
        }
        this.workParticle();
        if (this.workTime-- > 0) {
            return;
        }
        this.workTime = maxWorkTime;
        if (!this.world.isRemote) {
            ItemStack stack = new ItemStack(this.item);
            stack.setCount(1);
            this.addStackFromSlot(0, stack);
        }
    }

    public void workParticle() {
        if (!this.world.isRemote) {
            return;
        }
        double rx, ry, rz, rx1, ry1, rz1, l;
        final double dn = 0.1;
        rx = Kanake.rand.nextDouble() - 0.5;
        ry = Kanake.rand.nextDouble() - 0.5;
        rz = Kanake.rand.nextDouble() - 0.5;
        rx1 = Kanake.rand.nextGaussian() * dn + 0.5 + this.pos.getX();
        ry1 = Kanake.rand.nextGaussian() * dn + 0.5 + this.pos.getY();
        rz1 = Kanake.rand.nextGaussian() * dn + 0.5 + this.pos.getZ();
        l = Kanake.rand.nextGaussian() + 8;
        Vec3d begin = new Vec3d(rx, ry, rz);
        begin = begin.normalize();
        begin = begin.scale(l);
        this.world.spawnParticle(ModParticles.MANA_PARTICLES1,
                begin.x + this.pos.getX(), begin.y + this.pos.getY(), begin.z + this.pos.getZ(),
                rx1, ry1, rz1, this.color);
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.workTime = compound.getInteger("workTime");
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("workTime", this.workTime);
        return compound;
    }
}
