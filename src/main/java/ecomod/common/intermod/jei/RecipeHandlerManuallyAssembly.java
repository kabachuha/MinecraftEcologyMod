package ecomod.common.intermod.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class RecipeHandlerManuallyAssembly implements IRecipeHandler<RecipeWrapperManuallyAssembly>
{
	@Override
	public Class<RecipeWrapperManuallyAssembly> getRecipeClass()
	{
		return RecipeWrapperManuallyAssembly.class;
	}
	
	@Override
	public String getRecipeCategoryUid()
	{
		return "ecomod.manually_assembly";
	}

	@Override
	public String getRecipeCategoryUid(RecipeWrapperManuallyAssembly recipe)
	{
		return "ecomod.manually_assembly";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(RecipeWrapperManuallyAssembly recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(RecipeWrapperManuallyAssembly recipe)
	{
		return true;
	}
}
