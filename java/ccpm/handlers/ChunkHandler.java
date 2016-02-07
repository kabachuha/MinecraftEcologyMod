package ccpm.handlers;

import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.utils.PollutionUtils;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class ChunkHandler {

	public ChunkHandler() {
		
	}
	
	
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		//Adding Chunk to PollutionManager
		if(WorldHandler.isLoaded)
		{
		if(event.getChunk()!=null)
		if(event.world != null && !event.world.isRemote && event.world.provider.dimensionId == 0)
         PollutionUtils.increasePollution(0, event.getChunk());
		}
		else
		{
			//Hm, seems event onChunkLoad fires before WorldLoad event
			//Let's create List in WorldHandler and put them chunks, that loaded before world
			if(event.getChunk()!=null)
				if(event.world != null && !event.world.isRemote && event.world.provider.dimensionId == 0)
				{
					ChunkPollution cp = new ChunkPollution();
					cp.setX(event.getChunk().xPosition);
					cp.setZ(event.getChunk().zPosition);
					cp.setPollution(0);
					WorldHandler.preChunks.add(cp);
				}
		}
	}
	
	@SubscribeEvent
	public void onTreeGrow(SaplingGrowTreeEvent event)
	{
		if(event.world.getBlock(event.x, event.y, event.z) != null && !event.world.isRemote)
		{
			// You can plant trees indoors normal
			// TODO Make checking isOutside more hardcore
			if(event.world.canBlockSeeTheSky(event.x, event.y, event.z))
			{
				Chunk c = event.world.getChunkFromBlockCoords(event.x, event.z);
				
				for(int i = 0; i < WorldHandler.instance.pm.chunksPollution.getCP().length; i++)
				if(WorldHandler.instance.pm.chunksPollution.getCP()[i].getX() == c.xPosition && WorldHandler.instance.pm.chunksPollution.getCP()[i].getZ() == c.zPosition)
				{
					if(WorldHandler.instance.pm.chunksPollution.getCP(i).getPollution() > 200000)
					{
						event.world.setBlock(event.x, event.y, event.z, Blocks.deadbush, 0, 2);
						event.setResult(Result.DENY);
						//event.setCanceled(true);
						break;
					}
					else
					{
						PollutionUtils.increasePollution(-10, c);
						event.setResult(Result.ALLOW);
						break;
					}
				}
			}
		}
	}

}
