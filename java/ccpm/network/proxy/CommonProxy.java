package ccpm.network.proxy;


import ccpm.gui.ConAdv;
import ccpm.gui.ContainerCompressor;
import ccpm.tiles.AdvancedAirFilter;
import ccpm.tiles.TileCompressor;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {

	
	public CommonProxy() {
		// TODO Auto-generated constructor stub
	}
	
	public void initGuisIDs()
	{
		
	}

	public void registerItemRenders(){}
	
	
	

	public void registerRenderHandler(){}
	
	public void registerFluidModel(IFluidBlock fluidblock){}
	
	public void registerFluidModels(){}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x,y,z));
		if(ID == CCPMConfig.guiAdvFilterId)
		{
			if(tile != null && tile instanceof AdvancedAirFilter)
			{
				return new ConAdv(player.inventory, tile);
			}
		}
		if(ID == CCPMConfig.guiCompressorId)
		{
			if(tile != null && tile instanceof TileCompressor)
			{
				return new ContainerCompressor(player.inventory, tile);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
}
