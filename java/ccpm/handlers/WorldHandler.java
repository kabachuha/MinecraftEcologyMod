package ccpm.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ccpm.ecosystem.PollutionManager;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import java.util.Arrays;
import java.util.LinkedList;

public class WorldHandler {

	public PollutionManager pm = null;
	
	public static WorldHandler instance = null;
	
	public static boolean isLoaded = false;
	
	public static List<ChunkPollution> preChunks = new ArrayList<ChunkPollution>();
	
	public WorldHandler() {
		instance = this;
	}

	WorldProcessingThread thread;
	
	@SubscribeEvent
	public void onLoad(WorldEvent.Load event)
	{
		World w = event.world;

		if(w !=null && !w.isRemote && w.provider.dimensionId == 0 && !isLoaded)
		{
			pm = new PollutionManager(w);
			
			pm.load();
			
			isLoaded=true;
			
			//Add all chunks loaded before world
			if(!preChunks.isEmpty())
			{
				FMLLog.info("[CCPM] Adding chunks loaded before Pollution Manager's initialization");
				ChunkPollution cp[] = pm.chunksPollution.getCP();
				
				List<ChunkPollution> lCp = new LinkedList<ChunkPollution>(Arrays.asList(cp));
				
				lCp.addAll(preChunks);
				
				pm.chunksPollution.setCP(lCp.toArray(new ChunkPollution[lCp.size()]));
				
				//Clear the preChunks list
				preChunks.clear();
			}
			
			
			thread = new WorldProcessingThread(w, pm);
			
			thread.start();
		}
	}
	
	@SubscribeEvent
	public void onSave(WorldEvent.Save event)
	{
		World w = event.world;

		if(w !=null && !w.isRemote && w.provider.dimensionId == 0)
		{
            if(pm != null && pm.chunksPollution != null && pm.chunksPollution.getCP()!=null && pm.chunksPollution.getCP().length>0)
            	pm.save();
		}
	}
	
	@SubscribeEvent
	public void onUnLoad(WorldEvent.Unload event)
	{
		World w = event.world;

		if(w !=null && !w.isRemote && w.provider.dimensionId == 0)
		{
			isLoaded=false;
			if(thread != null && thread.isAlive())
				thread.interrupt();
		}
		
	}
}
