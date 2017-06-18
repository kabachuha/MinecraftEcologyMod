package ecomod.api;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionEmissionEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EcomodAPI
{
	/**
	 * 
	 * 
	 * @param world The world
	 * @param chunkLoc The location of the chunk
	 * @param emission Amount of pollution emitted. If it is negative pollution is reduced
	 * @param scheduled Determines whether the emission event should be put in queue and handled by the thread on its next run if true or immediately by the PollutionManager if false. It's recommended to use the first option (true).
	 * @return Whether the event had passed and hadn't been canceled
	 * 
	 * @see ecomod.api.pollution.PollutionEmissionEvent
	 * @see net.minecraft.world.chunk.Chunk
	 * @see ecomod.api.pollution.PollutionData  
	 */
	public static boolean emitPollution(World world, Pair<Integer,Integer> chunkLoc, PollutionData emission, boolean scheduled)
	{
		PollutionEmissionEvent em = new PollutionEmissionEvent(world, chunkLoc.getLeft(), chunkLoc.getRight(), emission, scheduled);
		
		return MinecraftForge.EVENT_BUS.post(em) && !em.isCanceled();
	}
	
	
}
