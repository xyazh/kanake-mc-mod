package com.xyazh.kanake.events;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.clean.TileClean;
import com.xyazh.kanake.block.blocks.rail.WSRailPoweredBase;
import com.xyazh.kanake.damage.CleanDamage;
import com.xyazh.kanake.damage.KillSlimeDamage;
import com.xyazh.kanake.entity.EntityShoot;
import com.xyazh.kanake.entity.EntityWSKnight;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.libs.weaponlib.shader.jim.Shader;
import com.xyazh.kanake.libs.weaponlib.shader.jim.ShaderManager;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.render.FramebufferExample;
import com.xyazh.kanake.util.ParticleUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.*;


@Mod.EventBusSubscriber(modid = Kanake.MODID)
public class Event {
    public static FramebufferExample FBO = null;
    public static Shader TEST_SHADER = null;

    @SubscribeEvent
    public static void onWorldRendered(RenderWorldLastEvent event) {
        if(FBO==null){
            FBO = new FramebufferExample(true);
        }
        if(TEST_SHADER == null){
            TEST_SHADER = ShaderManager.loadVMWShader("test");
        }
        int bindFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        FBO.copyFramebuffer(bindFBO);
        TEST_SHADER.use();
        TEST_SHADER.uniform2f("wh",FBO.width,FBO.height);
        FBO.renderFboQuad();
        TEST_SHADER.release();
    }


    @SubscribeEvent
    public static void drop(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote) {
            return;
        }
        if (entity instanceof EntitySlime && event.getSource() instanceof KillSlimeDamage) {
            int size = ((EntitySlime) entity).getSlimeSize();
            float f;
            if (size >= 4) {
                f = 0.5f;
            } else if (size >= 2) {
                f = 0.125f;
            } else {
                f = 0.03125f;
            }
            if (Kanake.rand.nextFloat() < f) {
                EntityItem entityItem = new EntityItem(entity.world);
                entityItem.setPosition(entity.posX, entity.posY, entity.posZ);
                ItemStack itemStack = new ItemStack(ModItems.SLIME_CORE);
                entityItem.setItem(itemStack);
                event.getDrops().add(entityItem);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onKnightHeal(LivingHurtEvent event) {
        Entity entity = event.getSource().getTrueSource();
        if (entity instanceof EntityWSKnight) {
            float damage = event.getAmount();
            if (damage > 0) {
                ((EntityWSKnight) entity).heal(damage);
                World world = entity.world;
                int amount = (int) damage;
                ParticleUtil.remoteSpawnParticle(world, amount, ModParticles.HEAL_PARTICLES.getParticleName(),
                        entity.posX, entity.posY, entity.posZ,
                        0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onKnightDeath(LivingDeathEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(entity instanceof EntityWSKnight){
            World world = entity.world;
            world.addWeatherEffect(new EntityLightningBolt(world,entity.posX,entity.posY,entity.posZ,false));
            if(!world.isRemote){
                world.createExplosion(entity,entity.posX, entity.posY, entity.posZ, 1,true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onCleanDamage(LivingDamageEvent event) {
        if (event.getSource() instanceof CleanDamage) {
            event.setCanceled(false);
            event.setAmount(TileClean.DAMAGE);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onCleanDamage1(LivingHurtEvent event) {
        if (event.getSource() instanceof CleanDamage) {
            event.setCanceled(false);
            event.setAmount(TileClean.DAMAGE);
        }
    }

    @SubscribeEvent()
    public static void getCollisionBoxes(GetCollisionBoxesEvent event){

    }


    @SubscribeEvent()
    public static void onExplosion(ExplosionEvent.Detonate event){
        Explosion explosion = event.getExplosion();
        for(Entity entity : event.getAffectedEntities()){
            if(!(entity instanceof EntityShoot)){
                continue;
            }
            EntityShoot shoot = (EntityShoot) entity;
            shoot.lastExplosion = explosion;
        }
    }

    @SubscribeEvent
    public static void onMinecraftMove(MinecartUpdateEvent event) {
        EntityMinecart minecraft = event.getMinecart();
        BlockPos pos = event.getPos();
        IBlockState state = minecraft.world.getBlockState(pos);
        Block block = state.getBlock();
        if(!(block instanceof WSRailPoweredBase)){
            return;
        }
        WSRailPoweredBase rail = (WSRailPoweredBase) block;
        double max = rail.getRailMaxSpeed(minecraft.world, minecraft, pos);
        double a = rail.getAcceleration(minecraft.world, minecraft, pos);
        if(minecraft.motionX >0){
            minecraft.motionX += a;
        }else if(minecraft.motionX < 0){
            minecraft.motionX -= a;
        }
        if(minecraft.motionZ >0){
            minecraft.motionZ += a;
        }else if(minecraft.motionZ < 0){
            minecraft.motionZ -= a;
        }
        minecraft.motionX = MathHelper.clamp(minecraft.motionX, -max, max);
        minecraft.motionZ = MathHelper.clamp(minecraft.motionZ, -max, max);
        double mX = minecraft.motionX;
        double mZ = minecraft.motionZ;
        if (minecraft.isBeingRidden())
        {
            mX *= 0.75D;
            mZ *= 0.75D;
        }
        minecraft.move(MoverType.SELF, mX, 0.0D, mZ);
    }
}
