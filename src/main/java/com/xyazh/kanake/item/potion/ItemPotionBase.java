package com.xyazh.kanake.item.potion;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.init.ModCreativeTab;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPotionBase extends ItemPotion implements IHasModel {
    public ItemPotionBase(String name)
    {
        super();
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(ModCreativeTab.KNK_CREATIVE);
        ModItems.ITEMS.add(this);
    }

    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }

    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            items.add(new ItemStack(this));
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn)
    {
    }

    @Override
    public void registerModels()
    {
        Kanake.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
