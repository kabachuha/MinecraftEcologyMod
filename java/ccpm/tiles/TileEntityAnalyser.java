package ccpm.tiles;

import DummyCore.Utils.BlockPosition;
import ccpm.api.ICCPMEnergySource;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;
import net.minecraftforge.fml.common.*;

@Optional.InterfaceList({
    @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers"),
    @Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "thaumcraft")
})
public class TileEntityAnalyser extends TileEntity implements SimpleComponent, IInfusionStabiliser {

	public TileEntityAnalyser() {

	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos pos) {
		return CCPMConfig.fstab;
	}

	@Override
	public String getComponentName() {
		return "pollutionAnalyser";
	}
	
	@Callback
	public Object[] getPollution(Context context, Arguments arguments) throws Exception
	{
		if(this.worldObj == null || this.isInvalid())
			return null;
		
		TileEntity t = worldObj.getTileEntity(this.getPos().down());
		
		if(t == null || t.isInvalid())
			return null;
		
		if(t instanceof ICCPMEnergySource)
		{
			ICCPMEnergySource source = (ICCPMEnergySource) t;
			
			if(source.useEnergy(10000, this))
			{
				return new Object[]{PollutionUtils.getChunkPollution(worldObj,this.getPos())};
			}
			else
				return null;
		}
		return null;
	}

}
