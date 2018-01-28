package ecomod.common.intermod.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecipeWrapperManuallyAssembly implements IRecipeWrapper
{
	private final List<List<ItemStack>> inputs;
	private final List<List<ItemStack>> output;
	@Nullable
	private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients;
	@Nullable
	private ItemStack lastLeftStack;
	@Nullable
	private ItemStack lastRightStack;
	private int lastCost;

	public RecipeWrapperManuallyAssembly(ItemStack leftInput, List<ItemStack> rightInputs, List<ItemStack> outputs) {
		this.inputs = Lists.newArrayList();
		this.inputs.add(Collections.singletonList(leftInput));
		this.inputs.add(rightInputs);

		this.output = Collections.singletonList(outputs);
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputLists(ItemStack.class, output);
	}

	public void setCurrentIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients) {
		this.currentIngredients = currentIngredients;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		String text = I18n.format("gui.jei.category.ecomod.manually_assembly.note");
		
		int width = minecraft.fontRenderer.getStringWidth(text);
		int x = -25;
		int y = 20;
		
		//minecraft.fontRenderer.drawString(text, x, y, Color.DARK_GRAY.getRGB());
		minecraft.fontRenderer.drawSplitString(text, x, y, (int)(recipeWidth*1.5F), Color.DARK_GRAY.getRGB());
	}
	
	
}
