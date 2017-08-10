package ecomod.common.proxy;

import ecomod.client.ClientHandler;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.core.EMConsts;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
	
	public void registerFluidModel(Block fluidBlock)
	{
		
	}
	
	public void openGUIAnalyzer(TileAnalyzer tile)
	{
		
	}
}
