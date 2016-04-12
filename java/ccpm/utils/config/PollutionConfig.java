package ccpm.utils.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager.ChunksPollution;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;

public class PollutionConfig {

	public PollutionConfig() {
		// TODO Auto-generated constructor stub
	}

	
	public static PollutionProp cfg;
	
	public static void load(String dir)
	{
		CCPM.log.info("Loading pollution config");
           String json = null;
           boolean shouldUpdate = true;
           File file = new File(dir+"//PollutionConfig.json");
			try
			{
				if(file.isDirectory())
				{
					throw new IOException("File PollutionConfig.json is a directory! Please, delete it and reload Minecraft!");
				}
				if(!file.exists())
				{
					ConfigDownloader.download(file, CCPMConfig.cfgURL);
					shouldUpdate = false;
				}
				
				if(file.canRead())
				{
					json = FileUtils.readFileToString(file);
				}
				else
				{
					throw new IOException();
				}
			 }
			 catch (IOException e)
			 {
			    CCPM.log.warn("Unable to read file PollutionConfig.json!");
			 	json = null;
			 	e.printStackTrace();
			 }
		
		
		
		
			if(json != null)
			{
				cfg = new Gson().fromJson(json, PollutionProp.class);
			}
			
			if(shouldUpdate)
				if(ConfigDownloader.updateConfig(CCPMConfig.cfgURL, cfg))
				{
					ConfigDownloader.download(file, CCPMConfig.cfgURL);
				}
			
			CCPM.log.info("Loaded "+cfg.tiles.length+" tilez");
			for(Tilez t : cfg.tiles)
			{
				CCPM.log.info(t.getName()+" "+t.pollution);
			}
	}
	
	public static Hashtable<String, Float> toHashNoModid()
	{
		Hashtable<String, Float> ret = new Hashtable<String, Float>();
		
		Tilez[] tiles = cfg.getTiles();
		
		for(Tilez t:tiles)
		{
			ret.put(t.name, t.getPollution());
		}
		return ret;
	}
	
	public static Hashtable<String, Tilez> toHashByName()
	{
		Hashtable<String, Tilez> ret = new Hashtable<String, Tilez>();
		
		Tilez[] tiles = cfg.getTiles();
		
		for(Tilez t:tiles)
		{
			ret.put(t.name, t);
		}
		return ret;
	}
	
	public static Hashtable<String, Tilez> toHashByModid()
	{
		Hashtable<String, Tilez> ret = new Hashtable<String, Tilez>();
		
		Tilez[] tiles = cfg.getTiles();
		
		for(Tilez t:tiles)
		{
			ret.put(t.getModid(), t);
		}
		return ret;
	}
	
	public static List<String> modidList()
	{
		List<String> ret = new ArrayList<String>();
		
		Tilez[] tiles = cfg.getTiles();
		
		for(Tilez t:tiles)
		{
			if(!ret.contains(t.getModid()))
			ret.add(t.getModid());
		}
		return ret;
	}
	
	public static void serializeAndSave(String dir)
	{
		CCPM.log.info("Starting serializing and saving PollutionConfig to "+dir);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(cfg, PollutionProp.class);
		
		File fi = new File(dir);
		
		if(fi != null)
		{

			try
			{
				String pth = fi.getAbsolutePath()+"//PollutionConfig.json";
				
				fi = new File(pth);
				
				if(fi.isDirectory())
				{
					throw new IOException("File PollutionConfig.json is a directory! Please, delete it and reload world!");
				}
				if(!fi.exists())
				{
					fi.createNewFile();
				}
				
				if(fi.canWrite())
				{
					CCPM.log.info("Writing json to file...");
					FileUtils.writeStringToFile(fi, json);
					CCPM.log.info("Pollution configuration saved to "+pth);
				}
				else
				{
					throw new IOException();
				}
			}
			catch (IOException e)
			{
				CCPM.log.warn("Unable to write "+ json.toString() +" to file!");
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	
	public static class PollutionProp
	{
		private String version;
		
		private Tilez[] tiles;
		
		//private Blockz[] blocks;
		
		//private Entitiez[] entity;
		
		
		
		public static class Tilez
		{
			private String modid;
			
			private String name;
			
			private float pollution;
			
			public String getModid()
			{
				return modid;
			}
			
			public void setModid(String s)
			{
				modid = s;
			}
			
			public String getName()
			{
				return name;
			}
			
			public void setName(String s)
			{
				name = s;
			}
			
			public float getPollution()
			{
				return pollution;
			}
			
			public void setPollution(float f)
			{
				pollution = f;
			}
		}
		
		//public static class Blockz
		//{
		//	
		//}
		
		//public static class Entitiez
		 
                public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Tilez[] getTiles() {
		return tiles;
	}

	public void setTiles(Tilez[] tiles) {
		this.tiles = tiles;
	}
	
	}
}
