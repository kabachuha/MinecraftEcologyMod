package ccpm.tiles;

import buildcraft.api.tiles.IHasWork;
import ccpm.api.ICCPMEnergySource;
import ccpm.api.ITilePollutionProducer;
import ccpm.utils.config.CCPMConfig;
import cpw.mods.fml.common.Optional;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;
@Optional.InterfaceList({
    @Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "thaumcraft")
})
public class TileEntityFilter extends TileEntity implements IInfusionStabiliser, ITilePollutionProducer {

	public TileEntityFilter() {
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean canStabaliseInfusion(World world, int x, int y, int z) {
		return CCPMConfig.fstab;
	}

	@Override
	public float getPollutionProdution() {
		
		if(this.worldObj == null || this.isInvalid())
			return 0;
		
		if(this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
		{
			TileEntity t = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
			
			if(t == null || t.isInvalid())
				return 0;
			
			if(t instanceof ICCPMEnergySource)
			{
				ICCPMEnergySource source = (ICCPMEnergySource) t;
				
				if(source.useEnergy(1000, this))
				{
					return -CCPMConfig.filterRed;
				}
				else
					return 0;
			}
		}
		
		return 0;
	}



}
