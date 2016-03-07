package ccpm.ecosystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class PollutionManager {

	private World wor;
	public static PollutionManager instance;
	
	public PollutionManager(World w)
	{
		this.chunksPollution.world = w.getWorldInfo().getWorldName();
		wor = w;
		instance = this;
		CCPM.log.info("Initialising Pollution Manager");
	}

	
	public ChunksPollution chunksPollution = new ChunksPollution();
	
	public boolean isSaving = false;
	public void save()
	{
		isSaving = true;
		CCPM.log.info("Saving Pollution Manager");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String json = gson.toJson(chunksPollution, ChunksPollution.class);
		
		File fi = wor.getSaveHandler().getWorldDirectory();
		
		if(fi != null)
		{

		try
		{
			String worldPath = fi.getAbsolutePath();
			
			File file = new File(worldPath+"//PollutionMap.json");
			
			if(file.isDirectory())
			{
				throw new IOException("File PollutionMap.json is a directory! Please, delete it and reload world!");
			}
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			
			if(file.canWrite())
			{
				CCPM.log.info("Saving Pollution Manager's data to file: "+file.getAbsolutePath()+"//"+file.getName());
				FileUtils.writeStringToFile(file, json);
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
		
		isSaving=false;
	}
	
	public void load()
	{
		String json = null;
		
		CCPM.log.info("Loading Pollution Manager");
		
        File fi = wor.getSaveHandler().getWorldDirectory();
		
		if(fi != null)
		{
		
			try
			{
				String worldPath = fi.getAbsolutePath();
				
				File file = new File(worldPath+"//PollutionMap.json");
				
				if(file.isDirectory())
				{
					throw new IOException("File PollutionMap.json is a directory! Please, delete it and reload world!");
				}
				if(!file.exists())
				{
					file.createNewFile();
				}
				
				if(file.canRead())
				{
					CCPM.log.info("Reading pollution manager data from file "+file.getAbsolutePath()+"//"+file.getName());
					json = FileUtils.readFileToString(file);
				}
				else
				{
					throw new IOException();
				}
			}
			catch (IOException e)
			{
				CCPM.log.warn("Unable to read file PollutionMap.json!");
				json = null;
				e.printStackTrace();
			}
		}
		
		
			//Argh! How FileUtils.readFileToString(file)'s description says, return is never null.
			//So, let's make length check
		
			//#UPD Yay! It works!
			if(/*json != null && */json.length() > 3)
			{
				CCPM.log.info("Deserializing chunks pollution data from JSON");
				chunksPollution = new Gson().fromJson(json, ChunksPollution.class);
			}
			else
			{
				CCPM.log.warn("No pollution manager's data has found in file. It's normal, if world is loading first time, but if it isn't, please, report to author!("+CCPM.githubURL+")");
				chunksPollution = new ChunksPollution();
				chunksPollution.setWorld(wor.getWorldInfo().getWorldName());
				chunksPollution.setCP(new ChunkPollution[0]);
			}
		
	}
	
	
	
	public static class ChunksPollution
	{
		private String world;
	    private ChunkPollution[] chunks;
		
	    
	    public String getWorld() {
	        return world;
	      }

	      public void setWorld(String w) {
	        this.world = w;
	      }

	      public ChunkPollution[] getCP() {
	        return chunks;
	      }
	    
	      public ChunkPollution getCP(int i) {
	          return chunks[i];
	        }

	        public void setCP(ChunkPollution[] cp) {
	          this.chunks =cp;
	        }
	    
		
		public static class ChunkPollution
		{
			private int x;
			private int z;
			private float pollution;
			
			public int getX()
			{
				return x;
			}
			
			public void setX(int newX)
			{
				this.x = newX;
			}
			
			public int getZ()
			{
				return z;
			}
			
			public void setZ(int newZ)
			{
				this.z = newZ;
			}
			
			public float getPollution()
			{
				return pollution;
			}
			
			public void setPollution(float newP)
			{
				this.pollution = newP;
			}
			
		}
	}
	
}
