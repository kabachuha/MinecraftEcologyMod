package ecomod.common.pollution.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.jna.platform.unix.X11.GC;

import ecomod.common.pollution.config.PollutionEffectsConfig.Effects;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class PollutionConfigManager
{
	String update_json_url = "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.12/config/pollution_config.json";
	
	/** update.properties
	 * 
	 * 	update_json_url=%update_json_url%
	 * 	TEPollutionConfig_update_to=version-1.3 / latest
	 * 	PollutionSourcesConfig_update_to=url-XXX|keep_entries
	 */
	private Properties update_props;
	private final File pollution_sources;
	private final File pollution_effects;
	private final File pollution_tiles;
	private final File props_file;
	
	private final File working_dir;
	
	private static Comparator<String> version_comparator;
	
	public PollutionConfigManager(File directory)
	{
		working_dir = directory;
		
		pollution_sources = new File(directory, "PollutionSources.json");
		pollution_effects = new File(directory, "PollutionEffects.json");
		pollution_tiles = new File(directory, "TEPollutionConfig.json");
		
		update_props = new Properties();
		props_file = new File(directory, "update.properties");
		if(props_file.exists())
		{
			try {
				update_props.load(new FileInputStream(props_file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		version_comparator = (a, b)->{return new ComparableVersion(a).compareTo(new ComparableVersion(b))  * -1;};
	}
	
	/** Config update json
	 * 	{
	 * 		"%MODVERSION%":
	 * 			-"PollutionSources":
	 * 				[
	 * 					{
	 * 						-version
	 * 						-url
	 * 					},
	 * 				]
	 * 	}
	 */
	private static class PollutionConfigUpdateJson
	{
		ModversionEntry modversions[];
		
		private static class ModversionEntry
		{
			String version;
			Map<String, String> TEPollutionConfig;//version-url
			Map<String, String> PollutionSources;
			Map<String, String> PollutionEffects; 
		}
		
		public static PollutionConfigUpdateJson read(String json)
		{
			if(Strings.isNullOrEmpty(json))
				return null;
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
			PollutionConfigUpdateJson ret = new PollutionConfigUpdateJson();
			
			ret = gson.fromJson(json, PollutionConfigUpdateJson.class);
			
			if(ret == null || ret.modversions == null || ret.modversions.length == 0)
				return null;
			
			List<String> version_organization = new ArrayList<String>();
			HashMap<String, String> temp_map = new HashMap<>();
			
			for(int m = 0; m < ret.modversions.length; m++)
			{
				ModversionEntry me = ret.modversions[m];
				
				version_organization = new ArrayList<String>(me.TEPollutionConfig.keySet());
				version_organization.sort(version_comparator);
				for(String s : version_organization)
					temp_map.put(s, me.TEPollutionConfig.get(s));
				ret.modversions[m].TEPollutionConfig = new HashMap<String, String>(temp_map);
				version_organization.clear();
				temp_map.clear();
				
				version_organization = new ArrayList<String>(me.PollutionSources.keySet());
				version_organization.sort(version_comparator);
				for(String s : version_organization)
					temp_map.put(s, me.PollutionSources.get(s));
				ret.modversions[m].PollutionSources = new HashMap<String, String>(temp_map);
				version_organization.clear();
				temp_map.clear();
				
				version_organization = new ArrayList<String>(me.PollutionEffects.keySet());
				version_organization.sort(version_comparator);
				for(String s : version_organization)
					temp_map.put(s, me.PollutionEffects.get(s));
				ret.modversions[m].PollutionEffects = new HashMap<String, String>(temp_map);
				version_organization.clear();
				temp_map.clear();
			}
			
			return ret;
		}
		
		public void save(File file)
		{
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().serializeNulls().create();
			
			String json = gson.toJson(this);
			
			try {
				FileUtils.write(file, json, Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public List<String> getAvaibleModversions()
		{
			List<String> ret = new ArrayList<>();
			for(ModversionEntry me : modversions)
				ret.add(me.version);
			return ret;
		}
		
		public List<ArtifactVersion> getAvaibleModversionsAsArtifacts()
		{
			List<ArtifactVersion> ret = new ArrayList<>();
			for(ModversionEntry me : modversions)
				ret.add(new DefaultArtifactVersion(me.version));
			
			ret.sort(null);
			
			return ret;
		}
		
		public ModversionEntry getModversionData(ArtifactVersion version)
		{
			for(ModversionEntry me : modversions)
				if(me.version.equals(version.toString()))
					return me;
			
			return null;
		}
	}
	
	public void doInitStuff()
	{
		setupAbsentProperties();
		
		ProgressManager.ProgressBar bar = ProgressManager.push("Configuring EcologyMod", 4, true);
		
		bar.step("Downloading config update json");
		
		PollutionConfigUpdateJson json = PollutionConfigUpdateJson.read(getConfigUpdateJson());
		
		ArtifactVersion ecomod_version = new DefaultArtifactVersion(EMConsts.version);
		
		if(json != null && json.getAvaibleModversions().size() > 0)
			ecomod_version = get_closest_modversion(json , ecomod_version);
		else
		{
			EcologyMod.log.error("Failed to get config update json!");
			EcologyMod.log.error("Trying to load config from files");
		}
		
		String update = update_props.getProperty("TEPollutionConfig_update_to");
		
		bar.step("Loading TEPollutionConfig");
		EcologyMod.instance.tepc = new TEPollutionConfig();
		
		boolean filed = false;
		
		if(update.startsWith("url"))
		{
			EcologyMod.instance.tepc.load(pollution_tiles.getAbsolutePath(), update.substring(update.indexOf('-')+1, update.indexOf('|') == -1 ? update.length() : update.indexOf('|')), keep_entries(update));
			
			if(EcologyMod.instance.tepc.data.size() == 0)
			{
				EcologyMod.log.error("Failed to get TEPollutionConfig from the specified URL "+update);
				
				filed = true;
			}
		}
		else
		{
			if(json == null || update.startsWith("null"))
			{
				filed = true;
			}
			
			if(!filed && json != null)
			{
				String req_version = "";
				
				Map<String, String> tepcs = json.getModversionData(ecomod_version).TEPollutionConfig;
				
				if(update.startsWith("version"))
				{
					req_version = update.substring("version-".length(), update.indexOf('|') == -1 ? update.length() : update.indexOf('|'));
					if(!tepcs.containsKey(req_version))
						throw new IllegalArgumentException("The specified version("+req_version+") of TEPollutionConfig had't been found in the config update json! Change the TEPollutionConfig_update_to in ecomod/update.properties");
				}
				
				if(req_version.length() == 0)
				{
					EcologyMod.log.warn("Downloading the latest version of TEPollutionConfig!");
					req_version = new ArrayList<String>(tepcs.keySet()).get(0);
				}
				
				EcologyMod.log.warn("Downloading the TEPollutionConfig version "+req_version);
				
				EcologyMod.instance.tepc.load(pollution_tiles.getAbsolutePath(), tepcs.get(req_version), keep_entries(update));
			}
		}
		
		if(filed)
		{
			EcologyMod.log.error("Trying to load TEPollutionConfig from file");
			if(!pollution_tiles.exists())
			{
				throw new NullPointerException("Unable to find TEPollutionConfig file("+pollution_tiles.getAbsolutePath()+")! EcologyMod won't work correctly! Try to get it manually from the GitHub repository and put it to this location.");
			}
			else
				EcologyMod.instance.tepc.loadFromFile(pollution_tiles.getAbsolutePath());
		}
		
		update = update_props.getProperty("PollutionSources_update_to");
		
		bar.step("Loading PollutionSources");
		PollutionSourcesConfig sources_cfg = new PollutionSourcesConfig();
		
		filed = false;
		
		if(update.startsWith("url"))
		{
			sources_cfg.load(pollution_sources.getAbsolutePath(), update.substring(update.indexOf('-')+1, update.indexOf('|') == -1 ? update.length() : update.indexOf('|')), keep_entries(update));
			
			if(sources_cfg.pollution_sources.size() == 0)
			{
				EcologyMod.log.error("Failed to get PollutionSources from the specified URL "+update);
				
				filed = true;
			}
		}
		else
		{
			if(json == null || update.startsWith("null"))
			{
				filed = true;
			}
			
			if(!filed && json != null)
			{
				String req_version = "";
				
				Map<String, String> tepcs = json.getModversionData(ecomod_version).PollutionSources;
				
				if(update.startsWith("version"))
				{
					req_version = update.substring("version-".length(), update.indexOf('|') == -1 ? update.length() : update.indexOf('|'));
					if(!tepcs.containsKey(req_version))
						throw new IllegalArgumentException("The specified version("+req_version+") of PollutionSourcesConfig had't been found in the config update json! Change the PollutionSources_update_to in ecomod/update.properties");
				}
				
				if(req_version.length() == 0)
				{
					EcologyMod.log.warn("Downloading the latest version of PollutionSources!");
					req_version = new ArrayList<String>(tepcs.keySet()).get(0);
				}
				
				EcologyMod.log.warn("Downloading the PollutionSourcesConfig version "+req_version);
				
				sources_cfg.load(pollution_sources.getAbsolutePath(), tepcs.get(req_version), keep_entries(update));
			}
		}
		
		if(filed)
		{
			EcologyMod.log.error("Trying to load PollutionSources from file");
			if(!pollution_sources.exists())
			{
				throw new NullPointerException("Unable to find PollutionSourcesConfig file("+pollution_sources.getAbsolutePath()+")! EcologyMod won't work correctly! Try to get it manually from the GitHub repository and put it to this location.");
			}
			else
				sources_cfg.loadFromFile(pollution_sources.getAbsolutePath());
		}
		
		sources_cfg.pushToApi();
		
		update = update_props.getProperty("PollutionEffects_update_to");
		
		bar.step("Loading PollutionEffects");
		PollutionEffectsConfig effects_cfg = new PollutionEffectsConfig();
		
		filed = false;
		
		if(update.startsWith("url"))
		{
			effects_cfg.load(pollution_effects.getAbsolutePath(), update.substring(update.indexOf('-')+1, update.indexOf('|') == -1 ? update.length() : update.indexOf('|')), keep_entries(update));
			
			if(effects_cfg.effects.size() == 0)
			{
				EcologyMod.log.error("Failed to get PollutionEffects from the specified URL "+update);
				
				filed = true;
			}
		}
		else
		{
			if(json == null || update.startsWith("null"))
			{
				filed = true;
			}
			
			if(!filed && json != null)
			{
				String req_version = "";
				
				Map<String, String> tepcs = json.getModversionData(ecomod_version).PollutionEffects;
				
				if(update.startsWith("version"))
				{
					req_version = update.substring("version-".length(), update.indexOf('|') == -1 ? update.length() : update.indexOf('|'));
					if(!tepcs.containsKey(req_version))
						throw new IllegalArgumentException("The specified version("+req_version+") of PollutionEffectsConfig had't been found in the config update json! Change the PollutionEffects_update_to in ecomod/update.properties");
				}
				
				if(req_version.length() == 0)
				{
					EcologyMod.log.warn("Downloading the latest version of PollutionEffectsConfig!");
					req_version = new ArrayList<String>(tepcs.keySet()).get(0);
				}
				
				EcologyMod.log.warn("Downloading the PollutionSourcesConfig version "+req_version);
				
				effects_cfg.load(pollution_effects.getAbsolutePath(), tepcs.get(req_version), keep_entries(update));
			}
		}
		
		if(filed)
		{
			EcologyMod.log.error("Trying to load PollutionEffectsConfig from file");
			if(!pollution_effects.exists())
			{
				throw new NullPointerException("Unable to find PollutionEffectsConfig file("+pollution_effects.getAbsolutePath()+")! EcologyMod won't work correctly! Try to get it manually from the GitHub repository and put it to this location.");
			}
			else
				effects_cfg.loadFromFile(pollution_effects.getAbsolutePath());
		}
		
		effects_cfg.pushToApi();
		
		ProgressManager.pop(bar);
		
		//makeDebugJson();
	}
	
	public void makeDebugJson()
	{
		PollutionConfigUpdateJson json = new PollutionConfigUpdateJson();
		
		json.modversions = new PollutionConfigUpdateJson.ModversionEntry[1];
		json.modversions[0] = new PollutionConfigUpdateJson.ModversionEntry();
		json.modversions[0].version = EMConsts.version;
		json.modversions[0].PollutionEffects = new HashMap<>();
		json.modversions[0].PollutionEffects.put("1.0", "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/PollutionEffects.json");
		json.modversions[0].PollutionEffects.put("1.1", "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.12/PollutionEffects.json");
		json.modversions[0].PollutionSources = new HashMap<>();
		json.modversions[0].PollutionSources.put("1.0", "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/PollutionSources.json");
		json.modversions[0].PollutionSources.put("1.1", "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.12/PollutionSources.json");
		json.modversions[0].TEPollutionConfig = new HashMap<>();
		json.modversions[0].TEPollutionConfig.put("1.0", "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.11/TEPC.json");
		json.modversions[0].TEPollutionConfig.put("1.1", "https://raw.githubusercontent.com/Artem226/MinecraftEcologyMod/1.12/TEPC.json");
		
		json.save(new File(working_dir, "DEBUG.json"));
	}
	
	private void setupAbsentProperties()
	{
		update_props.putIfAbsent("update_json_url", update_json_url);
		update_props.putIfAbsent("TEPollutionConfig_update_to", "latest");
		update_props.putIfAbsent("PollutionSources_update_to", "latest");
		update_props.putIfAbsent("PollutionEffects_update_to", "latest");
		
		try {
			update_props.store(new FileOutputStream(props_file), "https://github.com/Artem226/MinecraftEcologyMod/wiki/Configuration");
		} catch (IOException e) {
			EcologyMod.log.error("Unable to save update properties file!!!");
			e.printStackTrace();
		}
	}
	
	private String getConfigUpdateJson()
	{
		update_json_url = EMUtils.parseMINECRAFTURL(update_props.getProperty("update_json_url"));
		EcologyMod.log.info("Receiving configuration update json from "+update_json_url);
		
		try
		{
			return EMUtils.getString(new URL(update_json_url));
		}
		catch (MalformedURLException e)
		{
			EcologyMod.log.error("Pollution config update URL is incorrect! Go to the mod config directory(config/ecomod/update.properties) and fix it!");
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		catch (JsonSyntaxException e)
		{
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	private boolean keep_entries(String cfg_info)
	{
		boolean ret = cfg_info.indexOf('|') == -1 ? false : (cfg_info.substring(cfg_info.indexOf('|')+1).equalsIgnoreCase("keep_entries"));
		if(ret)
			EcologyMod.log.info("~Keeping previous config entries!");
		return ret;
	}
	
	private ArtifactVersion get_closest_modversion(PollutionConfigUpdateJson json, ArtifactVersion modversion)
	{
		List<ArtifactVersion> modversions = json.getAvaibleModversionsAsArtifacts(); 
		if(modversions.contains(modversion))
			return modversion;
		
		return modversions.get(modversions.size()-1);//As modversions had been sorted
	}
}
