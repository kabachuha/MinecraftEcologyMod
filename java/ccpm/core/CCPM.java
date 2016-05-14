package ccpm.core;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.UnmodifiableIterator;
import com.ibm.icu.util.BytesTrie.Iterator;

import DummyCore.Blocks.BlocksRegistry;
import DummyCore.Core.Core;
import DummyCore.Core.CoreInitialiser;
import DummyCore.Items.ItemRegistry;
import DummyCore.Utils.MiscUtils;
import DummyCore.Utils.ModVersionChecker;
import ccpm.achivements.CCPMAchivements;
import ccpm.biomes.Wasteland;
import ccpm.blocks.BlockAdvFilter;
import ccpm.blocks.BlockAdvThaum;
import ccpm.blocks.BlockAnalyser;
import ccpm.blocks.BlockCompressor;
import ccpm.blocks.BlockEnergyCellBase;
import ccpm.blocks.BlockFilter;
import ccpm.blocks.BlockPollutionBricks;
import ccpm.blocks.ItemAdThaum;
import ccpm.blocks.ItemBlockCell;
import ccpm.commands.CommandAddTile;
import ccpm.commands.CommandGetPollution;
import ccpm.commands.CommandGetRegTiles;
import ccpm.commands.CommandIncPollution;
import ccpm.commands.CommandRemoveTile;
import ccpm.commands.CommandSaveConfiguration;
import ccpm.commands.CommandTestWand;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.fluids.CCPMFluids;
import ccpm.fluids.FluidPW;
import ccpm.fluids.FluidPollution;
import ccpm.handlers.CCPMFuelHandler;
import ccpm.handlers.ChunkHandler;
import ccpm.handlers.PlayerHandler;
import ccpm.handlers.WorldHandler;
import ccpm.integration.buildcraft.BCIntegration;
import ccpm.items.PWBucket;
import ccpm.items.PistonArray;
import ccpm.items.PollutedArmor;
import ccpm.items.PollutedMisc;
import ccpm.items.PollutedSword;
import ccpm.items.PortableAnalyzer;
import ccpm.items.RespiratorBase;
import ccpm.items.SrapBrick;
import ccpm.network.proxy.CommonProxy;
import ccpm.potions.PotionSmog;
import ccpm.render.CCPMRenderHandler;
import ccpm.render.RespHud;
import ccpm.tiles.AdvancedAirFilter;
import ccpm.tiles.TileAdvThaum;
import ccpm.tiles.TileCompressor;
//import ccpm.render.RenderRespirator;
import ccpm.tiles.TileEnergyCellMana;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;
import ccpm.tiles.TileEntityAnalyser;
import ccpm.tiles.TileEntityFilter;
import ccpm.utils.config.CCPMConfig;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = CCPM.MODID, name = CCPM.NAME, version = CCPM.version, dependencies = CCPM.dependencies)
public class CCPM {

	public static final String MODID = "ccpm";
	public static final String NAME = /*"Artem226's Climate Change And Pollution Mod"*/ "Artem226's Ecology Mod";
	public static final String version = "0.2.189.1A";
	public static final String dependencies = "required-before:DummyCore;";
	
	public static Item respirator = new RespiratorBase("ccpmRespirator", RespiratorBase.respiratorMatter);
	
	public static Item buckPw;
	
	public static Item pollutionBrick = new SrapBrick();
	
	public static Block cell = new BlockEnergyCellBase();
	
	public static Block an = new BlockAnalyser();
	
	public static Block filter = new BlockFilter();
	
	public static Block pollFlu;
	
	public static Block pw;
	
	public static Block baf = new BlockAdvFilter();
	
	public static Block advThaum = new BlockAdvThaum();
	
	public static Item pistons = new PistonArray();
	
	public static Item sword = new PollutedSword();
	
	public static Item portableAnalyzer = new PortableAnalyzer();
	
	public static Block pollutionBricks = new BlockPollutionBricks();
	
	public static Item[] pollArmor = new Item[]{new PollutedArmor(PollutedArmor.pollution,0,"ccpm.pollhelm").setTextureName("ccpm:Helmet"), new PollutedArmor(PollutedArmor.pollution,1,"ccpm.pollchest").setTextureName("ccpm:chest"), new PollutedArmor(PollutedArmor.pollution,2,"ccpm.polllegs").setTextureName("ccpm:leg"), new PollutedArmor(PollutedArmor.pollution,3,"ccpm.pollboots").setTextureName("ccpm:boots")};

	public static Block compressor = new BlockCompressor();
	
