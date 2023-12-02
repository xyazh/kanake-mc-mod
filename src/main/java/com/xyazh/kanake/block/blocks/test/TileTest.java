package com.xyazh.kanake.block.blocks.test;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.TileBase;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

public class TileTest extends TileBase implements ITickable {
    public float lastPT = 0;
    public double[] args = {
            0,0,0,
            0,0,0,
            0,0,0
    };

    public double[] my = {
            randVM(),randVM(),randVM(),
            randVM(),randVM(),randVM(),
            randVM(),randVM(),randVM()
    };

    public double[] sy = {
            randVS(),randVS(),randVS(),
            randVS(),randVS(),randVS(),
            randVS(),randVS(),randVS()
    };

    public double[] t = {
            TileTest.randVS() * 200, TileTest.randVS() * 200, TileTest.randVS() * 200,
            TileTest.randVS() * 200, TileTest.randVS() * 200, TileTest.randVS() * 200,
            TileTest.randVS() * 200, TileTest.randVS() * 200, TileTest.randVS() * 200
    };

    public double[] mt = {
            TileTest.randVS() * 200, TileTest.randVS() * 200, TileTest.randVS() * 200,
            TileTest.randVS() * 200, TileTest.randVS() * 200, TileTest.randVS() * 200,
            TileTest.randVS() * 200, TileTest.randVS() * 200, TileTest.randVS() * 200
    };

    public double age = 0;

    public static double randV(){
        double r = Kanake.rand.nextDouble()/2+0.5;
        r *= Kanake.rand.nextInt(100)%2==0? -1 : 1;
        return r;
    }

    public static double randVS(){
        return Math.abs(randV());
    }

    public static double randVM(){
        return -randVS();
    }

    @Nonnull
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
        //return new AxisAlignedBB(Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MIN_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
    }

    @Override
    public void update() {
        this.age += 1;
    }
}
