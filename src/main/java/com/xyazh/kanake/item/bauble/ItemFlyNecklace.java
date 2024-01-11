package com.xyazh.kanake.item.bauble;

import com.xyazh.kanake.data.ManaData;
import com.xyazh.kanake.entity.EntityFireBall;
import com.xyazh.kanake.entity.EntityMagic;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;


public class ItemFlyNecklace extends ItemBaubleBase{
    public ItemFlyNecklace(String name) {
        super(name);
        this.setBaubleType(AMULET);
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        super.onEquipped(itemstack, player);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        super.onUnequipped(itemstack, player);
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
        if(flag){
            Vec3d m = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
            player.motionX += m.x/4;
            player.motionY += m.y/4+0.4;
            player.motionZ += m.z/4;
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
