package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityNuclearStrike;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Super-tier scroll: Nuclear Strike.
 *
 * <p>The player designates a point up to 384 blocks away. A successful block
 * hit produces a ground burst; if no block is found the strike is an airburst
 * at the end of the look vector. The actual ritual, damage and crater are
 * owned by {@link EntityNuclearStrike}.</p>
 */
public class ItemNuclearStrikeScroll extends ItemBase {
    public static final double MANA_COST = 640.0D;
    public static final double CAST_RANGE = EntityNuclearStrike.CAST_RANGE;

    public ItemNuclearStrikeScroll(String name) {
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

            Vec3d start = player.getPositionEyes(1.0F);
            Vec3d look = player.getLookVec();
            Vec3d end = start.add(look.scale(CAST_RANGE));
            RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);

            boolean airburst = hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK;
            double x;
            double y;
            double z;
            if (airburst) {
                x = end.x;
                y = Math.max(1.0D, Math.min(end.y, world.getActualHeight() - 2.0D));
                z = end.z;
            } else {
                x = hit.hitVec.x;
                y = hit.hitVec.y + 0.10D;
                z = hit.hitVec.z;
            }

            EntityNuclearStrike strike = new EntityNuclearStrike(world);
            strike.setPosition(x, y, z);
            strike.setAirburst(airburst);
            strike.shootingEntity = player;
            // The target may be far outside the player's loaded area. The
            // chunk ticket has to exist before the entity is spawned, otherwise
            // the entity itself would never tick.
            strike.prepareInitialChunkLoad();
            // If the ray passed through unloaded terrain, the endpoint can be
            // below the real surface. Convert those cases to a ground burst
            // instead of leaving the star core buried in rock.
            strike.refineTargetAfterChunkLoad();
            world.spawnEntity(strike);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
