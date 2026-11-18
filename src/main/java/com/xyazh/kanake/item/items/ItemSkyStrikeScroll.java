package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntitySkyStrikeProjectile;
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
 * The blank scroll carrying the super-tier spell "Sky Strike".
 */
public class ItemSkyStrikeScroll extends ItemBase {
    public static final double MANA_COST = 180.0D;

    public ItemSkyStrikeScroll(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
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
            EntitySkyStrikeProjectile projectile = new EntitySkyStrikeProjectile(world);
            projectile.entityShoot(player, direction);
            world.spawnEntity(projectile);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(I18n.format("item.scroll_sky_strike.desc"));
        tooltip.add(I18n.format("item.scroll_sky_strike.desc.1"));
        tooltip.add(I18n.format("item.scroll_sky_strike.desc.2"));
        tooltip.add(I18n.format("item.scroll_sky_strike.desc.3"));
    }
}
