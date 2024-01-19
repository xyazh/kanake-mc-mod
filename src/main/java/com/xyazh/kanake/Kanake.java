package com.xyazh.kanake;

import com.xyazh.kanake.block.blocks.clean.TileClean;
import com.xyazh.kanake.block.blocks.clean.render.RenderClean;
import com.xyazh.kanake.common.ConfigLoader;
import com.xyazh.kanake.gen.GenOreHarmoniumCrystal;
import com.xyazh.kanake.init.LoopThread;
import com.xyazh.kanake.init.RegistryHandler;
import com.xyazh.kanake.network.*;
import com.xyazh.kanake.particle.ModParticles;
import com.xyazh.kanake.proxy.ProxyBase;
import com.xyazh.kanake.recipes.brewing.MyBrewing;
import com.xyazh.kanake.recipes.furnace.MyFurnace;
import com.xyazh.kanake.recipes.mono.MonoFunctions;
import com.xyazh.kanake.recipes.mono.MonoRecipes;
import com.xyazh.kanake.util.Reference;
import com.xyazh.kanake.world.ModWorlds;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = Kanake.MODID, name = Kanake.NAME, version = Kanake.VERSION,dependencies="before:baubles@[1.5.0,)")
public class Kanake
{
    public static final String MODID = "kanake";
    public static final String NAME = "Kannmein na kenndaimahou";
    public static final String VERSION = "0.1.14.beta";

    public static SimpleNetworkWrapper network;

    public static Logger logger;

    public static Random rand = new Random();

    @Mod.Instance
    public static Kanake instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static ProxyBase proxy;

    public static boolean HAS_BAUBLES = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        HAS_BAUBLES = Loader.isModLoaded("baubles");
        ConfigLoader.init(event);
        LoopThread.creatThread();
        MonoFunctions.addFunc();
        RegistryHandler.preInitRegistries(event);
        GameRegistry.registerWorldGenerator(new GenOreHarmoniumCrystal(),130);

        network.registerMessage(new KooriEntityHandler(), KooriEntityPacket.class, 3, Side.CLIENT);
        network.registerMessage(new ManaHandler(), ManaPacket.class, 4, Side.CLIENT);
        network.registerMessage(new PlayerManaHandler(), PlayerManaPacket.class, 5, Side.CLIENT);
        network.registerMessage(new FlyHandler(), FlyPacket.class, 6, Side.SERVER);

        if(Side.CLIENT.equals(event.getSide())){
            preInitClient(event);
        }else {
            preInitServer(event);
        }

        ModWorlds.registerAllDim();
    }

    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event){
        /*
        ShaderProgram shaderProgram = new ShaderProgram();
        String vertexShaderSource = ResourceLocationHelper.loadResourceToStr(new ResourceLocation(Kanake.MODID,"shader/dis.vs"));
        String fragmentShaderSource = ResourceLocationHelper.loadResourceToStr(new ResourceLocation(Kanake.MODID,"shader/dis.fs"));
        if(vertexShaderSource != null && fragmentShaderSource!=null){
            Shader v = Shader.craftVertexShaderFromString(ShaderDisSources.vertexShaderSource);
            Shader f = Shader.craftFragmentShaderFromString(ShaderDisSources.fragmentShaderSource);
            if(v!=null&& f!=null){
                shaderProgram.addShader(v);
                shaderProgram.addShader(f);
                shaderProgram.link();
                shaderProgram.use();
                shaderProgram.unUse();
            }
        }*/
        //net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(TileClean.class, new RenderClean());
    }

    public void preInitServer(FMLPreInitializationEvent event){

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if(Side.CLIENT.equals(event.getSide())){
            ModParticles.registerParticles();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        RegistryHandler.postInitReg();
        MonoRecipes.addMonoRecipes();
        MyBrewing.addBrewingRecipes();
        MyFurnace.addFurnaceRecipes();
    }

    @EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        RegistryHandler.serverRegistries(event);
    }
}
