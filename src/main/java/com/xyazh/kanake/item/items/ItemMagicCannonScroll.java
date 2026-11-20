package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityMagicCannonBeam;
import com.xyazh.kanake.entity.EntityMagicCannonCharge;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Charge-and-release spell scroll. Holding the scroll for the full use duration
 * builds the two arrays, then fires the fixed Magic Cannon beam.
 */
public class ItemMagicCannonScroll extends ItemBase {
    public static final double MANA_COST = 80.0D;
    // Important: do not store the charge entity id in the held ItemStack NBT.
    // Mutating that stack while the item is active makes the client's
    // activeItemStack reference diverge from the synchronized held stack,
    // which resets the vanilla active-hand state. A server-side map avoids
    // changing the stack at all.
    private static final Map<UUID, EntityMagicCannonCharge> CHARGES = new HashMap<UUID, EntityMagicCannonCharge>();

    public ItemMagicCannonScroll(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(@Nonnull ItemStack stack) {
        return EntityMagicCannonCharge.CHARGE_TICKS;
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(@Nonnull ItemStack stack) {
        return EnumAction.BOW;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // If a previous use left a stale active-hand state, clear it before
        // starting the next charge. Do not interrupt an active use of a
        // different item.
        if (player.isHandActive()) {
            ItemStack activeStack = player.getActiveItemStack();
            if (activeStack.isEmpty() || activeStack.getItem() == this) {
                player.stopActiveHand();
            } else {
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
            }
        }

        // The client always starts the hold state; mana is authoritative on the
        // server. This avoids a client/server active-hand mismatch where the
        // server charges while the client never enters the use state.
        if (!world.isRemote && !player.isCreative() && ManaData.get(player) < MANA_COST) {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
        }

        player.setActiveHand(hand);
        if (!world.isRemote && player.isHandActive()) {
            // Remove an orphaned focus from an interrupted previous use.
            this.cancelCharge(player);

            if (!player.isCreative()) {
                if (player instanceof EntityPlayerMP) {
                    ManaData.subSync((EntityPlayerMP) player, MANA_COST);
                } else {
                    ManaData.sub(player, MANA_COST);
                }
            }

            EntityMagicCannonCharge charge = new EntityMagicCannonCharge(world);
            charge.setOwner(player);
            charge.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            world.spawnEntity(charge);
            CHARGES.put(player.getUniqueID(), charge);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entityLiving) {
        if (!world.isRemote && entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            EntityMagicCannonCharge charge = this.findCharge(player);
            if (charge != null) {
                Vec3d direction = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
                double originX = player.posX + direction.x * 1.5D;
                double originY = player.posY + player.getEyeHeight() + direction.y * 1.5D;
                double originZ = player.posZ + direction.z * 1.5D;

                EntityMagicCannonBeam beam = new EntityMagicCannonBeam(world);
                beam.setOwner(player);
                beam.setPosition(originX, originY, originZ);
                beam.setForward(direction);
                world.spawnEntity(beam);

                charge.setDead();
                CHARGES.remove(player.getUniqueID());
            }
        }
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entityLiving, int timeLeft) {
        if (!world.isRemote && entityLiving instanceof EntityPlayer) {
            this.cancelCharge((EntityPlayer) entityLiving);
        }
    }

    private EntityMagicCannonCharge findCharge(EntityPlayer player) {
        UUID id = player.getUniqueID();
        EntityMagicCannonCharge charge = CHARGES.get(id);
        if (charge == null || charge.isDead || charge.getOwner() != player) {
            CHARGES.remove(id);
            return null;
        }
        return charge;
    }

    private void cancelCharge(EntityPlayer player) {
        EntityMagicCannonCharge charge = this.findCharge(player);
        if (charge != null) {
            charge.setDead();
        }
        CHARGES.remove(player.getUniqueID());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(I18n.format("item.scroll_magic_cannon.desc"));
        tooltip.add(I18n.format("item.scroll_magic_cannon.desc.1"));
        tooltip.add(I18n.format("item.scroll_magic_cannon.desc.2"));
    }
}
