package ecomod.common.intermod.jei;

import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.config.PollutionSourcesConfig;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperAirFiltration implements IRecipeWrapper
{
	public RecipeWrapperAirFiltration()
	{
		
	}
	
	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInput(PollutionData.class, getInput());
		ingredients.setOutput(FluidStack.class, getOutput());
	}

	public static PollutionData getInput()
	{
		return PollutionSourcesConfig.getSource("advanced_filter_reduction").multiplyAll(-1);
	}
	
	public static FluidStack getOutput()
	{
		PollutionData adv_filter_reduction = PollutionSourcesConfig.getSource("advanced_filter_reduction").multiplyAll(-1);
		return new FluidStack(EcomodStuff.concentrated_pollution, MathHelper.floor(adv_filter_reduction.getAirPollution()+2 * adv_filter_reduction.getWaterPollution()+ 4 * adv_filter_reduction.getSoilPollution()));
	}
}
