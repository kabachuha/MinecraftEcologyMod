package ccpm.integration.waila;

import java.util.List;

import ccpm.api.ICCPMEnergySource;
import ccpm.api.IHasProgress;
import ccpm.api.ITilePollutionProducer;
import ccpm.utils.config.CCPMConfig;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WailaDataProvider implements IWailaDataProvider {

	public WailaDataProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		if(accessor.getTileEntity() != null)
		{
			if(accessor.getTileEntity() instanceof ICCPMEnergySource)
			{
				currenttip.add("Energy: "+((ICCPMEnergySource)accessor.getTileEntity()).getEnergy() + "/" + ((ICCPMEnergySource)accessor.getTileEntity()).getMaxEnergy());
			}
			
			if(accessor.getTileEntity() instanceof IHasProgress)
			{
				if(((IHasProgress)accessor.getTileEntity()).getProgress() > 0)
				{
					currenttip.add("Progress: "+ ((IHasProgress)accessor.getTileEntity()).getProgress()+ "/"+((IHasProgress)accessor.getTileEntity()).getMaxProgress());
				}
			}
			
			if(accessor.getTileEntity() instanceof ITilePollutionProducer)
			{
				currenttip.add("This tile entity produces "+((ITilePollutionProducer)accessor.getTileEntity()).getPollutionProdution()*CCPMConfig.pollutionMultiplier + "pollution every "+CCPMConfig.processingDelay+" milliseconds");
			}
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		// TODO Auto-generated method stub
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world,
			BlockPos pos) {
		// TODO Auto-generated method stub
		return tag;
	}
	
	public static void reg(IWailaRegistrar registrar)
	{
	registrar.registerBodyProvider(new WailaDataProvider(), Block.class);
	}
}
