package com.xyazh.kanake.item.items;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.gui.test.GuiHandlerTest;
import com.xyazh.kanake.gui.test.RuneItemStackHandler;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import com.xyazh.kanake.magic.glyph.OrderSet;
import com.xyazh.kanake.magic.mana.ManaSourceRef;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class ItemEmptyMagicRune extends ItemBase {
    public ItemEmptyMagicRune(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        items.add(new ItemStack(this));
    }

    public OrderSet getOrders(@Nonnull ItemStack itemStack) {
        LinkedList<GlyphOrder> normal = new LinkedList<>();
        LinkedList<GlyphOrder> finish = new LinkedList<>();
        if (itemStack.isEmpty()) {
            return new OrderSet(normal, finish);
        }
        readOrders(itemStack, "rune_order", 54, 0, normal);
        readOrders(itemStack, "rune_finish", 18, 1000, finish);
        return new OrderSet(normal, finish);
    }

    private void readOrders(ItemStack itemStack, String key, int size, int uidOffset, LinkedList<GlyphOrder> target) {
        RuneItemStackHandler handler = new RuneItemStackHandler(itemStack, key, size);
        for (int i = 0; i < handler.getSlots(); i++) {
            Item item = handler.getStackInSlot(i).getItem();
            if (!(item instanceof ItemEmblem)) {
                continue;
            }
            ItemEmblem emblem = (ItemEmblem) item;
            target.add(new GlyphOrder(emblem.commandId, uidOffset + i, GlyphData.get(handler.getStackInSlot(i))));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if(player.isSneaking()){
            player.openGui(Kanake.instance, GuiHandlerTest.GUI_ID, world, 0, 0, 0);
        }else if(!world.isRemote){
            Vec3d m = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
            EntityEmptyMagic entity = new EntityEmptyMagic(world);
            entity.entityShoot(player,m);
            entity.rotationYaw = player.rotationYaw;
            entity.rotationPitch = player.rotationPitch;
            entity.prevRotationYaw = player.prevRotationYaw;
            entity.prevRotationPitch = player.prevRotationPitch;
            entity.g = 0;
            entity.gdy = 0;
            entity.cooldown = 5;
            entity.prevPosX = entity.posX;
            entity.prevPosY = entity.posY;
            entity.prevPosZ = entity.posZ;
            entity.setManaSource(ManaSourceRef.player(player));
            OrderSet orders = this.getOrders(player.getHeldItem(hand));
            entity.setOrders(orders.normal, orders.finish);
            world.spawnEntity(entity);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
        return super.hitEntity(stack, target, attacker);
    }
}
