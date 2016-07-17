package ccpm.tiles;

import ccpm.api.ICCPMEnergySource;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.*;

//@Optional.InterfaceList({
//    @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers"),
//    @Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "thaumcraft")
//})
public class TileEntityAnalyser extends TileEntity /*implements SimpleComponent, IInfusionStabiliser */{

	public TileEntityAnalyser() {

	}
/*
//	@Override
//	public boolean canStabaliseInfusion(World world, BlockPos pos) {
//		return CCPMConfig.fstab;
//	}

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
				worldObj.playSoundEffect(getPos().getX(), getPos().getY(), getPos().getZ(), "mob.wither.idle", 7, 0.8F + worldObj.rand.nextFloat() * 0.3F);
				return new Object[]{PollutionUtils.getChunkPollution(worldObj,this.getPos())};
			}
			else
				return null;
		}
		return null;
	}
*/
}
