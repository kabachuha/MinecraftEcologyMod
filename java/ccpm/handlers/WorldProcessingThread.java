package ccpm.handlers;

import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import scala.collection.parallel.ParIterableLike.Copy;

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
	      CCPM.log.info("Initialising World Processing Thread");
	}

	@Override
	public void run()
	{
		while(true)
		{
			if(!WorldHandler.isLoaded)
				continue;
			
			//if(!PlayerHandler.firstPlayerJoinedWorld)
			//	continue;
			
			CCPM.log.info("Starting processing chunks");
			if(pm.isSaving)
			{
				CCPM.log.info("Unable to process chunks while PM is saving");
				continue;
			}
			ChunkPollution[] cp = pm.chunksPollution.getCP();
			cp = cp.clone();
			
			if(world == null || world.isRemote)
				continue;
			
			if(cp!=null && cp.length>0)
			{
			CCPM.log.info(cp.length+" chunks will be processed");
			if(pm.isSaving)
			{
				CCPM.log.warning("Unable to process chunks while PM is saving");
				continue;
			}
			for(int i = 0; i<cp.length; i++)
			{
			      Chunk chunk = world.getChunkFromChunkCoords(cp[i].getX(), cp[i].getZ());

			      
			      if(chunk != null && chunk.isLoaded())
			      {
			    	  PollutionUtils.increasePollution(PollutionUtils.processChunk(chunk), chunk);
			    	  PollutionUtils.doPollutionEffects(chunk, PollutionUtils.getChunkPollution(chunk));
			      }
			}
			}
			
			CCPM.log.info("Chunks processed, sleeping for "+CCPMConfig.processingDelay+" milliseconds");
			try
			{
				this.sleep(CCPMConfig.processingDelay);
			}
			catch (InterruptedException e)
			{
				CCPM.log.info("World processing thread has interrupted!");
				return;
			}
		}
	}
    
}
