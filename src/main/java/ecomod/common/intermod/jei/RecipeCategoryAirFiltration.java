package ecomod.common.intermod.jei;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import ecomod.api.pollution.PollutionData;
import ecomod.client.EMClientUtils;
import ecomod.client.renderer.RenderAdvancedFilter;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMConfig;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Constants;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.FluidStack;

public class RecipeCategoryAirFiltration implements IRecipeCategory<RecipeWrapperAirFiltration>
{
	private final IDrawable background;
	private final IDrawable background_vent;
	private final IDrawable tank_frame;
	private final IDrawable background_arrow;
	private final IDrawableAnimated arrow;
	private final String localizedName;
	
	private static final int tankx = 150;
	private static final int tanky = 0;
	private static final int pollutionx = -10;
	private static final int pollutiony = 0;
	private static final int tsx = 16;
	private static final int tsy = 64;
	private static final int vent_size = 16;
	
	public RecipeCategoryAirFiltration(IGuiHelper guiHelper)
	{
		background = guiHelper.createBlankDrawable(170, tsy);
		background_vent = new IDrawable(){
			ITickTimer time = guiHelper.createTickTimer(20, 40, false);
			TextureAtlasSprite vent = RenderAdvancedFilter.vent_s;
			
			@Override
			public int getWidth() {
				return vent_size;
			}

			@Override
			public int getHeight() {
				return vent_size;
			}

			@Override
			public void draw(Minecraft minecraft, int xOffset, int yOffset)
			{ 
				GlStateManager.pushMatrix();
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				EMClientUtils.drawRotatedTexture(xOffset, yOffset, vent, vent_size, vent_size, (360F * time.getValue()) / time.getMaxValue());
				GlStateManager.popMatrix();
			}
		};
		
		background_arrow = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 75, 169, 24, 17);
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 82, 128, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 100, IDrawableAnimated.StartDirection.LEFT, false);
		
		localizedName = Translator.translateToLocal("gui.jei.category.ecomod.air_filtration");
		
		tank_frame = new IDrawable() {
			@Override
			public int getWidth() {
				return tsx;
			}

			@Override
			public int getHeight() {
				return tsy;
			}
			@Override
			public void draw(Minecraft minecraft, int xOffset, int yOffset)
			{
				EMClientUtils.drawPixelFrameSize(xOffset, yOffset, tsx, tsy, Color.BLACK);
			}
		};
	}
	
	@Override
	public String getUid()
	{
		return "ecomod.air_filtration";
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
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperAirFiltration recipeWrapper, IIngredients ingredients)
	{
		IGuiFluidStackGroup gui_fluid = recipeLayout.getFluidStacks();
		
		FluidStack output = recipeWrapper.getOutput();
		
		gui_fluid.init(1, false, tankx, tanky, tsx, tsy, (int)Math.pow(10, (int)Math.floor(Math.log10(output.amount))+1), true, tank_frame);
		gui_fluid.set(1, output);
		
		IGuiIngredientGroup gui_pollution = recipeLayout.getIngredientsGroup(PollutionData.class);
		
		gui_pollution.init(0, true, new PollutionDataIngrRender(), pollutionx, pollutiony, 126, 68, 0, 0);
		gui_pollution.set(0, recipeWrapper.getInput());
	}

	@Override
	public void drawExtras(Minecraft minecraft)
	{
		background_arrow.draw(minecraft, tankx-30, tanky+tsy/2-9);
		arrow.draw(minecraft, tankx-30, tanky+tsy/2-9);
		background_vent.draw(minecraft, tankx-24, tanky+tsy/2-25);
		
		minecraft.fontRenderer.drawString(EMConfig.advanced_filter_energy_per_second * EMConfig.adv_filter_delay_secs+" RF", tankx-36, tanky+tsy/2+18, Color.RED.getRGB());
		minecraft.fontRenderer.drawString(EMConfig.adv_filter_delay_secs+" s", tankx-24, tanky+tsy/2-35, Color.BLACK.getRGB());
	}
}
