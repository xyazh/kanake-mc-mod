package com.xyazh.kanake.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ReportedException;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityBlue {
    private boolean inited = false;

    private double partialTicks;
    private Entity viewEntity;
    Entity entity;
    private Minecraft mc;
    private double d0;
    private double d1;
    private double d2;
    private double d3;
    private double d4;
    private double d5;
    private double f;
    public double x;
    public double y;
    public double z;

    private RenderManager renderManager;
    private RenderLivingBase render;

    private EntityZombie zombie = null;

    public void init(RenderLivingEvent.Pre<EntityLivingBase> event){
        if(inited){
            return;
        }
        mc = Minecraft.getMinecraft();
        inited = true;
    }

    private void update(RenderLivingEvent.Pre<EntityLivingBase> event){
        viewEntity = mc.getRenderViewEntity();
        entity = event.getEntity();
        partialTicks = event.getPartialRenderTick();
        render = event.getRenderer();
        renderManager = render.getRenderManager();

        d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        d3 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        d4 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        d5 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        x = d0 - d3;
        y = d1 - d4;
        z = d2 - d5;
        int i = entity.getBrightnessForRender();
        if (entity.isBurning())
        {
            i = 15728880;
        }
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
    }

    public void render(RenderLivingEvent.Pre<EntityLivingBase> event){
        /*if (event.getEntity() instanceof EntityZombie){
            return;
        }*/
        this.update(event);
        if(partialTicks<0){
            return;
        }
        event.setCanceled(true);
        if(zombie == null){
            zombie = new EntityZombie(event.getEntity().world);
        }
        zombie.setPosition(entity.posX,entity.posY,entity.posZ);

        //renderManager.renderEntity(zombie,x,args,z,(float) force,(float) partialTicks,true);
        this.renderEntity(entity,x,y,z,(float) f,(float) partialTicks,true);
    }

    public void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_)
    {
        Render<Entity> render = null;
        try
        {
            render = renderManager.<Entity>getEntityRenderObject(entityIn);
            if (render != null && renderManager.renderEngine != null)
            {
                try
                {
                    render.doRender(entityIn, x, y, z, yaw, -1);

                }
                catch (Throwable throwable1)
                {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Rendering entity in world"));
                }
            }
        }
        catch (Throwable throwable3)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
            entityIn.addEntityCrashInfo(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
            crashreportcategory1.addCrashSection("Assigned renderer", render);
            crashreportcategory1.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashreportcategory1.addCrashSection("Rotation", Float.valueOf(yaw));
            crashreportcategory1.addCrashSection("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }
}
