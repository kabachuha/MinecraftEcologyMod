package ecomod.api.pollution;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.world.World;

@Cancelable
public class PollutionEmissionEvent extends Event
{
	private final World w;
	
	private final int chunkX;
	private final int chunkZ;
	
	@Nonnull
	private PollutionData pollution;
	
	private final boolean scheduled;
	
	/**
	 * A cancelable event which is fired to change pollution in a chunk with given coordinates. 
	 * 
	 * @param world The world
	 * @param x The X coordinate of the chunk
	 * @param z The Z coordinate of the chunk
	 * @param emission	Pollution emitted into the world 
	 * @param scheduled Determines whether this event should be put in queue and handled by the thread on its next run if true or immediately by the PollutionManager if false. It's recommended to use the first option (true).
	 */
	public PollutionEmissionEvent(@Nonnull World world, int x, int z, @Nonnull PollutionData emission, boolean scheduled)
	{
		w = world;
		
		chunkX = x;
		chunkZ = z;
		
		pollution = emission;
		
		this.scheduled = scheduled;
	}
	
	public World getWorld()
	{
		return w;
	}
	
	public int getChunkX()
	{
		return chunkX;
	}
	
	public int getChunkZ()
	{
		return chunkZ;
	}
	
	public PollutionData getEmission()
	{
		return pollution;
	}
	
	public void setEmission(PollutionData new_emission)
	{
		pollution = new_emission;
	}
	
	public boolean isScheduled()
	{
		return scheduled;
	}
}
