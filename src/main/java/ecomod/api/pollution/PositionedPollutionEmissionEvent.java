package ecomod.api.pollution;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

@Cancelable
public class PositionedPollutionEmissionEvent extends Event
{
	private final int x;
	private final int y;
	private final int z;
	
	private final World world;
	
	@Nonnull
	private PollutionData emission;
	
	private final boolean scheduled;
	
	public PositionedPollutionEmissionEvent(@Nonnull World world, int x, int y, int z, @Nonnull PollutionData emission, boolean scheduled)
	{
		this.world = world;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.emission = emission;
		
		this.scheduled = scheduled;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public PollutionData getEmission()
	{
		return emission;
	}
	
	public void setEmission(PollutionData new_emission)
	{
		emission = new_emission;
	}
	
	public boolean isScheduled()
	{
		return scheduled;
	}
}
