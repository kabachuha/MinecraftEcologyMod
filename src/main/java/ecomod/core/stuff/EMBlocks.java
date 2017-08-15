package ecomod.core.stuff;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.common.blocks.*;
import ecomod.common.items.ItemBlockFrame;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class EMBlocks
{
	
	
	public static void doPreInit()
	{
		if(!EcomodBlocks.inited)
		{
			EcomodBlocks.inited = true;
			EcomodBlocks.FILTER = new BlockFilter().setUnlocalizedName(EMConsts.modid+".filter");
			EcomodBlocks.ADVANCED_FILTER = new BlockAdvancedFilter().setUnlocalizedName(EMConsts.modid+".advanced_filter");
			EcomodBlocks.ANALYZER = new BlockAnalyzer().setUnlocalizedName(EMConsts.modid+".analyzer");
			EcomodBlocks.FRAME = new BlockFrame().setUnlocalizedName(EMConsts.modid+".frame");
		
			
			EcomodBlocks.FLUID_POLLUTION = new BlockFluidPollution();
			EcomodBlocks.FLUID_POLLUTION.setRegistryName("block_"+EcomodStuff.concentrated_pollution.getName());
			GameRegistry.register(EcomodBlocks.FLUID_POLLUTION);
			ItemBlock ib = new ItemBlock(EcomodBlocks.FLUID_POLLUTION);
			ib.setRegistryName("block_"+EcomodStuff.concentrated_pollution.getName());
			GameRegistry.register(ib);
		
			regBlock(EcomodBlocks.FILTER, "filter");
		
			regBlock(EcomodBlocks.ADVANCED_FILTER, "advanced_filter");
			
			regBlock(EcomodBlocks.ANALYZER, "analyzer");
			
			regBlockNoItem(EcomodBlocks.FRAME, "frame", false);
			
			ib = new ItemBlockFrame();
			
			ib.setRegistryName(EMUtils.resloc("frame"));
			GameRegistry.register(ib);
			
			ModelBakery.registerItemVariants(Item.getItemFromBlock(EcomodBlocks.FRAME), EMUtils.resloc("basic_frame"), EMUtils.resloc("advanced_frame"));
		}
	}
	
	public static void doInit()
	{
		EcologyMod.proxy.registerBlockModel(EcomodBlocks.FRAME, 0, "basic_frame");
		EcologyMod.proxy.registerBlockModel(EcomodBlocks.FRAME, 1, "advanced_frame");
	}
	
	public static void regBlock(Block block, String name)
	{
		regBlockNoItem(block, name);
		
		ItemBlock ib = new ItemBlock(block);
		
		ib.setRegistryName(EMUtils.resloc(name));
		
		GameRegistry.register(ib);
	}
	
	public static void regBlockNoItem(Block block, String name)
	{
		regBlockNoItem(block, name, true);
	}
	
	public static void regBlockNoItem(Block block, String name, boolean model)
	{
		ResourceLocation resloc = EMUtils.resloc(name);
		
		block.setRegistryName(resloc);
		
		GameRegistry.register(block);
		
		if(model)
			EcologyMod.proxy.putBlockToBeRegistred(block);
	}
}
