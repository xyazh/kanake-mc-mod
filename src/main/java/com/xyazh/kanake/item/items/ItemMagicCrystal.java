package com.xyazh.kanake.item.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMagicCrystal extends ItemBase {
    public static final int ENHANCE = 1;
    public static final int QUICKEN = 2;
    public static final int DELAY = 3;
    public static final int DIRECTION = 4;

    private final int crystalType;

    public ItemMagicCrystal(String name, int crystalType) {
        super(name);
        this.crystalType = crystalType;
    }

    public int getCrystalType() {
        return this.crystalType;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        tooltip.add(I18n.format("item.magic_crystal.desc"));
        super.addInformation(stack, world, tooltip, flag);
    }
}
