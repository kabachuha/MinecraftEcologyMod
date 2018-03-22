package ecomod.common.intermod.jei;

import ecomod.api.pollution.PollutionData;
import ecomod.client.EMClientUtils;
import ecomod.client.renderer.RenderAdvancedFilter;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMConfig;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

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
				EMClientUtils.drawRotatedTexture(xOffset, yOffset, RenderAdvancedFilter.vent_s, vent_size, vent_size, (360F * time.getValue()) / time.getMaxValue());
				GlStateManager.popMatrix();
			}

			@Override
			public void draw(Minecraft minecraft) {
				
			}
		};

		background_arrow = guiHelper.createDrawable(new ResourceLocation("minecraft", "textures/gui/container/furnace.png"), 80, 34, 24, 17);
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(new ResourceLocation("minecraft", "textures/gui/container/furnace.png"), 176, 14, 24, 17);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 100, IDrawableAnimated.StartDirection.LEFT, false);
		
		localizedName = I18n.format("gui.jei.category.ecomod.air_filtration");
		
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

			@Override
			public void draw(Minecraft minecraft) {
				
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
		
		minecraft.fontRendererObj.drawString(EMConfig.advanced_filter_energy_per_second * EMConfig.adv_filter_delay_secs+" RF", tankx-30, tanky+tsy/2+35, Color.RED.getRGB());
		minecraft.fontRendererObj.drawString(EMConfig.adv_filter_delay_secs+" s", tankx-24, tanky+tsy/2+16, Color.BLACK.getRGB());
	}

	@Override
	public IDrawable getIcon() {
		return null;
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, RecipeWrapperAirFiltration recipeWrapper) {
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}
}
