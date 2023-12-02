package com.xyazh.kanake.util;

public class RopePhysics {
    public final PhysicsNode3d node1;
    public final PhysicsNode3d node2;
    public final PhysicsNode3d node3;
    public final PhysicsNode3d node4;
    protected float l1;
    protected float l2;
    protected float l3;
    public float k = 100f;
    public float friction = 0.01f;

    public RopePhysics(Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4) {
        this.node1 = new PhysicsNode3d();
        this.node1.set(p1);
        this.node2 = new PhysicsNode3d();
        this.node2.set(p2);
        this.node3 = new PhysicsNode3d();
        this.node3.set(p3);
        this.node4 = new PhysicsNode3d();
        this.node4.set(p4);
        this.l1 = (float) Vec3d.delivery(p1, p2);
        this.l2 = (float) Vec3d.delivery(p2, p3);
        this.l3 = (float) Vec3d.delivery(p3, p4);
    }
    public RopePhysics() {
        this.node1 = new PhysicsNode3d();
        this.node2 = new PhysicsNode3d();
        this.node3 = new PhysicsNode3d();
        this.node4 = new PhysicsNode3d();
        this.l1 = 0;
        this.l2 = 0;
        this.l3 = 0;
    }

    public void set(Vec3d p1, Vec3d p2, Vec3d p3, Vec3d p4){
        this.node1.set(p1);
        this.node2.set(p2);
        this.node3.set(p3);
        this.node4.set(p4);
        this.l1 = (float) Vec3d.delivery(p1, p2);
        this.l2 = (float) Vec3d.delivery(p2, p3);
        this.l3 = (float) Vec3d.delivery(p3, p4);
    }

    public void setGravity(float gravity) {
        this.node1.gravity = gravity;
        this.node2.gravity = gravity;
        this.node3.gravity = gravity;
        this.node4.gravity = gravity;
    }

    public void updateTick() {
        this.setGravity(-9.81f);
        this.k = 100f;
        this.node1.updateTick();
        this.node2.updateTick();
        this.node3.updateTick();
        this.node4.updateTick();
        Vec3d dn1 = new Vec3d();
        Vec3d dn2 = new Vec3d();
        Vec3d dn3 = new Vec3d();
        dn1.sub(this.node2,this.node1);
        dn2.sub(this.node3,this.node2);
        dn3.sub(this.node4,this.node3);
        double dl1,dl2,dl3;
        dl1 = Math.max(dn1.length()-this.l1,0);
        dl2 = Math.max(dn2.length()-this.l2,0);
        dl3 = Math.max(dn3.length()-this.l3,0);
        this.node1.force.set(0,0,0);
        this.node2.force.set(0,0,0);
        this.node3.force.set(0,0,0);
        this.node4.force.set(0,0,0);
        if(dn1.length()!=0){
            dn1.normalize();
            dn1.mul(dl1*this.k);
        }
        this.node1.force.add(dn1);
        this.node2.force.sub(dn1);
        if(dn2.length()!=0){
            dn2.normalize();
            dn2.mul(dl2*this.k);
        }
        this.node2.force.add(dn2);
        this.node3.force.sub(dn2);
        if(dn3.length()!=0){
            dn3.normalize();
            dn3.mul(dl3*this.k);
        }
        this.node3.force.add(dn3);
        this.node4.force.sub(dn3);
    }

    public void updatePhysics(double dt) {
        this.node1.updatePhysics(dt);
        this.node2.updatePhysics(dt);
        this.node3.updatePhysics(dt);
        this.node4.updatePhysics(dt);
    }
}
