package ccpm.tiles;

import ccpm.api.ICCPMEnergySource;
import ccpm.api.ITilePollutionProducer;
import ccpm.utils.config.CCPMConfig;
import net.minecraftforge.fml.common.*;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.InterfaceList({
    @Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
})
public class TileEntityFilter extends TileEntity implements IInfusionStabiliser, ITilePollutionProducer {

	public TileEntityFilter() {
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public boolean canStabaliseInfusion(World world, BlockPos p) {
		return CCPMConfig.fstab;
	}

	@Override
	public float getPollutionProdution() {
		
		if(this.worldObj == null || this.isInvalid())
			return 0;
		
		if(this.worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0)
		{
			TileEntity t = worldObj.getTileEntity(getPos().down());
			
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
