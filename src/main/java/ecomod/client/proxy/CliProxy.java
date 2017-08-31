package ecomod.client.proxy;

import java.util.ArrayList;
import java.util.List;

import ecomod.api.EcomodBlocks;
import ecomod.api.EcomodStuff;
import ecomod.client.ClientHandler;
import ecomod.client.gui.GuiAnalyzer;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.TEPollutionConfig;
import ecomod.common.proxy.ComProxy;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.common.utils.EMUtils;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	List<Item> items = new ArrayList<Item>();
	
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
			registerBlockModel(b, 0, b.getRegistryName());
		}
		
		for(Item i : items)
		{
			registerItemModel(i, 0, i.getRegistryName());
		}
		
		blocks.clear();
		items.clear();
	}
	
	@Override
	public void putBlockToBeRegistred(Block b)
	{
		blocks.add(b);
	}
	
	@Override
	public void putItemToBeRegistred(Item item)
	{
		items.add(item);
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
	
	@Override
	public void openGUIAnalyzer(TileAnalyzer tile)
	{
		if(tile != null)
			Minecraft.getMinecraft().displayGuiScreen(new GuiAnalyzer(tile));
	}

	@Override
	public void registerItemModel(Item item, int meta, String model) {
		super.registerItemModel(item, meta, model);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(EMUtils.resloc(model), "inventory"));
	}
	
	@Override
	public void registerItemModel(Item item, int meta, ResourceLocation model) {
		super.registerItemModel(item, meta, model);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(model, "inventory"));
	}

	@Override
	public void registerBlockModel(Block block, int meta, String model) {
		super.registerBlockModel(block, meta, model);
		registerItemModel(Item.getItemFromBlock(block), meta, model);
	}
	
	@Override
	public void registerBlockModel(Block block, int meta, ResourceLocation model) {
		super.registerBlockModel(block, meta, model);
		registerItemModel(Item.getItemFromBlock(block), meta, model);
	}
	
	@Override
	public void registerItemVariants(Item item, ResourceLocation... names) {
		super.registerItemVariants(item, names);

		ModelBakery.registerItemVariants(item, names);
	}

	@Override
	public void registerItemVariants(Block item, ResourceLocation... names) {
		super.registerItemVariants(item, names);
		
		registerItemVariants(Item.getItemFromBlock(item), names);
	}
}
