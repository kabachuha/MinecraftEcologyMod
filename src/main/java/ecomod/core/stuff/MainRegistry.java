package ecomod.core.stuff;

import ecomod.api.EcomodStuff;
import ecomod.client.gui.EMGuiHandler;
import ecomod.common.utils.EMUtils;
import ecomod.common.world.FluidPollution;
import ecomod.core.EcologyMod;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
		SoundEvent.REGISTRY.putObject(EMUtils.resloc("advanced_filter_working"), EcomodStuff.advanced_filter_working = new SoundEvent(EMUtils.resloc("advanced_filter_working")));
		SoundEvent.REGISTRY.putObject(EMUtils.resloc("analyzer"), EcomodStuff.analyzer = new SoundEvent(EMUtils.resloc("analyzer")));
		
		IGuiHandler igh = new EMGuiHandler();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(EcologyMod.instance, igh);
	}
	
	public static void doPostInit()
	{
		
	}
	
	
	public static void initAnalyzerPollutionEffects()
	{
		
	}
}
