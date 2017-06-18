package ecomod.core.stuff;

import ecomod.common.blocks.*;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
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
	public static Block FILTER;
	
	public static void doPreInit()
	{
		FILTER = new BlockFilter().setUnlocalizedName(EMConsts.modid+".block.filter");
		
		regBlock(FILTER, "filter");
	}
	
	public static void regBlock(Block block, String name)
	{
		ResourceLocation resloc = EMUtils.resloc(name);
		
		block.setRegistryName(resloc);
		
		GameRegistry.register(block);
		
		ItemBlock ib = new ItemBlock(block);
		
		ib.setRegistryName(resloc);
		
		GameRegistry.register(ib);
		
		if(FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER)
			ModelBakery.registerItemVariants(ib, resloc);
	}
}
