package ecomod.common.pollution.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ecomod.api.EcomodStuff;
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
import ecomod.common.utils.WPTProfiler;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.MainRegistry;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;


public class WorldProcessingThread extends Thread
{
	private PollutionManager manager;
	private boolean isWorking = false;
	
	private List<Pair<Integer, Integer>> loadedChunks = new CopyOnWriteArrayList<Pair<Integer,Integer>>();
	
	private List<ChunkPollution> scheduledEmissions = new CopyOnWriteArrayList<ChunkPollution>();
	
	public final WPTProfiler profiler = new WPTProfiler();
	//private List<ChunkPollution> delta = new ArrayList<ChunkPollution>();
	
	public boolean isWorldTicking = false;
	
	public WorldProcessingThread(PollutionManager pm)
	{
		super("WPT_"+pm.getDim());
		
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
		
		while(!isInterrupted() && manager != null && EcologyMod.ph.threads.containsKey(PollutionUtils.genPMid(manager)) && DimensionManager.getWorld(manager.getDim()) != null && !manager.getWorld().isRemote)
		{
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

			EcologyMod.log.info("Starting world processing... (dim "+manager.getDim()+")");
			
			long timestamp = System.currentTimeMillis();
			
			World world = manager.getWorld();
			
			List<Chunk> chks = new ArrayList<Chunk>();
			
			for(Pair<Integer, Integer> c : loadedChunks)
				chks.add(PollutionUtils.coordsToChunk(world, c));
			
			List<ChunkPollution> temp = /*Collections.synchronizedList(*/new ArrayList<ChunkPollution>()/*)*/;
			profiler.endSection();
			
			for(Chunk c : chks)
			{
				try
				{
					temp = new ArrayList<ChunkPollution>();
					PollutionData d = calculateChunkPollution(c);
					Map<PollutionType, Float> m = calculateMultipliers(c);	
					
					profiler.startSection("WPT_HANDLING_SCHEDULED_EMISSIONS");
					
					temp.addAll(scheduledEmissions);
					
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
						
					scheduledEmissions.clear();
						
					scheduledEmissions.addAll(temp);
						
					temp.clear();
					
					profiler.endStartSection("WPT_UPDATING_MANAGER");
					
					for(PollutionType pt : PollutionType.values())
					{
						d = d.multiply(pt, m.get(pt));
					}
				
					if(!(d.getAirPollution() == 0.0D && d.getWaterPollution() == 0.0D && d.getSoilPollution() == 0.0D))
						manager.addPollution(c.xPosition, c.zPosition, d);//(new ChunkPollution(c.xPosition, c.zPosition, d));
					
					profiler.endSection();
					
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
		EcologyMod.log.info("["+this.getName()+"]Carefully shuting down...");
		
		if(profiler.profilingEnabled)
		{
			profiler.endSection();
			profiler.clearProfiling();
		}
		profiler.profilingEnabled = false;
		
		isWorking = false;
		
		if(EcologyMod.ph.threads.containsKey(PollutionUtils.genPMid(manager)))
			EcologyMod.ph.threads.remove(PollutionUtils.genPMid(manager));
		
		if(manager != null)
		{
			manager.save();
			manager = null;
		}
		
		loadedChunks.clear();
		scheduledEmissions.clear();
		
		System.gc();
		
		EcologyMod.log.info("["+this.getName()+"]Shutted down.");
		
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
		
		waitForTickEnd();
		
		List<TileEntity> tes = new CopyOnWriteArrayList<TileEntity>(c.chunkTileEntityMap.values());
		
		PollutionData ret = new PollutionData();
		
		for(TileEntity te : tes)
		{
			waitForTickEnd();
			
			if(te == null || te.isInvalid())
				continue;
			
			int wir = EMUtils.countWaterInRadius(c.worldObj, new EMBlockPos(te), EMConfig.wpr);
			boolean rain = EMUtils.isRainingAt(c.worldObj, new EMBlockPos(te));
			
			boolean overriden_by_func = false;
			
			for(Function<TileEntity, Object[]> func : EcomodStuff.custom_te_pollution_determinants)
			{
				Object[] func_result = new Object[0];
				
				try
				{
					func_result = func.apply(te);
				}
				catch(Exception e)
				{
					EcologyMod.log.error("Exception while processing a custom TileEntity pollution determining function:");
					EcologyMod.log.info(e.toString());
					e.printStackTrace();
					continue;
				}

				if(func_result.length < 3)
					continue;
				
				ret.add(PollutionType.AIR, (Double)func_result[0]);
				ret.add(PollutionType.WATER, (Double)func_result[1]);
				ret.add(PollutionType.SOIL, (Double)func_result[2]);
				
				if(func_result.length > 3)
				{
					if(func_result[3] != null && func_result[3] instanceof Boolean)
						if(!overriden_by_func)
							overriden_by_func = (Boolean)func_result[3];
				}
			}
			
			waitForTickEnd();
			
			if(!overriden_by_func)
			if(te instanceof IPollutionEmitter)
			{
				IPollutionEmitter ipe = (IPollutionEmitter) te;
				
				int filters = 0;
				
				for(EnumFacing f : EnumFacing.values())
				{
					TileEntity tile = EMUtils.getTile(c.worldObj, new EMBlockPos(te).offset(f));
					
					if(tile instanceof TileFilter && ((TileFilter)tile).isWorking())
						filters++;
				}
				
				ret.add(ipe.pollutionEmission(false).clone()
						.multiply(PollutionType.AIR, 1 - EMConfig.filter_adjacent_tiles_redution * filters).multiply(PollutionType.WATER, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 2).multiply(PollutionType.SOIL, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 3)
						.multiply(PollutionType.WATER, rain ? 2 : 1).multiply(PollutionType.SOIL, rain ? 1.2F : 1).multiply(PollutionType.WATER, wir == 0 ? 1 : wir)
						);
			}
			else
			{
				if(EcologyMod.instance.tepc.hasTile(te))
				{
					if(PollutionUtils.isTEWorking(c.worldObj, te))
					{
						TEPollution tep = EcologyMod.instance.tepc.getTEP(te);
						if(tep != null)
						{
							int filters = 0;
							
							for(EnumFacing f : EnumFacing.values())
							{
								TileEntity tile = EMUtils.getTile(c.worldObj, new EMBlockPos(te).offset(f));
								
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
		profiler.endSection();
		return ret.multiplyAll(EMConfig.wptcd/60F);
	}
	
	public Map<PollutionType, Float> calculateMultipliers(Chunk c)
	{
		profiler.startSection("WPT_CALCULATING_POLLUTION_MULTIPLIERS");
		List<TileEntity> tes = new CopyOnWriteArrayList<TileEntity>(c.chunkTileEntityMap.values());
		
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
		
		profiler.endSection();
		return ret;
	}
	
	public void handleChunk(Chunk c)
	{
		profiler.startSection("WPT_HANDLING_CHUNK");
		
		if(manager!=null)
		if(PollutionEffectsConfig.isEffectActive("wasteland", manager.getPollution(c.xPosition, c.zPosition)))
		{
			for(int i = 0; i < 16; i++)
				for(int j = 0; j < 16; j++)
				{
					if(c.worldObj.rand.nextInt(10) == 0)
					{
						int strtx = c.xPosition << 4;
						int strtz = c.zPosition << 4;
					
						if(c.worldObj.getBiomeGenForCoords(i + strtx, j + strtz) != MainRegistry.biome_wasteland)
							EMUtils.setBiome(c, MainRegistry.biome_wasteland, i + strtx, j + strtz);
					}
				}
		}
		profiler.endSection();
	}
	
	
	public void forceSE()
	{
		profiler.startSection("WPT_FORCED_HANDLING_SCHEDULED_EMISSIONS");
		if(manager!=null)
		if(getScheduledEmissions().size() > 0)
		{
			List<ChunkPollution> temp = new ArrayList<ChunkPollution>();
			List<ChunkPollution> managerdata = new ArrayList<ChunkPollution>();
			managerdata.addAll(manager.getData());
			
			temp.addAll(getScheduledEmissions());
		
			for(ChunkPollution cp : temp.toArray(new ChunkPollution[temp.size()]))
					for(ChunkPollution c : managerdata)
						if(cp.getX() == c.getX() && cp.getZ() == c.getZ())
						{
							manager.addPollution(c.getX(), c.getZ(), cp.getPollution());
							scheduledEmissions.remove(cp);
						}
		}
		profiler.endSection();
	}
	
	public void waitForTickEnd()
	{
		while(isWorldTicking)
		try
		{
			sleep(5);
		} 
		catch (InterruptedException e)
		{
			return;
		}
	}
}
