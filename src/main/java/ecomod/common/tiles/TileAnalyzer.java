package ecomod.common.tiles;

import net.minecraftforge.fml.common.Optional;
import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.core.stuff.EMConfig;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;


//@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class TileAnalyzer extends TileEnergy implements /*SimpleComponent, */ITickable
{
	public TileAnalyzer()
	{
		super(EMConfig.analyzer_energy);
		
	}

	@Override
	public void update()
	{
		//TODO
	}

	//@Override
	public String getComponentName()
	{
		return "ecomod.analyzer";
	}

	public PollutionData getPollution()
	{
		return EcomodAPI.getPollution(getWorld(), getChunkCoords().getLeft(), getChunkCoords().getRight());
	}
	
	//@Callback(getter = true)
	//@Optional.Method(modid = "OpenComputers")
	public Object[] get_pollution(Context context, Arguments arguments) throws Exception
	{
		Object[] ret = new Object[PollutionData.PollutionType.values().length];
		
		PollutionData pd = getPollution();
		
		if(pd == null)
			return null;
		
		ret[0] = pd.getAirPollution();
		ret[1] = pd.getWaterPollution();
		ret[2] = pd.getSoilPollution();
		
		return ret;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		return nbt;
	}
}
