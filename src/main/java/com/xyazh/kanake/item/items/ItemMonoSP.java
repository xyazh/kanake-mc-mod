package com.xyazh.kanake.item.items;

import com.xyazh.kanake.Kanake;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMonoSP extends ItemBase {
    public ItemMonoSP(String name) {
        super(name);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return EnumActionResult.SUCCESS;
        }
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();
        Template template = templateManager.get(server, new ResourceLocation(Kanake.MODID, "mono_level2"));
        if (template == null) {
            Kanake.logger.warn("Structurs template not found:{}", "mono_level2");
            return EnumActionResult.SUCCESS;
        }
        int x, y, z;
        x = pos.getX();
        y = pos.getY()+1;
        z = pos.getZ();
        int chunkX = x>>4;
        int chunkZ = z>>4;
        for(int j=y;j<y+5;j++){
            for(int i=0;i<16;i++){
                for(int k=0;k<16;k++){
                    BlockPos pos1 = new BlockPos((chunkX<<4)+i,j,(chunkZ<<4)+k);
                    Kanake.logger.debug(pos1);
                    if(!(worldIn.isAirBlock(pos1)||worldIn.getBlockState(pos1).getMaterial().isReplaceable())){
                        player.sendMessage(new TextComponentString(I18n.format("message.mono_sp.fail")));
                        return EnumActionResult.FAIL;
                    }
                }
            }
        }
        Rotation rotation = Rotation.NONE;
        EnumFacing playerFacing = player.getHorizontalFacing();
        if (playerFacing.equals(EnumFacing.EAST)){
            int cx = x % 16;
            if(7 <= cx){
                x = (x>>4<<4) + 7;
            }else if(-9 <= cx && cx <= -1){
                x = ((x>>4)+1<<4) - 9;
            }
            int cz = z % 16;
            if(7 <= cz){
                z = (z>>4<<4) + 7;
            }else if(-9 <= cz && cz <= -1){
                z = ((z>>4)+1<<4) - 9;
            }
        }else if (playerFacing.equals(EnumFacing.SOUTH)) {
            rotation = Rotation.CLOCKWISE_90;
            int cx = x % 16;
            if(0 <= cx && cx <= 7){
                x = (x>>4<<4) + 8;
            }else if(cx <= -9){
                x = ((x>>4)+1<<4) - 9;
            }
            int cz = z % 16;
            if(7 <= cz){
                z = (z>>4<<4) + 7;
            }else if(-9 <= cz && cz <= -1){
                z = ((z>>4)+1<<4) - 9;
            }
        } else if (playerFacing.equals(EnumFacing.WEST)) {
            rotation = Rotation.CLOCKWISE_180;
            int cx = x % 16;
            if(0 <= cx && cx <= 7){
                x = (x>>4<<4) + 8;
            }else if(cx <= -9){
                x = ((x>>4)+1<<4) - 9;
            }
            int cz = z % 16;
            if(0 <= cz && cz <= 7){
                z = (z>>4<<4) + 8;
            }else if(cz <= -9){
                z = ((z>>4)+1<<4) - 9;
            }
        } else if (playerFacing.equals(EnumFacing.NORTH)) {
            rotation = Rotation.COUNTERCLOCKWISE_90;
            int cx = x % 16;
            if(7 <= cx){
                x = (x>>4<<4) + 7;
            }else if(-9 <= cx && cx <= -1){
                x = ((x>>4)+1<<4) - 9;
            }
            int cz = z % 16;
            if(0 <= cz && cz <= 7){
                z = (z>>4<<4) + 8;
            }else if(cz <= -9){
                z = ((z>>4)+1<<4) - 9;
            }
        }
        pos = new BlockPos(x, y, z);
        template.addBlocksToWorld(worldIn, pos, new PlacementSettings().setRotation(rotation), 2 | 4 | 16);
        if(player.isCreative()){
            return EnumActionResult.SUCCESS;
        }
        ItemStack itemStack = player.getHeldItem(hand);
        itemStack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull ItemStack itemStack, @Nullable World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.mono_sp.desc"));
    }
}
