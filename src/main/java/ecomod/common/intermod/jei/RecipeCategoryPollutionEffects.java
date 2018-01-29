package ecomod.common.intermod.jei;

import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.AnalyzerPollutionEffect;
import ecomod.core.EMConsts;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryPollutionEffects implements IRecipeCategory<RecipeWrapperPollutionEffect>
{
	public static final int recipeWidth = 160;
	public static final int recipeHeight = 200;
	
	private final IDrawable background;
	private final IDrawable icon;
	private final String localizedName;
	
	public RecipeCategoryPollutionEffects(IGuiHelper guiHelper)
	{
		background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
		icon = guiHelper.createDrawable(new ResourceLocation("minecraft:textures/blocks/deadbush.png"), 0, 0, 16, 16, 16, 16);
		localizedName = I18n.format("gui.jei.category.ecomod.pollution_effects");
	}

	@Override
	public String getUid()
	{
		return "ecomod.pollution_effects";
	}

	@Override
	public String getTitle()
	{
		return localizedName;
	}

	@Override
	public String getModName()
	{
		return EMConsts.name;
	}

	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public IDrawable getIcon()
	{
		return icon;
	}
	
	private static final int pollutionx = -16;
	private static final int pollutiony = 15;
	
	private static final int effectx = 123;
	private static final int effecty = pollutiony + 5;

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperPollutionEffect recipeWrapper, IIngredients ingredients)
	{
		IGuiIngredientGroup gui_pollution = recipeLayout.getIngredientsGroup(PollutionData.class);
		
		if(!recipeWrapper.getEffect().getIcon().equals(IAnalyzerPollutionEffect.BLANK_ICON))
		{
			IGuiIngredientGroup gui_effect = recipeLayout.getIngredientsGroup(AnalyzerPollutionEffect.class);
		
			gui_effect.init(1, false, new PollutionEffectIngrRender(), effectx, effecty, 50, 50, 0, 0);
			gui_effect.set(1, recipeWrapper.getEffect());
			
			gui_pollution.init(0, true, new PollutionDataIngrRender().setRoundValues(), pollutionx, pollutiony, 126, 68, 0, 0);
		}
		else
		{
			gui_pollution.init(0, true, new PollutionDataIngrRender().setRoundValues(), 0, pollutiony, 126, 68, 0, 0);
		}
		
		gui_pollution.set(0, recipeWrapper.getEffect().getTriggerringPollution());
	}
}
