package ecomod.core.stuff;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import ecomod.api.EcomodStuff;
import net.minecraftforge.fml.common.API;
import net.minecraftforge.fml.common.ModAPIManager;

public class EMIntermod
{
	public static void registerBCFuels()
	{
		if(ModAPIManager.INSTANCE.hasAPI("BuildCraftAPI|fuels"))
		{
			if(BuildcraftFuelRegistry.fuel != null)
			{
				BuildcraftFuelRegistry.fuel.addFuel(EcomodStuff.concentrated_pollution, EMConfig.fuel_concentrated_pollution_burn_energy, EMConfig.fuel_concentrated_pollution_burn_time);
			}
		}
	}
}
