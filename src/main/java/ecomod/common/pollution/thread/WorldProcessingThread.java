package ecomod.common.pollution.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.IPollutionEmitter;
import ecomod.api.pollution.IPollutionMultiplier;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.pollution.TEPollutionConfig.TEPollution;
import ecomod.common.tiles.TileFilter;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.MainRegistry;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class WorldProcessingThread extends Thread
{
	private PollutionManager manager;
	private boolean isWorking = false;
	
	private List<Pair<Integer, Integer>> loadedChunks = Collections.synchronizedList(new ArrayList<Pair<Integer,Integer>>());
	
	private List<ChunkPollution> scheduledEmissions = Collections.synchronizedList(new ArrayList<ChunkPollution>());
	
	//private List<ChunkPollution> delta = new ArrayList<ChunkPollution>();
	
	//TODO Add a profiler?
	
	public WorldProcessingThread(PollutionManager pm)
	{
		super();
		
		manager = pm;
		
		this.setName("WPT_"+pm.getDim());
		this.setDaemon(true);
		this.setPriority(2);//2 of 10 
	}
	
	@Override
	public void run()
	{
		EcologyMod.log.info("Starting: "+getName());
		
		if(!EMConfig.wptimm)
			slp();
		
		//Some debug stuff
		//EcologyMod.log.info(isInterrupted());
		//EcologyMod.log.info(PollutionUtils.genPMid(manager));
		//EcologyMod.log.info(EcologyMod.ph.threads.containsKey(PollutionUtils.genPMid(manager)));
		
		while(!isInterrupted() && manager != null && EcologyMod.ph.threads.containsKey(PollutionUtils.genPMid(manager)) && DimensionManager.getWorld(manager.getDim()) != null && !manager.getWorld().isRemote)
		{
			while(Minecraft.getMinecraft().isGamePaused())
				slp(15); //Don't make anything while MC is paused
			
			//delta.clear();
			
			int error_counter = 0;
			isWorking = true;
			EcologyMod.log.info("Starting world processing... (dim "+manager.getDim()+")");
			
			World world = manager.getWorld();
			
			List<Chunk> chks = new ArrayList<Chunk>();
			
			synchronized(loadedChunks)
			{
				for(Pair<Integer, Integer> c : loadedChunks)
					chks.add(PollutionUtils.coordsToChunk(world, c));
			}
			
			List<ChunkPollution> temp = /*Collections.synchronizedList(*/new ArrayList<ChunkPollution>()/*)*/;
			
			
			for(Chunk c : chks)
			{
				try
				{
					temp = new ArrayList<ChunkPollution>();
					PollutionData d = calculateChunkPollution(c);
					Map<PollutionType, Float> m = calculateMultipliers(c);	
					
					synchronized(scheduledEmissions)
					{
						temp.addAll(scheduledEmissions);
					}
					
					//synchronized(temp)
					//{
						for(ChunkPollution cp : temp.toArray(new ChunkPollution[temp.size()]))
							if(cp != null)
								if(cp.getX() == c.xPosition && cp.getZ() == c.zPosition)
								{
									d.add(cp.getPollution());
									temp.remove(cp);
								}
					//}
						
					synchronized(scheduledEmissions)
					{
						scheduledEmissions.clear();
						
						scheduledEmissions.addAll(temp);
					}
						
					temp.clear();
						
					for(PollutionType pt : PollutionType.values())
					{
						d = d.multiply(pt, m.get(pt));
					}
				
					if(!(d.getAirPollution() == 0.0D && d.getWaterPollution() == 0.0D && d.getSoilPollution() == 0.0D))
						manager.addPollution(c.xPosition, c.zPosition, d);//(new ChunkPollution(c.xPosition, c.zPosition, d));
					
					handleChunk(c);
				}
				catch (Exception e)
				{
					EcologyMod.log.error("Caught an exception while processing chunk ("+c.xPosition+";"+c.zPosition+")!");
					EcologyMod.log.error(e.toString());
					e.printStackTrace();
					
					error_counter++;
				}
			}
			
			manager.do_diffusion();
			
			manager.save();
			
			
			if(error_counter > 10)
			{
				EcologyMod.log.error("It seems there were many exceptions while processing chunks. If exceptions were the same, please, restart Minecraft or go to https://github.com/Artem226/MinecraftEcologyMod/issues and make an issue about the exception (Don't forget to include the log!)");
			}
			
			slp();
		}
		
		shutdown();
	}
	
	public void shutdown()
	{
		EcologyMod.log.info("Carefully shuting down...");
		
		isWorking = false;
		manager.save();
		
		if(EcologyMod.ph.threads.containsKey(PollutionUtils.genPMid(manager)))
			EcologyMod.ph.threads.remove(PollutionUtils.genPMid(manager));
		
		manager = null;
		loadedChunks.clear();
		scheduledEmissions.clear();
		System.gc();
	}
	
	public PollutionManager getPM()
	{
		return manager;
	}
	
	public List<ChunkPollution> getScheduledEmissions()
	{
		return scheduledEmissions;
	}
	
	public boolean isWorking()
	{
		return isWorking;
	}
	
	public List<Pair<Integer,Integer>> getLoadedChunks()
	{
		return loadedChunks;
	}
	
	public void slp(int seconds)
	{
		isWorking = false;
		
		try
		{
			EcologyMod.log.info("Sleeping for "+seconds+" seconds");
			sleep(seconds*1000);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	private void slp()
	{
		slp(EMConfig.wptcd);
	}
	
	public PollutionData calculateChunkPollution(Chunk c)
	{
		List<TileEntity> tes = new LinkedList<TileEntity>(c.getTileEntityMap().values());
		
		PollutionData ret = new PollutionData();
		
		for(TileEntity te : tes)
		{
			int wir = EMUtils.countWaterInRadius(c.getWorld(), te.getPos(), EMConfig.wpr);
			boolean rain = c.getWorld().isRainingAt(te.getPos());
			
			if(te instanceof IPollutionEmitter)
			{
				IPollutionEmitter ipe = (IPollutionEmitter) te;
				
				int filters = 0;
				
				for(EnumFacing f : EnumFacing.VALUES)
				{
					TileEntity tile = c.getWorld().getTileEntity(te.getPos().offset(f));
					
					if(tile instanceof TileFilter && ((TileFilter)tile).isWorking())
						filters++;
				}
				
				ret.add(ipe.pollutionEmission().clone()
						.multiply(PollutionType.AIR, 1 - EMConfig.filter_adjacent_tiles_redution * filters).multiply(PollutionType.WATER, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 2).multiply(PollutionType.SOIL, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 3)
						.multiply(PollutionType.WATER, rain ? 2 : 1).multiply(PollutionType.SOIL, rain ? 1.2F : 1).multiply(PollutionType.WATER, wir == 0 ? 1 : wir)
						);
			}
			else
			{
				if(EcologyMod.instance.tepc.hasTile(te))
				{
					if(PollutionUtils.isTEWorking(c.getWorld(), te))
					{
						TEPollution tep = EcologyMod.instance.tepc.getTEP(te);
						if(tep != null)
						{
							int filters = 0;
							
							for(EnumFacing f : EnumFacing.VALUES)
							{
								TileEntity tile = c.getWorld().getTileEntity(te.getPos().offset(f));
								
								if(tile instanceof TileFilter && ((TileFilter)tile).isWorking())
									filters++;
							}
							
							ret.add(tep.getEmission().clone()
									.multiply(PollutionType.AIR, 1 - EMConfig.filter_adjacent_tiles_redution * filters).multiply(PollutionType.WATER, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 2).multiply(PollutionType.SOIL, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 3)
									.multiply(PollutionType.WATER, rain ? 3 : 1).multiply(PollutionType.SOIL, rain ? 1.5F : 1).multiply(PollutionType.WATER, wir == 0 ? 1 : wir)
									);
						}
					}
				}
			}
		}
		
		return ret.multiplyAll(EMConfig.wptcd/60);
	}
	
	public Map<PollutionType, Float> calculateMultipliers(Chunk c)
	{
		List<TileEntity> tes = new LinkedList<TileEntity>(c.getTileEntityMap().values());
		
		Map<PollutionType, Float> ret = new HashMap<PollutionType, Float>();
		
		//Multipliers
		float mA = 1, mW = 1, mS = 1;
		
		for(TileEntity te : tes)
			if(te instanceof IPollutionMultiplier)
			{
				IPollutionMultiplier ipm = (IPollutionMultiplier) te;
				mA *= ipm.pollutionFactor(PollutionType.AIR);
				mW *= ipm.pollutionFactor(PollutionType.WATER);
				mS *= ipm.pollutionFactor(PollutionType.SOIL);
			}
				
		ret.put(PollutionType.AIR, mA);
		ret.put(PollutionType.WATER, mW);
		ret.put(PollutionType.SOIL, mS);
		
		return ret;
	}
	
	public void handleChunk(Chunk c)
	{
		if(PollutionEffectsConfig.isEffectActive("wasteland", manager.getPollution(c.xPosition, c.zPosition)))
		for(int i = 0; i < 16; i++)
			for(int j = 0; j < 16; j++)
			{
				if(c.getWorld().rand.nextInt(10) == 0)
				{
					int strtx = c.xPosition << 4;
					int strtz = c.zPosition << 4;
					
					if(c.getBiome(new BlockPos(i + strtx, c.getWorld().getActualHeight(), j + strtz), c.getWorld().getBiomeProvider()) != MainRegistry.biome_wasteland)
						EMUtils.setBiome(c, MainRegistry.biome_wasteland, i + strtx, j + strtz);
				}
			}
	}
	
	
	public void forceSE()
	{
		List<ChunkPollution> temp = Collections.synchronizedList(new ArrayList<ChunkPollution>());
		
		synchronized(getScheduledEmissions())
		{
			temp.addAll(getScheduledEmissions());
		}
		
		synchronized(temp)
		{
			for(ChunkPollution cp : temp.toArray(new ChunkPollution[temp.size()]))
				for(ChunkPollution c : manager.getData())
					if(cp.getX() == c.getX() && cp.getZ() == c.getZ())
					{
						manager.addPollution(c.getX(), c.getZ(), cp.getPollution());
					}
		}
	}
}
