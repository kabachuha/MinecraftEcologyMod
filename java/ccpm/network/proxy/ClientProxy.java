package ccpm.network.proxy;

import DummyCore.Client.RenderAccessLibrary;
import ccpm.core.CCPM;
import ccpm.fluids.CCPMFluids;
import ccpm.render.CCPMRenderHandler;
import ccpm.render.RenderAdvThaum;
import ccpm.render.RenderCell;
import ccpm.render.RenderItemAdvThaum;
import ccpm.tiles.TileAdvThaum;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void registerItemRenders()
	{
		//MinecraftForgeClient.registerItemRenderer(CCPM.respirator, new RenderRespirator());
		RenderAccessLibrary.registerItemRenderingHandler(Item.getItemFromBlock(CCPM.advThaum), new RenderItemAdvThaum());
	}
	
	@Override
	public void registerRenderHandler()
	{
		CCPM.log.info("Registering tile entity special renders.");
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellRf.class, new RenderCell());
		
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellThaumium.class, new RenderCell());
		ClientRegistry.bindTileEntitySpecialRenderer(TileAdvThaum.class, new RenderAdvThaum());
	}
	
	@Override
	public void registerFluidModels()
	{
		registerFluidModel((IFluidBlock) CCPMFluids.concentratedPollution.getBlock());
		registerFluidModel((IFluidBlock) CCPMFluids.pollutedWater.getBlock());
	}
	
	@Override
	public void registerFluidModel(IFluidBlock fluidBlock) {
		Item item = Item.getItemFromBlock((Block) fluidBlock);

		ModelBakery.registerItemVariants(item);

		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("ccpm:fluid" ,fluidBlock.getFluid().getName());

		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
        {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return modelResourceLocation;
            }
        });

		ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
	}
	
	
	
}
