package ccpm.network.proxy;

import DummyCore.Client.RenderAccessLibrary;
import ccpm.core.CCPM;
import ccpm.render.CCPMRenderHandler;
import ccpm.render.RenderAdvThaum;
import ccpm.render.RenderCell;
import ccpm.render.RenderItemAdvThaum;
import ccpm.tiles.TileAdvThaum;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;
import net.minecraft.item.Item;
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
		RenderAccessLibrary.registerItemRenderingHandler(Item.getItemFromBlock(CCPM.advThaum), new RenderItemAdvThaum());
	}
	
	@Override
	public void registerRenderHandler()
	{
		CCPM.log.info("Registering tile entity special renders.");
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellRf.class, new RenderCell());
		
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellThaumium.class, new RenderCell());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAdvThaum.class, new RenderAdvThaum());
	}
}
