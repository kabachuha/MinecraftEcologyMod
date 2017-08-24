package ecomod.api;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.capabilities.IPollution;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.pollution.IPollutionGetter;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionEmissionEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class EcomodAPI
{
	/**
	 * 
	 * 
	 * @param world The world
	 * @param chunkLoc The location of the chunk
	 * @param emission Amount of pollution emitted. If it is negative pollution is reduced
	 * @param scheduled Determines whether the emission event should be put in queue and handled by the thread on its next run if true or immediately by the PollutionManager if false. It's recommended to use the first option (true).
	 * @return Whether the event had passed and hadn't been canceled
	 * 
	 * @see ecomod.api.pollution.PollutionEmissionEvent
	 * @see net.minecraft.world.chunk.Chunk
	 * @see ecomod.api.pollution.PollutionData  
	 */
	public static boolean emitPollution(World world, Pair<Integer,Integer> chunkLoc, PollutionData emission, boolean scheduled)
	{
		if(emission == null || (emission.compareTo(PollutionData.getEmpty()) == 0)) return false;
		
		PollutionEmissionEvent em = new PollutionEmissionEvent(world, chunkLoc.getLeft(), chunkLoc.getRight(), emission, scheduled);
		
		return MinecraftForge.EVENT_BUS.post(em);
	}
	
	public static IPollutionGetter pollution_getter = null;
	
	@Nullable
	public static PollutionData getPollution(World w, int chunkX, int chunkZ)
	{
		if(pollution_getter != null)
		{
			return pollution_getter.getPollution(w, chunkX, chunkZ);
		}
		
		return null;
	}
	
	public static boolean addAnalyzerPollutionEffect(IAnalyzerPollutionEffect iape)
	{
		if(EcomodStuff.pollution_effects.containsKey(iape.getId()) || EcomodStuff.pollution_effects.containsValue(iape))
			return false;
		
		EcomodStuff.pollution_effects.put(iape.getId(), iape);
		
		return true;
	}
	
	public static boolean addAnalyzerPollutionEffect(String id, String header, String desc, @Nullable ResourceLocation icon, PollutionData triggering_pollution, IAnalyzerPollutionEffect.TriggeringType triggering_type)
	{
		return addAnalyzerPollutionEffect(new IAnalyzerPollutionEffect(){
			@Override
			public PollutionData getTriggerringPollution() {
				return triggering_pollution;
			}

			@Override
			public TriggeringType getTriggeringType() {
				return triggering_type;
			}

			@Override
			public String getId() {
				return id;
			}

			@Override
			public String getHeader() {
				return header;
			}

			@Override
			public String getDescription() {
				return desc;
			}

			@Override
			public ResourceLocation getIcon() {
				return icon == null ? IAnalyzerPollutionEffect.BLANK_ICON : icon;
			}
		});
	}
	
	static
	{
		CapabilityManager.INSTANCE.register(IPollution.class, new IPollution.Storage(), new IPollution.Factory());
	}
}
