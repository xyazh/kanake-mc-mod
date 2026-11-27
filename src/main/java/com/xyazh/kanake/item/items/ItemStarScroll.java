package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityStar;
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

import javax.annotation.Nonnull;

/**
 * Super-tier scroll: Star (超位魔法——恒星).
 *
 * <p>The player locks a surface point up to 256 blocks away. A containment
 * array unfolds there and a star is born above it. Looking at the sky falls
 * back to the ground below the caster.</p>
 */
public class ItemStarScroll extends ItemBase {
    public static final double MANA_COST = 700.0D;
    public static final double CAST_RANGE = 256.0D;

    public ItemStarScroll(String name) {
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

            Vec3d start = player.getPositionEyes(1.0F);
            Vec3d look = player.getLookVec();
            Vec3d end = start.add(look.scale(CAST_RANGE));
            RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);

            double x;
            double y;
            double z;
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                x = hit.hitVec.x;
                y = hit.hitVec.y;
                z = hit.hitVec.z;
            } else if (look.y < -0.35D) {
                x = end.x;
                z = end.z;
                BlockPos surface = world.getHeight(new BlockPos(
                        MathHelper.floor(x), 0, MathHelper.floor(z)));
                y = surface.getY();
            } else {
                x = player.posX;
                z = player.posZ;
                BlockPos surface = world.getHeight(new BlockPos(
                        MathHelper.floor(x), 0, MathHelper.floor(z)));
                y = surface.getY();
            }
            y = Math.max(0.05D, Math.min(y, world.getActualHeight() - 0.1D));

            EntityStar star = new EntityStar(world);
            star.setPosition(x, y, z);
            star.shootingEntity = player;
            star.prepareInitialChunkLoad();
            world.spawnEntity(star);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
