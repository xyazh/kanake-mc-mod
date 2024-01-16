package com.xyazh.kanake.block.blocks.manatable;

import com.xyazh.kanake.block.ModBlocks;
import com.xyazh.kanake.init.LoopThread;
import com.xyazh.kanake.util.OreDictUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class TileTableCheckStructMono extends TileTableMono{
    public boolean isStructuralIntegrityS = false;
    public boolean isStructuralIntegrityB = false;
    protected BlockPos blockPosAA,blockPosAS,blockPosSA,blockPosSS;
    protected BlockPos blockPosW1,blockPosW2,blockPosW3;
    protected BlockPos blockPosA1,blockPosA2,blockPosA3;
    protected BlockPos blockPosS1,blockPosS2,blockPosS3;
    protected BlockPos blockPosD1,blockPosD2,blockPosD3;
    protected boolean isInited = false;
    public int checkTime = 0;
    public int maxCheckTime = 40;
    public boolean shouldSetSubBlock = false;
    public boolean isOutTheChunk = false;



    protected void init(){
        if(isInited){
            return;
        }
        int x,y,z;
        x = this.pos.getX();
        y = this.pos.getY()-2;
        z = this.pos.getZ();
        blockPosW1 = new BlockPos(x+3,y,z+3);
        blockPosW2 = new BlockPos(x+3,y,z+1);
        blockPosW3 = new BlockPos(x+3,y,z-1);
        blockPosA1 = new BlockPos(x+3,y,z-3);
        blockPosA2 = new BlockPos(x+1,y,z-3);
        blockPosA3 = new BlockPos(x-1,y,z-3);
        blockPosS1 = new BlockPos(x-3,y,z-3);
        blockPosS2 = new BlockPos(x-3,y,z-1);
        blockPosS3 = new BlockPos(x-3,y,z+1);
        blockPosD1 = new BlockPos(x-3,y,z+3);
        blockPosD2 = new BlockPos(x-1,y,z+3);
        blockPosD3 = new BlockPos(x+1,y,z+3);
        x = this.pos.getX();
        y = this.pos.getY()-1;
        z = this.pos.getZ();
        blockPosAA = new BlockPos(x+2,y,z+2);
        blockPosAS = new BlockPos(x+2,y,z-2);
        blockPosSA = new BlockPos(x-2,y,z+2);
        blockPosSS = new BlockPos(x-2,y,z-2);
        isInited = true;
    }

    public int getLevel(){
        int x,y,z;
        x = this.pos.getX();
        y = this.pos.getY();
        z = this.pos.getZ();
        Block b1 = world.getBlockState(new BlockPos(x,y-1,z)).getBlock();
        int level = 0;
        if(!OreDictUtil.hasOreDict(b1,"blockQuartz")){
            return level;
        }
        Block b2 = world.getBlockState(new BlockPos(x,y-2,z)).getBlock();
        if(!OreDictUtil.hasOreDict(b2,"blockQuartz")){
            return level;
        }
        level = 1;
        Block b3 = world.getBlockState(new BlockPos(x,y-3,z)).getBlock();
        if(!OreDictUtil.hasOreDict(b3,"blockQuartz")){
            return level;
        }
        level = 2;
        return level;
    }

    private boolean checkStructB(){
        int x,y,z;
        x = this.pos.getX();
        y = this.pos.getY()-2;
        z = this.pos.getZ();
        BlockPos[] blockPos = {
                blockPosW1,blockPosW2,blockPosW3,blockPosA1,
                blockPosD3,                      blockPosA2,
                blockPosD2,                      blockPosA3,
                blockPosD1,blockPosS3,blockPosS2,blockPosS1
        };
        for(BlockPos p:blockPos){
            if(!(this.pos.getX() >> 4 == p.getX() >> 4) && (this.pos.getZ() >> 4 == p.getZ() >> 4)){
                this.isOutTheChunk = true;
                return false;
            }
            Block b = world.getBlockState(p).getBlock();
            if(!OreDictUtil.hasOreDict(b,"blockQuartz")){
                return false;
            }
        }
        for(int i=-1;i<=1;i++){
            for(int k=-1;k<=1;k++){
                if(Math.abs(i)==Math.abs(k)){
                    continue;
                }
                Block b = world.getBlockState(new BlockPos(x+i,y,z+k)).getBlock();
                if(!OreDictUtil.hasOreDict(b,"blockQuartz")){
                    return false;
                }
            }
        }
        y -= 1;
        for(int i=-4;i<=4;i++){
            for(int k=-4;k<=4;k++){
                Block b = world.getBlockState(new BlockPos(x+i,y,z+k)).getBlock();
                if(i==-4||k==-4||i==4||k==4){
                    if(!Blocks.QUARTZ_STAIRS.equals(b)){
                        return false;
                    }
                }else {
                    if(!OreDictUtil.hasOreDict(b,"blockQuartz")){
                        return false;
                    }
                }
            }
        }
        this.isStructuralIntegrityB = true;
        return true;
    }

    private boolean checkStructS(){
        int x,y,z;
        x = this.pos.getX();
        y = this.pos.getY()-1;
        z = this.pos.getZ();
        BlockPos[] blockPos = {blockPosAA,blockPosAS,blockPosSA,blockPosSS};
        for(BlockPos p:blockPos){
            if(!(this.pos.getX() >> 4 == p.getX() >> 4) && (this.pos.getZ() >> 4 == p.getZ() >> 4)){
                this.isOutTheChunk = true;
                return false;
            }
            Block b = world.getBlockState(p).getBlock();
            if(!OreDictUtil.hasOreDict(b,"blockQuartz")){
                return false;
            }
        }
        y -= 1;
        for(int i=-3;i<=3;i++){
            for(int k=-3;k<=3;k++){
                Block b = world.getBlockState(new BlockPos(x+i,y,z+k)).getBlock();
                if(i==-3||k==-3||i==3||k==3){
                    if(!Blocks.QUARTZ_STAIRS.equals(b)){
                        return false;
                    }
                }else {
                    if(!OreDictUtil.hasOreDict(b,"blockQuartz")){
                        return false;
                    }
                }
            }
        }
        this.isStructuralIntegrityS = true;
        return true;
    }

    public boolean checkStruct(){
        this.init();
        this.isStructuralIntegrityB = false;
        this.isStructuralIntegrityS = false;
        switch (this.getLevel()){
            case 1:
                return this.checkStructS();
            case 2:
                if(this.checkStructB()){
                    return true;
                }
                return this.checkStructS();
        }
        return false;
    }

    public void setSubTile(){
        this.init();
        if(this.isStructuralIntegrityB){
            BlockPos[] blockPos = {
                    blockPosW1,blockPosW2,blockPosW3,blockPosA1,
                    blockPosD3,                      blockPosA2,
                    blockPosD2,                      blockPosA3,
                    blockPosD1,blockPosS3,blockPosS2,blockPosS1
            };
            for(BlockPos p:blockPos){
                this.initSubTile(p);
            }
        }else if(this.isStructuralIntegrityS){
            BlockPos[] blockPos = {blockPosAA,blockPosAS,blockPosSA,blockPosSS};
            for(BlockPos p:blockPos){
                this.initSubTile(p);
            }
        }
    }

    protected void initSubTile(BlockPos pos){
        TileEntity te = this.world.getTileEntity(pos);
        TileTableMono tileTableMono;
        if(te instanceof TileTableMono){
            tileTableMono = (TileTableMono) te;
        }else {
            this.world.setBlockState(pos, ModBlocks.T_MONO.getDefaultState());
            te = this.world.getTileEntity(pos);
            if(te instanceof TileTableMono){
                tileTableMono = (TileTableMono) te;
            }else {
                tileTableMono = new TileTableMono();
                this.world.setTileEntity(pos,tileTableMono);
            }
        }
        tileTableMono.coreBlockPos = this.pos;
    }

    public LinkedList<TileTableMono> getSubTile(){
        LinkedList<TileTableMono> tileTableMonos = new LinkedList<>();
        if(this.isInited){
            if(this.isStructuralIntegrityB){
                BlockPos[] blockPos = {
                        blockPosW1,blockPosW2,blockPosW3,blockPosA1,
                        blockPosD3,                      blockPosA2,
                        blockPosD2,                      blockPosA3,
                        blockPosD1,blockPosS3,blockPosS2,blockPosS1
                };
                for(BlockPos p:blockPos){
                    TileEntity te = world.getTileEntity(p);
                    if(te instanceof TileTableMono){
                        tileTableMonos.add((TileTableMono) te);
                    }
                }
            }else if(this.isStructuralIntegrityS){
                BlockPos[] blockPos = {blockPosAA,blockPosAS,blockPosSA,blockPosSS};
                for(BlockPos p:blockPos){
                    TileEntity te = world.getTileEntity(p);
                    if(te instanceof TileTableMono){
                        tileTableMonos.add((TileTableMono) te);
                    }
                }
            }
        }
        return tileTableMonos;
    }

    public void checkStructQueue(){
        this.shouldSetSubBlock = this.checkStruct();
    }

    public boolean finishStruct(){
        return this.isStructuralIntegrityS||this.isStructuralIntegrityB;
    }

    @Override
    public void update() {
        super.update();
        if(world.isRemote){
            return;
        }
        if(this.checkTime--<0){
            LoopThread.blockingQueue.add(this::checkStructQueue);
            this.checkTime = this.maxCheckTime;
        }
        if(this.shouldSetSubBlock){
            this.setSubTile();
            this.shouldSetSubBlock = false;
        }
    }

    public void readFromNBT(@Nonnull NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.isStructuralIntegrityS = compound.getBoolean("isStructuralIntegrityS");
        this.isStructuralIntegrityB = compound.getBoolean("isStructuralIntegrityB");
    }

    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setBoolean("isStructuralIntegrityS", this.isStructuralIntegrityS);
        compound.setBoolean("isStructuralIntegrityB", this.isStructuralIntegrityB);
        return compound;
    }
}
