package ecomod.common.intermod.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.blocks.BlockFrame;
import ecomod.common.pollution.config.PollutionSourcesConfig;
import ecomod.core.stuff.EMConfig;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@mezz.jei.api.JEIPlugin
public class EcomodJEIPlugin implements IModPlugin
{
	@Override
	public void registerIngredients(IModIngredientRegistration registry)
	{
		registry.register(PollutionData.class, Collections.emptySet(), new PollutionDataIngrHelper(), new PollutionDataIngrRender(true));
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		if(PollutionSourcesConfig.hasSource("advanced_filter_reduction"))
			registry.addRecipeCategories(new RecipeCategoryAirFiltration(registry.getJeiHelpers().getGuiHelper()));
		
		if(EMConfig.enable_manually_assembly)
			registry.addRecipeCategories(new RecipeCategoryManuallyAssembly(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void register(IModRegistry registry)
	{
		if(PollutionSourcesConfig.hasSource("advanced_filter_reduction"))
		{
			registry.addRecipeCatalyst(new ItemStack(EcomodBlocks.ADVANCED_FILTER), "ecomod.air_filtration");
		
			registry.addRecipes(Collections.singleton(new RecipeWrapperAirFiltration()), "ecomod.air_filtration");
		}
		
		if(EMConfig.enable_manually_assembly)
		{
			List<RecipeWrapperManuallyAssembly> assembly_recipes = new ArrayList<RecipeWrapperManuallyAssembly>();
			ItemStack frame = new ItemStack(EcomodBlocks.FRAME, 1, 0);
			ItemStack advanced_frame = new ItemStack(EcomodBlocks.FRAME, 1, 1);
			
			assembly_recipes.add(new RecipeWrapperManuallyAssembly(frame, Collections.singletonList(new ItemStack(EcomodItems.CORE, 1, 0)), Collections.singletonList(new ItemStack(EcomodBlocks.FILTER))));
			assembly_recipes.add(new RecipeWrapperManuallyAssembly(frame, Collections.singletonList(new ItemStack(EcomodItems.CORE, 1, 2)), Collections.singletonList(new ItemStack(EcomodBlocks.ANALYZER))));
			assembly_recipes.add(new RecipeWrapperManuallyAssembly(advanced_frame, Collections.singletonList(new ItemStack(EcomodItems.CORE, 1, 1)), Collections.singletonList(new ItemStack(EcomodBlocks.ADVANCED_FILTER))));
			
			if(EMConfig.is_oc_analyzer_interface_crafted_by_right_click && BlockFrame.oc_adapter != null)
				assembly_recipes.add(new RecipeWrapperManuallyAssembly(frame, Collections.singletonList(BlockFrame.oc_adapter), Collections.singletonList(new ItemStack(EcomodBlocks.OC_ANALYZER_ADAPTER))));
			
			registry.addRecipes(assembly_recipes, "ecomod.manually_assembly");
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		
	}
}
