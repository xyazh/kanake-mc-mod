package com.xyazh.kanake.entity;

import com.xyazh.kanake.Magic;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.LinkedList;

public class EntityEmptyMagic extends EntityShoot{{
}
    public LinkedList<Integer> order = new LinkedList<>();

    public EntityEmptyMagic(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected boolean customMotion() {
        return true;
    }

    public void setOrder(LinkedList<Integer> order){
        this.order = new LinkedList<>(order);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.execute();
    }

    public void execute(){
        if(this.order.size()<=0){
            return;
        }
        switch (this.order.poll()){
            case Magic.SPAWN:
                EntityEmptyMagic entity = this.copy();
                this.world.spawnEntity(entity);
                break;
        }
    }

    public EntityEmptyMagic copy(){
        EntityEmptyMagic entity = new EntityEmptyMagic(this.world);
        entity.order.addAll(this.order);
        entity.setPosition(this.posX,this.posY,this.posZ);
        return entity;
    }
}
