package ecomod.common.intermod.jei;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.core.EMConsts;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class PollutionEffectIngrHelper implements IIngredientHelper<AnalyzerPollutionEffect>
{
	@Override
	public List<AnalyzerPollutionEffect> expandSubtypes(List<AnalyzerPollutionEffect> ingredients)
	{
		return ingredients;
	}

	@Override
	public AnalyzerPollutionEffect getMatch(Iterable<AnalyzerPollutionEffect> ingredients, AnalyzerPollutionEffect ingredientToMatch)
	{
		for(AnalyzerPollutionEffect effect : ingredients)
			if(effect.equals(ingredientToMatch))
				return effect;
		
		return null;
	}

	@Override
	public String getDisplayName(AnalyzerPollutionEffect ingredient)
	{
		return I18n.format("gui.jei.ingredient.ecomod.pollution_effect")+' '+I18n.format(ingredient.getHeader());
	}

	@Override
	public String getUniqueId(AnalyzerPollutionEffect ingredient)
	{
		return "ecomod.pollution_effect:"+ingredient.getId();
	}

	@Override
	public String getWildcardId(AnalyzerPollutionEffect ingredient)
	{
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(AnalyzerPollutionEffect ingredient)
	{
		return EMConsts.modid;
	}

	@Override
	public Iterable<Color> getColors(AnalyzerPollutionEffect ingredient)
	{
		return Collections.emptySet();
	}

	@Override
	public String getErrorInfo(AnalyzerPollutionEffect ingredient) {
		if(ingredient==null)
			return "null";
		
		return "PollutionEffect"+ingredient.toString();
	}
}
