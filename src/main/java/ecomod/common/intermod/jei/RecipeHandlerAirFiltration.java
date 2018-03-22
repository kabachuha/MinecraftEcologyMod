package ecomod.common.intermod.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeHandlerAirFiltration implements IRecipeHandler<RecipeWrapperAirFiltration>
{
	@Override
	public Class<RecipeWrapperAirFiltration> getRecipeClass()
	{
		return RecipeWrapperAirFiltration.class;
	}

	@Override
	public String getRecipeCategoryUid()
	{
		return "ecomod.air_filtration";
	}

	@Override
	public String getRecipeCategoryUid(RecipeWrapperAirFiltration recipe)
	{
		return "ecomod.air_filtration";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(RecipeWrapperAirFiltration recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(RecipeWrapperAirFiltration recipe)
	{
		return true;
	}
}
