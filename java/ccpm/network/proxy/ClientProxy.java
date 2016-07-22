package ccpm.network.proxy;

import ccpm.core.CCPM;
import ccpm.fluids.CCPMFluids;
import ccpm.gui.ConAdv;
import ccpm.gui.ContainerCompressor;
import ccpm.gui.GuiAdvFilter;
import ccpm.gui.GuiCompressor;
import ccpm.render.CCPMRenderHandler;
import ccpm.render.RespHud;
import ccpm.tiles.AdvancedAirFilter;
import ccpm.tiles.TileCompressor;
import ccpm.tiles.TileEnergyCellRf;
import ccpm.tiles.TileEnergyCellThaumium;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
		//RenderAccessLibrary.registerItemRenderingHandler(Item.getItemFromBlock(CCPM.advThaum), new RenderItemAdvThaum());
		//RenderAccessLibrary.registerItemRenderingHandler(CCPM.portableAnalyzer, new RenderPortableAnalyzer());
	}
	
	@Override
	public void registerRenderHandler()
	{
		MinecraftForge.EVENT_BUS.register(new CCPMRenderHandler());
		
		//MiscUtils.addHUDElement(new PortableAnalyzerHud());
		
		CCPM.log.info("Registering tile entity special renders.");
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellRf.class, new RenderCell());
		
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEnergyCellThaumium.class, new RenderCell());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileAdvThaum.class, new RenderAdvThaum());
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
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x,y,z));
		if(ID == CCPMConfig.guiAdvFilterId)
		{
			if(tile != null && tile instanceof AdvancedAirFilter)
			{
				return new GuiAdvFilter(new ConAdv(player.inventory, tile), tile);
			}
		}
		if(ID == CCPMConfig.guiCompressorId)
		{
			if(tile != null && tile instanceof TileCompressor)
			{
				return new GuiCompressor(new ContainerCompressor(player.inventory, tile), tile);
			}
		}
		return null;
	}
	
@Override
public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
	// TODO Auto-generated method stub
	return null;
}
}