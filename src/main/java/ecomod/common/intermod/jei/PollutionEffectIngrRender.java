package ecomod.common.intermod.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.client.EMClientUtils;
import ecomod.common.utils.AnalyzerPollutionEffect;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class PollutionEffectIngrRender implements IIngredientRenderer<AnalyzerPollutionEffect>
{
	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, AnalyzerPollutionEffect ingredient)
	{
		EMClientUtils.drawPixelFrameSize(xPosition-1, yPosition-1, 52, 52, Color.BLACK.getRGB());
		
		GlStateManager.color(1, 1, 1, 1);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		
		ResourceLocation icon = ingredient.getIcon();
		
		if(icon == null || icon.equals(IAnalyzerPollutionEffect.BLANK_ICON))
		{
			EMClientUtils.drawPixelFrameSize(xPosition-1, yPosition-1, 52, 52, new Color(66, 80, 67).getRGB());
			getFontRenderer(minecraft, ingredient).drawSplitString(I18n.format(ingredient.getHeader()), xPosition, yPosition, 50, new Color(66, 80, 67).getRGB());
		}
		else
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(ingredient.getIcon());
			Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, 50, 50, 50, 50);
		}
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, AnalyzerPollutionEffect ingredient, ITooltipFlag tooltipFlag)
	{
		List<String> tooltip = new ArrayList<String>();
		tooltip.add(I18n.format(ingredient.getHeader()));
		tooltip.add(TextFormatting.YELLOW + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.air") + TextFormatting.RESET + ' ' + Float.toString(ingredient.getTriggerringPollution().getAirPollution()));
		tooltip.add(TextFormatting.AQUA + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.water") + TextFormatting.RESET + ' ' + Float.toString(ingredient.getTriggerringPollution().getWaterPollution()));
		tooltip.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + I18n.format("gui.jei.desc.ecomod.pollution.soil") + TextFormatting.RESET + ' ' + Float.toString(ingredient.getTriggerringPollution().getSoilPollution()));
		tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger") + ' ' + TextFormatting.RESET + (ingredient.getTriggeringType() == IAnalyzerPollutionEffect.TriggeringType.AND ? I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger.and") : I18n.format("gui.jei.ingredient.ecomod.pollution_effect.trigger.or"))); 
		return tooltip;
	}
	
	
}
