package ecomod.common.pollution.thread;

import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.IPollutionMultiplier;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.PositionedEmissionObject;
import ecomod.common.utils.WPTProfiler;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.MainRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldProcessingThread extends Thread
{
	private PollutionManager manager;
	private boolean isWorking;
	
	private List<Pair<Integer, Integer>> loadedChunks = new CopyOnWriteArrayList<>();
	
	private List<ChunkPollution> scheduledEmissions = new CopyOnWriteArrayList<>();
	
	private List<PositionedEmissionObject> positioned_emissions = new CopyOnWriteArrayList<>();
	
	public final WPTProfiler profiler = new WPTProfiler();
	//private List<ChunkPollution> delta = new ArrayList<ChunkPollution>();
	
	public volatile boolean should_update_tiles;
	
	public WorldProcessingThread(PollutionManager pm)
	{
		super(SidedThreadGroups.SERVER, "WPT_"+pm.getDim());
		
		manager = pm;

		this.setDaemon(true);
		this.setPriority(2);//2 of 10 
	}
	
	@Override
	public void run()
	{
		EcologyMod.log.info("Starting: "+getName());
		
		if(!EMConfig.wptimm)
			slp();
		
		profiler.clearProfiling();
		
		//Some debug stuff
		//EcologyMod.log.info(isInterrupted());
		//EcologyMod.log.info(PollutionUtils.genPMid(manager));
		//EcologyMod.log.info(EcologyMod.ph.threads.containsKey(PollutionUtils.genPMid(manager)));
		
		while(!isInterrupted() && manager != null && EcologyMod.ph.threads.containsKey(manager.getDim()) && DimensionManager.getWorld(manager.getDim()) != null && !manager.getWorld().isRemote)
		{
			should_update_tiles = true;
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
			while(Minecraft.getMinecraft().isGamePaused())
				slp(15); //Don't make anything while MC is paused
			
			if(manager == null)
			{
				shutdown();
				return;
			}
			
			profiler.profilingEnabled = true;
			
			//delta.clear();
			profiler.startSection("WPT_PREPARING_FOR_RUN");
			int error_counter = 0;
			isWorking = true;

			EcologyMod.log.info("Starting world processing... (dim "+manager.getDim()+ ')');
			
			long timestamp = System.currentTimeMillis();
			
			waitForTilesUpdate();
			
			World world = manager.getWorld();
			
			List<Chunk> chks = new ArrayList<>();
			
			for(Pair<Integer, Integer> c : loadedChunks)
				chks.add(PollutionUtils.coordsToChunk(world, c));
			
			List<ChunkPollution> temp = /*Collections.synchronizedList(*/new ArrayList<>()/*)*/;
			profiler.endSection();
			
			for(Chunk c : chks)
			{
				if(!c.isLoaded())
					continue;
				try
				{
					temp = new ArrayList<>();
					PollutionData d = calculateChunkPollution(c);
					Map<PollutionType, Float> m = calculateMultipliers(c);	
					
					profiler.startSection("WPT_HANDLING_SCHEDULED_EMISSIONS");
					
					temp.addAll(scheduledEmissions);

					for(ChunkPollution cp : temp.toArray(new ChunkPollution[temp.size()]))
						if(cp != null)
							if(cp.getX() == c.x && cp.getZ() == c.z)
							{
								d.add(cp.getPollution());
								temp.remove(cp);
							}
					
						
					scheduledEmissions.clear();
						
					scheduledEmissions.addAll(temp);
						
					temp.clear();
					
					profiler.endStartSection("WPT_UPDATING_MANAGER");
					
					for(PollutionType pt : PollutionType.values())
					{
						d = d.multiply(pt, m.get(pt));
					}
				
					if(!(d.getAirPollution() == 0.0D && d.getWaterPollution() == 0.0D && d.getSoilPollution() == 0.0D))
						manager.addPollution(c.x, c.z, d);//(new ChunkPollution(c.xPosition, c.zPosition, d));
					
					profiler.endSection();
					
					handleChunk(c);
				}
				catch (Exception e)
				{
					EcologyMod.log.error("Caught an exception while processing chunk ("+c.x+ ';' +c.z+")!");
					EcologyMod.log.error(e.toString());
					e.printStackTrace();
					
					error_counter++;
				}
			}
			
			profiler.startSection("WPT_DO_DIFFUSION");
			
			manager.do_diffusion();
			
			profiler.endStartSection("WPT_SAVING_TO_FILE");
			
			manager.save();
			
			profiler.endSection();
			
			if(error_counter > 10)
			{
				EcologyMod.log.error("It seems there were many exceptions while processing chunks. If exceptions were the same, please, restart Minecraft or go to https://github.com/Artem226/MinecraftEcologyMod/issues and make an issue about the exception (Don't forget to include the log!)");
			}
			
			profiler.profilingEnabled = false;
			
			EcologyMod.log.info("World processing completed in " + (System.currentTimeMillis() - timestamp) / 1000F + " seconds");
			
			slp();
		}
		
		shutdown();
	}
	
	public void shutdown()
	{
		EcologyMod.log.info('[' +this.getName()+"]Carefully shutting down...");
		
		if(profiler.profilingEnabled)
		{
			profiler.endSection();
			profiler.clearProfiling();
		}
		profiler.profilingEnabled = false;
		
		isWorking = false;
		
		if(manager != null)
		{
			if(EcologyMod.ph.threads.containsKey(manager.getDim()))
				EcologyMod.ph.threads.remove(manager.getDim());
		
			manager.save();
			manager = null;
		}
		
		loadedChunks.clear();
		scheduledEmissions.clear();
		
		System.gc();
		
		EcologyMod.log.info('[' +this.getName()+"]Shut down.");
		
		//Bye, bye
		interrupt();
	}
	
	public PollutionManager getPM()
	{
		return manager;
	}
	
	public List<ChunkPollution> getScheduledEmissions()
	{
		return scheduledEmissions;
	}
	
	public List<PositionedEmissionObject> getPositionedEmissions()
	{
		return positioned_emissions;
	}
	
	public boolean isWorking()
	{
		return isWorking;
	}
	
	public List<Pair<Integer, Integer>> getLoadedChunks()
	{
		return loadedChunks;
	}
	
	public void slp(int seconds)
	{
		isWorking = false;
		
		try
		{
			if(manager != null)
				EcologyMod.log.info("[WPT_"+manager.getDim()+"]Sleeping for "+seconds+" seconds");
			sleep(seconds*1000);
		} 
		catch (InterruptedException e)
		{
			//e.printStackTrace();
			return;
		}
	}
	
	private void slp()
	{
		slp(EMConfig.wptcd);
	}
	
	public PollutionData calculateChunkPollution(Chunk c)
	{
		profiler.startSection("WPT_CALCULATING_CHUNK_POLLUTION");
		
		World w = manager.getWorld();
		
		PollutionData ret = PollutionData.getEmpty();
		
		for(final PositionedEmissionObject peo : positioned_emissions)
		{
			if(peo.getChunkX() == c.x && peo.getChunkZ() == c.z)
			{
				ret.add(peo.getValue());
				
				positioned_emissions.remove(peo);
			}
		}
		
		profiler.endSection();
		return ret;
	}
	
	public Map<PollutionType, Float> calculateMultipliers(Chunk c)
	{
		profiler.startSection("WPT_CALCULATING_POLLUTION_MULTIPLIERS");
		List<TileEntity> tes = new CopyOnWriteArrayList<>(c.getTileEntityMap().values());
		
		Map<PollutionType, Float> ret = new HashMap<>();
		
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
		
		profiler.endSection();
		return ret;
	}
	
	public void handleChunk(Chunk c)
	{
		profiler.startSection("WPT_HANDLING_CHUNK");
		
		if(manager!=null)
		if(PollutionEffectsConfig.isEffectActive("wasteland", manager.getPollution(c.x, c.z)))
		{
			((WorldServer)manager.getWorld()).addScheduledTask(()->{
				for(int i = 0; i < 16; i++)
					for(int j = 0; j < 16; j++)
					{
						if(c.getWorld().rand.nextInt(10) == 0)
						{
							int strtx = c.x << 4;
							int strtz = c.z << 4;
					
							if(c.getBiome(new BlockPos(i + strtx, c.getWorld().getActualHeight(), j + strtz), c.getWorld().getBiomeProvider()) != MainRegistry.biome_wasteland)
								EMUtils.setBiome(c, MainRegistry.biome_wasteland, i + strtx, j + strtz);
						}
					}
			});
		}
		profiler.endSection();
	}
	
	
	public void forceSE()
	{
		profiler.startSection("WPT_FORCED_HANDLING_SCHEDULED_EMISSIONS");
		if(manager!=null)
		{
			if(getScheduledEmissions().size() > 0)
			{
				for(final ChunkPollution p : getScheduledEmissions())
					manager.addPollution(p.getKey(), p.getValue());
			}
			if(positioned_emissions.size() > 0)
			{
				for(final PositionedEmissionObject peo : positioned_emissions)
					manager.addPollution(peo.getChunkX(), peo.getChunkZ(), peo.getValue());
			}
		}
		profiler.endSection();
	}

	public void waitForTilesUpdate()
	{
		while(should_update_tiles)
			try
			{
				sleep(5);
			} 
			catch (InterruptedException e)
			{
				return;
			}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + manager.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorldProcessingThread other = (WorldProcessingThread) obj;
		if (manager == null) {
			return other.manager == null;
		} else return manager.equals(other.manager);
	}
	
	
}
