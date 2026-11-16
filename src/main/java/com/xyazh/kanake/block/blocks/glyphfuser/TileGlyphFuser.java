package com.xyazh.kanake.block.blocks.glyphfuser;

import com.xyazh.kanake.magic.Magic;
import com.xyazh.kanake.magic.command.Command;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.item.items.ItemEmblem;
import com.xyazh.kanake.item.items.ItemMagicCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

public class TileGlyphFuser extends TileEntity {
    private ItemStack glyphStack = ItemStack.EMPTY;

    public ItemStack getGlyphStack() {
        return this.glyphStack;
    }

    public void setGlyphStack(ItemStack stack) {
        this.glyphStack = stack == null ? ItemStack.EMPTY : stack;
        this.markDirty();
    }

    public boolean onActivate(EntityPlayer player, EnumHand hand) {
        if (this.world == null) {
            return false;
        }
        if (this.world.isRemote) {
            return true;
        }
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            if (this.glyphStack.isEmpty()) {
                return true;
            }
            ItemStack out = this.glyphStack.copy();
            this.glyphStack = ItemStack.EMPTY;
            this.giveToPlayer(player, out);
            this.markDirty();
            return true;
        }

        if (held.getItem() instanceof ItemEmblem) {
            if (!this.glyphStack.isEmpty()) {
                this.giveToPlayer(player, this.glyphStack.copy());
            }
            this.glyphStack = held.copy();
            this.glyphStack.setCount(1);
            held.shrink(1);
            if (held.isEmpty()) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
            this.markDirty();
            this.playEffect();
            return true;
        }

        if (held.getItem() instanceof ItemMagicCrystal) {
            if (this.glyphStack.isEmpty() || !(this.glyphStack.getItem() instanceof ItemEmblem)) {
                this.message(player, "message.glyph_fuser.no_glyph");
                return true;
            }
            ItemMagicCrystal crystal = (ItemMagicCrystal) held.getItem();
            ItemEmblem emblem = (ItemEmblem) this.glyphStack.getItem();
            Command command = Magic.ORDER_MAP.get(emblem.commandId);
            if (command == null || !command.canUpgrade(crystal.getCrystalType())) {
                this.message(player, "message.glyph_fuser.unsupported");
                return true;
            }
            NBTTagCompound data = GlyphData.getOrCreate(this.glyphStack);
            command.applyUpgrade(data, crystal.getCrystalType());
            held.shrink(1);
            if (held.isEmpty()) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
            this.markDirty();
            this.playEffect();
            this.message(player, "message.glyph_fuser.upgraded");
            return true;
        }

        if (held.getItem() instanceof ItemDye) {
            if (this.glyphStack.isEmpty() || !(this.glyphStack.getItem() instanceof ItemEmblem)) {
                this.message(player, "message.glyph_fuser.no_glyph");
                return true;
            }
            ItemEmblem emblem = (ItemEmblem) this.glyphStack.getItem();
            Command command = Magic.ORDER_MAP.get(emblem.commandId);
            if (command == null || !command.canApplyDye()) {
                this.message(player, "message.glyph_fuser.unsupported");
                return true;
            }
            EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(held.getMetadata());
            command.applyDye(GlyphData.getOrCreate(this.glyphStack), dyeColor.getColorValue());
            held.shrink(1);
            if (held.isEmpty()) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
            this.markDirty();
            this.playEffect();
            this.message(player, "message.glyph_fuser.colored");
            return true;
        }

        this.message(player, "message.glyph_fuser.invalid");
        return true;
    }

    private void giveToPlayer(EntityPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
        }
    }

    private void message(EntityPlayer player, String key) {
        player.sendStatusMessage(new TextComponentTranslation(key), true);
    }

    private void playEffect() {
        if (this.world == null) {
            return;
        }
        this.world.playSound(null, this.pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.BLOCKS, 0.7f, 1.0f);
        for (int i = 0; i < 12; i++) {
            this.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
                    this.pos.getX() + 0.5 + (this.world.rand.nextDouble() - 0.5) * 0.8,
                    this.pos.getY() + 0.8 + this.world.rand.nextDouble() * 0.5,
                    this.pos.getZ() + 0.5 + (this.world.rand.nextDouble() - 0.5) * 0.8,
                    (this.world.rand.nextDouble() - 0.5) * 0.2,
                    this.world.rand.nextDouble() * 0.2,
                    (this.world.rand.nextDouble() - 0.5) * 0.2);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("Glyph")) {
            this.glyphStack = new ItemStack(compound.getCompoundTag("Glyph"));
        } else {
            this.glyphStack = ItemStack.EMPTY;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!this.glyphStack.isEmpty()) {
            compound.setTag("Glyph", this.glyphStack.writeToNBT(new NBTTagCompound()));
        }
        return compound;
    }
}
