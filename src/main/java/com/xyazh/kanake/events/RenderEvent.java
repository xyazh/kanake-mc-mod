package com.xyazh.kanake.events;


import com.xyazh.kanake.Kanake;
import com.xyazh.kanake.libs.weaponlib.shader.jim.Shader;
import com.xyazh.kanake.libs.weaponlib.shader.jim.ShaderManager;
import com.xyazh.kanake.render.FramebufferExample;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = Kanake.MODID)
@SideOnly(Side.CLIENT)
public class RenderEvent {
    public static FramebufferExample SCREEN_FBO = null;
    public static FramebufferExample EFFECT_FBO = null;
    public static FramebufferExample LOW_EFFECT_FBO = null;
    public static Shader EFFECT_SHADER = null;
    public static Shader EFFECT_SHADER_1 = null;
    public static final ArrayList<Runnable> POST_RENDER_TASKS = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderInit(TextureStitchEvent.Post event) {
        SCREEN_FBO = new FramebufferExample(true);
        EFFECT_FBO = new FramebufferExample(true);
        LOW_EFFECT_FBO = new FramebufferExample(true, 0.5f);
        EFFECT_SHADER = ShaderManager.loadVMWShader("effect");
        EFFECT_SHADER_1 = ShaderManager.loadVMWShader("effect1");
    }

    public static void addPostRenderTask(Runnable task){
        POST_RENDER_TASKS.add(task);
    }

    public static void foreachPostRenderTask(boolean clear){
        for (Runnable task : POST_RENDER_TASKS){
            try {
                task.run();
            }catch (Exception e){
                Kanake.logger.error("Error in post render task: " + task.toString());
                e.printStackTrace();
            }
        }
        if (clear) {
            POST_RENDER_TASKS.clear();
        }
    }

    @SubscribeEvent
    public static void onWorldRendered(RenderWorldLastEvent event) {
        if (POST_RENDER_TASKS.size() <= 0){
            return;
        }
        int activeTextureUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        RenderEvent.foreachPostRenderTask(false);
        SCREEN_FBO.copyBindBuffer();
        EFFECT_FBO.renderToFramebufferStart();
        RenderEvent.foreachPostRenderTask(true);
        EFFECT_FBO.renderToFramebufferEnd();


        EFFECT_SHADER.use();
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.enableTexture2D();
        SCREEN_FBO.bindFramebufferTexture();
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.enableTexture2D();
        EFFECT_FBO.bindFramebufferTexture();
        EFFECT_SHADER.uniform1i("tex1", 1);
        LOW_EFFECT_FBO.renderToFramebufferStart();
        GL11.glViewport(0, 0, LOW_EFFECT_FBO.realWidth, LOW_EFFECT_FBO.realHeight);
        EFFECT_FBO.renderFboQuad();
        LOW_EFFECT_FBO.renderToFramebufferEnd();
        EFFECT_SHADER.release();
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
        GlStateManager.setActiveTexture(activeTextureUnit);

        EFFECT_SHADER_1.use();
        GL11.glViewport(0, 0, LOW_EFFECT_FBO.width, LOW_EFFECT_FBO.height);
        LOW_EFFECT_FBO.renderFboQuad();
        EFFECT_SHADER_1.release();
    }
}
