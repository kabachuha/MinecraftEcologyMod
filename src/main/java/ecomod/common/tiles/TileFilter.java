package ecomod.common.tiles;

import java.util.Collection;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.IPollutionMultiplier;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;

public class TileFilter extends TileEnergy{

	public TileFilter()
	{
		super(EMConfig.filter_energy_per_minute * EMConfig.wptcd/60 * 5);
	}
	
	public boolean isWorking()
	{
		if(world.isRemote) return false;
		
		if(energy.getEnergyStored() >= (int)(EMConfig.filter_energy_per_minute * (EMConfig.wptcd/60F)))
		{
			energy.extractEnergyNotOfficially((int)(EMConfig.filter_energy_per_minute * (EMConfig.wptcd/60F)), false);
				
			return true;
		}
		
		return false;
	}
}
