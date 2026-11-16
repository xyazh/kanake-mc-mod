package com.xyazh.kanake.block.blocks.rail;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WSRailPoweredBase extends WSRailBase {
    public float acceleration = 0.2f;
    public WSRailPoweredBase(String name) {
        super(name);
        setLightLevel(7);
    }

    public WSRailPoweredBase(String name,float speed) {
        super(name, speed);
        setLightLevel(7);
    }

    public WSRailPoweredBase setAcceleration(float acceleration){
        this.acceleration = acceleration;
        return this;
    }


    public float getAcceleration(@Nonnull World world, @Nonnull EntityMinecart cart, @Nonnull BlockPos pos){
        return this.acceleration;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World player, List<String> tooltip, @Nonnull ITooltipFlag advanced){
        tooltip.add(I18n.format("tile.ws_powered_rail.desc.1"));
        tooltip.add(I18n.format("tile.ws_powered_rail.desc.2"));
        tooltip.add(I18n.format("tile.ws_powered_rail.desc.3"));
        tooltip.add(I18n.format("tile.ws_powered_rail.desc.4",this.speed));
    }


    @Override
    public boolean isFlexibleRail(@Nonnull IBlockAccess world, @Nonnull BlockPos pos)
    {
        return false;
    }

}
