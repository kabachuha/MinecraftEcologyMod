package ecomod.client.proxy;

import ecomod.client.ClientHandler;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.proxy.ComProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


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
	}
}
