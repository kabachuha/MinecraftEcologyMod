package ecomod.client.proxy;

import java.util.ArrayList;
import java.util.List;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.client.ClientHandler;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.proxy.ComProxy;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class CliProxy extends ComProxy
{
	ClientHandler handler;
	
	List<Block> blocks = new ArrayList<Block>();
	
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
		for(Block b : blocks)
		{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory"));
		}
		
		blocks.clear();
	}
	
	@Override
	public void putBlockToBeRegistred(Block b)
	{
		blocks.add(b);
	}
	
	@Override
	public void registerFluidModel(Block fluidBlock)
	{
		Item item = Item.getItemFromBlock(fluidBlock);
		
		ModelBakery.registerItemVariants(item);
		
		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(EMConsts.modid+":fluid" ,((IFluidBlock)fluidBlock).getFluid().getName());
		
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return modelResourceLocation;
            }
        });

		ModelLoader.setCustomStateMapper(fluidBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
		
		
	}
}
