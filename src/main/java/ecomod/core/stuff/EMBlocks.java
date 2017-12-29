package ecomod.core.stuff;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.common.blocks.*;
import ecomod.common.items.ItemBlockFrame;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ForgeInternalHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class EMBlocks
{
	
	public static void doPreInit()
	{
			EcologyMod.log.info("Setuping blocks");
			
			EcomodBlocks.inited = true;
			
			regBlock(EcomodBlocks.FLUID_POLLUTION = new BlockFluidPollution(), "block_"+EcomodStuff.concentrated_pollution.getName());
			
			FluidContainerRegistry.registerFluidContainer(EcomodStuff.concentrated_pollution, new ItemStack(EcomodBlocks.FLUID_POLLUTION));
		
			regBlock(EcomodBlocks.FILTER = new BlockFilter(), "filter");
		
			regBlock(EcomodBlocks.ADVANCED_FILTER = new BlockAdvancedFilter(), "advanced_filter");
			
			regBlock(EcomodBlocks.ANALYZER = new BlockAnalyzer(), "analyzer");
			
			regBlockWithItem(EcomodBlocks.FRAME = new BlockFrame(), ItemBlockFrame.class, "frame");
	}
	
	public static void regBlock(Block block, String name)
	{
		EcologyMod.log.info("Registring block ecomod:"+name);
		GameRegistry.registerBlock(block, name);
	}
	
	public static void regBlockWithItem(Block block, Class<? extends ItemBlock> item,String name)
	{
		EcologyMod.log.info("Registring block ecomod:"+name);
		GameRegistry.registerBlock(block, item, name);
	}
}
