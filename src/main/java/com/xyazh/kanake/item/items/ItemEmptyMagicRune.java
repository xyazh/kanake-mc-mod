package com.xyazh.kanake.item.items;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.entity.EntityEmptyMagic;
import com.xyazh.kanake.gui.test.GuiHandlerTest;
import com.xyazh.kanake.gui.test.RuneItemStackHandler;
import com.xyazh.kanake.item.ModItems;
import com.xyazh.kanake.magic.Magic;
import com.xyazh.kanake.magic.glyph.GlyphData;
import com.xyazh.kanake.magic.glyph.GlyphOrder;
import com.xyazh.kanake.magic.glyph.OrderSet;
import com.xyazh.kanake.magic.mana.ManaSourceRef;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ItemEmptyMagicRune extends ItemBase {
    private static final String TAG_PRESET = "Preset";

    public ItemEmptyMagicRune(String name) {
        super(name);
        this.setMaxStackSize(1);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        items.add(new ItemStack(this));

        // 简单：向前发射一枚基础法术弹，母体发射后终结
        addPreset(items, "missile", new ItemStack[]{
                glyph(Magic.LAUNCH_CAST.order),
                glyph(Magic.NO_INHERIT.order),
                glyph(Magic.FINAL.order)
        }, new ItemStack[]{});

        // 进阶：法术弹飞行后爆炸
        addPreset(items, "blast_bolt", new ItemStack[]{
                glyph(Magic.LAUNCH_CAST.order, GlyphData.ENHANCE, 1),
                glyph(Magic.NO_INHERIT.order),
                glyph(Magic.FINAL.order),
                glyph(Magic.EXPLODE_MAGIC.order, GlyphData.ENHANCE, 1)
        }, new ItemStack[]{});

        // 进阶：原地留下死亡十字
        addPreset(items, "death_cross", new ItemStack[]{
                glyph(Magic.PLACE_CAST.order),
                glyph(Magic.NO_INHERIT.order),
                glyph(Magic.FINAL.order),
                glyph(Magic.DEATH_CROSS.order, GlyphData.ENHANCE, 1)
        }, new ItemStack[]{});

        // 复杂：两枚子法术围绕母体最后位置环绕并展开死亡十字
        addPreset(items, "orbit_cross", new ItemStack[]{
                glyph(Magic.MULTI_CAST.order, GlyphData.COUNT, 2),
                glyph(Magic.LAUNCH_CAST.order, GlyphData.ENHANCE, 1),
                glyph(Magic.NO_INHERIT.order),
                glyph(Magic.FINAL.order),
                glyph(Magic.ORBIT.order, GlyphData.SPEED, 2, GlyphData.RADIUS, 1, GlyphData.MODE, 0),
                glyph(Magic.DEATH_CROSS.order, GlyphData.ENHANCE, 1)
        }, new ItemStack[]{});

        // 复杂：三枚环绕子法术，死亡十字 + 延迟多重爆炸，母体终结时爆炸并点火
        addPreset(items, "star_finale", new ItemStack[]{
                glyph(Magic.MULTI_CAST.order, GlyphData.COUNT, 3),
                glyph(Magic.LAUNCH_CAST.order, GlyphData.ENHANCE, 2),
                glyph(Magic.NO_INHERIT.order),
                glyph(Magic.FINAL.order),
                glyph(Magic.ORBIT.order, GlyphData.SPEED, 3, GlyphData.RADIUS, 2, GlyphData.MODE, 2),
                glyph(Magic.DEATH_CROSS.order, GlyphData.ENHANCE, 2),
                glyph(Magic.DELAY_CAST.order, GlyphData.COUNT, 1, GlyphData.DELAY, 2),
                glyph(Magic.MULTI_EXPLODE.order, GlyphData.ENHANCE, 2)
        }, new ItemStack[]{
                glyph(Magic.EXPLODE_MAGIC.order, GlyphData.ENHANCE, 2),
                glyph(Magic.IGNITE.order, GlyphData.ENHANCE, 2)
        });
    }

    private void addPreset(NonNullList<ItemStack> items, String preset, ItemStack[] normal, ItemStack[] finish) {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(TAG_PRESET, preset);
        stack.setTagCompound(tag);

        RuneItemStackHandler normalHandler = new RuneItemStackHandler(stack, "rune_order", 54);
        RuneItemStackHandler finishHandler = new RuneItemStackHandler(stack, "rune_finish", 18);
        for (int i = 0; i < normal.length && i < normalHandler.getSlots(); i++) {
            if (!normal[i].isEmpty()) {
                normalHandler.setStackInSlot(i, normal[i]);
            }
        }
        for (int i = 0; i < finish.length && i < finishHandler.getSlots(); i++) {
            if (!finish[i].isEmpty()) {
                finishHandler.setStackInSlot(i, finish[i]);
            }
        }
        items.add(stack);
    }

    private static ItemStack glyph(int commandId) {
        ItemEmblem emblem = ModItems.getEmblem(commandId);
        return emblem == null ? ItemStack.EMPTY : new ItemStack(emblem);
    }

    private static ItemStack glyph(int commandId, String key, int value) {
        ItemStack stack = glyph(commandId);
        if (!stack.isEmpty()) {
            GlyphData.setInt(stack, key, value);
        }
        return stack;
    }

    private static ItemStack glyph(int commandId, String key1, int value1, String key2, int value2) {
        ItemStack stack = glyph(commandId, key1, value1);
        if (!stack.isEmpty()) {
            GlyphData.setInt(stack, key2, value2);
        }
        return stack;
    }

    private static ItemStack glyph(int commandId, String key1, int value1, String key2, int value2, String key3, int value3) {
        ItemStack stack = glyph(commandId, key1, value1, key2, value2);
        if (!stack.isEmpty()) {
            GlyphData.setInt(stack, key3, value3);
        }
        return stack;
    }

    private static String getPreset(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return "";
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(TAG_PRESET) ? tag.getString(TAG_PRESET) : "";
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        String preset = getPreset(stack);
        if (!preset.isEmpty()) {
            return "item.rune_empty_magic.preset." + preset;
        }
        return super.getUnlocalizedName(stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        String preset = getPreset(stack);
        if (!preset.isEmpty()) {
            tooltip.add(I18n.format("item.rune_empty_magic.preset." + preset + ".desc"));
        }
        super.addInformation(stack, world, tooltip, flag);
    }

    public OrderSet getOrders(@Nonnull ItemStack itemStack) {
        LinkedList<GlyphOrder> normal = new LinkedList<>();
        LinkedList<GlyphOrder> finish = new LinkedList<>();
        if (itemStack.isEmpty()) {
            return new OrderSet(normal, finish);
        }
        readOrders(itemStack, "rune_order", 54, 0, normal);
        readOrders(itemStack, "rune_finish", 18, 1000, finish);
        return new OrderSet(normal, finish);
    }

    private void readOrders(ItemStack itemStack, String key, int size, int uidOffset, LinkedList<GlyphOrder> target) {
        RuneItemStackHandler handler = new RuneItemStackHandler(itemStack, key, size);
        for (int i = 0; i < handler.getSlots(); i++) {
            Item item = handler.getStackInSlot(i).getItem();
            if (!(item instanceof ItemEmblem)) {
                continue;
            }
            ItemEmblem emblem = (ItemEmblem) item;
            target.add(new GlyphOrder(emblem.commandId, uidOffset + i, GlyphData.get(handler.getStackInSlot(i))));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if(player.isSneaking()){
            player.openGui(Kanake.instance, GuiHandlerTest.GUI_ID, world, 0, 0, 0);
        }else if(!world.isRemote){
            Vec3d m = Vec3d.fromPitchYaw(player.rotationPitch, player.rotationYaw);
            EntityEmptyMagic entity = new EntityEmptyMagic(world);
            entity.entityShoot(player,m);
            entity.rotationYaw = player.rotationYaw;
            entity.rotationPitch = player.rotationPitch;
            entity.prevRotationYaw = player.prevRotationYaw;
            entity.prevRotationPitch = player.prevRotationPitch;
            entity.g = 0;
            entity.gdy = 0;
            entity.cooldown = 5;
            entity.prevPosX = entity.posX;
            entity.prevPosY = entity.posY;
            entity.prevPosZ = entity.posZ;
            entity.setManaSource(ManaSourceRef.player(player));
            OrderSet orders = this.getOrders(player.getHeldItem(hand));
            entity.setOrders(orders.normal, orders.finish);
            world.spawnEntity(entity);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
        return super.hitEntity(stack, target, attacker);
    }
}
