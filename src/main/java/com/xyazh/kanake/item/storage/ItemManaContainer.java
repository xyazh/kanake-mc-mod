package com.xyazh.kanake.item.storage;

import com.xyazh.kanake.api.IItemManaStorage;
import com.xyazh.kanake.api.IManaStorage;
import com.xyazh.kanake.data.ManaData;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemManaContainer extends ItemStorageBase {
    public ItemManaContainer(String name) {
        super(name, 200);
    }

    public ItemManaContainer(String name, int maxMana) {
        super(name, maxMana);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (itemStack.getItem().equals(this)) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof IManaStorage) {
                IManaStorage storage = (IManaStorage) te;
                if (player.isSneaking()) {
                    int mana = this.getMana(itemStack);
                    mana = (int)storage.receiveMana(mana);
                    this.setMana(itemStack,mana);
                } else {
                    int mana = this.remain(itemStack);
                    mana = (int)storage.extractMana(mana);
                    this.addMana(itemStack,mana);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (itemStack.getItem().equals(this)) {
            if (player.isSneaking()) {
                if (player.isCreative()) {
                    this.setFull(itemStack);
                } else {
                    int mana = this.remain(itemStack);
                    int last = (int) ManaData.sub(player, mana);
                    this.addMana(itemStack, mana - last);
                }
            } else {
                int mana = (int) ManaData.remain(player);
                int last = this.subMana(itemStack, mana);
                ManaData.add(player, mana - last);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("tooltip.mana.mana_container.0"));
        tooltip.add(I18n.format("tooltip.mana.mana_container.1"));
        tooltip.add(I18n.format(""));
        super.addInformation(stack, world, tooltip, flag);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return this.getMana(stack) > 3;
    }
}
