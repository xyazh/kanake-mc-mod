package com.xyazh.kanake.item.items;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.util.IHasModel;
import net.minecraft.nbt.NBTTagCompound;
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
            ModItems.EMBLEM_MAP.put(commandId, this);
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
            NBTTagCompound data = GlyphData.get(stack);
            if (data.hasKey(GlyphData.ENHANCE)) {
                tooltip.add(I18n.format("tooltip.glyph.enhance", data.getInteger(GlyphData.ENHANCE)));
            }
            if (data.hasKey(GlyphData.QUICKEN)) {
                tooltip.add(I18n.format("tooltip.glyph.quicken", data.getInteger(GlyphData.QUICKEN)));
            }
            if (data.hasKey(GlyphData.DELAY)) {
                tooltip.add(I18n.format("tooltip.glyph.delay", data.getInteger(GlyphData.DELAY)));
            }
            if (data.hasKey(GlyphData.COUNT)) {
                tooltip.add(I18n.format("tooltip.glyph.count", data.getInteger(GlyphData.COUNT)));
            }
            if (data.hasKey(GlyphData.SPEED)) {
                tooltip.add(I18n.format("tooltip.glyph.speed", data.getInteger(GlyphData.SPEED)));
            }
            if (data.hasKey(GlyphData.RADIUS)) {
                tooltip.add(I18n.format("tooltip.glyph.radius", data.getInteger(GlyphData.RADIUS)));
            }
            if (data.hasKey(GlyphData.MODE)) {
                tooltip.add(I18n.format("tooltip.glyph.mode." + (data.getInteger(GlyphData.MODE) % 3)));
            }
            if (data.hasKey(GlyphData.COLOR)) {
                tooltip.add(I18n.format("tooltip.glyph.color", String.format("#%06X", data.getInteger(GlyphData.COLOR) & 0xFFFFFF)));
            }
            if (data.hasKey(GlyphData.INHERIT)) {
                tooltip.add(I18n.format("tooltip.glyph.inherit"));
            }
            super.addInformation(stack, world, tooltip, flag);
        }


    }