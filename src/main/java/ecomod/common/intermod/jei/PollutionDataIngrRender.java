package ecomod.common.intermod.jei;

import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.EMUtils;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PollutionDataIngrRender implements IIngredientRenderer<PollutionData>
{
	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, PollutionData pollution)
	{
		FontRenderer fontRenderer = this.getFontRenderer(minecraft, pollution);
		if(pollution.getAirPollution() < 0.1D)
			fontRenderer.drawString("0", xPosition+105, yPosition+8, new Color(255, 255, 126).getRGB());
		else
			fontRenderer.drawString(fts(pollution.getAirPollution()), xPosition+105, yPosition+8, new Color(255, 255, 126).getRGB());

		if(pollution.getWaterPollution() < 0.1D)
			fontRenderer.drawString("0", xPosition+105, yPosition+28, new Color(60, 212, 252).getRGB());
		else
			fontRenderer.drawString(fts(pollution.getWaterPollution()), xPosition+105, yPosition+28, new Color(60, 212, 252).getRGB());

		if(pollution.getSoilPollution() < 0.1D)
			fontRenderer.drawString("0", xPosition+105, yPosition+48, new Color(89, 61, 41).getRGB());
		else
			fontRenderer.drawString(fts(pollution.getSoilPollution()), xPosition+105, yPosition+48, new Color(89, 61, 41).getRGB());

		GlStateManager.color(1, 1, 1, 1);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

		ResourceLocation lang_texture = EMUtils.resloc("textures/gui/analyzer/pollution_local/"+MinecraftForgeClient.getLocale().getLanguage().toLowerCase()+".png");

		if(!MinecraftForgeClient.getLocale().toString().toLowerCase().equals("en_us"))
		{
			try
			{
				IResource ir = Minecraft.getMinecraft().getResourceManager().getResource(lang_texture);
				if(ir.getInputStream().available() <= 0)
				{
					lang_texture = EMUtils.resloc("textures/gui/analyzer/pollution_local/en_us.png");
				}
			}
			catch (IOException e)
			{
				lang_texture = EMUtils.resloc("textures/gui/analyzer/pollution_local/en_us.png");
			}
		}

		Minecraft.getMinecraft().getTextureManager().bindTexture(lang_texture);
		Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, 100, 60, 100, 60);
	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, PollutionData ingredient, ITooltipFlag tooltipFlag) 
	{
		List<String> tooltip = new ArrayList<>();
		
		tooltip.add(I18n.format("gui.jei.desc.ingr.ecomod.pollution"));
		tooltip.add(TextFormatting.WHITE+I18n.format("gui.jei.desc.ecomod.pollution.air")+ ' ' +TextFormatting.YELLOW+fts(ingredient.getAirPollution()));
		tooltip.add(TextFormatting.WHITE+I18n.format("gui.jei.desc.ecomod.pollution.water")+ ' ' +TextFormatting.BLUE+fts(ingredient.getWaterPollution()));
		tooltip.add(TextFormatting.WHITE+I18n.format("gui.jei.desc.ecomod.pollution.soil")+ ' ' +TextFormatting.GRAY+fts(ingredient.getSoilPollution()));
		
		return tooltip;
	}
	
	private String fts(float f)
	{
		return Float.toString(f);
	}
}