	/**
		ccpb_dust, ccpb_tiny_dust, ccpb_nugget, ccpb_wand_cap, ccpb_wand_rod, ccpb_wand_rod_inverted, ccpb_wand_staff, ccpb_wand_staff_inverted
	
	*/
	public static Item miscIngredient = new PollutedMisc("ccpb_dust", "ccpb_tiny_dust", "ccpb_nugget", "ccpb_wand_cap", "ccpb_wand_rod", "ccpb_wand_rod_inverted", "ccpb_wand_staff", "ccpb_wand_staff_inverted");
	
	@Instance(MODID)
	public static CCPM instance;
	
	public static CCPMConfig cfg = new CCPMConfig();
	
	@SidedProxy(clientSide = "ccpm.network.proxy.ClientProxy", serverSide = "ccpm.network.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static PotionSmog smog;
	
	public static Wasteland wasteland;
	
	/**
	 * URL to my GitHub 
	 */
	public static final String githubURL = "https://github.com/Artem226/MinecraftEcologyMod/issues";
	
	public static Logger log = LogManager.getLogger(NAME);
	
	public static String cfgpath = "";
	
	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		instance = this;
		cfgpath=event.getModConfigurationDirectory().getAbsolutePath();
		Core.registerModAbsolute(getClass(), NAME, cfgpath, cfg);
		
		
		log.info("Configuration directory is "+cfgpath);
		PollutionConfig.load(cfgpath);
		
		ModVersionChecker.addRequest(getClass(), "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.8/version.txt");
		
		
		
		ModMetadata meta = event.getModMetadata();
		meta.authorList = Arrays.asList(new String[]{"Artem226"});
		meta.autogenerated = false;
		meta.description = "Mod, that adds pollution and climate changing system in the Minecraft";
		meta.version = version;
		meta.modId = MODID;
		meta.name = NAME;
		meta.credits = "Author: Artem226";
		meta.url = "http://minecraft.curseforge.com/projects/ecology-mod";
		
		//MiscUtils.extendPotionArray(1);
		smog = new PotionSmog(/*Potion.potionTypes.length-1, false, new Color(61, 54 , 54).getRGB()*/);
		log.info("Potion smog id is "+smog.id);
		wasteland = new Wasteland(CCPMConfig.wasteId);
		
		BiomeManager.addBiome(BiomeType.DESERT, new BiomeEntry(wasteland, 1));
		
		CCPMFluids.init();
		
		pollFlu  = new FluidPollution();
		
		pw = new FluidPW();
		
		BlocksRegistry.registerBlock(pollFlu, "liquid_ccpm_pollution", getClass(), null);
		BlocksRegistry.registerBlock(pw, "liquid_ccpm_pw", getClass(), null);
		
		ItemRegistry.registerItem(respirator, "itemRespirator", getClass());
		ItemRegistry.registerItem(pistons, "pistons", getClass());
		ItemRegistry.registerItem(pollutionBrick, "pollutionBrick", getClass());
		
		ItemRegistry.registerItem(pollArmor[0], pollArmor[0].getUnlocalizedName(), getClass());
		ItemRegistry.registerItem(pollArmor[1], pollArmor[1].getUnlocalizedName(), getClass());
		ItemRegistry.registerItem(pollArmor[2], pollArmor[2].getUnlocalizedName(), getClass());
		ItemRegistry.registerItem(pollArmor[3], pollArmor[3].getUnlocalizedName(), getClass());
		
		ItemRegistry.registerItem(sword, "ccpmSword", getClass());
		
		ItemRegistry.registerItem(miscIngredient, "ccpmIngr", getClass());
		
		ItemRegistry.registerItem(portableAnalyzer, "itemPortAnalyser", getClass());
		
		BlocksRegistry.registerBlock(cell, "energycell", getClass(), ItemBlockCell.class);
		BlocksRegistry.registerBlock(an, "analyser", getClass(), null);
		BlocksRegistry.registerBlock(filter, "filter", getClass(), null);
		
		BlocksRegistry.registerBlock(baf, "adv_filter", getClass(), null);
		BlocksRegistry.registerBlock(advThaum, "adv_thaum_cell", getClass(), ItemAdThaum.class);
		BlocksRegistry.registerBlock(pollutionBricks, "pollution_bricks", getClass(), null);
		BlocksRegistry.registerBlock(compressor, "compressor", getClass(), null);
		
		buckPw = new PWBucket();
		
		//I won't use it now, because there are the Universal Bucket from Minecraft Forge
		//ItemRegistry.registerItem(buckPw, "itemBucketPw", getClass());
		
		//FluidContainerRegistry.registerFluidContainer(CCPMFluids.pollutedWater, new ItemStack(buckPw), new ItemStack(Items.bucket));
		GameRegistry.registerTileEntity(TileEnergyCellMana.class, "TECM");
		GameRegistry.registerTileEntity(TileEnergyCellRf.class, "TECR");
		GameRegistry.registerTileEntity(TileEnergyCellThaumium.class, "TECT");
		GameRegistry.registerTileEntity(TileEntityFilter.class, "TEF");
		GameRegistry.registerTileEntity(TileEntityAnalyser.class, "TEA");
		GameRegistry.registerTileEntity(AdvancedAirFilter.class, "TEAAF");
		GameRegistry.registerTileEntity(TileAdvThaum.class, "TEADVTHAUM");
		GameRegistry.registerTileEntity(TileCompressor.class, "TECCPMCOMPRESSOR");
		
		proxy.registerFluidModels();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		if(instance != this)
			instance = this;
		log.info("Initialising");
		
		
		MinecraftForge.EVENT_BUS.register(new WorldHandler());
		//FMLCommonHandler.instance().bus().register(new WorldHandler());
		ChunkHandler ch = new ChunkHandler();
		MinecraftForge.EVENT_BUS.register(ch);
		//FMLCommonHandler.instance().bus().register(new ChunkHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerHandler());
		//FMLCommonHandler.instance().bus().register(new PlayerHandler());
		MinecraftForge.TERRAIN_GEN_BUS.register(ch);
	    
		//FMLCommonHandler.instance().bus().register(new CCPMRenderHandler());
		
		//FMLCommonHandler.instance().
		if(proxy!=null)
		{
			proxy.registerItemRenders();
			proxy.registerRenderHandler();
			NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		}

		if(Loader.isModLoaded("Thaumcraft"))
			FMLInterModComms.sendMessage("Thaumcraft", "biomeBlacklist", cfg.wasteId+":0");
		
		FMLInterModComms.sendMessage("Waila", "register", "ccpm.integration.waila.WailaDataProvider.reg");
		
		RecipeRegistry.init();
		
		GameRegistry.registerFuelHandler(new CCPMFuelHandler());
	}
	
