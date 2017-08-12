package ecomod.api;

import java.util.Map;

import ecomod.api.client.IAnalyzerPollutionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;

public class EcomodStuff
{
	public static Fluid concentrated_pollution = null;
	
	public static SoundEvent advanced_filter_working = null;
	public static SoundEvent analyzer = null;
	
	public static Map<String, IAnalyzerPollutionEffect> pollution_effects = null;
}
