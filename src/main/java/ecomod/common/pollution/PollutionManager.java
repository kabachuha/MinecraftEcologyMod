package ecomod.common.pollution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ecomod.api.pollution.PollutionData;
import ecomod.core.EcologyMod;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import scala.actors.threadpool.Arrays;

public class PollutionManager 
{
	//Vars
	private World world;
	private int dim;
	private List<ChunkPollution> data;
	
	//Constructor
	public PollutionManager(World w)
	{
		world = w;
		
		dim = w.provider.getDimension();
		
		data = Collections.synchronizedList(new ArrayList<ChunkPollution>());
	}
	
	//IO
	public boolean save()
	{
		WorldPollution wp = new WorldPollution();
		
		wp.setData(data.toArray(new ChunkPollution[data.size()]));
		
		EcologyMod.log.info("Serializing and saving pollution manager for dimension "+dim);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
		
		String json = gson.toJson(wp, WorldPollution.class);
		
		File file = world.getSaveHandler().getWorldDirectory();
		
		if(file != null)
		{
			try
			{
				String worldPath = file.getAbsolutePath();
				
				File save = new File(worldPath+"/PollutionMap.json");
				
				if(save.isDirectory())
				{
					throw new IOException("File PollutionMap.json is a directory! Please, delete it and restart the world!");
				}
				
				if(!save.exists())
				{
					save.createNewFile();
				}
				
				if(save.canWrite())
				{
					FileUtils.writeStringToFile(save, json);
					return true;
				}
				else
				{
					throw new IOException("The save file is not writable!!!");
				}
			}
			catch(IOException e)
			{
				EcologyMod.log.error("Unable to write data of the pollution manager for dimension "+dim);
				EcologyMod.log.error(e.toString());
				
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	
	public boolean load()
	{
		EcologyMod.log.info("Loading pollution manager for dimension "+dim);
		
		Gson gson = new GsonBuilder().create();
		
		String json;
		
		WorldPollution wp = new WorldPollution();
		
		File file = world.getSaveHandler().getWorldDirectory();
		
		if(file != null)
		{
			try
			{
				String worldPath = file.getAbsolutePath();
				
				File save = new File(worldPath+"/PollutionMap.json");
				
				if(save.isDirectory())
				{
					throw new IOException("File PollutionMap.json is a directory! Please, delete it and reload world!");
				}
				
				if(!save.exists())
					return false;
				
				if(save.canRead())
				{
					json = FileUtils.readFileToString(save);
					
					if(json == null)
						return false;
				}
				else
				{
					throw new IOException("The save file is not readable!!!");
				}
			}
			catch(IOException e)
			{
				EcologyMod.log.error("Unable to write data of the pollution manager for dimension "+dim);
				EcologyMod.log.error(e.toString());
				
				e.printStackTrace();
				
				return false;
			}
		}
		else
		{
			EcologyMod.log.fatal("There is no world save directory!!!");
			return false;
		}
		
		try
		{
			wp = gson.fromJson(json, WorldPollution.class);
		}
		catch(JsonSyntaxException e)
		{
			EcologyMod.log.error("PollutionManager's file has incorrect syntax!!!");
			e.printStackTrace();
			return false;
		}
		
		if(wp == null)
			return false;
		
		List<ChunkPollution> l = new ArrayList<ChunkPollution>();
		
		for(ChunkPollution u : wp.getData())
			l.add(u);
		
		synchronized(data)
		{
			data = l;
		}
		
		if(data == null)
			return false;
		
		
		EcologyMod.log.info("PM has been loaded!");
		return true;
	}
	
	//Interaction
	public void reset()
	{
		EcologyMod.log.warn("Pollution manager for dim "+dim+" has been reset!!!");
		data.clear();
	}
	
	public boolean contains(Pair<Integer, Integer> coord)
	{
		for(ChunkPollution cp : data)
			if(cp.getX() == coord.getLeft() && cp.getZ() == coord.getRight())
				return true;
		
		return false;
	}
	
	public ChunkPollution getChunkPollution(Pair<Integer, Integer> coord)
	{
		for(ChunkPollution cp : data)
			if(cp.getX() == coord.getLeft() && cp.getZ() == coord.getRight())
				return cp;
		
		return new ChunkPollution(coord.getLeft(), coord.getRight(), PollutionData.getEmpty());
	}
	
	public void setChunkPollution(ChunkPollution cp)
	{
		cp = new ChunkPollution(cp.getX(), cp.getZ(), cp.getPollution());
		
		Pair<Integer, Integer> coords = Pair.of(cp.getX(), cp.getZ());
		
		if(!cp.isEmpty())
		{
			if(contains(coords))
			{
				data.remove(getChunkPollution(coords));
				data.add(cp);
			}
			else
			{
				data.add(cp);
			}
		}
	}
	
	public Chunk getChunk(Pair<Integer, Integer> coord)
	{
		for(ChunkPollution cp : data)
			if(cp.getX() == coord.getLeft() && cp.getZ() == coord.getRight())
				return world.getChunkFromChunkCoords(coord.getLeft(), coord.getRight());
		
		return null;
	}
	
	//Getters
	
	public World getWorld()
	{
		return world;
	}
	
	public int getDim()
	{
		return dim;
	}
	
	public List<ChunkPollution> getData()
	{
		return data;
	}
	
	
	//Data types
	
	public static class ChunkPollution
	{
		private int chunkX;
		private int chunkZ;
		
		
		private PollutionData pollution;
		
		public ChunkPollution()
		{
			
		}

		public ChunkPollution(int xPosition, int zPosition, PollutionData data) {
			chunkX = xPosition;
			chunkZ = zPosition;
			pollution = data;
		}

		/**
		 * @return the chunkX
		 */
		public int getX() {
			return chunkX;
		}

		/**
		 * @param chunkX the chunkX to set
		 */
		public void setX(int chunkX) {
			this.chunkX = chunkX;
		}

		/**
		 * @return the chunkY
		 */
		public int getZ() {
			return chunkZ;
		}

		/**
		 * @param chunkY the chunkY to set
		 */
		public void setZ(int chunkZ) {
			this.chunkZ = chunkZ;
		}

		/**
		 * @return the pollution
		 */
		public PollutionData getPollution() {
			return pollution;
		}

		/**
		 * @param pollution the pollution to set
		 */
		public void setPollution(PollutionData pollution) {
			this.pollution = pollution;
		}
		
		public boolean isEmpty()
		{
			return pollution == null || (pollution.getAirPollution() == 0 && pollution.getWaterPollution() == 0 && pollution.getSoilPollution() == 0);
		}
		
		public static boolean coordEquals(ChunkPollution f, ChunkPollution s)
		{
			return (f.getX() == s.getX()) && (f.getZ() == s.getZ());
		}
		
		public String toString()
		{
			return "{ \"chunkX\" : "+chunkX+", \"chunkZ\" : "+chunkZ+", \"pollution\" : "+pollution.toString()+"}";
		}
	}
	
	//Just for serialization
		public static class WorldPollution
		{
			private ChunkPollution[] data;

			/**
			 * @return the chunks
			 */
			public ChunkPollution[] getData() {
				return data;
			}

			/**
			 * @param chunks the chunks to set
			 */
			public void setData(ChunkPollution[] chunks) {
				this.data = chunks;
			}
		}
		
}
