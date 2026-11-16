package com.xyazh.kanake.entity.render;

import com.xyazh.kanake.Kanake;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * ModelTreeMan - xya
 * Created using Tabula 7.1.0
 */
public class ModelTreeMan extends ModelBase {
    public final LinkedList<ModelRenderer> modelRenderers = new LinkedList<>();

    public ModelRenderer newBox(int texOffX,int texOffY,float x,float y,float z){
        ModelRenderer box = new ModelRenderer(this, texOffX, texOffY);
        box.setRotationPoint(-8-16*x, -8-16*y, -8-16*z);
        box.addBox(0.0F, 0.0F, 0.0F, 16, 16, 16, 0.0F);
        return box;
    }

    protected void newLogBox(float x,float y,float z){
        this.modelRenderers.add(this.newBox(0,0,x,y,z));
    }

    protected void newLeavesBox(float x,float y,float z){
        this.modelRenderers.add(this.newBox(0,32,x,y,z));
    }

    public ModelTreeMan() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.generate();
    }

    @Override
    public void render(@Nonnull Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        for(ModelRenderer modelRenderer:this.modelRenderers){
            modelRenderer.render(f5);
        }
    }

    public void generate()
    {
        BlockPos position = new BlockPos(0,-1,0);
        Random rand = Kanake.rand;
        int i = rand.nextInt(3) + 5;
        for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2)
        {
            int k2 = i2 - (position.getY() + i);
            int l2 = 1 - k2 / 2;
            for (int i3 = position.getX() - l2; i3 <= position.getX() + l2; ++i3)
            {
                int j1 = i3 - position.getX();

                for (int k1 = position.getZ() - l2; k1 <= position.getZ() + l2; ++k1)
                {
                    int l1 = k1 - position.getZ();

                    if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || rand.nextInt(2) != 0 && k2 != 0)
                    {
                        this.newLeavesBox(i3, i2, k1);
                    }
                }
            }
        }
        for (int j2 = 0; j2 < i; ++j2)
        {
            BlockPos upN = position.up(j2);
            this.newLogBox(upN.getX(), upN.getY(), upN.getZ());
        }
    }
}
