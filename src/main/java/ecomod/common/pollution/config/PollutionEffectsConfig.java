package ecomod.common.pollution.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.config.TEPollutionConfig.TEPollution;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PollutionEffectsConfig
{
	//Version format:
	//(Config version) + "-" + (mod version config is compatible with)
	
	public String version = "1.0-"+EMConsts.version;
	
	public List<IAnalyzerPollutionEffect> effects = new ArrayList<IAnalyzerPollutionEffect>();

	public void pushToApi()
	{
		for(IAnalyzerPollutionEffect iape : effects)
		{
			EcomodAPI.addAnalyzerPollutionEffect(iape);
		}
	}
	
	public static boolean isEffectPresent(String effect_id)
	{
		return EcomodStuff.pollution_effects.containsKey(effect_id);
	}
	
	public static boolean isEffectActive(String id, PollutionData chunk_pd)
	{
		if(chunk_pd != null && !chunk_pd.isEmpty())
		if(EcomodStuff.pollution_effects.containsKey(id))
		{
			IAnalyzerPollutionEffect iape = EcomodStuff.pollution_effects.get(id);
			
			if(iape != null)
			{
				if(iape.getTriggeringType() == TriggeringType.AND)
				{
					return chunk_pd.compareTo(iape.getTriggerringPollution()) >= 0;
				}
				else if(iape.getTriggeringType() == TriggeringType.OR)
				{
					return chunk_pd.compareOR(iape.getTriggerringPollution()) >= 0;
				}
			}
		}
		return false;
	}
	
	public boolean loadFromFile(String cfg_path)
	{
		EcologyMod.log.info("Trying to load PollutionEffects from file");
		
		Gson gson = new GsonBuilder().create();
		
		String json;
		
		Effects t;
		
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
			t = gson.fromJson(json, Effects.class);
		}
		catch(JsonSyntaxException e)
		{
			EcologyMod.log.error("PollutionEffects.json has incorrect JSON syntax!!! Please, fix it! (see https://en.wikipedia.org/wiki/JSON)");
			e.printStackTrace();
			return false;
		}
		
		if(t == null)
			return false;
		
		this.version = t.getVersion();
		this.effects = new ArrayList<IAnalyzerPollutionEffect>();
		
		if(t.getEffects() != null)
		for(AnalyzerPollutionEffect ape : t.getEffects())
			effects.add(ape);
		
		return true;
	}
	
	public boolean save(String cfg_path)
	{
		File f = new File(cfg_path);
		
		EcologyMod.log.info("Saving PollutionEffects.json");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		
		Effects t = new Effects(version, effects.toArray(new IAnalyzerPollutionEffect[effects.size()]));
		
		String json = gson.toJson(t, Effects.class);
		
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
				throw new IOException("The PollutionEffects file is not writable!!!");
			}
		}
		catch(IOException e)
		{
			EcologyMod.log.error("Unable to write PollutionEffects!");
			EcologyMod.log.error(e.toString());
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static PollutionEffectsConfig get(String urlstr)
	{
		EcologyMod.log.info("Getting PollutionEffects from "+urlstr);
		
		urlstr = EMUtils.parseMINECRAFTURL(urlstr);
		
		String json;
		
		try
		{
			URL url = new URL(urlstr);
			
			json = EMUtils.getString(url);
			
			Gson gson = new GsonBuilder().create();
			
			Effects t = gson.fromJson(json, Effects.class);
			
			PollutionEffectsConfig pec = new PollutionEffectsConfig();
			
			pec.effects = new ArrayList<IAnalyzerPollutionEffect>();
			
			if(t.getEffects() != null)
				for(AnalyzerPollutionEffect ape : t.getEffects())
					pec.effects.add(ape);
			
			pec.version = t.getVersion();
			
			return pec;
		}
		catch (MalformedURLException e)
		{
			EcologyMod.log.error("The URL of PollutionEffects is incorrect! Go to the mod config file ("+EMConsts.modid+".cfg) and fix it! (see https://en.wikipedia.org/wiki/URL#Syntax) Don't forget to restart Minecraft later.");
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
	
	public void load(String cfg_path, String url, boolean keep_entries, boolean force_update)
	{
		EcologyMod.log.info("Loading PollutionEffects");
		
		boolean loaded_from_file = loadFromFile(cfg_path);
		
		PollutionEffectsConfig pec = get(url);
		
		if(pec == null)
		{
			if(!loaded_from_file)
			{
				//Crash MC
				throw new NullPointerException("Impossible to load the PollutionEffects for the first time! Look for the reason in the log! If TEPC is located remotely make sure you have connection to the resource! URL ("+url+ ')');
			}
		}
		else
		{
			if(loaded_from_file)
			{
				if(force_update || !version.equals(pec.version))
				{
					if(keep_entries)
					{
						EMUtils.mergeLists(pec.effects, effects);
					}
				
					effects = pec.effects;
					version = pec.version;
				}
			}
			else
			{
				effects = pec.effects;
				version = pec.version;
			}
		}
		
		EcologyMod.log.info("Loaded "+effects.size()+" pollution effects");
		
		if(!save(cfg_path))
		{
			EcologyMod.log.error("Unable to save PollutionEffects as PollutionEffects.json in "+cfg_path+"! It will very likely have serious problems later!!");
			EcologyMod.log.error("Look for the reason in the log!");
		}
	}
	
	public static class Effects
	{
		private String version;
		
		private AnalyzerPollutionEffect[] effects;
		
		public Effects()
		{
			
		}
		
		public Effects(String v, AnalyzerPollutionEffect[] effcts)
		{
			version = v;
			effects = effcts;
		}
		
		public Effects(String v, IAnalyzerPollutionEffect[] effcts)
		{
			version = v;
			AnalyzerPollutionEffect[] npefs = new AnalyzerPollutionEffect[effcts.length];
			for(int i = 0; i < npefs.length; i++)
			{
				npefs[i] = new AnalyzerPollutionEffect(effcts[i]);
			}
			
			effects = npefs;
		}
		
		public AnalyzerPollutionEffect[] getEffects()
		{
			return effects;
		}
		
		public String getVersion()
		{
			return version;
		}
	}
	
	
}
