package ecomod.common.proxy;

import ecomod.client.ClientHandler;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.network.EMPacketUpdateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

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
	
	public void putBlockToBeRegistred(Block b)
	{
		
	}
	
	public void putItemToBeRegistred(Item item)
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
	
	public void registerItemModel(Item item, int meta, String model)
	{
		
	}
	
	public void registerBlockModel(Block block, int meta, String model)
	{
		
	}
	
	public void registerBlockModel(Block block, int meta, ResourceLocation model)
	{
		
	}

	public void registerItemModel(Item item, int meta, ResourceLocation model) {
		
	}
	
	public void registerItemVariants(Item item, ResourceLocation... names)
	{
		
	}
	
	public void registerItemVariants(Block item, ResourceLocation... names)
	{
		
	}
	
	public void packetUpdateTE_do_stuff(EMPacketUpdateTileEntity message)
	{
		
	}
}
