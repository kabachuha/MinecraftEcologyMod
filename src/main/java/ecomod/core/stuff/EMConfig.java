package ecomod.core.stuff;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

import ecomod.api.pollution.PollutionData;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraftforge.common.config.Configuration;

public class EMConfig
{
	public static Configuration config;
	
	
	public static int[] allowedDims = new int[]{0};
	
	public static boolean wptimm = false;
	
	public static int wptcd = 180;
	
	public static String tepcURL = /*"https://downloader.disk.yandex.ru/disk/d1c1dc1dcff5a58f703a76434c01a3c91642048ce1627af7d932b34e019b99e2/593ea3e1/JstuHvxzsJJZTMsLJiY3kWBEYCgRf6hkJvH7SpEQz2vrbj2aYRcB6LYhr3PNMOxzZKiLMCFcEAgz72_sqkJIpQ%3D%3D?uid=168863970&filename=tepc.json&disposition=attachment&hash=&limit=0&content_type=application%2Fjson&fsize=438&hid=5549cb99a5d14d585159ba6466385ae3&media_type=unknown&tknv=v2&etag=7b782ded405ecc8889bc5516d3910710";*/"file:///<MINECRAFT>/tepc.json";
	
	public static float filtmult = 0.92F;
	
	public static PollutionData pp2di = new PollutionData(0,1,1);
	
	public static List<String> item_blacklist = new ArrayList<String>();
	
	public static int wpr = 2;
	
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
			
			wptimm = config.getBoolean("ImmediateStart", "THREAD", false, "Whether the thread starts without delay", lang("thread.wptimm"));
			
			wptcd = config.getInt("Delay", "THREAD", 180, 60, 3600, "The delay between thread runs.", lang("thread.wptcd"));
			
			tepcURL = config.getString("TEPC_URL", "CORE", "file:///<MINECRAFT>/tepc.json", "A URL to the TEPollutionConfig. See format at https://en.wikipedia.org/wiki/URL. If the TEPC is remotely located you should have a connection to its location!  If you point a local file you can type <MINECRAFT> instead of a path to the Minecraft directory (like this 'file:///<MINECRAFT>/tepc.json').  When you are playing on a server you will use its TEPC.", lang("tepc.url"));
			
			filtmult = config.getFloat("FilterMultiplier", "POLLUTION", 0.92F, 0, 1, "Filter pollution multiplier.", lang("pollution.filter"));
			
			wpr = config.getInt("WaterPollutionRadius", "POLLUTION", 2, 0, 128, "", lang("pollution.wpr"));
			
			String ppdi = config.getString("PollutionPerDecayedItem", "POLLUTION", new PollutionData(0,1,1).toString(), "Default pollution emission per stack of decayed items", lang("pollution.ppdi"));
			
			Gson gson = new GsonBuilder().create();
			
			try
			{
				pp2di = gson.fromJson(ppdi, PollutionData.class);
			}
			catch (JsonSyntaxException e)
			{
				EcologyMod.log.error("Failed to get PollutionPerStackOfDecayedItems property from config! Invalid JSON syntax!");
				pp2di = new PollutionData(0,1,1);
			}
			
			String ib[] = config.getStringList("BlacklistedItems", "POLLUTION", new String[]{"minecraft:apple", "minecraft:stick", "minecraft:mushroom_stew", "minecraft:string", "minecraft:feather", "minecraft:gunpowder", "minecraft:wheat", "minecraft:wheat_seeds", "minecraft:porkchop", "minecraft:snowball", "minecraft:leather", "minecraft:reeds", "minecraft:slime_ball", "minecraft:egg", "minecraft:fish", "minecraft:sugar", "minecraft:melon", "minecraft:pumpkin_seeds", "minecraft:melon_seeds", "minecraft:beef", "minecraft:chicken", "minecraft:carrot", "minecraft:potato", "minecraft:rabbit", "minecraft:mutton", "minecraft:chorus_fruit", "minecraft:beetroot", "minecraft:beetroot_seeds"}, "");
			
			item_blacklist.addAll(Arrays.asList(ib));
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
