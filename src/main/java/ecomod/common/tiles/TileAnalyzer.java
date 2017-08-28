package ecomod.common.tiles;

import net.minecraftforge.fml.common.Optional;

import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.stuff.EMConfig;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;


public class TileAnalyzer extends TileEnergy
{
	public PollutionData pollution = null;
	public long last_analyzed = -1;
	
	public TileAnalyzer()
	{
		super(EMConfig.analyzer_energy);
	}

	public String getComponentName()
	{
		return "ecomod.analyzer";
	}

	public PollutionData getPollution()
	{
		return EcomodAPI.getPollution(getWorld(), getChunkCoords().getLeft(), getChunkCoords().getRight());
	}
	
	public Pair<Long, PollutionData> analyze()
	{
		if(world.isRemote)
			return null;
		
		if(energy.getEnergyStored() == energy.getMaxEnergyStored())
		{
			if(PollutionUtils.hasSurfaceAccess(getWorld(), getPos()))
			{
				world.playSound(null, getPos(), EcomodStuff.analyzer, SoundCategory.BLOCKS, 3F, 1F);
			
				energy.setEnergyStored(0);
			
				pollution = getPollution();
			
				last_analyzed = new Date().getTime();
			
				return Pair.of(last_analyzed, pollution);
			}
			else
			{
				for(EntityPlayerMP pl : world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(getPos().add(-4, -4, -4), getPos().add(4, 4, 4))))
				{
					pl.sendMessage(new TextComponentString("Analyzer is unable to work without a surface access!"));
				}
			}
		}
		
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		pollution = EMUtils.pollutionDataFromJSON(nbt.getString("pollution"), null);
		last_analyzed = nbt.getLong("last_analyzed");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		if(pollution != null)
			nbt.setString("pollution", pollution.toString());
		nbt.setLong("last_analyzed", last_analyzed);
		
		return nbt;
	}
}