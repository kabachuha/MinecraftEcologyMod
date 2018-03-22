package ecomod.common.intermod.jei;

import java.util.Collections;
import java.util.List;

import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.config.PollutionSourcesConfig;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
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
		return new FluidStack(EcomodStuff.concentrated_pollution, (int)Math.floor(adv_filter_reduction.getAirPollution()+2 * adv_filter_reduction.getWaterPollution()+ 4 * adv_filter_reduction.getSoilPollution()));
	}

	@Override
	public List getInputs() {
		return null;
	}

	@Override
	public List getOutputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return Collections.singletonList(getOutput());
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
