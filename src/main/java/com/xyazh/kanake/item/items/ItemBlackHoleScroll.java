package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityBlackHole;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Launch scroll for the existing black-hole entity.
 *
 * <p>This class only configures the public radius/speed/lifetime/colour fields
 * and spawns {@link EntityBlackHole}. The entity logic itself is intentionally
 * untouched.</p>
 */
public class ItemBlackHoleScroll extends ItemBase {
    public static final double MANA_COST = 300.0D;
    public static final float PROJECTILE_RADIUS = 3.0F;
    public static final double PROJECTILE_SPEED = 0.75D;
    public static final int PROJECTILE_LIFETIME = 400;

    public ItemBlackHoleScroll(String name) {
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
            EntityBlackHole hole = new EntityBlackHole(world);
            hole.setRadius(PROJECTILE_RADIUS);
            hole.color = 0x000000;
            hole.alpha = 255;
            hole.speed = PROJECTILE_SPEED;
            hole.setLivingMaxAge(PROJECTILE_LIFETIME);
            hole.entityShoot(player, direction);
            hole.speed = PROJECTILE_SPEED;
            world.spawnEntity(hole);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, World world,
                               @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(I18n.format("item.scroll_black_hole.desc"));
        tooltip.add(I18n.format("item.scroll_black_hole.desc.1"));
        tooltip.add(I18n.format("item.scroll_black_hole.desc.2"));
    }
}
