package ccpm.handlers;

import ccpm.ecosystem.PollutionManager;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class WorldProcessingThread extends Thread {

	private World world;
	private PollutionManager pm;
	
	public WorldProcessingThread(World w, PollutionManager poma)
	{
		super("[CCPM]WorldProcessingThread for world "+w.getWorldInfo().getWorldName());
		//To shut down with Minecraft
	      this.setDaemon(true);
	      //Working in background
	      this.setPriority(MIN_PRIORITY);
	      world = w;
	      pm = poma;
	      FMLLog.info("Initialising World Processing Thread");
	}

	@Override
	public void run()
	{
		while(true)
		{
			FMLLog.info("Starting processing chunks");
			if(pm.isSaving)
			{
				FMLLog.info("Unable to process chunks while PM is saving");
				continue;
			}
			ChunkPollution[] cp = pm.chunksPollution.getCP();
			
			if(cp!=null && cp.length>0)
			{
			FMLLog.info(cp.length+" chunks will be processed");
			if(pm.isSaving)
			{
				FMLLog.info("Unable to process chunks while PM is saving");
				continue;
			}
			for(int i = 0; i<cp.length; i++)
			{
			      Chunk chunk = world.getChunkFromChunkCoords(cp[i].getX(), cp[i].getZ());
			      
			      if(chunk.isChunkLoaded)
			      {
			    	  PollutionUtils.increasePollution(PollutionUtils.processChunk(chunk), chunk);
			    	  PollutionUtils.doPollutionEffects(chunk, PollutionUtils.getChunkPollution(chunk));
			      }
			}
			}
			
			FMLLog.info("Chunks processed, sleeping for "+CCPMConfig.processingDelay+" milliseconds");
			try
			{
				this.sleep(CCPMConfig.processingDelay);
			}
			catch (InterruptedException e)
			{
				FMLLog.info("World processing thread has interrupted!");
				return;
			}
		}
	}
    
}
