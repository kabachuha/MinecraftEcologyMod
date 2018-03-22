package ecomod.common.intermod.jei;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.AnalyzerPollutionEffect;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapperPollutionEffect implements IRecipeWrapper
{
	private final AnalyzerPollutionEffect effect;
	public RecipeWrapperPollutionEffect(AnalyzerPollutionEffect e)
	{
		effect = e;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInput(PollutionData.class, effect.getTriggerringPollution());
		ingredients.setOutput(AnalyzerPollutionEffect.class, effect);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		String text = I18n.format(effect.getHeader());
		
		minecraft.fontRendererObj.drawString(text, (int)(recipeWidth/2F - minecraft.fontRendererObj.getStringWidth(text) / 2F), 0, Color.BLACK.getRGB());
		
		int x = 0;
		int y = 75 + minecraft.fontRendererObj.FONT_HEIGHT;
		
		text = I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger") + ' ' + (effect.getTriggeringType() == IAnalyzerPollutionEffect.TriggeringType.AND ? I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger.and") : I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger.or")); 
		
		minecraft.fontRendererObj.drawString(text, (int)(recipeWidth/2F - minecraft.fontRendererObj.getStringWidth(text) / 2F), y, Color.MAGENTA.getRGB());
		
		y += minecraft.fontRendererObj.FONT_HEIGHT * 2;
		
		text = I18n.format(effect.getDescription());
		
		minecraft.fontRendererObj.drawSplitString(I18n.format(effect.getDescription()), x, y, recipeWidth, Color.DARK_GRAY.getRGB());
	}
	
	public AnalyzerPollutionEffect getEffect()
	{
		return effect;
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		if(mouseX >= 0 && mouseX <= RecipeCategoryPollutionEffects.recipeWidth)
		if(mouseY >= 73 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT && mouseY < 77 + 2 * Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT)
			return Collections.singletonList(effect.getTriggeringType() == IAnalyzerPollutionEffect.TriggeringType.AND ? I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger.and.desc") : I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger.or.desc"));
		
		return Collections.emptyList();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((effect == null) ? 0 : effect.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RecipeWrapperPollutionEffect)
			return getEffect().equals(((RecipeWrapperPollutionEffect)obj).getEffect());
		return false;
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
		return null;
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
}
