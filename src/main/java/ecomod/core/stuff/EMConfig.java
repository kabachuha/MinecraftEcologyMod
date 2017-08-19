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
import ecomod.common.pollution.PollutionSourcesConfig;
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
	public static String effectsURL = /*"https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/pollution_effects.json";*/"file:///<MINECRAFT>/pollution_effects.json";
	public static String sourcesURL = /*"https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/pollution_sources.json";*/"file:///<MINECRAFT>/pollution_sources.json";
	
	public static float filtmult = 0.92F;
	
	public static int filter_energy = 5000;
	
	public static int adv_filter_energy = 70000;
	
	public static int adv_filter_capacity = 5000;
	
	public static int adv_filter_delay_secs = 5;
	
	public static boolean enable_advanced_filter = true;
	
	public static List<String> item_blacklist = new ArrayList<String>();
	
	public static int wpr = 2;
	
	public static boolean allow_acid_rain_render = true;
	
	public static int cached_pollution_radius = 5;
	
	public static boolean check_client_pollution = true;
	
	public static float diffusion_factor = 0.0001F;
	
	public static boolean enable_concentrated_pollution_flow_texture = true;
	
	public static int advanced_filter_energy_per_second = 5000;
	
	public static int analyzer_energy = 450000;
	
	public static boolean isConcentratedPollutionExplosive = true;
	
	public static float acid_rain_item_deterioriation_factor = 0.05F;
	
	public static int fuel_concentrated_pollution_burn_time = 40;
	
	public static int fuel_concentrated_pollution_burn_energy = 600;
	
	public static int filter_durability = 30;
	
	public static float food_polluting_factor = 0.001F;
	
	public static float pollution_to_food_poison[] = new float[]{0.01F, 0.01F, 0.03F};
	
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
			
			cached_pollution_radius = config.getInt("CachedPollutionRadius", "CLIENT", 5, 1, EMConsts.max_cached_pollution_radius, "", lang("client.max_cpr"));
			
			check_client_pollution = config.getBoolean("CheckClientPollution", "CLIENT", true, "Determines whether the pollution data received from the server should be validated. When unabled the 'client' performance could be improved but the EcologyMod client part might be destabilized! Thus it is not recommended!", lang("client.shouldcheck"));
			
			enable_advanced_filter = config.getBoolean("EnableAdvancedFilterCrafting", "TILES", true, "");
			
			adv_filter_delay_secs = config.getInt("AdvancedFilterDelaySeconds", "TILES", 5, 1, Integer.MAX_VALUE, "", lang("tiles.advancedfilter.delay"));
			
			advanced_filter_energy_per_second = config.getInt("AdvancedFilterEnergyPerSecons", "TILES", 5000, 0, adv_filter_energy, "", lang("tiles.advancedfilter.consumption"));
			
			enable_concentrated_pollution_flow_texture = config.getBoolean("EnableConcentratedPollutionFlowTexture", "CLIENT", true, "", lang("client.concentrated_pollution.flow"));
			
			analyzer_energy = config.getInt("AnalyzerEnergy", "TILES", 450000, 1, Integer.MAX_VALUE, "Analyzer energetic capacity", lang("tiles.analyzer.energy"));
			
			isConcentratedPollutionExplosive = config.getBoolean("ConcentratedPollutionExplosive", "POLLUTION", true, "", lang("pollution.concentrated_pollution_explosive"));
			
			acid_rain_item_deterioriation_factor = config.getFloat("AcidRainItemDeterioriationFactor", "POLLUTION", 0.05F, 0, 1, "", lang("pollution.acid_rain_item_deterioriation_factor"));
			
			fuel_concentrated_pollution_burn_time = config.getInt("FuelConcentratedPollutionBurnTime", "INTERMOD", 40, 1, Integer.MAX_VALUE, "");
			
			fuel_concentrated_pollution_burn_energy = config.getInt("FuelConcentratedPollutionBurnEnergy", "INTERMOD", 600, 1, Integer.MAX_VALUE, "");
			
			filter_durability = config.getInt("FilterCoreDurability", "ITEMS", 30, 1, Integer.MAX_VALUE, "");
			
			food_polluting_factor = config.getFloat("FoodPollutiingFactor", "POLLUTION", 0.001F, 0, 1, "");
			
			
			double dbls[] = config.get("POLLUTION", "PollutionToFoodPoisonFactors", new double[]{0.01F, 0.01F, 0.03F}).getDoubleList();
			if(dbls.length == 3)
			{
				pollution_to_food_poison = new float[3];
				for(int i = 0; i < 3; i++)
				{
					pollution_to_food_poison[i] = (float)dbls[i];
				}
			}
			else
			{
				EcologyMod.log.error("Unable to read PollutionToFoodPoisonFactors property from the config!!! Using default value"+new double[]{0.001F, 0.01F, 0.03F}.toString());
			}
			
			
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
		
		PollutionData no_fish_pollution = new PollutionData(0, 80000, 0);
		
		PollutionData useless_hoe_pollution = new PollutionData(0, 0, 200000);
		
		PollutionData no_crop_growing_pollution = new PollutionData(100000, 125000 ,125000);
		
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
		defs.add(AnalyzerPollutionEffect.createSimple("dead_trees", dead_trees_pollution, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimple("no_trees", no_trees_pollution, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("acid_rain", acid_rain_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimpleNull("wasteland", wasteland_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimple("no_plowing", useless_hoe_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimple("no_animals", no_animals, TriggeringType.OR));
		defs.add(AnalyzerPollutionEffect.createSimple("no_bonemeal", bonemeal_limiting_pollution, TriggeringType.AND));
		defs.add(AnalyzerPollutionEffect.createSimple("no_crops_growing", no_crop_growing_pollution, TriggeringType.OR));
		
		PollutionEffectsConfig pec = new PollutionEffectsConfig();
		
		pec.effects.addAll(defs);
		
		pec.save(cfg_dir);
		
		pec.pushToApi();
	}
	
	public static void setupSources(String cfg_dir)
	{
		PollutionData adv_filter_redution = new PollutionData(-1.8, -0.02, -0.0005);
		PollutionData item_expire_pollution = new PollutionData(0, 0.0625D, 0.125D);
		PollutionData explosion_pollution = new PollutionData(20.5, 6.25, 8.25);
		PollutionData concentrated_pollution_explosion_pollution = new PollutionData(50,5,10);
		PollutionData bonemeal_pollution = new PollutionData(0.05, 0.15, 0.5);
		PollutionData pollution_per_potion_brewed = new PollutionData(6, 0, 0);
		PollutionData pollution_reduced_by_tree = new PollutionData(-22, -2.5, -17);
		PollutionData hoe_plowing_reducion = new PollutionData(0,0,-0.5);
		PollutionData fire_pollution = new PollutionData(2, 0, 0);
		PollutionData leaves_redution = new PollutionData(-0.018, 0, 0);
		PollutionData smelted_item_pollution = new PollutionData(3, 0.6, 1.9);
		
		List<String> item_blacklist = new ArrayList<String>();
		item_blacklist.addAll(Arrays.asList(new String[]{"minecraft:apple", "minecraft:stick", "minecraft:mushroom_stew", "minecraft:string", "minecraft:feather", "minecraft:gunpowder", "minecraft:wheat", "minecraft:wheat_seeds", "minecraft:porkchop", "minecraft:snowball", "minecraft:leather", "minecraft:reeds", "minecraft:slime_ball", "minecraft:egg", "minecraft:fish", "minecraft:sugar", "minecraft:melon", "minecraft:pumpkin_seeds", "minecraft:melon_seeds", "minecraft:beef", "minecraft:chicken", "minecraft:carrot", "minecraft:potato", "minecraft:rabbit", "minecraft:mutton", "minecraft:chorus_fruit", "minecraft:beetroot", "minecraft:beetroot_seeds"}));
		
		PollutionSourcesConfig psc = new PollutionSourcesConfig();
		psc.blacklisted_items.addAll(item_blacklist);
		psc.pollution_sources.put("advanced_filter_redution", adv_filter_redution);
		psc.pollution_sources.put("expired_item", item_expire_pollution);
		psc.pollution_sources.put("explosion_pollution_per_power", explosion_pollution);
		psc.pollution_sources.put("bonemeal_pollution", bonemeal_pollution);
		psc.pollution_sources.put("fire_pollution", fire_pollution);
		psc.pollution_sources.put("brewing_potion_pollution", pollution_per_potion_brewed);
		psc.pollution_sources.put("tree_growing_pollution_redution", pollution_reduced_by_tree);
		psc.pollution_sources.put("concentrated_pollution_explosion_pollution", concentrated_pollution_explosion_pollution);
		psc.pollution_sources.put("hoe_plowing_reducion", hoe_plowing_reducion);
		psc.pollution_sources.put("leaves_redution", leaves_redution);
		psc.pollution_sources.put("default_smelted_item_pollution", smelted_item_pollution);
		
		psc.save(cfg_dir);
		
		psc.pushToApi();
	}
	
	private static String lang(String str)
	{
		return EMConsts.modid+".config."+str;
	}
}
