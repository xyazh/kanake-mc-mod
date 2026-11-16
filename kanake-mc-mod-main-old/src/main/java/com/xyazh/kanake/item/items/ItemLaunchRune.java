package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityLaunch;
import com.xyazh.kanake.entity.EntityMagic;
import com.xyazh.kanake.entity.EntityShoot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemLaunchRune extends ItemBase{
    public ItemLaunchRune(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemStack = new ItemStack(this);
        boolean flag = false;
        double mana = ManaData.get(player);
        if(player.isCreative()){
            flag = true;
        }else if(mana>=12){
            flag = true;
            ManaData.sub(player,12);
        }
        if(!world.isRemote&&flag){
            Vec3d m = Vec3d.fromPitchYaw(
                    (float) (player.rotationPitch),
                    (float) (player.rotationYaw));
            EntityShoot entity = new EntityLaunch(world);
            entity.entityShoot(player, m);
            world.spawnEntity(entity);
            player.startRiding(entity);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}
