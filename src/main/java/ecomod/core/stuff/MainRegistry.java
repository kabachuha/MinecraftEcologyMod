package ecomod.core.stuff;

import java.util.ArrayList;
import java.util.function.Function;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.api.EcomodItems;
import ecomod.api.EcomodStuff;
import ecomod.client.gui.EMGuiHandler;
import ecomod.common.blocks.BlockFrame;
import ecomod.common.utils.EMUtils;
import ecomod.common.world.FluidPollution;
import ecomod.common.world.gen.BiomeWasteland;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

public class MainRegistry
{
	public static BiomeWasteland biome_wasteland = new BiomeWasteland();
	
	public static void doPreInit()
	{
		MinecraftForge.EVENT_BUS.register(MainRegistry.class);
		
		EcomodStuff.concentrated_pollution = new FluidPollution();
		
		FluidRegistry.registerFluid(EcomodStuff.concentrated_pollution);
		
		EcomodStuff.ecomod_creative_tabs = new CreativeTabs(EMConsts.modid){
			@Override
			public Item getTabIconItem() {
				return EcomodItems.RESPIRATOR;
			}
		};
		
		EMBlocks.doPreInit();
		
		if(Loader.isModLoaded("OpenComputers|Core"))
			EMIntermod.OCpreInit();
		
		EMItems.doPreInit();
		
		EMTiles.doPreInit();
		
		EMRecipes.doPreInit();
		
		EMIntermod.thermal_expansion_imc();
		
		EcomodStuff.custom_te_pollution_determinants = new ArrayList<Function<TileEntity, Object[]>>();
	}
	
	public static void doInit()
	{
		if(!ModAPIManager.INSTANCE.hasAPI("ecomodapi"))
		{
			EcologyMod.log.error("EcomodAPI has not been loaded!!!");
			throw new NullPointerException("EcomodAPI has not been loaded!!!");
		}
		
		IGuiHandler igh = new EMGuiHandler();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(EcologyMod.instance, igh);
		
		if(EMConfig.wasteland_spawns_naturally)
			BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(biome_wasteland, 10));
		BiomeDictionary.registerBiomeType(biome_wasteland, BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.WET, BiomeDictionary.Type.COLD);
		BiomeManager.strongHoldBiomes.add(biome_wasteland);
		
		EcologyMod.log.info("Wasteland ID : "+biome_wasteland.biomeID);
		
		if(Loader.isModLoaded("IC2"))
			EMIntermod.init_ic2_support();
		
		EMRecipes.doInit();
		
		EMAchievements.setup();
		
		//FMLInterModComms.sendFunctionMessage(EMConsts.modid, EMIntermod.key_add_te_pollution_determinant, ecomod.test.TEST_TEPollutionDeterminant.class.getName());
	}
	
	public static void doPostInit()
	{
		EMIntermod.registerBCFuels();
		
		if(Loader.isModLoaded("IC2"))
			EMIntermod.setup_ic2_support();
		
		if(Loader.isModLoaded("OpenComputers|Core"))
		{
			BlockFrame.oc_adapter = GameRegistry.findBlock("OpenComputers", "adapter");
		}
		
		EMRecipes.doPostInit();
	}
}
