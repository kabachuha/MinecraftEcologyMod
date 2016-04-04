package ccpm.fluids;

import ccpm.core.CCPM;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class CCPMFluids {

	public static Fluid concentratedPollution;
	public static Fluid pollutedWater;

	
	public static void init(/*Block poll, Block wat*/)
	{
		CCPM.log.info("Registering fluids(gases)");
		concentratedPollution = new Fluid("pollution", new ResourceLocation("ccpm:fluids/pollution_still"), new ResourceLocation("ccpm:fluids/pollution_flow"));
		concentratedPollution.setGaseous(true);
		concentratedPollution.setDensity(-600);
		concentratedPollution.setTemperature(600);
		concentratedPollution.setLuminosity(16);
		concentratedPollution.setRarity(EnumRarity.RARE);
		concentratedPollution.setUnlocalizedName("concentrated.pollution");
		//concentratedPollution.setBlock(poll);
		
		pollutedWater = new Fluid("water.polluted", new ResourceLocation("ccpm:fluids/pw_still"), new ResourceLocation("ccpm:fluids/pw_flow"));
		pollutedWater.setGaseous(false);
		pollutedWater.setRarity(EnumRarity.COMMON);
		pollutedWater.setUnlocalizedName("polluted.water");
		//pollutedWater.setBlock(wat);
		
		FluidRegistry.registerFluid(concentratedPollution);
		FluidRegistry.registerFluid(pollutedWater);
		FluidRegistry.addBucketForFluid(concentratedPollution);
		FluidRegistry.addBucketForFluid(pollutedWater);
	}
}
