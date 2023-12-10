package com.xyazh.kanake.block.blocks.platform.render;

import com.xyazh.kanake.block.blocks.test.TileTest;
import com.xyazh.kanake.render.GLRenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderPlatform extends TileEntitySpecialRenderer<TileTest> {
    public boolean isGlobalRenderer(@Nonnull TileTest te) {
        return true;
    }

    public void render(@Nonnull TileTest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GLRenderHelper.inCommonUseBegin1();
        GLRenderHelper.horizontalHexagonAndEdging(x,y,z,0.5,1);
        GLRenderHelper.inCommonUseEnd1();
    }
}
