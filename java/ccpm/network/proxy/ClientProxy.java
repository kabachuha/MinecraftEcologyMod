package ccpm.network.proxy;

import ccpm.core.CCPM;
import ccpm.render.CCPMRenderHandler;
import ccpm.render.RenderCell;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerItemRenders()
	{
		//MinecraftForgeClient.registerItemRenderer(CCPM.respirator, new RenderRespirator());
	}
	
	@Override
	public void registerRenderHandler()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellRf.class, new RenderCell());
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellThaumium.class, new RenderCell());
	}
}
