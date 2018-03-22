package ecomod.common.intermod.jei;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.PollutionData;
import ecomod.common.blocks.BlockFrame;
import ecomod.common.pollution.config.PollutionSourcesConfig;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@mezz.jei.api.JEIPlugin
public class EcomodJEIPlugin implements IModPlugin
{
	@Override
	public void registerIngredients(IModIngredientRegistration registry)
	{
		registry.register(PollutionData.class, Collections.emptySet(), new PollutionDataIngrHelper(), new PollutionDataIngrRender());
		registry.register(AnalyzerPollutionEffect.class, Collections.emptySet(), new PollutionEffectIngrHelper(), new PollutionEffectIngrRender());
	}

	@Override
	public void register(IModRegistry registry)
	{
		if(PollutionSourcesConfig.hasSource("advanced_filter_reduction"))
		{
			registry.addRecipeCategories(new RecipeCategoryAirFiltration(registry.getJeiHelpers().getGuiHelper()));
			registry.addRecipeHandlers(new RecipeHandlerAirFiltration());
		}
		
		if(EMConfig.enable_manually_assembly)
		{
			registry.addRecipeCategories(new RecipeCategoryManuallyAssembly(registry.getJeiHelpers().getGuiHelper()));
			registry.addRecipeHandlers(new RecipeHandlerManuallyAssembly());
		}
		
		if(EMConfig.pollution_effects_as_recipe_category)
		{
			registry.addRecipeCategories(new RecipeCategoryPollutionEffects(registry.getJeiHelpers().getGuiHelper()));
			registry.addRecipeHandlers(new RecipeHandlerPollutionEffect());
		}
		
		if(PollutionSourcesConfig.hasSource("advanced_filter_reduction"))
		{
			registry.addRecipeCategoryCraftingItem(new ItemStack(EcomodBlocks.ADVANCED_FILTER), "ecomod.air_filtration");
		
			registry.addRecipes(Collections.singletonList(new RecipeWrapperAirFiltration()));
		}
		
		if(EMConfig.enable_manually_assembly)
		{
			List<RecipeWrapperManuallyAssembly> assembly_recipes = new ArrayList<>();
			ItemStack frame = new ItemStack(EcomodBlocks.FRAME, 1, 0);
			ItemStack advanced_frame = new ItemStack(EcomodBlocks.FRAME, 1, 1);
			
			assembly_recipes.add(new RecipeWrapperManuallyAssembly(frame, Collections.singletonList(new ItemStack(EcomodItems.CORE, 1, 0)), new ItemStack(EcomodBlocks.FILTER)));
			assembly_recipes.add(new RecipeWrapperManuallyAssembly(frame, Collections.singletonList(new ItemStack(EcomodItems.CORE, 1, 2)), new ItemStack(EcomodBlocks.ANALYZER)));
			assembly_recipes.add(new RecipeWrapperManuallyAssembly(advanced_frame, Collections.singletonList(new ItemStack(EcomodItems.CORE, 1, 1)), new ItemStack(EcomodBlocks.ADVANCED_FILTER)));
			
			if(EMConfig.is_oc_analyzer_interface_crafted_by_right_click && BlockFrame.oc_adapter != null && BlockFrame.oc_adapter.stackSize > 0)
				assembly_recipes.add(new RecipeWrapperManuallyAssembly(frame, Collections.singletonList(BlockFrame.oc_adapter), new ItemStack(EcomodBlocks.OC_ANALYZER_ADAPTER)));
			
			registry.addRecipes(assembly_recipes);
		}
		
		if(EMConfig.pollution_effects_as_recipe_category)
		{
			registry.addRecipeCategoryCraftingItem(new ItemStack(EcomodBlocks.ANALYZER), "ecomod.pollution_effects");
		
			List<RecipeWrapperPollutionEffect> pollution_effects = new ArrayList<>();
		
			for(IAnalyzerPollutionEffect effect : EcomodStuff.pollution_effects.values())
				pollution_effects.add(new RecipeWrapperPollutionEffect(new AnalyzerPollutionEffect(effect)));
		
			registry.addRecipes(pollution_effects);
		}
	}
	
	private static IJeiRuntime jei;

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
	{
		jei = jeiRuntime;
	}
	
	public static void updateEffectsCategory()
	{
		if(jei != null && EMConfig.pollution_effects_as_recipe_category)
		{
			List<IRecipeWrapper> recipes = jei.getRecipeRegistry().getRecipeWrappers(jei.getRecipeRegistry().getRecipeCategories(Collections.singletonList("ecomod.pollution_effects")).get(0));
			
			for(IRecipeWrapper wrap : recipes)
				jei.getRecipeRegistry().removeRecipe(wrap);
			
			for(IAnalyzerPollutionEffect effect : EcologyMod.proxy.getClientHandler().pollution_effects.values())
			{
				RecipeWrapperPollutionEffect rec = new RecipeWrapperPollutionEffect(new AnalyzerPollutionEffect(effect));
				if(!recipes.contains(rec))
					jei.getRecipeRegistry().addRecipe(rec);
				else
				for(IRecipeWrapper wrap : recipes)
				{
					AnalyzerPollutionEffect e = ((RecipeWrapperPollutionEffect)wrap).getEffect();
					if(e.getId().equals(effect.getId()))
					{
						if(e.equals(effect))
						{
							jei.getRecipeRegistry().addRecipe(wrap);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		
	}
}
