package ecomod.core.stuff.compat;

import java.util.List;

import ecomod.api.EcomodStuff;
import ecomod.common.blocks.BlockAdvancedFilter;
import ecomod.common.blocks.BlockAnalyzer;
import ecomod.common.blocks.BlockFilter;
import ecomod.common.tiles.TileAdvancedFilter;
import ecomod.common.tiles.TileEnergy;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class EMWailaHandler implements IWailaDataProvider
{
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
	{
		TileEntity tile = accessor.getTileEntity();
		
		if(tile != null)
		{
			if(tile instanceof TileEnergy)
			{
				currenttip.add("Energy: "+TextFormatting.YELLOW.toString()+((TileEnergy)tile).getEnergyStored() +" / "+((TileEnergy)tile).getMaxEnergyStored()+" RF");
			}
			if(tile instanceof TileAdvancedFilter)
			{
				currenttip.add(I18n.translateToLocal(EcomodStuff.concentrated_pollution.getUnlocalizedName()) + " " + ((TileAdvancedFilter)tile).tank.getFluidAmount() + " / " + ((TileAdvancedFilter)tile).tank.getCapacity()+" mb");
				currenttip.add(((TileAdvancedFilter)tile).was_working ? TextFormatting.GREEN.toString()+"Working" : TextFormatting.RED.toString()+"Not "+TextFormatting.RED.toString()+"Working");
			}
		}
		
		return currenttip;
	}

	public static void callbackRegister(IWailaRegistrar registrar)
	{
		registrar.registerBodyProvider(new EMWailaHandler(), BlockFilter.class);
		registrar.registerBodyProvider(new EMWailaHandler(), BlockAnalyzer.class);
		registrar.registerBodyProvider(new EMWailaHandler(), BlockAdvancedFilter.class);
	}
}
