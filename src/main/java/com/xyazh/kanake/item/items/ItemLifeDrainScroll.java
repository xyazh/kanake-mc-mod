package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityLifeDrain;
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

public class ItemLifeDrainScroll extends ItemBase {
    public static final double MANA_COST = 20.0D;

    public ItemLifeDrainScroll(String name) {
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
            EntityLifeDrain drain = new EntityLifeDrain(world);
            drain.setOwner(player);
            drain.setPosition(player.posX, player.posY + player.height + 0.7D, player.posZ);
            world.spawnEntity(drain);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
