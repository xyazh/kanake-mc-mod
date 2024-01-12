package com.xyazh.kanake.entity;

import com.xyazh.kanake.particle.ModParticles;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class EntityLaunch extends EntityShoot{
    public EntityLaunch(World worldIn) {
        super(worldIn);
    }

    @Override
    public void entityInit() {
    }

    @Override
    protected boolean canFitPassenger(@Nonnull Entity passenger) {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(!this.hasNoGravity()){
            dy += 0.0012;
        }
        if (world.isRemote) {
            for (int i = 0; i <= 20; i++) {
                this.world.spawnParticle(ModParticles.MAGIC_PARTICLES, posX, posY, posZ, 0, 0, 0);
                this.world.spawnParticle(ModParticles.MAGIC_PARTICLES1, posX, posY, posZ, 0, 0, 0);
            }
        }

        if(this.collided){
            this.setDeadParticle();
            this.setDead();
        }
    }

    protected void setDeadParticle(){
        if (world.isRemote) {
            for (int i = 0; i <= 200; i++) {
                this.world.spawnParticle(ModParticles.TEST_PARTICLES2, posX, posY, posZ, 0, 0, 0);
            }
        }
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.livingMaxAge = buffer.readInt();
        this.livingAge = buffer.readInt();
        this.forward = new Vec3d(
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
        this.speed = buffer.readDouble();
        this.dy = buffer.readDouble();
        boolean hasPlayerShooter = buffer.readBoolean();
        if(hasPlayerShooter){
            long highBits = buffer.readLong();
            long lowBits = buffer.readLong();
            UUID id = new UUID(highBits, lowBits);
            this.shootingEntity = this.world.getPlayerEntityByUUID(id);
            if(this.shootingEntity != null){
                this.shootingEntity.startRiding(this);
            }
        }
    }
}
