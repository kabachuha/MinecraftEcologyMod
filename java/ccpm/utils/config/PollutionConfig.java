package ccpm.utils.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import ccpm.ecosystem.PollutionManager.ChunksPollution;
import cpw.mods.fml.common.FMLLog;

public class PollutionConfig {

	public PollutionConfig() {
		// TODO Auto-generated constructor stub
	}

	
	public static PollutionProp cfg;
	
	public static void load(String dir)
	{
		FMLLog.info("[CCPM]Loading pollution config");
           String json = null;

			try
			{
				File file = new File(dir+"//PollutionConfig.json");
				
				if(file.isDirectory())
				{
					throw new IOException("File PollutionConfig.json is a directory! Please, delete it and reload Minecraft!");
				}
				if(!file.exists())
				{
					ConfigDownloader.download(file, "https://www.dropbox.com/s/4x5s9mpt888phxq/PollutionProp.json?dl=1");
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
			    FMLLog.bigWarning("[CCPM]Unable to read file PollutionConfig.json!");
			 	json = null;
			 	e.printStackTrace();
			 }
		
		
		
		
			if(json != null)
			{
				cfg = new Gson().fromJson(json, PollutionProp.class);
			}
		
			FMLLog.info("[CCPM]Loaded "+cfg.tiles.length+" tilez");
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
