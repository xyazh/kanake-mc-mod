package com.xyazh.kanake.item.items;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.clean.TileClean;
import com.xyazh.kanake.entity.*;
import com.xyazh.kanake.util.TpHelper;
import com.xyazh.kanake.world.ModWorlds;
import com.xyazh.kanake.world.provider.ProviderArea;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemTestItem extends ItemBase {

    public ItemTestItem(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemStack = new ItemStack(this);
        //player.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI,1000));
        //int dim = ModWorlds.getDimIdByName(ProviderArea.providerName);
        //TpHelper.changeDimension(player,dim);
        //ManaData.add(player,50);
        /*if(!world.isRemote){
            for(int i=0;i<100;i++){
                Vec3d m = Vec3d.fromPitchYaw(
                        (float) (player.rotationPitch + Kanake.rand.nextGaussian()*4),
                        (float) (player.rotationYaw + Kanake.rand.nextGaussian()*4));
                EntityExplosion entity = new EntityExplosion(world);
                entity.entityShoot(player,m);
                world.spawnEntity(entity);
            }
        }*/
        return super.onItemRightClick(world, player, hand);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        worldIn.setTileEntity(pos,new TileClean());
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
        //target.addPotionEffect(new PotionEffect(PotionKoori.POTION_KOORI,1000));
        return super.hitEntity(stack, target, attacker);
    }
}
