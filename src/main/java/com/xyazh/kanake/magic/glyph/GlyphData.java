package com.xyazh.kanake.magic.glyph;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * 纹章物品上的可融合参数。所有数值都写在 ItemStack 的 GlyphData 标签里。
 */
public final class GlyphData {
    public static final String TAG = "GlyphData";

    public static final String ENHANCE = "enhance"; // XX1
    public static final String QUICKEN = "quicken"; // XX2
    public static final String DELAY = "delay";     // XX3
    public static final String COUNT = "count";
    public static final String MODE = "mode";
    public static final String SPEED = "speed";
    public static final String RADIUS = "radius";
    public static final String COLOR = "color";
    public static final String INHERIT = "inherit";

    private GlyphData() {
    }

    public static NBTTagCompound get(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new NBTTagCompound();
        }
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null || !compound.hasKey(TAG)) {
            return new NBTTagCompound();
        }
        return compound.getCompoundTag(TAG).copy();
    }

    public static NBTTagCompound getOrCreate(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        if (!tag.hasKey(TAG)) {
            tag.setTag(TAG, new NBTTagCompound());
        }
        return tag.getCompoundTag(TAG);
    }

    public static int getInt(ItemStack stack, String key, int def) {
        NBTTagCompound data = get(stack);
        return data.hasKey(key) ? data.getInteger(key) : def;
    }

    public static int getInt(NBTTagCompound data, String key, int def) {
        return data != null && data.hasKey(key) ? data.getInteger(key) : def;
    }

    public static void setInt(ItemStack stack, String key, int value) {
        getOrCreate(stack).setInteger(key, value);
    }

    public static int getColor(ItemStack stack, int def) {
        NBTTagCompound data = get(stack);
        return data.hasKey(COLOR) ? data.getInteger(COLOR) : def;
    }

    public static void setColor(ItemStack stack, int color) {
        setInt(stack, COLOR, color);
    }
}
