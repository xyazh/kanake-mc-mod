package com.xyazh.kanake.block.blocks.unstableteleportation;

import com.xyazh.kanake.block.blocks.teleportation.TileTeleportation;
import com.xyazh.kanake.util.Vec3d;
import com.xyazh.kanake.util.Vec3dFinal;

public class TileUnstableTeleportation extends TileTeleportation {
    public Vec3d randVec3 = Vec3d.random(0.1);

    @Override
    public boolean undulating() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if(this.world.isRemote){
            this.randVec3.rand(0.1);
        }
    }

    @Override
    public Vec3d datumOffset(float partialTicks) {
        return Vec3dFinal.UP;
    }
}
