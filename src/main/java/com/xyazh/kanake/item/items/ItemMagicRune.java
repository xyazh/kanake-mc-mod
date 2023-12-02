package com.xyazh.kanake.item.items;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityExplosion;
import com.xyazh.kanake.entity.EntityMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemMagicRune extends ItemBase{
    public ItemMagicRune(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemStack = new ItemStack(this);
        boolean flag = false;
        double mana = ManaData.get(player);
        if(player.isCreative()){
            flag = true;
        }else if(mana>=4){
            flag = true;
            ManaData.sub(player,4);
        }
        if(!world.isRemote&&flag){
            Vec3d m = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
            EntityMagic entity = new EntityMagic(world);
            entity.entityShoot(player,m);
            world.spawnEntity(entity);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }
}
