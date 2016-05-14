package ccpm.integration.buildcraft;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import buildcraft.api.tiles.IHasWork;
import ccpm.fluids.CCPMFluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;


public class BCIntegration {

	public BCIntegration() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean IsHasWork(TileEntity tile)
	{
		if(tile instanceof IHasWork)
			return true;
		
		return false;
	}

	
	public static boolean isWorking(TileEntity tile)
	{
		if(IsHasWork(tile))
		{
			return ((IHasWork)tile).hasWork();
		}
		
		return false;
	}
	
	public static void regFuels()
	{
		BuildcraftFuelRegistry.fuel.addFuel(CCPMFluids.concentratedPollution,100, 14400);
		
		BuildcraftFuelRegistry.coolant.addCoolant(CCPMFluids.pollutedWater, 0.1488F);
	}
	
	public static void regRecipes()
	{
		//BuildcraftRecipeRegistry.complexRefinery.addDistilationRecipe(new FluidStack(CCPMFluids.pollutedWater, FluidContainerRegistry.BUCKET_VOLUME), new FluidStack(CCPMFluids.concentratedPollution, 100), new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME), 2000, true);
		BuildcraftRecipeRegistry.refinery.addRecipe("ccpm:pollutedWaterToCCPB", new FluidStack(CCPMFluids.pollutedWater, 1), FluidRegistry.getFluidStack("oil", 1), new FluidStack(CCPMFluids.concentratedPollution, 1), 300, 2);
	}
}
