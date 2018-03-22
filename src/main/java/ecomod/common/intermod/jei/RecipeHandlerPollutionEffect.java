package ecomod.common.intermod.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeHandlerPollutionEffect implements IRecipeHandler<RecipeWrapperPollutionEffect>
{
	@Override
	public Class<RecipeWrapperPollutionEffect> getRecipeClass()
	{
		return RecipeWrapperPollutionEffect.class;
	}

	@Override
	public String getRecipeCategoryUid()
	{
		return "ecomod.pollution_effects";
	}

	@Override
	public String getRecipeCategoryUid(RecipeWrapperPollutionEffect recipe) 
	{
		return "ecomod.pollution_effects";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(RecipeWrapperPollutionEffect recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(RecipeWrapperPollutionEffect recipe)
	{
		return true;
	}
}
