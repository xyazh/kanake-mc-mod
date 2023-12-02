package com.xyazh.kanake.util;

public class PhysicsNode3d extends Vec3d{
    public float mass = 1f;
    public final Vec3d force = new Vec3d(0,0,0);
    protected final Vec3d acceleration = new Vec3d(0,0,0);
    public final Vec3d velocity = new Vec3d(0,0,0);
    public float gravity = -0.00981f;
    public float damping = 0.5f;
    public boolean isNoPhysics = false;

    public void updateTick(){

    }

    public void updatePhysics(double dt){
        if(isNoPhysics){
            return;
        }
        damping = 0.90f;
        acceleration.x = force.x/ mass;
        acceleration.y = force.y/ mass;
        acceleration.z = force.z/ mass;
        velocity.x += acceleration.x * dt;
        velocity.y += (gravity + acceleration.y) * dt;
        velocity.z += acceleration.z * dt;
        x += velocity.x * dt;
        y += velocity.y * dt;
        z += velocity.z * dt;

        force.x *= damping;
        force.y *= damping;
        force.z *= damping;

        acceleration.x = force.x / mass;
        acceleration.y = (gravity + force.y) / mass;
        acceleration.z = force.z / mass;

        velocity.x += acceleration.x * dt;
        velocity.y += acceleration.y * dt;
        velocity.z += acceleration.z * dt;

        velocity.x *= damping;
        velocity.y *= damping;
        velocity.z *= damping;

        x += velocity.x * dt*damping;
        y += velocity.y * dt*damping;
        z += velocity.z * dt*damping;
    }
}
