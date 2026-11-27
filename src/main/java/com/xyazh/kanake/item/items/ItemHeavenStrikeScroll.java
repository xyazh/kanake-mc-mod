package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityHeavenStrike;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Super-tier scroll: Sky Strike (天击).
 *
 * <p>The player aims at a surface point up to 192 blocks away. The purple
 * array unfolds there, the energy sphere gathers above the clouds and then
 * slams into the selected point. Looking at the sky falls back to the caster's
 * own position so the spell never lands in an arbitrary unloaded chunk.</p>
 */
public class ItemHeavenStrikeScroll extends ItemBase {
    // Base 200 + two high mana rings (250 each) = 700, so the spell is
    // attainable while still being the most expensive scroll in the mod.
    public static final double MANA_COST = 700.0D;
    public static final double CAST_RANGE = 256.0D;

    public ItemHeavenStrikeScroll(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player,
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

            Vec3d start = player.getPositionEyes(1.0F);
            Vec3d look = player.getLookVec();
            Vec3d end = start.add(look.scale(CAST_RANGE));
            RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);

            double x;
            double y;
            double z;
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                // Use the exact point the player locked onto. The renderer
                // lifts the visible array slightly above this point.
                x = hit.hitVec.x;
                y = hit.hitVec.y;
                z = hit.hitVec.z;
            } else if (look.y > 0.6D) {
                // Looking at the sky: cast around the caster instead of at the
                // far end of the look vector.
                x = player.posX;
                y = player.posY;
                z = player.posZ;
            } else {
                x = end.x;
                z = end.z;
                int bx = MathHelper.floor(x);
                int bz = MathHelper.floor(z);
                BlockPos surface = world.getHeight(new BlockPos(bx, 0, bz));
                y = surface.getY();
            }
            y = Math.max(0.05D, Math.min(y, world.getActualHeight() - 0.1D));

            EntityHeavenStrike strike = new EntityHeavenStrike(world);
            strike.setPosition(x, y, z);
            strike.shootingEntity = player;
            // The target can be up to 12 chunks away. The ticket has to exist
            // before the entity is spawned, otherwise it may never tick.
            strike.prepareInitialChunkLoad();
            world.spawnEntity(strike);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
