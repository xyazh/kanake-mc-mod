package com.xyazh.kanake.render;

import com.xyazh.kanake.util.Vec3d;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GLRenderHelper {
    private static final Vec3d[] vec3ds= new Vec3d[]{
            new Vec3d(0,0,1),new Vec3d(0.866025,0,0.5), new Vec3d(-0.866025,0,0.5),
            new Vec3d(0.866025,0,-0.5),new Vec3d(-0.866025,0,-0.5),new Vec3d(0,0,-1)
        };
    private static final int[] index = {0,1,3,5,4,2};

    public static void inCommonUseBegin1(){
        GlStateManager.disableTexture2D();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void inCommonUseEnd1(){
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
    }

    public static void horizontalHexagonAndEdging(double x,double y,double z,double size,long color){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        x += 0.5;
        y += 1.0;
        z += 0.5;
        for (Vec3d vec3d : vec3ds) {
            bufferbuilder.pos(x + size * vec3d.x, y, z + size * vec3d.z).color(32, 164, 164, 164).endVertex();
        }
        tessellator.draw();
        BufferBuilder bufferbuilder1 = tessellator.getBuffer();
        GL11.glLineWidth(2.0f);
        bufferbuilder1.begin(2, DefaultVertexFormats.POSITION_COLOR);
        for (int i : index) {
            Vec3d vec3d = vec3ds[i];
            bufferbuilder.pos(x + size * vec3d.x, y, z + size * vec3d.z).color(64, 255, 255, 255).endVertex();
        }
        tessellator.draw();
    }

    static public void vertexQuads(double x1, double y1, double z1, double x2, double y2, double z2) {
        //右上角点
        //glTexCoord2f(1.0, 1.0)
        GL11.glVertex3d(x2, y2, z2);
        //左上角点
        //glTexCoord2f(0.0, 1.0)
        GL11.glVertex3d(x1, y2, z1);
        //右下角点
        //glTexCoord2f(1.0, 0.0)
        GL11.glVertex3d(x2, y1, z2);
        //左下角点
        //glTexCoord2f(0.0, 0.0)
        GL11.glVertex3d(x1, y1, z1);
    }

    public static void renderBox(double x1, double y1, double z1, double x2, double y2, double z2) {

        // front face
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y1, z1);
        GL11.glVertex3d(x1, y2, z1);
        GL11.glVertex3d(x2, y2, z1);

        // right face
        GL11.glVertex3d(x2, y1, z1);
        GL11.glVertex3d(x2, y1, z2);
        GL11.glVertex3d(x2, y2, z1);
        GL11.glVertex3d(x2, y2, z2);

        // back face
        GL11.glVertex3d(x2, y1, z2);
        GL11.glVertex3d(x1, y1, z2);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x1, y2, z2);

        // left face
        GL11.glVertex3d(x1, y1, z2);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y2, z2);
        GL11.glVertex3d(x1, y2, z1);
    }

    public static void renderStar() {
        GL11.glBegin(GL11.GL_TRIANGLES);

        // 定义四个角的坐标
        double x1 = 0.0;
        double y1 = 0.5;
        double z1 = 0.0;

        double x2 = -0.5;
        double y2 = 0.0;
        double z2 = 0.0;

        double x3 = 0.0;
        double y3 = -0.5;
        double z3 = 0.0;

        double x4 = 0.5;
        double y4 = 0.0;
        double z4 = 0.0;

        // 绘制四角星
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x4, y4, z4);

        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x3, y3, z3);
        GL11.glVertex3d(x4, y4, z4);

        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x3, y3, z3);

        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x4, y4, z4);
        GL11.glVertex3d(x3, y3, z3);

        GL11.glEnd();
    }


    static public void drawSphere(double xOffset, double yOffset, double zOffset, float radius, int slices, int stacks) {
        float rho, theta;
        float drho = (float) Math.PI / stacks;
        float dtheta = 2.0f * (float) Math.PI / slices;
        float s, t, x, y, z;
        GL11.glPushMatrix();
        GL11.glTranslated(xOffset, yOffset, zOffset);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            for (int j = 0; j <= slices; j++) {
                theta = j * dtheta;
                x = (float) (Math.sin(rho) * Math.cos(theta));
                y = (float) (Math.sin(rho) * Math.sin(theta));
                z = (float) Math.cos(rho);
                s = (float) j / (float) slices;
                t = (float) i / (float) stacks;
                GL11.glTexCoord2f(s, t);
                GL11.glVertex3f(radius * x, radius * y, radius * z);
                x = (float) (Math.sin(rho + drho) * Math.cos(theta));
                y = (float) (Math.sin(rho + drho) * Math.sin(theta));
                z = (float) Math.cos(rho + drho);
                s = (float) j / (float) slices;
                t = (float) (i + 1) / (float) stacks;
                GL11.glTexCoord2f(s, t);
                GL11.glVertex3f(radius * x, radius * y, radius * z);
            }
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
