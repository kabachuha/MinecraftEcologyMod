package ecomod.core.stuff;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.common.blocks.compat.BlockAnalyzerAdapter;
import ecomod.common.tiles.compat.TileAnalyzerAdapter;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import net.minecraftforge.fml.common.API;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
	
	public static void OCpreInit()
	{
		EcomodBlocks.OC_ANALYZER_ADAPTER = new BlockAnalyzerAdapter().setUnlocalizedName(EMConsts.modid + ".oc_analyzer_adapter");
		EMBlocks.regBlock(EcomodBlocks.OC_ANALYZER_ADAPTER, "analyzer_adapter");
		GameRegistry.registerTileEntity(TileAnalyzerAdapter.class, EMUtils.resloc("analyzer_adapter").toString());
	}
}
