package ecomod.common.pollution.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.config.TEPollutionConfig.TEPollution;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PollutionSourcesConfig
{
	public String version;
	
	public List<String> blacklisted_items;
	
	public Map<String, PollutionData> polluting_items;
	
	public Map<String, PollutionData> pollution_sources;
	
	public Map<String, PollutionData> smelted_items_pollution;

	public PollutionSourcesConfig()
	{
		version = "1.0-"+EMConsts.version;
		blacklisted_items = new ArrayList<String>();
		polluting_items = new HashMap<String, PollutionData>();
		pollution_sources = new HashMap<String, PollutionData>();
		smelted_items_pollution = new HashMap<String, PollutionData>();
	}
	
	public void pushToApi()
	{
		if(EcomodStuff.blacklisted_items == null)
			EcomodStuff.blacklisted_items = new ArrayList<String>();
		if(EcomodStuff.polluting_items == null)
			EcomodStuff.polluting_items = new HashMap<String, PollutionData>();
		if(EcomodStuff.pollution_sources == null)
			EcomodStuff.pollution_sources = new HashMap<String, PollutionData>();
		if(EcomodStuff.smelted_items_pollution == null)
			EcomodStuff.smelted_items_pollution = new HashMap<String, PollutionData>();
		
		
		if(blacklisted_items != null)
			EcomodStuff.blacklisted_items.addAll(blacklisted_items);
		if(polluting_items   != null)
			EcomodStuff.polluting_items.putAll(polluting_items);
		if(pollution_sources != null)
			EcomodStuff.pollution_sources.putAll(pollution_sources);
		if(smelted_items_pollution != null)
			EcomodStuff.smelted_items_pollution.putAll(smelted_items_pollution);
	}
	
	public static PollutionData getSource(String id)
	{
		if(EcomodStuff.pollution_sources == null)
			return null;
		
		if(EcomodStuff.pollution_sources.containsKey(id))
			return EcomodStuff.pollution_sources.get(id).clone();
		
		return null;
	}
	
	public static boolean hasSource(String id)
	{
		return EcomodStuff.pollution_sources.containsKey(id);
	}
	
	public static PollutionData getItemPollution(String item, String metastring)
	{
		if(item == null)
			return PollutionData.getEmpty();
		
		if(!EcomodStuff.blacklisted_items.contains(item) && !EcomodStuff.blacklisted_items.contains(item+metastring))
		{
			if(EcomodStuff.polluting_items.containsKey(item))
			{
				return EcomodStuff.polluting_items.get(item).clone();
			}
			else if(EcomodStuff.polluting_items.containsKey(item+metastring))
			{
				return EcomodStuff.polluting_items.get(item+metastring).clone();
			}
			else
				return getSource("expired_item");
		}
		else
			return PollutionData.getEmpty();
	}
	
	public static PollutionData getItemPollution(ItemStack item)
	{
		if(item == null)return null;
		
		return getItemPollution(GameRegistry.findUniqueIdentifierFor(item.getItem()).toString(), "@"+item.getItemDamage());
	}
	
	public static PollutionData getItemPollution(Item item, int meta)
	{
		if(item == null)return null;
		
		return getItemPollution(GameRegistry.findUniqueIdentifierFor(item).toString(), "@"+meta);
	}
	
	public static PollutionData getSmeltedItemPollution(String item, String meta)
	{
		if(item == null)
			return PollutionData.getEmpty();
		
		if(EcomodStuff.smelted_items_pollution == null)
			return getSource("default_smelted_item_pollution");
		
		if(EcomodStuff.smelted_items_pollution.containsKey(item))
		{
			return EcomodStuff.smelted_items_pollution.get(item).clone();
		}
		else if(EcomodStuff.smelted_items_pollution.containsKey(item+meta))
		{
			return EcomodStuff.smelted_items_pollution.get(item+meta).clone();
		}
		else
			return getSource("default_smelted_item_pollution");
	}
	
	public static PollutionData getSmeltedItemPollution(ItemStack item)
	{
		if(item == null)return null;
		
		return getSmeltedItemPollution(GameRegistry.findUniqueIdentifierFor(item.getItem()).toString(), "@"+item.getItemDamage());
	}
	
	public static PollutionData getSmeltedItemPollution(Item item, int meta)
	{
		if(item == null)return null;
		
		return getSmeltedItemPollution(GameRegistry.findUniqueIdentifierFor(item).toString(), "@"+meta);
	}
	
	public static PollutionData getSmeltedItemStackPollution(ItemStack is)
	{
		if(is == null)return null;
		
		PollutionData ret = getSmeltedItemPollution(is).clone();
		
		ret.multiplyAll(is.stackSize);
		
		return ret;
	}
	
	public static PollutionData getItemStackPollution(ItemStack is)
	{
		if(is == null)return null;
		
		PollutionData ret = getItemPollution(is).clone();
		
		ret.multiplyAll(is.stackSize);
		
		return ret;
	}
	
	public boolean save(String cfg_path)
	{
		File f = new File(cfg_path);
		
		EcologyMod.log.info("Saving PollutionSources.json");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Sources s = new Sources(version, pollution_sources, blacklisted_items, polluting_items, smelted_items_pollution, protected_entries);
		
		String json = gson.toJson(s, Sources.class);
		
		try
		{	
			if(f.isDirectory())
			{
				f.delete();
			}
			
			if(!f.exists())
			{
				f.createNewFile();
			}
			
			if(f.canWrite())
			{
				FileUtils.writeStringToFile(f, json, Charset.defaultCharset());
				return true;
			}
			else
			{
				throw new IOException("The PollutionSources file is not writable!!!");
			}
		}
		catch(IOException e)
		{
			EcologyMod.log.error("Unable to write PollutionSources!");
			EcologyMod.log.error(e.toString());
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static PollutionSourcesConfig get(String urlstr)
	{
		EcologyMod.log.info("Getting PollutionSources from "+urlstr);
		
		urlstr = EMUtils.parseMINECRAFTURL(urlstr);
		
		String json;
		
		try
		{
			URL url = new URL(urlstr);
			
			EcologyMod.log.info(url.toString());
			
			json = EMUtils.getString(url);
			
			Gson gson = new GsonBuilder().create();
			
			Sources t = gson.fromJson(json, Sources.class);
			
			PollutionSourcesConfig pec = new PollutionSourcesConfig();
			
			pec.blacklisted_items = t.blacklisted_items;
			pec.polluting_items = Sources.StrPDListToMap(t.custom_item_pollution);
			pec.pollution_sources = Sources.StrPDListToMap(t.sources);
			pec.smelted_items_pollution = Sources.StrPDListToMap(t.smelted_item_pollution);
			pec.version = t.version;
			
			return pec;
		}
		catch (MalformedURLException e)
		{
			EcologyMod.log.error("The URL of PollutionSources is incorrect! Go to the mod config file ("+EMConsts.modid+".cfg) and fix it! (see https://en.wikipedia.org/wiki/URL#Syntax) Don't forget to restart Minecraft later.");
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//TODO add message
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		catch (JsonSyntaxException e)
		{
			//TODO add message
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}

		return null;
	}
	
	private List<String> protected_entries = null;
	
	public boolean loadFromFile(String cfg_path)
	{		
		EcologyMod.log.info("Trying to load PollutionSources from file");
		
		Gson gson = new GsonBuilder().create();
		
		String json;
		
		Sources t;
		
		try
		{
			File f = new File(cfg_path);
			
			if(f.isDirectory())
			{
				f.delete();
				return false;
			}
			
			if(!f.exists())
				return false;
			
			if(f.canRead())
			{
				json = FileUtils.readFileToString(f, Charset.defaultCharset());
				
				if(json == null)
					return false;
			}
			else
			{
				throw new IOException("The file is not readable!!!");
			}
		}
		catch (IOException e)
		{
			EcologyMod.log.error(e.toString());
			
			e.printStackTrace();
			
			return false;
		}
		
		try
		{
			t = gson.fromJson(json, Sources.class);
		}
		catch(JsonSyntaxException e)
		{
			EcologyMod.log.error("PollutionSources.json has incorrect JSON syntax!!! Please, fix it! (see https://en.wikipedia.org/wiki/JSON)");
			e.printStackTrace();
			return false;
		}
		
		if(t == null)
			return false;
		
		protected_entries = new ArrayList<String>();
		
		for(Sources.StrPD str : t.sources)
			if(str.keep_entry != null && str.keep_entry == true)
				protected_entries.add(str.id);
		
		blacklisted_items = t.blacklisted_items;
		polluting_items = Sources.StrPDListToMap(t.custom_item_pollution);
		pollution_sources = Sources.StrPDListToMap(t.sources);
		smelted_items_pollution = Sources.StrPDListToMap(t.smelted_item_pollution);
		version = t.version;
		
		EcologyMod.log.info("Loaded PollutionSources from file");
		
		return true;
	}
	
	public void load(String cfg_path, String url, boolean keep_entries, boolean force_update)
	{
		EcologyMod.log.info("Loading PollutionSources");
		
		boolean loaded_from_file = loadFromFile(cfg_path);
		
		PollutionSourcesConfig pec = get(url);
		
		if(pec == null)
		{
			if(!loaded_from_file)
			{
				//Crash MC
				throw new NullPointerException("Impossible to load the PollutionSources for the first time! Look for the reason in the log! If TEPC is located remotely make sure you have connection to the resource! URL ("+url+ ')');
			}
		}
		else
		{
			if(loaded_from_file)
			{
				if(force_update || !version.equals(pec.version))
				{
					if(protected_entries != null)
						for(String p : protected_entries)
							if(pec.pollution_sources.containsKey(p))
							{
								pec.pollution_sources.replace(p, pollution_sources.get(p));
							}
				
					if(keep_entries)
					{
						EMUtils.mergeMaps(pec.polluting_items, polluting_items);
						EMUtils.mergeMaps(pec.pollution_sources, pollution_sources);
						EMUtils.mergeMaps(pec.smelted_items_pollution, smelted_items_pollution);
						EMUtils.mergeLists(pec.blacklisted_items, blacklisted_items);
					}
				
					blacklisted_items = pec.blacklisted_items;
					polluting_items = pec.polluting_items;
					pollution_sources = pec.pollution_sources;
					smelted_items_pollution = pec.smelted_items_pollution;
					version = pec.version;
				}
			}
			else
			{
				blacklisted_items = pec.blacklisted_items;
				polluting_items = pec.polluting_items;
				pollution_sources = pec.pollution_sources;
				smelted_items_pollution = pec.smelted_items_pollution;
				version = pec.version;
			}
		}
		
		EcologyMod.log.info("Loaded "+pollution_sources.size()+" pollution sources");
		
		if(!save(cfg_path))
		{
			EcologyMod.log.error("Unable to save PollutionSources as PollutionSources.json in "+cfg_path+"! It will very likely have serious problems later!!");
			EcologyMod.log.error("Look for the reason in the log!");
		}
	}
	
	private static class Sources
	{
		String version;
		
		List<StrPD> sources;
		
		List<String> blacklisted_items;
		List<StrPD> custom_item_pollution;
		
		List<StrPD> smelted_item_pollution;
		
		public Sources()
		{
			version = "1.0-"+EMConsts.version;
			sources = new ArrayList<StrPD>();
			blacklisted_items = new ArrayList<String>();
			custom_item_pollution = new ArrayList<StrPD>();
			smelted_item_pollution = new ArrayList<StrPD>();
		}
		
		public Sources(String v, List<StrPD> src, List<String> blacklisted, List<StrPD> item_pollution, List<StrPD> smelted)
		{
			version = v;
			sources = src;
			blacklisted_items = blacklisted;
			custom_item_pollution = item_pollution;
			smelted_item_pollution = smelted;
		}
		
		public Sources(String v, Map<String, PollutionData> src, List<String> blacklisted, Map<String, PollutionData> item_pollution, Map<String, PollutionData> smelted, List<String> protected_entries)
		{
			version = v;
			if(src != null)
				sources = mapToStrPDList(src, protected_entries);
			if(blacklisted != null)
				blacklisted_items = blacklisted;
			if(item_pollution != null)
				custom_item_pollution = mapToStrPDList(item_pollution);
			if(smelted != null)
				smelted_item_pollution = mapToStrPDList(smelted);
		}
		
		static class StrPD
		{
			String id;
			PollutionData pollution;
			Boolean keep_entry = null;
			
			public StrPD()
			{
				
			}
			
			public StrPD(String k, PollutionData v, boolean keep)
			{
				id = k;
				pollution = v;
				
				if(keep)
					keep_entry = new Boolean(true);
			}
		}
		
		static List<StrPD> mapToStrPDList(Map<String, PollutionData> map, List<String> protected_entries)
		{
			List<StrPD> ret = new ArrayList<StrPD>();
			
			for(Entry<String, PollutionData> e : map.entrySet())
			{
				ret.add(new StrPD(e.getKey(), e.getValue(), protected_entries != null && protected_entries.contains(e.getKey())));
			}
			
			return ret;
		}
		
		static List<StrPD> mapToStrPDList(Map<String, PollutionData> map)
		{
			return mapToStrPDList(map, null);
		}
		
		static Map<String, PollutionData> StrPDListToMap(List<StrPD> list)
		{
			Map<String, PollutionData> ret = new HashMap<String, PollutionData>();
			
			for(StrPD pd : list)
			{
				ret.put(pd.id, pd.pollution);
			}
			
			return ret;
		}
	}
}