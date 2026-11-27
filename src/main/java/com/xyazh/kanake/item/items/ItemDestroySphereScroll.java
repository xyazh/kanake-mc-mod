package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityDestroy;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Launch scroll for the existing destruction-sphere entity.
 *
 * <p>Only public radius/speed/lifetime/colour fields are configured here; the
 * entity logic itself is intentionally untouched.</p>
 */
public class ItemDestroySphereScroll extends ItemBase {
    public static final double MANA_COST = 200.0D;
    public static final float PROJECTILE_RADIUS = 3.0F;
    public static final double PROJECTILE_SPEED = 0.75D;
    public static final int PROJECTILE_LIFETIME = 400;

    public ItemDestroySphereScroll(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world,
                                                    @Nonnull EntityPlayer player,
                                                    @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && (player.isCreative() || ManaData.get(player) >= MANA_COST)) {
            if (!player.isCreative()) {
                if (player instanceof EntityPlayerMP) {
                    ManaData.subSync((EntityPlayerMP) player, MANA_COST);
                } else {
                    ManaData.sub(player, MANA_COST);
                }
            }

            Vec3d direction = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
            EntityDestroy sphere = new EntityDestroy(world);
            sphere.setRadius(PROJECTILE_RADIUS);
            sphere.color = 0x000000;
            sphere.alpha = 255;
            sphere.speed = PROJECTILE_SPEED;
            sphere.setLivingMaxAge(PROJECTILE_LIFETIME);
            sphere.entityShoot(player, direction);
            sphere.speed = PROJECTILE_SPEED;
            world.spawnEntity(sphere);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
