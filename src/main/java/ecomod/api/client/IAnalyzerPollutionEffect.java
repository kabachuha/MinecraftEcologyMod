package ecomod.api.client;

import ecomod.api.pollution.PollutionData;
import net.minecraft.util.ResourceLocation;

public interface IAnalyzerPollutionEffect
{
	ResourceLocation BLANK_ICON = new ResourceLocation("ecomod:textures/gui/analyzer/icons/null.png");
	
	PollutionData getTriggerringPollution();
	
	TriggeringType getTriggeringType();
	
	//unique id
	String getId();
	
	String getHeader();
	
	String getDescription();
	
	/**
	 * 50x50 texture
	 */
	ResourceLocation getIcon();
	
	enum TriggeringType
	{
		AND,
		OR
	}
}
