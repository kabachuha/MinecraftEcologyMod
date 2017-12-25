package ecomod.api;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.ITEPollutionConfig;
import ecomod.api.pollution.PollutionData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

public class EcomodStuff
{
	public static Fluid concentrated_pollution = null;
	
	public static String advanced_filter_working_sound = null;
	public static String analyzer_sound = null;
	
	public static Map<String, IAnalyzerPollutionEffect> pollution_effects = null;
	
	public static Map<String, PollutionData> pollution_sources = null;
	
	//Format:  item_registry_name:[meta(optional)]
	public static List<String> blacklisted_items = null;
	
	public static Map<String, PollutionData> polluting_items = null;
	
	public static Map<String, PollutionData> smelted_items_pollution = null;
	
	public static CreativeTabs ecomod_creative_tabs = null;
	
	public static Map<String, Boolean> additional_blocks_air_penetrating_state = null;
	
	public static List<Function<TileEntity, Object[]>> custom_te_pollution_determinants = null;
	
	public static ITEPollutionConfig tile_entity_pollution = null;
}
