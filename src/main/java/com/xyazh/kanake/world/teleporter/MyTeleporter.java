package com.xyazh.kanake.world.teleporter;


import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class MyTeleporter extends Teleporter {

    public static MyTeleporter getTeleporterForDim(MinecraftServer server, int dim) {
        WorldServer ws = server.getWorld(dim);
        for (Teleporter t : ws.customTeleporters) {
            if (t instanceof MyTeleporter) {
                return (MyTeleporter) t;
            }
        }
        MyTeleporter tp = new MyTeleporter(ws);
        ws.customTeleporters.add(tp);
        return tp;
    }

    public MyTeleporter(WorldServer worldIn) {
        super(worldIn);
    }

    public int getHeight(int i,int k){
        int r = this.world.getSeaLevel() + 1;
        for(int j=255;j>=0;j--){
            if(!this.world.getBlockState(new BlockPos(i,j,k)).getBlock().equals(Blocks.AIR)){
                r = j + 1;
                break;
            }
        }
        return r;
    }

    @Override
    public void placeInPortal(@Nonnull Entity entityIn, float rotationYaw) {
        int id = this.world.provider.getDimensionType().getId();
        if (id != 1) {
            if(id == 0){
                int i = MathHelper.floor(entityIn.posX);
                int k = MathHelper.floor(entityIn.posZ);
                int j = this.getHeight(i,k);

                entityIn.setLocationAndAngles(i, j, k, entityIn.rotationYaw, 0.0F);
                entityIn.motionX = 0.0D;
                entityIn.motionY = 0.0D;
                entityIn.motionZ = 0.0D;
            }else if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
                //this.makePortal(entityIn);
                this.placeInExistingPortal(entityIn, rotationYaw);
            }
        } else {
            int i = MathHelper.floor(entityIn.posX);
            int k = MathHelper.floor(entityIn.posZ);
            int j = this.world.getHeight(i,k);

            entityIn.setLocationAndAngles(i, j, k, entityIn.rotationYaw, 0.0F);
            entityIn.motionX = 0.0D;
            entityIn.motionY = 0.0D;
            entityIn.motionZ = 0.0D;
        }
    }

    public void placeInPortal(@Nonnull Entity entityIn,BlockPos pos, float rotationYaw) {
        int id = this.world.provider.getDimensionType().getId();
        if (id != 1) {
            if(id == 0){
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                entityIn.setLocationAndAngles(i, j, k, entityIn.rotationYaw, 0.0F);
                entityIn.motionX = 0.0D;
                entityIn.motionY = 0.0D;
                entityIn.motionZ = 0.0D;
            }else if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
                this.placeInExistingPortal(entityIn, rotationYaw);
            }
        } else {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            entityIn.setLocationAndAngles(i, j, k, entityIn.rotationYaw, 0.0F);
            entityIn.motionX = 0.0D;
            entityIn.motionY = 0.0D;
            entityIn.motionZ = 0.0D;
        }
    }

    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        BlockPos pos = entityIn.getPosition();
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        int ci = i >> 4;
        int ck = k >> 4;
        i = ((ci % 2 == 0 ? ci : ci + 1) << 4) + 7;
        j = ((Math.min(Math.max(1, j), 127) >> 4) << 4) + 1;
        k = ((ck % 2 == 0 ? ck : ck + 1) << 4) + 7;
        entityIn.setLocationAndAngles(i, j, k, entityIn.rotationYaw, 0.0F);
        entityIn.motionX = 0.0D;
        entityIn.motionY = 0.0D;
        entityIn.motionZ = 0.0D;
        return true;
    }
}
