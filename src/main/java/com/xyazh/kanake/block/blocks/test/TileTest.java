package com.xyazh.kanake.block.blocks.test;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.TileBase;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

public class TileTest extends TileBase implements ITickable {
    public Vec3d[] points;
    public Vec3d[] m;

    public TileTest(){
        super();
        points = new Vec3d[1000];
        m = new Vec3d[this.points.length];
        for(int i=0;i<this.points.length;i++){
            points[i] = new Vec3d();
            points[i].x = 0;
            points[i].y = 0;
            points[i].z = 0;
            m[i] = new Vec3d();
            m[i].x = Kanake.rand.nextGaussian()*Kanake.rand.nextGaussian();
            m[i].y = Kanake.rand.nextGaussian()*Kanake.rand.nextGaussian();
            m[i].z = Kanake.rand.nextGaussian()*Kanake.rand.nextGaussian();
            m[i].normalize();
            m[i].mul(0.05);
        }
    }

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
    }

    @Override
    public void update() {
        this.age += 1;
        for(int i=0;i<this.points.length;i++){
            points[i].x += m[i].x;
            points[i].y += m[i].y;
            points[i].z += m[i].z;
        }
    }
}
