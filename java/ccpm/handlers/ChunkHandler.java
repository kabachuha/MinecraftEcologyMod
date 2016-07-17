package ccpm.handlers;

import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.utils.PollutionUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
		if(event.getWorld() != null && !event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
         PollutionUtils.increasePollution(0, event.getChunk());
		}
		else
		{
			//Hm, seems event onChunkLoad fires before WorldLoad event
			//Let's create List in WorldHandler and put them chunks, that loaded before world
			if(event.getChunk()!=null)
				if(event.getWorld() != null && !event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
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
		if(event.getWorld().getBlockState(event.getPos()) != null && !event.getWorld().isRemote)
		{
			// You can plant trees indoors normal
			// TODO Make checking isOutside more hardcore
			if(event.getWorld().canBlockSeeSky(event.getPos()))
			{
				Chunk c = event.getWorld().getChunkFromBlockCoords(event.getPos());
				
				for(int i = 0; i < WorldHandler.instance.pm.chunksPollution.getCP().length; i++)
				if(WorldHandler.instance.pm.chunksPollution.getCP()[i].getX() == c.xPosition && WorldHandler.instance.pm.chunksPollution.getCP()[i].getZ() == c.zPosition)
				{
					if(WorldHandler.instance.pm.chunksPollution.getCP(i).getPollution() > 200000)
					{
						event.getWorld().setBlockState(event.getPos(), Blocks.DEADBUSH.getDefaultState(), 2);
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
	
	
	@SubscribeEvent
	public void onExplosion(ExplosionEvent event)
	{
		if(event.getExplosion() != null && event.getWorld() != null && !event.getWorld().isRemote)
		{
			Chunk c = event.getWorld().getChunkFromBlockCoords(new BlockPos(event.getExplosion().getPosition()));
			
			float size = ReflectionHelper.getPrivateValue(Explosion.class, event.getExplosion(), 8);
			
			if(c!=null &c.isLoaded())
				PollutionUtils.increasePollution(size * 10, c);
		}
	}

}
