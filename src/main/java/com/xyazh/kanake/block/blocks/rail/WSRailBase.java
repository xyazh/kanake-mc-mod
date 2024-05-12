package com.xyazh.kanake.block.blocks.rail;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WSRailBase extends BlockRail implements IHasModel {
    public float speed = 0.1f;
    public WSRailBase(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
        setHardness(0.3f);
    }

    public WSRailBase setISoundType(SoundType soundType){
        setSoundType(soundType);
        return this;
    };



    public WSRailBase(String name,float speed) {
        setUnlocalizedName(name);
        setRegistryName(name);
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
        setHardness(0.3f);
        setRailMaxSpeed(speed);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }

    public WSRailBase setRailMaxSpeed(float speed){
        this.speed = speed;
        return this;
    }

    @Override
    public float getRailMaxSpeed(@Nonnull World world, @Nonnull EntityMinecart cart, @Nonnull BlockPos pos) {
        return this.speed;
    }

    @Nonnull
    @Override
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune)
    {
        return super.getItemDropped(state, rand, fortune);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World player, List<String> tooltip, @Nonnull ITooltipFlag advanced){
        tooltip.add("高速轨道");
        tooltip.add("不需要红石信号激活");
        tooltip.add("小心脱轨!");
        tooltip.add("速度倍率 x"+this.speed);
    }

    @Override
    public int quantityDropped(@Nonnull Random rand) {
        return super.quantityDropped(rand);
    }

    @Override
    public void registerModels() {
        Kanake.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

}
