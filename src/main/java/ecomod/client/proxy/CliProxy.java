package ecomod.client.proxy;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodItems;
import ecomod.api.EcomodStuff;
import ecomod.client.ClientHandler;
import ecomod.client.gui.GuiAnalyzer;
import ecomod.client.gui.GuiPollutionEffectsBook;
import ecomod.client.renderer.RenderItemCore;
import ecomod.client.renderer.RendererFramedItem;
import ecomod.client.renderer.RendererFramedTile;
import ecomod.client.renderer.RendererItemCraftIngredient;
import ecomod.client.renderer.RendererItemRespirator;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.config.TEPollutionConfig;
import ecomod.common.proxy.ComProxy;
import ecomod.common.tiles.TileAdvancedFilter;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.common.tiles.TileEnergy;
import ecomod.common.tiles.TileFilter;
import ecomod.common.tiles.TileFrame;
import ecomod.common.tiles.compat.TileAnalyzerAdapter;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.network.EMPacketUpdateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileFilter.class, new RendererFramedTile(0));
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnalyzer.class, new RendererFramedTile(2));
		ClientRegistry.bindTileEntitySpecialRenderer(TileAdvancedFilter.class, new RendererFramedTile(1));
		if(Loader.isModLoaded("OpenComputers|Core"))
			ClientRegistry.bindTileEntitySpecialRenderer(TileAnalyzerAdapter.class, new RendererFramedTile(3));
		ClientRegistry.bindTileEntitySpecialRenderer(TileFrame.class, new RendererFramedTile(-1));
		
		MinecraftForgeClient.registerItemRenderer(EcomodItems.CRAFT_INGREDIENT, new RendererItemCraftIngredient());
		MinecraftForgeClient.registerItemRenderer(EcomodItems.CORE, new RenderItemCore());
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EcomodBlocks.FILTER), new RendererFramedItem());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EcomodBlocks.ADVANCED_FILTER), new RendererFramedItem());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EcomodBlocks.ANALYZER), new RendererFramedItem());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EcomodBlocks.FRAME), new RendererFramedItem());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(EcomodBlocks.OC_ANALYZER_ADAPTER), new RendererFramedItem());
		
		MinecraftForgeClient.registerItemRenderer(EcomodItems.RESPIRATOR, new RendererItemRespirator());
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
	public void openGUIEffectsBook(EntityPlayer player)
	{
		FMLClientHandler.instance().displayGuiScreen(player == null ? Minecraft.getMinecraft().thePlayer : player, new GuiPollutionEffectsBook());
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
