package com.xyazh.kanake.render;

import com.xyazh.kanake.util.MathUtils;
import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;

public class RenderBezierTube {
    public final Vec3d p1 = new Vec3d();
    public final Vec3d p2 = new Vec3d();
    public final Vec3d p3 = new Vec3d();
    public final Vec3d p4 = new Vec3d();
    public final Vec3d color = new Vec3d(255, 0,255);
    public int a = 255;
    public static final Vec3d VN1 = new Vec3d(0,1,0);
    public double step = 0.01;
    public double width = 0.1;

    public RenderBezierTube(){

    }

    public void render(BufferBuilder bufferbuilder){
        Vec3d lastCtx1, lastCtx2, lastCtx3, lastCtx4;
        Vec3d thisCtx1, thisCtx2, thisCtx3, thisCtx4;
        lastCtx1 = new Vec3d();
        lastCtx2 = new Vec3d();
        lastCtx3 = new Vec3d();
        lastCtx4 = new Vec3d();
        thisCtx1 = new Vec3d();
        thisCtx2 = new Vec3d();
        thisCtx3 = new Vec3d();
        thisCtx4 = new Vec3d();
        Vec3d lastC = new Vec3d(p1.x,p1.y,p1.z);
        lastC.set(p1);
        Vec3d thisC = new Vec3d();
        thisC.x = MathUtils.bezierQuick(p1.x,p2.x,p3.x,p4.x,this.step);
        thisC.y = MathUtils.bezierQuick(p1.y,p2.y,p3.y,p4.y,this.step);
        thisC.z = MathUtils.bezierQuick(p1.z,p2.z,p3.z,p4.z,this.step);
        Vec3d d = new Vec3d();
        Vec3d n1,n2;
        n1 = new Vec3d();
        n2 = new Vec3d();
        d.sub(thisC,lastC);
        n1.cross(d,VN1);
        n1.normalize();
        n1.mul(width);
        n2.cross(d,n1);
        n2.normalize();
        n2.mul(width);
        lastCtx1.sub(lastC,n1);
        lastCtx1.sub(n2);
        lastCtx2.add(lastC,n1);
        lastCtx2.sub(n2);
        lastCtx3.add(lastC,n1);
        lastCtx3.add(n2);
        lastCtx4.sub(lastC,n1);
        lastCtx4.add(n2);
        thisCtx1.sub(thisC,n1);
        thisCtx1.sub(n2);
        thisCtx2.add(thisC,n1);
        thisCtx2.sub(n2);
        thisCtx3.add(thisC,n1);
        thisCtx3.add(n2);
        thisCtx4.sub(thisC,n1);
        thisCtx4.add(n2);
        double t = 0;
        while (t<=1){
            lastC.set(thisC);
            thisC.x = MathUtils.bezierQuick(p1.x,p2.x,p3.x,p4.x,t);
            thisC.y = MathUtils.bezierQuick(p1.y,p2.y,p3.y,p4.y,t);
            thisC.z = MathUtils.bezierQuick(p1.z,p2.z,p3.z,p4.z,t);
            lastCtx1.set(thisCtx1);
            lastCtx2.set(thisCtx2);
            lastCtx3.set(thisCtx3);
            lastCtx4.set(thisCtx4);
            d.sub(thisC,lastC);
            n1.cross(d,VN1);
            n1.normalize();
            n1.mul(width);
            n2.cross(d,n1);
            n2.normalize();
            n2.mul(width);
            thisCtx1.sub(thisC,n1);
            thisCtx1.sub(n2);
            thisCtx2.add(thisC,n1);
            thisCtx2.sub(n2);
            thisCtx3.add(thisC,n1);
            thisCtx3.add(n2);
            thisCtx4.sub(thisC,n1);
            thisCtx4.add(n2);
            bufferbuilder.pos(lastCtx1.x, lastCtx1.y, lastCtx1.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(lastCtx2.x, lastCtx2.y, lastCtx2.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(thisCtx2.x, thisCtx2.y, thisCtx2.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(lastCtx3.x, lastCtx3.y, lastCtx3.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(thisCtx3.x, thisCtx3.y, thisCtx3.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(lastCtx4.x, lastCtx4.y, lastCtx4.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(thisCtx4.x, thisCtx4.y, thisCtx4.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            bufferbuilder.pos(lastCtx1.x, lastCtx1.y, lastCtx1.z).color((int) color.x, (int)color.y,(int)color.z, a).endVertex();
            t += this.step;
        }
    }
}
