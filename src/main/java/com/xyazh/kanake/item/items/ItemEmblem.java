package com.xyazh.kanake.item.items;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemEmblem extends Item implements IHasModel {
        public String[] oreDictName = {};
        String name;
        public final int commandId;
        public ItemEmblem(int commandId)
        {
            this.name = "emblem_" + commandId;
            this.commandId = commandId;
            setUnlocalizedName(name);
            setRegistryName(name);
            ModItems.ITEMS.add(this);
        }

        public ItemEmblem setOreDict(String[] oreDictName){
            this.oreDictName = oreDictName;
            return this;
        }

        @Nonnull
        @Override
        public ItemEmblem setCreativeTab(@Nonnull CreativeTabs tab)
        {
            super.setCreativeTab(tab);
            return this;
        }

        @SideOnly(Side.CLIENT)
        public boolean hasEffect(ItemStack stack)
        {
            return stack.isItemEnchanted();
        }

        @Override
        public void registerModels()
        {
            Kanake.proxy.registerItemRenderer(this, 0, "inventory");
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
            tooltip.add(I18n.format("item." + this.name + ".desc"));
            super.addInformation(stack, world, tooltip, flag);
        }


    }