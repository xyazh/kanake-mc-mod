package com.xyazh.kanake.entity;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;


public class EntityDestroy extends EntityShoot {
    public double radius;
    public boolean hasNoGravity()
    {
        return true;
    }

    public EntityDestroy(World worldIn) {
        super(worldIn);
        this.radius = 0.5;
        float length = 2 * (float)this.radius;
        this.setSize(length, length);
        this.noClip = true;
        this.speed = 0.1f;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.world.isRemote){
            for(int i=0;i<3;i++){
                Vec3d vec3d = Vec3d.random();
                this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX+vec3d.x, this.posY+vec3d.y-1,this.posZ+vec3d.z, 0, 0, 0);
            }
        }
        this.world.destroyBlock(this.getPosition(), false);
    }

    @Override
    protected void entityInit() {

    }
}
