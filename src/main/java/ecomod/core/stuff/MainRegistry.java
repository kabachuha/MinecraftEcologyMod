package ecomod.core.stuff;

import ecomod.api.EcomodStuff;
import ecomod.common.utils.EMUtils;
import ecomod.common.world.FluidPollution;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidRegistry;

public class MainRegistry
{
	public static void doPreInit()
	{
		EcomodStuff.concentrated_pollution = new FluidPollution();
		
		FluidRegistry.registerFluid(EcomodStuff.concentrated_pollution);
		
		FluidRegistry.addBucketForFluid(EcomodStuff.concentrated_pollution);
		
		EMBlocks.doPreInit();
		EMTiles.doPreInit();
	}
	
	public static void doInit()
	{
		EcomodStuff.advanced_filter_working = new SoundEvent(EMUtils.resloc("advanced_filter_working"));
		
		SoundEvent.REGISTRY.putObject(EMUtils.resloc("advanced_filter_working"), EcomodStuff.advanced_filter_working);
	}
	
	public static void doPostInit()
	{
		
	}
}
