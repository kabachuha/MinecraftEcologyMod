package ccpm.network.proxy;

import ccpm.core.CCPM;
import ccpm.render.CCPMRenderHandler;
import ccpm.render.RenderRespirator;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerItemRenders()
	{
		MinecraftForgeClient.registerItemRenderer(CCPM.respirator, new RenderRespirator());
	}
	
	@Override
	public void registerRenderHandler()
	{
		
	}
}
