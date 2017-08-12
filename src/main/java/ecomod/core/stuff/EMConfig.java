package ecomod.core.stuff;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.*;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class EMConfig
{
	public static Configuration config;
	
	
	public static int[] allowedDims = new int[]{0};
	
	public static boolean wptimm = false;
	
	public static int wptcd = 180;
	
	public static String tepcURL = /*"https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/TEPC.json";*/"file:///<MINECRAFT>/tepc.json";
	
	public static float filtmult = 0.92F;
	
	public static PollutionData adv_filter_redution = new PollutionData(1.8, 0.02, 0.0005);
	
	public static int filter_energy = 5000;
	
	public static int adv_filter_energy = 70000;
	
	public static int adv_filter_capacity = 5000;
	
	public static int adv_filter_delay_secs = 5;
	
	public static boolean enable_advanced_filter = true;
	
	public static PollutionData pp2di = new PollutionData(0, 0.0625D, 0.125D);
	
	public static List<String> item_blacklist = new ArrayList<String>();
	
	public static int wpr = 2;
	
	public static boolean allow_acid_rain_render = true;
	
	public static PollutionData explosion_pollution = new PollutionData(20.5, 6.25, 8.25);
	
	public static PollutionData bonemeal_pollution = new PollutionData(0.05, 0.15, 0.5);
	
	public static int cached_pollution_radius = 5;
	
	public static boolean check_client_pollution = true;
	
	public static PollutionData pollution_per_potion_brewed = new PollutionData(6, 0, 0); 
	
	public static PollutionData pollution_reduced_by_tree = new PollutionData(-45, -5, -35);
	
	public static float diffusion_factor = 0.0001F;
	
	public static boolean enable_concentrated_pollution_flow_texture = true;
	
	public static int advanced_filter_energy_per_second = 5000;
	
	public static int analyzer_energy = 450000;
	
	//public static PollutionData indication_dangerous_pollution = new PollutionData(150000, 200000, 150000);
	
	public static void sync()
	{
		if(config == null)
		{
			EcologyMod.log.error("The configuration file hasn't been loaded!");
			throw new NullPointerException("The configuration file hasn't been loaded!");
		}
		
		
		try
		{
			config.load();
			
			allowedDims = config.get("CORE", "allowedDimensions", allowedDims).getIntList();
			
			allow_acid_rain_render = config.getBoolean("allowAcidRainRender", "CLIENT", true, "", lang("client.rain"));
			
			wptimm = config.getBoolean("ImmediateStart", "THREAD", false, "Whether the thread starts without delay", lang("thread.wptimm"));
			
			wptcd = config.getInt("Delay", "THREAD", 180, 60, 3600, "The delay between thread runs.", lang("thread.wptcd"));
			
			tepcURL = config.getString("TEPC_URL", "CORE", "file:///<MINECRAFT>/tepc.json", "A URL to the TEPollutionConfig. See format at https://en.wikipedia.org/wiki/URL. If the TEPC is remotely located you should have a connection to its location!  If you point a local file you can type <MINECRAFT> instead of a path to the Minecraft directory (like this 'file:///<MINECRAFT>/tepc.json').  When you are playing on a server you will use its TEPC.", lang("tepc.url"));
			
			filtmult = config.getFloat("FilterMultiplier", "POLLUTION", 0.92F, 0, 1, "Filter pollution multiplier.", lang("pollution.filter"));
			
			diffusion_factor = config.getFloat("DiffusionFactor", "POLLUTION", 0.0001F, 0, 1, "", lang("pollution.diffusion_factor"));
			
			wpr = config.getInt("WaterPollutionRadius", "POLLUTION", 2, 0, 128, "", lang("pollution.wpr"));
			
			String ppdi = config.getString("PollutionPerDecayedItem", "POLLUTION", new PollutionData(0,0.0625D,0.125D).toString(), "Default pollution emission decayed item", lang("pollution.ppdi"));
			
			pp2di = EMUtils.pollutionDataFromJSON(ppdi, new PollutionData(0, 0.0625D, 0.125D), "Failed to get PollutionPerDecayedItem property from config! Invalid JSON syntax!");
			
			String ppe = config.getString("PollutionPerExplosionUnitOfPower", "POLLUTION", new PollutionData(20.5, 6.25, 8.25).toString(), "Pollution emitted by an explosion per unit of its power. (The TNT's explosion power is 4.0 by default)", lang("pollution.explosion"));
			
			explosion_pollution = EMUtils.pollutionDataFromJSON(ppe, new PollutionData(20.5, 6.25, 8.25), "Failed to get PollutionPerExplosionUnitOfPower property from config! Invalid JSON syntax!");
			
			String ib[] = config.getStringList("BlacklistedItems", "POLLUTION", new String[]{"minecraft:apple", "minecraft:stick", "minecraft:mushroom_stew", "minecraft:string", "minecraft:feather", "minecraft:gunpowder", "minecraft:wheat", "minecraft:wheat_seeds", "minecraft:porkchop", "minecraft:snowball", "minecraft:leather", "minecraft:reeds", "minecraft:slime_ball", "minecraft:egg", "minecraft:fish", "minecraft:sugar", "minecraft:melon", "minecraft:pumpkin_seeds", "minecraft:melon_seeds", "minecraft:beef", "minecraft:chicken", "minecraft:carrot", "minecraft:potato", "minecraft:rabbit", "minecraft:mutton", "minecraft:chorus_fruit", "minecraft:beetroot", "minecraft:beetroot_seeds"}, "Items which do not create pollution when dropped and expired");
			
			item_blacklist.addAll(Arrays.asList(ib));
			
			String bone_pollution = config.getString("BonemealPollution", "POLLUTION", new PollutionData(0.05, 0.15, 0.5).toString(), "Bonemeal usage pollution", lang("pollution.bonemeal"));
			
			bonemeal_pollution = EMUtils.pollutionDataFromJSON(bone_pollution, new PollutionData(0.05, 0.15, 0.5), "Failed to get BonemealPollution property from config! Invalid JSON syntax!");
			
			cached_pollution_radius = config.getInt("CachedPollutionRadius", "CLIENT", 5, 1, EMConsts.max_cached_pollution_radius, "", lang("client.max_cpr"));
			
			check_client_pollution = config.getBoolean("CheckClientPollution", "CLIENT", true, "Determines whether the pollution data received from the server should be validated. When unabled the 'client' performance could be improved but the EcologyMod client part might be destabilized! Thus it is not recommended!", lang("client.shouldcheck"));
			
			adv_filter_delay_secs = config.getInt("AdvancedFilterDelaySeconds", "TILES", 5, 1, Integer.MAX_VALUE, "", lang("tiles.advancedfilter.delay"));
			
			advanced_filter_energy_per_second = config.getInt("AdvancedFilterEnergyPerSecons", "TILES", 5000, 0, adv_filter_energy, "", lang("tiles.advancedfilter.consumption"));
			
			enable_concentrated_pollution_flow_texture = config.getBoolean("EnableConcentratedPollutionFlowTexture", "CLIENT", true, "", lang("client.concentrated_pollution.flow"));
			
			analyzer_energy = config.getInt("AnalyzerEnergy", "TILES", 450000, 1, Integer.MAX_VALUE, "Analyzer energetic capacity", lang("tiles.analyzer.energy"));
			
			EcomodStuff.pollution_effects = new HashMap<String, IAnalyzerPollutionEffect>();
		}
		catch(Exception e)
		{
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		finally
		{
			if(config.hasChanged())
				config.save();
		}	
	}
	
	public static void setupEffects(String cfg_dir)
	{
		PollutionData food_pollution_start = new PollutionData(15000, 10000, 10000);
		
		PollutionData bonemeal_limiting_pollution = new PollutionData(0,0, 65000);
		
		PollutionData wasteland_pollution = new PollutionData(300000, 300000, 150000);
		
		PollutionData smog_pollution = new PollutionData(150000, 0, 0);
		
		PollutionData bad_sleep_pollution = new PollutionData(45000, 0, 0);
		
		PollutionData poisonous_sleep_pollution = new PollutionData(70000, 0, 0);
		
		PollutionData no_fish_pollution = new PollutionData(18000, 0, 0);
		
		PollutionData useless_hoe_pollution = new PollutionData(0, 0, 150000);
		
		PollutionData polluted_water_pollution = new PollutionData(0, 200000, 0);
		
		PollutionData no_animals = new PollutionData(250000, 275000, 400000);
		
		PollutionData acid_rain_pollution = new PollutionData(40000, 15000, 0);
		
		PollutionData dead_trees_pollution = new PollutionData(1750000, 250000, 150000);
		
		PollutionData no_trees_pollution = new PollutionData(2750000, 400000, 310000);
		
		List<AnalyzerPollutionEffect> defs = new ArrayList<AnalyzerPollutionEffect>();
		
		defs.add(AnalyzerPollutionEffect.createSimple("food_pollution", food_pollution_start, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("smog", smog_pollution, TriggeringType.AND));
		defs.add(new AnalyzerPollutionEffect("bad_sleep", "ape.ecomod.bad_sleep.name", "ape.ecomod.bad_sleep.desc", new ResourceLocation("ecomod:textures/gui/analyzer/icons/sleep.png"), bad_sleep_pollution, TriggeringType.AND));
		defs.add(new AnalyzerPollutionEffect("poisonous_sleep", "ape.ecomod.poisonous_sleep.name", "ape.ecomod.poisonous_sleep.desc", new ResourceLocation("ecomod:textures/gui/analyzer/icons/sleep.png"), poisonous_sleep_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimple("no_fish", no_fish_pollution, TriggeringType.AND));
		
		defs.add(AnalyzerPollutionEffect.createSimpleNull("polluted_water", polluted_water_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("dead_trees", dead_trees_pollution, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("no_trees", no_trees_pollution, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("acid_rain", acid_rain_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("wasteland", wasteland_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimple("no_plowing", useless_hoe_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimple("no_animals", no_animals, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimple("no_bonemeal", bonemeal_limiting_pollution, TriggeringType.AND));
		
		PollutionEffectsConfig pec = new PollutionEffectsConfig();
		
		pec.effects.addAll(defs);
		
		pec.save(cfg_dir);
		
		pec.pushToApi();
	}
	
	private static String lang(String str)
	{
		return EMConsts.modid+".config."+str;
	}
}
