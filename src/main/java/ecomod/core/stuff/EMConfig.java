package ecomod.core.stuff;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraftforge.common.config.Configuration;

public class EMConfig
{
	public static Configuration config;
	
	
	public static int[] allowedDims = new int[]{0};
	
	public static boolean wptimm = false;
	
	public static int wptcd = 180;
	
	public static String tepcURL = /*"https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/TEPC.json";*/"file:///<MINECRAFT>/tepc.json";
	
	public static float filtmult = 0.92F;
	
	public static PollutionData pp2di = new PollutionData(0, 0.0625D, 0.125D);
	
	public static List<String> item_blacklist = new ArrayList<String>();
	
	public static int wpr = 2;
	
	public static int min_ps = 10000;
	
	public static boolean allow_acid_rain_render = true;
	
	public static PollutionData explosion_pollution = new PollutionData(20.5, 6.25, 8.25);
	
	public static PollutionData bonemeal_pollution = new PollutionData(0.05, 0.15, 0.5);
	
	public static int cached_pollution_radius = 5;
	
	public static boolean check_client_pollution = true;
	
	public static double food_pollution_start = 10000;
	
	public static double bonemeal_limiting_soil_pollution = 65000;
	
	public static double wasteland_pollution = 300000;
	
	public static double smog_pollution = 150000;
	
	public static double bad_sleep_pollution = 15000;
	
	public static double poisonous_sleep_pollution = 70000;
	
	public static double no_fish_pollution = 18000;
	
	public static double useless_hoe_pollution = 75000;
	
	public static double poisoned_water_pollution = 200000;
	
	public static PollutionData no_animals = new PollutionData(250000, 275000, 400000);
	
	public static PollutionData pollution_per_potion_brewed = new PollutionData(6, 0, 0); 
	
	public static PollutionData pollution_reduced_by_tree = new PollutionData(-45, -5, -35);
	
	public static PollutionData dead_trees_pollution = new PollutionData(1750000, 250000, 150000);
	
	public static PollutionData no_trees_pollution = new PollutionData(2750000, 700000, 310000);
	
	public static float diffusion_factor = 0.0001F;
	
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
			
			min_ps = config.getInt("MinPollutionToSpread", "POLLUTION", 10000, 10, Integer.MAX_VALUE, "", lang("pollution.min_ps"));
			
			String bone_pollution = config.getString("BonemealPollution", "POLLUTION", new PollutionData(0.05, 0.15, 0.5).toString(), "Bonemeal usage pollution", lang("pollution.bonemeal"));
			
			bonemeal_pollution = EMUtils.pollutionDataFromJSON(bone_pollution, new PollutionData(0.05, 0.15, 0.5), "Failed to get BonemealPollution property from config! Invalid JSON syntax!");
			
			cached_pollution_radius = config.getInt("CachedPollutionRadius", "CLIENT", 5, 1, EMConsts.max_cached_pollution_radius, "", lang("client.max_cpr"));
			
			check_client_pollution = config.getBoolean("CheckClientPollution", "CLIENT", true, "Determines whether the pollution data received from the server should be validated. When unabled the 'client' performance could be improved but the EcologyMod client part might be destabilized! Thus it is not recommended!", lang("client.shouldcheck"));
			
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
