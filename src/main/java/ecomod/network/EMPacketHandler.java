package ecomod.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import ecomod.core.EMConsts;


public class EMPacketHandler
{
	public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(EMConsts.modid);
	public static int id = 0;
	
	public static void init()
	{
		reg(EMPacketString.Handler.class, EMPacketString.class, Side.CLIENT);
		reg(EMPacketString.Handler.class, EMPacketString.class, Side.SERVER);
		reg(EMPacketUpdateTileEntity.Handler.class, EMPacketUpdateTileEntity.class, Side.CLIENT);
	}
	
	
	
	public static void reg(Class handler, Class message, Side side)
	{
		WRAPPER.registerMessage(handler, message, id++, side);
	}
}
