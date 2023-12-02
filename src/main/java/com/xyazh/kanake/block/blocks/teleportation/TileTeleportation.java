package com.xyazh.kanake.block.blocks.teleportation;

import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.block.blocks.TileBase;

public class TileTeleportation extends TileBase {
    public float lastPT = 0;
    public double[] y = {
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
            TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,
            TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,
            TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200
    };

    public double[] mt = {
            TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,
            TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,
            TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200,TileTeleportation.randVS() * 200
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
}
