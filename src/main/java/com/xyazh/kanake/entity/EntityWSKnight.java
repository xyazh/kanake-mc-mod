package com.xyazh.kanake.entity;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class EntityWSKnight extends EntitySkeleton {
    public EntityWSKnight(World worldIn) {
        super(worldIn);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(55.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);

    }


    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance diff, @Nullable IEntityLivingData livingData) {
        livingData = super.onInitialSpawn(diff, livingData);
        this.setCanPickUpLoot(true);
        Random random = new Random();
        if(random.nextInt(3)==1){
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.WS_MAGIC_SWORD));
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ModItems.WS_MAGIC_SWORD));
        }else{
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModItems.WS_SWORD));
            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ModItems.WS_SWORD));
        }

        ItemStack head = new ItemStack(Blocks.MOB_SPAWNER);
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);

        ItemStack chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, chest);

        ItemStack legs = new ItemStack(Items.DIAMOND_LEGGINGS);
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);

        ItemStack feet = new ItemStack(Items.DIAMOND_BOOTS);
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
        return livingData;
    }


    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityWolf.class, true));
    }

    protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source){
        super.dropLoot(wasRecentlyHit,lootingModifier,source);
        Random random = Kanake.rand;
        this.dropItem(ModItems.WS_WS_KAKERA,2+random.nextInt(4));
    }
}
