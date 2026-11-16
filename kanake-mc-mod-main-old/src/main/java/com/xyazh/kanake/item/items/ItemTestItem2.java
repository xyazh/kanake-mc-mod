package com.xyazh.kanake.item.items;


import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.magic.Magic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

public class ItemTestItem2 extends ItemBase {

    public ItemTestItem2(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    public LinkedList<Integer> getOrder(){
        LinkedList<Integer> list = new LinkedList<>();
        list.add(Magic.GRAVITY.order);
        list.add(Magic.SPEED_LOW.order);
        list.add(Magic.CALLBACK.order);
        list.add(Magic.EXPLODE_BIG.order);
        list.add(Magic.CALLBACK.order);
        list.add(Magic.SPAWN.order);
        list.add(Magic.CALLBACK.order);
        list.add(Magic.CALLBACK.order);
        list.add(Magic.CALLBACK.order);
        list.add(Magic.SPAWN.order);
        return list;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if(!world.isRemote){
            Vec3d m = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
            EntityEmptyMagic entity = new EntityEmptyMagic(world);
            entity.entityShoot(player,m);
            entity.setOrder(this.getOrder());
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
