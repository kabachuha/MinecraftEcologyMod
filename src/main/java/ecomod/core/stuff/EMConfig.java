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
import ecomod.common.pollution.config.PollutionEffectsConfig;
import ecomod.common.pollution.config.PollutionSourcesConfig;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class EMConfig
{
	public static Configuration config;
	
	
	public static int[] allowedDims = new int[]{0};
	
	public static boolean wptimm = false;
	
	public static int wptcd = 60;
	
	public static float filter_adjacent_tiles_reduction = 0.06F;
	
	public static int filter_energy_per_emission = 5000;
	
	public static float pollution_precision = 5.0F;
	
	public static boolean enable_manually_assembly = true;
	
	public static boolean waila_shows_pollution_info = true;
	
	public static boolean pollution_effects_as_recipe_category = true;
	
	public static int adv_filter_energy = 70000;
	
	public static int adv_filter_capacity = 5000;
	
	public static int adv_filter_delay_secs = 5;
	
	public static boolean enable_advanced_filter = true;
	
	public static List<String> item_blacklist = new ArrayList<String>();
	
	public static int wpr = 1;
	
	public static boolean allow_acid_rain_render = true;
	
	public static int cached_pollution_radius = 5;
	
	public static boolean check_client_pollution = true;
	
	public static float diffusion_factor = 0.01F;
	
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
	
	public static boolean isConcentratedPollutionIC2Fuel = true;
	
	public static boolean is_oc_analyzer_interface_crafted_by_right_click = true;
	
	public static boolean wasteland_spawns_naturally = false;
	
	public static float smog_rendering_distance_intensity_exponent = 1.4F; 
	
	public static int wpt_profiler_timeout_warning = 500;
	
	public static int wpt_profiler_critical_timeout_warning = 10000;
	
	public static float eu_to_rf_conversion = 4F;
	
	public static int wasteland_id = 117;
	
	public static float advanced_filter_max_rps = 2F;
	
	public static boolean enable_sponge_recipe = true;
	
	public static boolean pollution_effects_books_gen = true;
	
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
			
			allowedDims = config.get("CORE", "allowedDimensions", allowedDims, "Dimensions where the pollution system is avaible").getIntList();
			
			Arrays.sort(allowedDims);
			
			allow_acid_rain_render = config.getBoolean("allowAcidRainRender", "CLIENT", true, "", lang("client.rain"));
			
			wptimm = config.getBoolean("ImmediateStart", "THREAD", false, "Whether the thread starts without delay", lang("thread.wptimm"));
			
			wptcd = config.getInt("Delay", "THREAD", 60, 60, 3600, "The delay between thread runs.", lang("thread.wptcd"));
			
			pollution_effects_as_recipe_category = config.getBoolean("pollution_effects_as_recipe_category", "INTERMOD", true, "");
			
			wpt_profiler_timeout_warning = config.getInt("timeoutWarningMillis", "THREAD", 1000, 1, Integer.MAX_VALUE, "Timeout warning for every WPT operation in milliseconds. If you want to disable the warning, make this value big enough.");
			
			wpt_profiler_critical_timeout_warning = config.getInt("criticalTimeoutWarinigMillis", "THREAD", 10000, 1, Integer.MAX_VALUE, "Critical timeout WPT warning(milliseconds).");
			
			pollution_precision = config.getFloat("PollutionDataPrecision", "THREAD", 5.0F, 0, 10000F, "Pollution values(full pollution) below this point are rounded to zero for optimization purposes.");
			
			enable_manually_assembly = config.getBoolean("enable_manually_assembly", "TILES", true, "Are tiles crafted by a right click?");
			
			filter_adjacent_tiles_reduction = config.getFloat("FilterAdjacentTilesReduction", "POLLUTION", 0.06F, 0, 1, "", lang("pollution.filter"));
			
			acid_rain_item_deterioriation_factor = config.getFloat("AcidRainItemDeteriorationFactor", "POLLUTION", 0.05F, 0, 1, "", lang("pollution.acid_rain_item_deterioriation_factor"));
			
			waila_shows_pollution_info = config.getBoolean("waila_shows_pollution_info", "INTERMOD", true, "Can tile entities pollution production be examined using Waila? This is a server config property, so each of the server clients will use this value.");
			
			filter_energy_per_emission = config.getInt("FilterEnergyPerNearbyPollutionEmission", "TILES", 5000, 0, Integer.MAX_VALUE, "");
			
			pollution_effects_books_gen = config.getBoolean("PollutionEffectsBookGeneration", "POLLUTION", true, "");
			
			diffusion_factor = config.getFloat("DiffusionFactor", "POLLUTION", 0.01F, 0, 1, "", lang("pollution.diffusion_factor"));
			
			wpr = config.getInt("WaterPollutionRadius", "POLLUTION", 1, 0, 128, "", lang("pollution.wpr"));
			
			//cached_pollution_radius = config.getInt("CachedPollutionRadius", "CLIENT", 5, 1, EMConsts.max_cached_pollution_radius, "", lang("client.max_cpr"));
			
			smog_rendering_distance_intensity_exponent = config.getFloat("smog_rendering_distance_intensity_exponent", "CLIENT", 1.4F, 0, 20F, "");
			
			advanced_filter_max_rps = config.getFloat("advanced_filer_vent_rps", "CLIENT", 2F, 0, 60, "Advanced filer's vent maximal rotational frequency(revolutions per second)");
			
			//check_client_pollution = config.getBoolean("CheckClientPollution", "CLIENT", true, "Determines whether the pollution data received from the server should be validated. When unabled the 'client' performance could be improved but the EcologyMod client part might be destabilized! Thus it is not recommended!", lang("client.shouldcheck"));
			
			enable_advanced_filter = config.getBoolean("EnableAdvancedFilterCrafting", "TILES", true, "");
			
			adv_filter_delay_secs = config.getInt("AdvancedFilterDelaySeconds", "TILES", 5, 1, Integer.MAX_VALUE, "", lang("tiles.advancedfilter.delay"));
			
			advanced_filter_energy_per_second = config.getInt("AdvancedFilterEnergyPerSeconds", "TILES", 5000, 0, adv_filter_energy, "", lang("tiles.advancedfilter.consumption"));
			
			enable_concentrated_pollution_flow_texture = config.getBoolean("EnableConcentratedPollutionFlowTexture", "CLIENT", true, "", lang("client.concentrated_pollution.flow"));
			
			analyzer_energy = config.getInt("AnalyzerEnergy", "TILES", 450000, 1, Integer.MAX_VALUE, "Analyzer energetic capacity", lang("tiles.analyzer.energy"));
			
			isConcentratedPollutionExplosive = config.getBoolean("ConcentratedPollutionExplosive", "POLLUTION", true, "", lang("pollution.concentrated_pollution_explosive"));
			
			acid_rain_item_deterioriation_factor = config.getFloat("AcidRainItemDeterioriationFactor", "POLLUTION", 0.05F, 0, 1, "", lang("pollution.acid_rain_item_deterioriation_factor"));
			
			fuel_concentrated_pollution_burn_time = config.getInt("FuelConcentratedPollutionBurnTime", "INTERMOD", 40, 1, Integer.MAX_VALUE, "");
			
			fuel_concentrated_pollution_burn_energy = config.getInt("FuelConcentratedPollutionBurnEnergy", "INTERMOD", 600, 1, Integer.MAX_VALUE, "");
			
			isConcentratedPollutionIC2Fuel = config.getBoolean("IsConcentratedPollutionIC2Fuel", "INTERMOD", true, "");
			
			is_oc_analyzer_interface_crafted_by_right_click = config.getBoolean("is_oc_analyzer_interface_crafted_by_right_click", "INTERMOD", true, "");
			
			filter_durability = config.getInt("FilterCoreDurability", "ITEMS", 30, 1, Integer.MAX_VALUE, "");
			
			enable_sponge_recipe = config.getBoolean("EnableSpongeRecipe", "ITEMS", true, "Enable sponge crafting recipe");
			
			food_polluting_factor = config.getFloat("FoodPollutingFactor", "POLLUTION", 0.001F, 0, 1, "");
			
			wasteland_id = config.getInt("WastelandId", "GENERATION", 117, 81, 255, "Wasteland biome id. Change this in the case of id conflict.");
			
			wasteland_spawns_naturally = config.getBoolean("WastelandSpawnsNaturally", "GENERATION", false, "Does wasteland spawn in the world without any pollution?");
			
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
				EcologyMod.log.error("Unable to read PollutionToFoodPoisonFactors property from the config!!! Using default value"+ Arrays.toString(new double[]{0.001F, 0.01F, 0.03F}));
			}
			
			
			EcomodStuff.pollution_effects = new HashMap<String, IAnalyzerPollutionEffect>();
			EcomodStuff.additional_blocks_air_penetrating_state = new HashMap<String, Boolean>();
			
			for(String s : config.getStringList("AirPenetrators", "AIR", new String[]{"ecomod:frame"}, ""))
			{
				EcomodStuff.additional_blocks_air_penetrating_state.put(s, true);
			}
			
			for(String s : config.getStringList("AirSealers", "AIR", new String[]{"minecraft:daylight_detector"}, ""))
			{
				EcomodStuff.additional_blocks_air_penetrating_state.put(s, false);
			}
			
			eu_to_rf_conversion = config.getFloat("eu_to_rf_conversion", "TILES", 4F, 0, Float.MAX_VALUE, "Industrial Craft EU to RF conversion ratio for machine powering(1 rf = k EU). If the value is zero, machines won't accept EU.");
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
	
	private static String lang(String str)
	{
		return EMConsts.modid+".config."+str;
	}
}