	@EventHandler
	public void postLoad(FMLPostInitializationEvent event)
	{
		log.info("Post initialisation");
		if(instance != this)
			instance = this;
		
		if(Loader.isModLoaded("Thaumcraft")&&CCPM.cfg.enableThaum)
			RecipeRegistry.thaum();
		
		if(Loader.isModLoaded("BuildCraft|Core") || Loader.isModLoaded("BuildCraft"))
		{
			BCIntegration.regFuels();
			
			BCIntegration.regRecipes();
		}
		
		CCPMAchivements.init();
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandGetPollution());
		event.registerServerCommand(new CommandTestWand());
		event.registerServerCommand(new CommandGetRegTiles());
		event.registerServerCommand(new CommandIncPollution());
		event.registerServerCommand(new CommandAddTile());
		event.registerServerCommand(new CommandSaveConfiguration());
		event.registerServerCommand(new CommandRemoveTile());
	}

	//Function to tell you where you have to report errors
	public static void addToEx()
	{
		log.warn("Please, report this to the author's(Artem226) GitHub repository!!!");
		log.warn(githubURL);
		log.info("Please, don't forget to include crash report/log");
	}
	
	@EventHandler
	public void imc(IMCEvent event)
	{
		UnmodifiableIterator<IMCMessage> iter = event.getMessages().iterator();
		
		while (iter.hasNext())
		{
			FMLInterModComms.IMCMessage message = (FMLInterModComms.IMCMessage) iter.next();
			if(message.key.equals("addPollutionTile"))
			if(message.isStringMessage())
			{
				String s[] = message.getStringValue().split(":");
				String modid = "";
				String name = "";
				float  pollution = Float.NaN;
				if(s!=null && s.length == 2)
				{
					name = s[0];
					try{
					pollution = Float.parseFloat(s[1]);
					}catch(NumberFormatException ex){ pollution = Float.NaN;}
					modid = message.getSender();
				}
				
				if(modid.length()> 1 && name.length() > 1 && pollution !=Float.NaN)
				{
					Tilez tile = new Tilez();
					
					tile.setModid(modid);
					tile.setName(name);
					tile.setPollution(pollution);
					
					List<Tilez> tiles = new LinkedList<Tilez>(Arrays.asList(PollutionConfig.cfg.getTiles()));
					
					if(!tiles.contains(tile))
					{
						tiles.add(tile);
					}
					
					PollutionConfig.cfg.setTiles(tiles.toArray(new Tilez[tiles.size()]));
					log.info("Mod "+modid+" adding a tile with id {"+name+"} to configuration and sets it's pollution prodution to "+pollution);
				}
				else
				{
					log.warn("Mod "+message.getSender()+" is unable to add a tile to config with IMC!");
				}
				
			}
		}
	}
	
}
