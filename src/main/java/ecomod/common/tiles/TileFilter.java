package ecomod.common.tiles;

import java.util.Collection;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.IPollutionMultiplier;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMConfig;
import li.cil.oc.api.network.Analyzable;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;

public class TileFilter extends TileEnergy/* implements IPollutionMultiplier */{

	public TileFilter()
	{
		super(5000);
		
	}

	/*
	@Override
	public float pollutionFactor(PollutionType type)
	{
		if(world.isRemote) return 1;
		
		if(energy.getEnergyStored() == energy.getMaxEnergyStored())
		{
			if(energy.extractEnergy(energy.getMaxEnergyStored(), false) == energy.getMaxEnergyStored())
			{
				return EMConfig.filtmult;
			}
		}
		
		return 1;
	}*/
	
	public boolean isWorking()
	{
		if(world.isRemote) return false;
		
		if(energy.getEnergyStored() == energy.getMaxEnergyStored())
		{
			if(energy.extractEnergy(energy.getMaxEnergyStored(), false) == energy.getMaxEnergyStored())
			{
				return true;
			}
		}
		
		return false;
	}
}
