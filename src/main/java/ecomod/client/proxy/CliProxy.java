package ecomod.client.proxy;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.client.ClientHandler;
import ecomod.client.gui.GuiAnalyzer;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.proxy.ComProxy;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.common.tiles.TileEnergy;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.network.EMPacketUpdateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;



public class CliProxy extends ComProxy
{
	ClientHandler handler;
	
	@Override
	public ClientHandler getClientHandler()
	{
		return handler;
	}
	
	@Override
	public void doPreInit()
	{
		handler = new ClientHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		
		EcologyMod.proxy.registerFluidModel(EcomodBlocks.FLUID_POLLUTION);
	}
	
	@Override
	public void doInit()
	{

	}
	
	@Override
	public void openGUIAnalyzer(TileAnalyzer tile)
	{
		openGUIAnalyzer(Minecraft.getMinecraft().thePlayer, tile);
	}
	
	@Override
	public void openGUIAnalyzer(EntityPlayer player, TileAnalyzer tile)
	{
		if(tile != null)
		{
			FMLClientHandler.instance().displayGuiScreen(player == null ? Minecraft.getMinecraft().thePlayer : player, new GuiAnalyzer(tile));
		}
	}

	@Override
	public void packetUpdateTE_do_stuff(EMPacketUpdateTileEntity message) {
		World world = Minecraft.getMinecraft().theWorld;
		
		NBTTagCompound nbt = message.getData();
		
		if(nbt != null && nbt.hasKey("x") && nbt.hasKey("y") && nbt.hasKey("z"))
		{
			TileEntity te = world.getTileEntity(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
			
			if(te != null)
			{
				if(te instanceof TileEnergy)
				{
					((TileEnergy)te).receiveUpdatePacket(message);
				}
			}
			else
			{
				EcologyMod.log.error("Invalid EMPacketUpdateTileEntity! TileEntity not found!");
			}
		}
		else
		{
			EcologyMod.log.error("Invalid EMPacketUpdateTileEntity! Wrong nbt format!");
		}
	}
	
	
}
