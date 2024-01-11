package com.xyazh.kanake.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityTreeMan extends EntityGolem {
    public EntityTreeMan(World worldIn) {
        super(worldIn);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

    }

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

    protected boolean canTriggerWalking()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.newPosRotationIncrements = 0;
    }

    public void applyEntityCollision(@Nonnull Entity entityIn)
    {
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
    }

    public float getCollisionBorderSize()
    {
        return 0.0F;
    }

    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount)
    {
        if(source.isMagicDamage()){
            amount = 0;
        }
        if(source.isFireDamage()){
            this.setFire(Integer.MAX_VALUE);
            amount *= 3;
        }
        if(source.isProjectile()){
            amount /= 10;
        }
        if(!(source.isExplosion()||source.isDamageAbsolute())){
            Entity entity = source.getTrueSource();
            if(entity != null){
                for(ItemStack itemStack:entity.getHeldEquipment()){
                    if (itemStack.getItem().getToolClasses(itemStack).contains("axe")){
                        amount *= 2;
                        break;
                    }
                }
            }else {
                amount /= 2;
            }
        }
        return super.attackEntityFrom(source,amount);
    }
}
