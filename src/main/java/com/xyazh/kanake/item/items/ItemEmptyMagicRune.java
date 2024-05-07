package com.xyazh.kanake.item.items;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.gui.test.GuiHandlerTest;
import com.xyazh.kanake.gui.test.RuneItemStackHandler;
import com.xyazh.kanake.magic.Magic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

    public LinkedList<Integer> getOrder(@Nonnull ItemStack itemStack){
        LinkedList<Integer> list = new LinkedList<>();
        if(itemStack.isEmpty()){
            return list;
        }
        RuneItemStackHandler magicInv = new RuneItemStackHandler(itemStack);
        for(int i=0;i<magicInv.getSlots();i++){
            Item item1 = magicInv.getStackInSlot(i).getItem();
            if (!(item1 instanceof ItemEmblem)){
                continue;
            }
            ItemEmblem emblem = (ItemEmblem) item1;
            list.add(emblem.commandId);
        }
        return list;
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
            entity.setOrder(this.getOrder(player.getHeldItem(hand)));
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
