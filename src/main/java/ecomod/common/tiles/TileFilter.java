package ecomod.common.tiles;

import ecomod.api.pollution.IPollutionAffector;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.core.stuff.EMConfig;
import net.minecraft.util.math.BlockPos;

public class TileFilter extends TileEnergy implements IPollutionAffector
{

	public TileFilter()
	{
		super(EMConfig.filter_energy_per_emission * 5);
	}

	@Override
	public PollutionData handleEmission(BlockPos pos, PollutionData emission)
	{
		if(world.isRemote) return emission;
		
		if(getPos().distanceSq(pos) <= 1 && energy.getEnergyStored() >= EMConfig.filter_energy_per_emission)
		{
			energy.extractEnergyNotOfficially(EMConfig.filter_energy_per_emission, false);
				
			return emission.multiply(PollutionType.AIR, emission.getAirPollution() <= 0 ? 1 : 1 - EMConfig.filter_adjacent_tiles_reduction).multiply(PollutionType.WATER, emission.getWaterPollution() <= 0 ? 1 : 1 - EMConfig.filter_adjacent_tiles_reduction / 2).multiply(PollutionType.SOIL, emission.getSoilPollution() <= 0 ? 1 : 1 - EMConfig.filter_adjacent_tiles_reduction / 3);
		}
		
		return emission;
	}
}
