package ecomod.common.intermod.jei;

import java.util.Collections;
import java.util.List;

import ecomod.core.EMConsts;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class RecipeCategoryManuallyAssembly implements IRecipeCategory<RecipeWrapperManuallyAssembly>
{
	private final IDrawable background;
	private final IDrawable icon;
	private final String localizedName;

	public RecipeCategoryManuallyAssembly(IGuiHelper guiHelper)
	{
		localizedName = I18n.format("gui.jei.category.ecomod.manually_assembly");
		background = guiHelper.createDrawable(new ResourceLocation("minecraft", "textures/gui/container/anvil.png"), 26, 46, 125, 18, 0, 20, 0, 0);
		icon = new IDrawable(){
			private final ResourceLocation texture = new ResourceLocation("minecraft", "textures/entity/steve.png");
			
			@Override
			public int getWidth() {
				return 8;
			}

			@Override
			public int getHeight() {
				return 8;
			}

			@Override
			public void draw(Minecraft minecraft) {}

			@Override
			public void draw(Minecraft minecraft, int xOffset, int yOffset) {
				minecraft.getTextureManager().bindTexture(texture);
				Gui.drawModalRectWithCustomSizedTexture(xOffset, yOffset, 8, 8, 8, 8, 64, 64);
			}
		};
	}
	
	@Override
	public String getUid()
	{
		return "ecomod.manually_assembly";
	}

	@Override
	public String getTitle()
	{
		return localizedName;
	}

	@Override
	public IDrawable getBackground()
	{
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperManuallyAssembly recipeWrapper, IIngredients ingredients)
	{
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, 0, 0);
		guiItemStacks.init(1, true, 49, 0);
		guiItemStacks.init(2, false, 107, 0);

		guiItemStacks.set(ingredients);

		recipeWrapper.setCurrentIngredients(guiItemStacks.getGuiIngredients());
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperManuallyAssembly recipeWrapper) {

	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	
}
