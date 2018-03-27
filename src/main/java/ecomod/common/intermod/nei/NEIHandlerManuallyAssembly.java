package ecomod.common.intermod.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.client.gui.GuiAnalyzer;
import ecomod.common.blocks.BlockFrame;
import ecomod.core.stuff.EMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class NEIHandlerManuallyAssembly extends TemplateRecipeHandler
{
	private static final ResourceLocation texture = new ResourceLocation("minecraft:textures/gui/container/anvil.png");
	
	public class CachedAssemblyRecipe extends CachedRecipe
    {
		public PositionedStack frame;
		public PositionedStack core;
		public PositionedStack result;
		
		public CachedAssemblyRecipe(ItemStack base, ItemStack in, ItemStack out)
		{
			frame = new PositionedStack(base, 21, 6);
			core = new PositionedStack(in, 70, 6);
			result = new PositionedStack(out, 128, 6);
		}
		
		@Override
		public PositionedStack getResult()
		{
			return result;
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			return Lists.<PositionedStack>newArrayList(frame, core);
		}
    }
	
	public NEIHandlerManuallyAssembly()
	{
		
	}

	@Override
	public String getRecipeName()
	{
		return I18n.format("gui.jei.category.ecomod.manually_assembly");
	}

	@Override
	public String getGuiTexture()
	{
		return texture.toString();
	}

	@Override
	public void drawBackground(int recipe)
	{
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(20, 5, 26, 46, 125, 18);
    }

	@Override
	public void drawExtras(int recipe)
	{
		String text = I18n.format("gui.jei.category.ecomod.manually_assembly.note");
		
		Minecraft.getMinecraft().fontRenderer.drawSplitString(text, 10, 30, 150, Color.DARK_GRAY.getRGB());
	}
	
	//TODO Too hardcoded?
	
	private CachedAssemblyRecipe FILTER = new CachedAssemblyRecipe(new ItemStack(EcomodBlocks.FRAME), new ItemStack(EcomodItems.CORE), new ItemStack(EcomodBlocks.FILTER));
	private CachedAssemblyRecipe ADVANCED_FILTER = new CachedAssemblyRecipe(new ItemStack(EcomodBlocks.FRAME, 1, 1), new ItemStack(EcomodItems.CORE, 1, 1), new ItemStack(EcomodBlocks.ADVANCED_FILTER));
	private CachedAssemblyRecipe ANALYZER = new CachedAssemblyRecipe(new ItemStack(EcomodBlocks.FRAME), new ItemStack(EcomodItems.CORE, 1, 2), new ItemStack(EcomodBlocks.ANALYZER));
	private CachedAssemblyRecipe ADAPTER = new CachedAssemblyRecipe(new ItemStack(EcomodBlocks.FRAME), new ItemStack(BlockFrame.oc_adapter), new ItemStack(EcomodBlocks.OC_ANALYZER_ADAPTER));

	private boolean adapter_condition()
	{
		return EMConfig.is_oc_analyzer_interface_crafted_by_right_click && EcomodBlocks.OC_ANALYZER_ADAPTER != null && BlockFrame.oc_adapter != null;
	}
	
	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if (outputId.equals("ecomod.manually_assembly"))
		{
			if(EMConfig.enable_manually_assembly)
			{
				arecipes.add(FILTER);
				arecipes.add(ADVANCED_FILTER);
				arecipes.add(ANALYZER);
			}
			
			if(adapter_condition())
			{
				arecipes.add(ADAPTER);
			}
		}
		else
            super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		if(EMConfig.enable_manually_assembly)
		{
			if(result.getItem() == Item.getItemFromBlock(EcomodBlocks.FILTER))
			{
				arecipes.add(FILTER);
			}
			else if(result.getItem() == Item.getItemFromBlock(EcomodBlocks.ANALYZER))
			{
				arecipes.add(ANALYZER);
			}
			else if(result.getItem() == Item.getItemFromBlock(EcomodBlocks.ADVANCED_FILTER))
			{
				arecipes.add(ADVANCED_FILTER);
			}
		}
	
		if(adapter_condition() && result.getItem() == Item.getItemFromBlock(EcomodBlocks.OC_ANALYZER_ADAPTER))
		{
			arecipes.add(ADAPTER);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		if(ingredient.getItem() == Item.getItemFromBlock(EcomodBlocks.FRAME))
		{
			if(ingredient.getItemDamage() == 0)
			{
				if(EMConfig.enable_manually_assembly)
				{
					arecipes.add(FILTER);
					arecipes.add(ANALYZER);
				}
				if(adapter_condition())
					arecipes.add(ADAPTER);
			}
			else if(EMConfig.enable_manually_assembly && ingredient.getItemDamage() == 1)
				arecipes.add(ADVANCED_FILTER);
		}
		
		if(adapter_condition() && ingredient.getItem() == Item.getItemFromBlock(BlockFrame.oc_adapter))
			arecipes.add(ADAPTER);
		
		if(ingredient.getItem() == EcomodItems.CORE)
		{
			int meta = ingredient.getItemDamage();
			if(meta == 0)
				arecipes.add(FILTER);
			else if(meta == 1)
				arecipes.add(ADVANCED_FILTER);
			else if(meta == 2)
				arecipes.add(ANALYZER);
		}
	}
}
