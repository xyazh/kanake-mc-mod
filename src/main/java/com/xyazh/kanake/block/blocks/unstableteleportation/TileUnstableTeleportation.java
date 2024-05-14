package com.xyazh.kanake.block.blocks.unstableteleportation;

import com.xyazh.kanake.block.blocks.teleportation.TileTeleportation;
import com.xyazh.kanake.util.Vec3d;
import com.xyazh.kanake.util.Vec3dFinal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TileUnstableTeleportation extends TileTeleportation {
    public Vec3d randVec3 = Vec3d.random(0.1);

    @Override
    public boolean undulating() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (this.world.isRemote) {
            this.randVec3.rand(0.1);
        } else {
            Block block = this.world.getBlockState(this.pos).getBlock();
            if (block instanceof BlockUnstableTeleportation) {
                BlockUnstableTeleportation blockUnstableTeleportation = (BlockUnstableTeleportation) block;
                Iterator<Map.Entry<Entity, Integer>> iterator = blockUnstableTeleportation.teleportingEntities.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Entity, Integer> entry = iterator.next();
                    Entity entity = entry.getKey();
                    int i = entry.getValue() - 1;
                    if (i <= 0) {
                        iterator.remove();
                    } else {
                        blockUnstableTeleportation.teleportingEntities.put(entity, i);
                    }
                }
            }
        }
    }


    @Override
    public Vec3d datumOffset(float partialTicks) {
        return Vec3dFinal.UP;
    }
}
