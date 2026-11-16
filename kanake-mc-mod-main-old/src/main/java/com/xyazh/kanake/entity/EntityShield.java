package com.xyazh.kanake.entity;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityShield extends EntityMob {

    public EntityShield(World worldIn) {
        super(worldIn);
    }

    @Override
    public void setEntityBoundingBox(@Nonnull AxisAlignedBB bb) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                this.posX-8,this.posY-8,this.posZ-8,
                this.posX+8,this.posY+8,this.posZ+8
        );
        super.setEntityBoundingBox(axisAlignedBB);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0f);
    }

    @Nonnull
    public EnumPushReaction getPushReaction()
    {
        return EnumPushReaction.IGNORE;
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        super.addVelocity(0, 0, 0);
    }

    @Override
    public void knockBack(@Nonnull Entity source, float strength, double xRatio, double zRatio) {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.rotationYaw = 0;
        this.rotationPitch = 0;
        this.rotationYawHead = 0;
        this.renderYawOffset = 0;
        this.prevRotationYaw = 0;
        this.prevRotationPitch = 0;
        this.prevRotationYawHead = 0;
        this.prevRenderYawOffset = 0;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.newPosRotationIncrements = 0;
    }

    @Override
    public void applyEntityCollision(@Nonnull Entity entityIn)
    {
    }

    @Override
    protected void collideWithEntity(@Nonnull Entity entityIn)
    {
    }

    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount)
    {
        return super.attackEntityFrom(source,amount);
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime = 20;
        super.onDeathUpdate();
    }
}
