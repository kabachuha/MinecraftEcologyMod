package ecomod.common.proxy;

import ecomod.client.ClientHandler;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.core.EMConsts;
import ecomod.network.EMPacketUpdateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class ComProxy
{
	public ClientHandler getClientHandler()
	{
		return null;
	}
	
	public void doPreInit()
	{
		
	}
	
	public void doInit()
	{
		
	}
	
	public void registerFluidModel(Block fluidBlock)
	{
		
	}
	
	public void openGUIAnalyzer(TileAnalyzer tile)
	{
		
	}
	
	public void openGUIAnalyzer(EntityPlayer player, TileAnalyzer tile)
	{
		
	}
	
	public void packetUpdateTE_do_stuff(EMPacketUpdateTileEntity message)
	{
		
	}
}
