package ecomod.common.pollution;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.jna.platform.unix.X11.XClientMessageEvent.Data;

import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class PollutionManager 
{
	//Vars
	private World world;
	private int dim;
	private CopyOnWriteArrayList<ChunkPollution> data;
	
	//Constructor
	public PollutionManager(World w)
	{
		world = w;
		
		dim = w.provider.getDimension();
		
		data = new CopyOnWriteArrayList<ChunkPollution>();
	}
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	//IO
	public boolean save()
	{
		WorldPollution wp = new WorldPollution();
		
		wp.setData(data.toArray(new ChunkPollution[data.size()]));
		
		EcologyMod.log.info("Serializing and saving pollution manager for dimension "+dim);
		
		String json = gson.toJson(wp, WorldPollution.class);
		
		File file = world.getSaveHandler().getWorldDirectory();
		
		if(file != null)
		{
			try
			{
				String worldPath = file.getAbsolutePath();
				
				File save = new File(worldPath +"/PollutionMap.json");
				
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
					FileUtils.writeStringToFile(save, json, Charset.forName("UTF-8"));
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
		
		String json;
		
		WorldPollution wp = new WorldPollution();
		
		File file = world.getSaveHandler().getWorldDirectory();
		
		if(file != null)
		{
			try
			{
				String worldPath = file.getAbsolutePath();
				
				File save = new File(worldPath + "/PollutionMap.json");
				
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
			if(!(u.getPollution().getAirPollution() == 0 && u.getPollution().getWaterPollution() == 0 && u.getPollution().getSoilPollution() == 0))
				l.add(u);
		
		data.clear();
		data.addAll(l);
		
		if(data == null)
			return false;
		
		EcologyMod.log.info("PM has been loaded!");
		return true;
	}
	
	public void writeByteBuf(ByteBuf bb)
	{
		bb.writeInt(getDim());
		for(ChunkPollution cp : data)
			cp.writeByteBuf(bb);
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
	
	public ChunkPollution setChunkPollution(ChunkPollution cp)
	{
		cp = new ChunkPollution(cp.getX(), cp.getZ(), cp.getPollution());
		
		PollutionData pd = cp.getPollution();
		
		if(pd.getAirPollution() < 0.00001)pd.setAirPollution(0);
		if(pd.getWaterPollution() < 0.00001)pd.setWaterPollution(0);
		if(pd.getSoilPollution() < 0.00001)pd.setSoilPollution(0);
		
		Pair<Integer, Integer> coords = cp.getLeft();
		

		if(contains(coords))
		{
			data.remove(getChunkPollution(coords));
			data.add(cp);
		}
		else
		{
			data.add(cp);
		}
			
		return cp;
	}
	
	public void setChunkPollution(Pair<Integer, Integer> of, PollutionData add) 
	{
		setChunkPollution(new ChunkPollution(of, add));
	}
	
	public void setChunkPollution(int x, int z, PollutionData add) 
	{
		setChunkPollution(new ChunkPollution(Pair.of(x, z), add));
	}
	
	public void addPollution(Pair<Integer, Integer> coord, PollutionData delta)
	{
		setChunkPollution(coord, getPollution(coord).add(delta));
	}
	
	public void addPollution(int x, int z, PollutionData delta)
	{
		setChunkPollution(Pair.of(x, z), getPollution(Pair.of(x, z)).add(delta));
	}
	
	public boolean addPollutionIfLoaded(int x, int z, PollutionData delta)
	{
		if(world.isChunkGeneratedAt(x, z))
		{
			setChunkPollution(Pair.of(x, z), getPollution(Pair.of(x, z)).add(delta));
			return true;
		}
		
		return false;
	}
	
	public PollutionData getPollution(Pair<Integer, Integer> coord)
	{
		return getChunkPollution(coord).getPollution().clone();
	}
	
	public PollutionData getPollution(int x, int z)
	{
		return getChunkPollution(Pair.of(x, z)).getPollution().clone();
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
	
	public void do_diffusion()
	{
		for(ChunkPollution c : data.toArray(new ChunkPollution[data.size()]))
			diffuse(c);
	}
	
	public void diffuse(ChunkPollution c)
	{
		int i = c.getX();
		int j = c.getZ();
		
		PollutionData to_spread = c.getPollution().clone();
		
		to_spread = to_spread.multiplyAll(EMConfig.diffusion_factor * EMConfig.wptcd / 60);
		to_spread.multiply(PollutionType.WATER, 0.5F);
		
		to_spread.multiply(PollutionType.SOIL, 0.07F);
		
		float count = 0;
		
		if(addPollutionIfLoaded(i + 1, j, to_spread))count--;
		if(addPollutionIfLoaded(i - 1, j, to_spread))count--;
		if(addPollutionIfLoaded(i, j - 1, to_spread))count--;
		if(addPollutionIfLoaded(i, j + 1, to_spread))count--;

        addPollutionIfLoaded(i, j, to_spread.multiplyAll(count));
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
