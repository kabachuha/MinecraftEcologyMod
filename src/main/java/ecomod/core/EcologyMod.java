package ecomod.core;

import java.io.File;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ProgressManager;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.IPollutionGetter;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.pollution.handlers.PollutionHandler;
import ecomod.common.proxy.ComProxy;
import ecomod.core.stuff.EMCommands;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.EMIntermod;
import ecomod.core.stuff.MainRegistry;
import ecomod.network.EMPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;


@Mod(modid = EMConsts.modid, name = EMConsts.name, version = EMConsts.version, dependencies = EMConsts.deps, canBeDeactivated = false)
public class EcologyMod
{
	@Instance(EMConsts.modid)
	public static EcologyMod instance;
	
	public static Logger log;
	
	@SidedProxy(modId=EMConsts.modid, clientSide=EMConsts.client_proxy, serverSide=EMConsts.common_proxy)
	public static ComProxy proxy;
	
	public static PollutionHandler ph;
	
	public TEPollutionConfig tepc;
	
	//ModEventHandlers
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		log = LogManager.getLogger(EMConsts.name);
		
		log.info("Preinitialization");
		
		if(!EMConsts.asm_transformer_inited)
		{
			log.fatal("The mod ASM transformer had not been initialized!!! Unable to continue.");
			throw new NullPointerException("Ecomod ASM transformer had not been initialized!!!");
		}
		
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		
		new File(event.getModConfigurationDirectory().getAbsolutePath()+"/"+EMConsts.modid).mkdirs();
		
		EMConfig.config = cfg;
		
		EMConfig.sync();
		
		setupMeta(event.getModMetadata());
		
		if(proxy == null)
		{
			log.fatal("Unable to load proxies!!");
			Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(new NullPointerException("Unable to load either common or client proxy!!!"), "Unable to load either common or client proxy!!!"));
			return;
		}
		
		MainRegistry.doPreInit();
		
		ph = new PollutionHandler();
		
		EcomodAPI.pollution_getter = (IPollutionGetter)ph;
		
		MinecraftForge.EVENT_BUS.register(ph);
		MinecraftForge.TERRAIN_GEN_BUS.register(ph);
		
		proxy.doPreInit();
		
		tepc = new TEPollutionConfig();
		
		tepc.load(event.getModConfigurationDirectory().getAbsolutePath());
		
		if(EcomodStuff.tile_entity_pollution != null)
			log.error("Field 'tile_entity_pollution' in EcomodStuff has been already filled! It should not be like this!!!");
		EcomodStuff.tile_entity_pollution = tepc;
		
		//EMConfig.setupEffects(event.getModConfigurationDirectory().getAbsolutePath());
		//EMConfig.setupSources(event.getModConfigurationDirectory().getAbsolutePath());
		
		PollutionSourcesConfig psc = new PollutionSourcesConfig();
		psc.load(event.getModConfigurationDirectory().getAbsolutePath());
		psc.pushToApi();
		
		PollutionEffectsConfig pec = new PollutionEffectsConfig();
		pec.load(event.getModConfigurationDirectory().getAbsolutePath());
		pec.pushToApi();
		
		EMPacketHandler.init();
		
		FMLCommonHandler.instance().registerCrashCallable(new PollutionHandler.PollutionHandlerCrashCallable());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		log.info("Initialization");
		
		MainRegistry.doInit();
		
		proxy.doInit();
		
		/*
		log.info(GameData.getTileEntityRegistry().getKeys().size());
		
		for(ResourceLocation rl : GameData.getTileEntityRegistry().getKeys())
		{
			log.info(rl.toString());
		}*/
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		log.info("Postinitialization");
		
		MainRegistry.doPostInit();
	}
	
	
	@EventHandler
	public void onIMC(IMCEvent event)
	{	
		EMIntermod.processIMC(event.getMessages());
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent event)
	{
		EMCommands.onServerStart(event);
	}
	
	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event)
	{
		ph.onServerStopping();
	}
	
	
	//Utils
	
	private static void setupMeta(ModMetadata meta)
	{
		meta.autogenerated=false;
		meta.credits = EMConsts.contributors;
		meta.authorList = EMConsts.authors;
		meta.modId = EMConsts.modid;
		meta.name = EMConsts.name;
		meta.logoFile = "emlogo.png";
		meta.url = EMConsts.projectURL;
		meta.version = EMConsts.version;
		
		meta.description = "EcologyMod adds an environmental pollution system to Minecraft that makes you care about consequences of your technical development.\n \n \n"
				+ "Issue Tracker https://github.com/Artem226/MinecraftEcologyMod/issues.\n"
				+ "Wiki https://github.com/Artem226/MinecraftEcologyMod/wiki.\n"
				+ "The mod Github repository https://github.com/Artem226/MinecraftEcologyMod\n"
				+ "The mod CurseForge project https://minecraft.curseforge.com/projects/ecology-mod\n"
				+ "The mod MinecraftForum page http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2657500-ecology-mod-mod-that-adds-pollution-and-climate";
	}
}
