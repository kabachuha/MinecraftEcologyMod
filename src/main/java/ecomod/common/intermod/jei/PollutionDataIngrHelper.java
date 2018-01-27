package ecomod.common.intermod.jei;

import ecomod.api.pollution.PollutionData;
import ecomod.core.EMConsts;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class PollutionDataIngrHelper implements IIngredientHelper<PollutionData>
{
	@Override
	public List<PollutionData> expandSubtypes(List<PollutionData> ingredients)
	{
		return ingredients;
	}

	@Override
	@Nullable
	public PollutionData getMatch(Iterable<PollutionData> ingredients, PollutionData ingredientToMatch)
	{
		for(PollutionData data : ingredients)
			if(data.equals(ingredientToMatch))
				return data;
		
		return null;
	}

	@Override
	public String getDisplayName(PollutionData ingredient)
	{
		return I18n.format("gui.jei.ingredient.ecomod.pollution", ingredient.getAirPollution(), ingredient.getWaterPollution(), ingredient.getSoilPollution());
	}

	@Override
	public String getUniqueId(PollutionData ingredient)
	{
		return "ecomod.pollution:air="+ingredient.getAirPollution()+"water="+ingredient.getWaterPollution()+"soil="+ingredient.getSoilPollution();
	}

	@Override
	public String getWildcardId(PollutionData ingredient)
	{
		return getUniqueId(ingredient);
	}

	@Override
	public String getModId(PollutionData ingredient)
	{
		return EMConsts.modid;
	}

	@Override
	public Iterable<Color> getColors(PollutionData ingredient) {
		return Collections.singleton(new Color(66, 80, 67));
	}

	@Override
	public String getResourceId(PollutionData ingredient)
	{
		return "ecomod.pollution";
	}

	@Override
	public PollutionData copyIngredient(PollutionData ingredient)
	{
		return ingredient.clone();
	}

	@Override
	public String getErrorInfo(PollutionData ingredient)
	{
		if(ingredient==null)
			return "null";
		
		return "Pollution"+ingredient.toString();
	}

	@Override
	public ItemStack getCheatItemStack(PollutionData ingredient) {
		return ItemStack.EMPTY;
	}
}
