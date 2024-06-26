package com.xyazh.kanake.block.blocks.teleportation;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.TileBase;
import com.xyazh.kanake.util.MathUtils;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.util.ITickable;

public class TileTeleportation extends TileBase implements ITickable {
    public int age = 0;
    public double[] pointsY = {0,0,0,0,0,0,0,0,0};
    public double[] lastPointY = {0,0,0,0,0,0,0,0,0};
    public double[] keyPoints1 = {0,0,0,0,0,0,0,0,0};
    public double[] keyPoints2 = {0,0,0,0,0,0,0,0,0};
    public int[] schedules = {0,0,0,0,0,0,0,0,0};
    public int[] maxSchedules = {0,0,0,0,0,0,0,0,0};
    public static double rand(){
        double r = Kanake.rand.nextDouble()/2+0.5;
        r *= Kanake.rand.nextInt(100)%2==0? -1 : 1;
        return r;
    }
    public static double randS(){
        return Math.abs(rand());
    }

    public static double randM(){
        return -randS();
    }

    public TileTeleportation() {
        int pointsCount = this.pointsY.length;
        for (int i = 0; i < pointsCount; i++) {
            this.keyPoints1[i] = randS();
            this.keyPoints2[i] = randM();
            this.maxSchedules[i] = (int) (randS() * 200);
        }
    }

    public boolean undulating(){
        return true;
    }

    @Override
    public void update() {
        if(this.world.isRemote){
            if(this.undulating()){
                int pointsCount = this.pointsY.length;
                for (int i = 0; i < pointsCount; i++) {
                    if(this.schedules[i] >= this.maxSchedules[i]){
                        this.schedules[i] = 0;
                        this.keyPoints1[i] = randS();
                        this.keyPoints2[i] = randM();
                        this.maxSchedules[i] = (int) (randS() * 200);
                    }
                    double t = (double) this.schedules[i] / (double) this.maxSchedules[i];
                    this.lastPointY[i] = this.pointsY[i];
                    this.pointsY[i] = MathUtils.bezier(0,this.keyPoints1[i],this.keyPoints2[i],0,t)/5;
                    this.schedules[i]++;
                }
            }
            this.age++;
        }
    }

    public Vec3d datumOffset(float partialTicks){
        return Vec3d.ZERO;
    }
}
