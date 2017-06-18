package ecomod.common.pollution.handlers;

import ecomod.core.*;
import ecomod.core.stuff.EMConfig;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.IGarbage;
import ecomod.api.pollution.PollutionEmissionEvent;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.*;
import ecomod.common.pollution.PollutionManager.ChunkPollution;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.utils.EMUtils;



/**
 * A Handler which setups pollution stuff and stores data about existing pollution managers and threads<br><br>
 * 
 * Server side only!
 * 
 * @author Artem226
 */
public class PollutionHandler
{
	public Map<String, WorldProcessingThread> threads = new HashMap<String, WorldProcessingThread>();
	
	//World handlers
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldLoad(WorldEvent.Load event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onWorldLoad");
		
		boolean b1 = false;
		
		for(int i : EMConfig.allowedDims)
			if(i == w.provider.getDimension())
				b1 = true;
		
		if(!b1)
			return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key) && PollutionUtils.genPMid(threads.get(key).getPM()) == key)
		{
			return;
		}
		
		PollutionManager pm = new PollutionManager(w);
		
		EcologyMod.log.debug("Creating PollutionManager for "+key);
		
		if(pm.load())
		{
			WorldProcessingThread thr = new WorldProcessingThread(pm);
			threads.put(PollutionUtils.genPMid(pm), thr);
			thr.start();
		}
		else
		{
			//EcologyMod.log.error("Unable to load the pollution manager and start the thread for dim "+w.provider.getDimension());
			pm = new PollutionManager(w);
			WorldProcessingThread thr = new WorldProcessingThread(pm);
			threads.put(PollutionUtils.genPMid(pm), thr);
			thr.start();
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldSave(WorldEvent.Save event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onWorldSave");
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key) && threads.get(key) != null && !threads.get(key).isWorking() && PollutionUtils.genPMid(threads.get(key).getPM()) == key)
		{
			WorldProcessingThread t = threads.get(key);
			
			try
			{
				t.wait();
			
				t.getPM().save();
			
				t.notify();
			}
			catch(InterruptedException e)
			{
				EcologyMod.log.error(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onWorldUnload");
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key) && PollutionUtils.genPMid(threads.get(key).getPM()) == key)
		{
			threads.get(key).interrupt();
			threads.remove(key);
		}
	}
	
	//Chunk handlers
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onChunkLoad");
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
		
			Pair<Integer, Integer> coord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);
		
			synchronized(wpt.getLoadedChunks())
			{
				if(!wpt.getLoadedChunks().contains(coord))
					wpt.getLoadedChunks().add(coord);
			}
		}
	}
	
	
	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onChunkUnload");
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
		
			Pair<Integer, Integer> coord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);
		
			synchronized(wpt.getLoadedChunks())
			{
				if(wpt.getLoadedChunks().contains(coord))
					wpt.getLoadedChunks().remove(coord);
			}
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEmission(PollutionEmissionEvent event)
	{		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onEmission");
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
			
			if(event.isScheduled())
			{
				synchronized(wpt.getScheduledEmissions())
				{
					wpt.getScheduledEmissions().add(new ChunkPollution(event.getChunkX(), event.getChunkZ(), event.getEmission()));
				}
			}
			else
			{
				wpt.getPM().setChunkPollution(new ChunkPollution(event.getChunkX(), event.getChunkZ(), event.getEmission()));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onItemExpire(ItemExpireEvent event)
	{
		if(event.isCanceled())return;
		
		EntityItem ei = event.getEntityItem();
		
		World w = ei.getEntityWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.info("PollutionHandler#onItemExpire");
		
		ItemStack is = ei.getEntityItem();
		
		if(EMConfig.item_blacklist.contains(is.getItem().getRegistryName().toString()))
			return;
		
		boolean isInWater = EMUtils.countWaterInRadius(w, ei.getPosition(), 1) >= 1;
		
		if(is.getItem() instanceof IGarbage)
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(ei.getPosition()), ((IGarbage)is.getItem()).getPollutionOnDecay().multiplyAll(is.getCount()).multiply(PollutionType.WATER, isInWater ? 2 : 1), true);
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(ei.getPosition()), EMConfig.pp2di.multiplyAll(is.getCount()).multiply(PollutionType.WATER, isInWater ? 2 : 1), true);
		}
	}
}
