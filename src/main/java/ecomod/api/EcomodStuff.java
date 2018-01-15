package ecomod.api;

import ecomod.api.capabilities.IPollution;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.ITEPollutionConfig;
import ecomod.api.pollution.PollutionData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.Fluid;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EcomodStuff
{
	public static Fluid concentrated_pollution;
	
	public static SoundEvent advanced_filter_working;
	public static SoundEvent analyzer;
	
	public static Map<String, IAnalyzerPollutionEffect> pollution_effects;
	
	public static Map<String, PollutionData> pollution_sources;
	
	//Format:  item_registry_name:[meta(optional)]
	public static List<String> blacklisted_items;
	
	public static Map<String, PollutionData> polluting_items;
	
	public static Map<String, PollutionData> smelted_items_pollution;
	
	@CapabilityInject(IPollution.class)
	public static final Capability<IPollution> CAPABILITY_POLLUTION = null;
	
	public static CreativeTabs ecomod_creative_tabs;
	
	public static Map<String, Boolean> additional_blocks_air_penetrating_state;
	
	public static NonNullList<Function<TileEntity, Object[]>> custom_te_pollution_determinants;
	
	public static ITEPollutionConfig tile_entity_pollution;
}
