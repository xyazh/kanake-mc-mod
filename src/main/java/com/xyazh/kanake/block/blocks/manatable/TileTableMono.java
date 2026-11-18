package com.xyazh.kanake.block.blocks.manatable;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileTableMono extends TileManaWithForeverEntity{
    public BlockPos coreBlockPos = new BlockPos(0,0,0);

    @Nullable
    public TileTableCoreMono getCoreMono(){
        if(this instanceof TileTableCoreMono){
            return (TileTableCoreMono) this;
        }
        TileEntity te = this.world.getTileEntity(this.coreBlockPos);
        if(te instanceof TileTableCoreMono){
            return (TileTableCoreMono) te;
        }
        return null;
    }

    @Override
    public void onInvChanges() {
        super.onInvChanges();
        TileTableCoreMono coreMono = this.getCoreMono();
        if(coreMono == null){
            return;
        }
        coreMono.shouldFindRecipe = true;
    }

    /**
     * 台座融合：台座上放着纹章时，手持魔法结晶或染料右击可把参数写入纹章。
     * 该方法在客户端与服务端都会修改台座物品栈，保证展示物一致；
     * 提示消息、音效和粒子只在服务端触发，避免重复。
     *
     * @return 是否已处理本次右击（包括纹章不支持该结晶/染料的情况）
     */
    public boolean tryFuse(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if(this.world == null || this.itemStacks[0].isEmpty()
                || !(this.itemStacks[0].getItem() instanceof ItemEmblem)){
            return false;
        }
        ItemStack held = player.getHeldItem(hand);
        if(held.isEmpty()){
            return false;
        }
        ItemEmblem emblem = (ItemEmblem) this.itemStacks[0].getItem();
        Command command = Magic.ORDER_MAP.get(emblem.commandId);

        if(held.getItem() instanceof ItemMagicCrystal){
            ItemMagicCrystal crystal = (ItemMagicCrystal) held.getItem();
            if(command == null || !command.canUpgrade(crystal.getCrystalType())){
                this.message(player, "message.glyph_fuse.unsupported");
                return true;
            }
            command.applyUpgrade(GlyphData.getOrCreate(this.itemStacks[0]), crystal.getCrystalType());
            this.consumeHeld(player, hand, held);
            this.finishFuse();
            this.message(player, "message.glyph_fuse.upgraded");
            return true;
        }

        if(held.getItem() instanceof ItemDye){
            if(command == null || !command.canApplyDye()){
                this.message(player, "message.glyph_fuse.unsupported");
                return true;
            }
            EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(held.getMetadata());
            command.applyDye(GlyphData.getOrCreate(this.itemStacks[0]), dyeColor.getColorValue());
            this.consumeHeld(player, hand, held);
            this.finishFuse();
            this.message(player, "message.glyph_fuse.colored");
            return true;
        }

        return false;
    }

    private void consumeHeld(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull ItemStack held) {
        held.shrink(1);
        if(held.isEmpty()){
            player.setHeldItem(hand, ItemStack.EMPTY);
        }
    }

    private void finishFuse() {
        this.onInvChanges();
        this.markDirty();
        this.updateDisplayedItem();
        this.playFuseEffect();
    }

    private void updateDisplayedItem() {
        if(this.feItemEntity != null && !this.feItemEntity.isDead){
            this.feItemEntity.setItem(this.itemStacks[0].copy());
        }
    }

    private void message(@Nonnull EntityPlayer player, @Nonnull String key) {
        if(!this.world.isRemote){
            player.sendStatusMessage(new TextComponentTranslation(key), true);
        }
    }

    private void playFuseEffect() {
        if(this.world.isRemote){
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
    public void update() {
        super.update();
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        int x,y,z;
        x = compound.getInteger("coreBlockPos.x");
        // 兼容旧存档：旧版本把 Y 写进了 coreBlockPos.args
        if (compound.hasKey("coreBlockPos.y")) {
            y = compound.getInteger("coreBlockPos.y");
        } else {
            y = compound.getInteger("coreBlockPos.args");
        }
        z = compound.getInteger("coreBlockPos.z");
        this.coreBlockPos = new BlockPos(x,y,z);
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("coreBlockPos.x",this.coreBlockPos.getX());
        compound.setInteger("coreBlockPos.y",this.coreBlockPos.getY());
        compound.setInteger("coreBlockPos.z",this.coreBlockPos.getZ());
        return compound;
    }
}