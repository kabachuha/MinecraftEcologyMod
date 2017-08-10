package ecomod.api.client;

import ecomod.api.pollution.PollutionData;
import net.minecraft.util.ResourceLocation;

public interface IAnalyzerPollutionEffect
{
	public static final ResourceLocation BLANK_ICON = new ResourceLocation("ecomod:textures/gui/analyzer/icons/null.png");
	
	public PollutionData getTriggerringPollution();
	
	public TriggeringType getTriggeringType();
	
	//unique id
	public String getId();
	
	public String getHeader();
	
	public String getDescription();
	
	/**
	 * 50x50 texture
	 */
	public ResourceLocation getIcon();
	
	public static enum TriggeringType
	{
		AND,
		OR
	}
}
