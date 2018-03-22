package ecomod.common.intermod.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecipeWrapperManuallyAssembly implements IRecipeWrapper
{
	private final List<List<ItemStack>> inputs;
	private final ItemStack output;
	@Nullable
	private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients;
	@Nullable
	private ItemStack lastLeftStack;
	@Nullable
	private ItemStack lastRightStack;
	private int lastCost;

	public RecipeWrapperManuallyAssembly(ItemStack leftInput, List<ItemStack> rightInputs, ItemStack output) {
		this.inputs = Lists.newArrayList();
		this.inputs.add(Collections.singletonList(leftInput));
		this.inputs.add(rightInputs);

		this.output = output;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	public void setCurrentIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients) {
		this.currentIngredients = currentIngredients;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		String text = I18n.format("gui.jei.category.ecomod.manually_assembly.note");
		
		int x = -25;
		int y = 20;
		
		minecraft.fontRendererObj.drawSplitString(text, x, y, (int)(recipeWidth*1.5F), Color.DARK_GRAY.getRGB());
	}

	@Override
	public List getInputs() {
		return inputs;
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(output);
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return null;
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {

	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return Collections.emptyList();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}
	
	
}
