package ecomod.common.tiles;

import java.util.Collection;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.IPollutionAffector;
import ecomod.api.pollution.IPollutionMultiplier;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class TileFilter extends TileEnergy implements IPollutionAffector{

	public TileFilter()
	{
		super(EMConfig.filter_energy_per_emission * 5);
	}
	
	@Override
	public PollutionData handleEmission(int x, int y, int z, PollutionData emission)
	{
		if(worldObj.isRemote) return emission;
		
		if(getPos().distanceTo(new EMBlockPos(x, y, z)) <= 1 && energy.getEnergyStored() >= (int)(EMConfig.filter_energy_per_emission))
		{
			energy.extractEnergyNotOfficially((int)(EMConfig.filter_energy_per_emission), false);
				
			return emission.multiply(PollutionType.AIR, emission.getAirPollution() <= 0 ? 1 : 1 - EMConfig.filter_adjacent_tiles_reduction).multiply(PollutionType.WATER, emission.getWaterPollution() <= 0 ? 1 : 1 - EMConfig.filter_adjacent_tiles_reduction / 2).multiply(PollutionType.SOIL, emission.getSoilPollution() <= 0 ? 1 : 1 - EMConfig.filter_adjacent_tiles_reduction / 3);
		}
		
		return emission;
	}
}
