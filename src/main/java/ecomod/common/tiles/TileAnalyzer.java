package ecomod.common.tiles;

import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.PollutionData;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.stuff.EMConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;


public class TileAnalyzer extends TileEnergy
{
	public PollutionData pollution = null;
	public long last_analyzed = -1;
	
	public TileAnalyzer()
	{
		super(EMConfig.analyzer_energy);
	}

	public PollutionData getPollution()
	{
		return EcomodAPI.getPollution(worldObj, getChunkCoords().getLeft(), getChunkCoords().getRight());
	}
	
	public Pair<Long, PollutionData> analyze()
	{
		if(worldObj.isRemote)
			return null;
		
		sendUpdatePacket();
		
		if(energy.getEnergyStored() == energy.getMaxEnergyStored())
		{
			if(PollutionUtils.hasSurfaceAccess(worldObj, getPos()))
			{
				worldObj.playSoundEffect(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D, "ecomod:analyzer", 3F, 1F);
			
				energy.setEnergyStored(0);
			
				pollution = getPollution();
			
				last_analyzed = new Date().getTime();
				
				return Pair.of(last_analyzed, pollution);
			}
			else
			{
				for(Object pl : worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, AxisAlignedBB.getBoundingBox(xCoord-4D, yCoord-4D, zCoord-4D, xCoord+4.5D, yCoord+4.5D, zCoord+4.5D)))
				{
					((EntityPlayerMP)pl).addChatMessage(new ChatComponentText("Analyzer is unable to work without a surface access!"));
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
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		
		if(pollution != null)
			nbt.setString("pollution", pollution.toString());
		nbt.setLong("last_analyzed", last_analyzed);
	}
	
	int sync_energy = -1;

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(!worldObj.isRemote)
		{
			if(energy.getEnergyStored() != sync_energy)
			{
				sendUpdatePacket();
				sync_energy = energy.getEnergyStored();
			}
		}
	}
}