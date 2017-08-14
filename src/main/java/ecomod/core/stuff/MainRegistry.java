package ecomod.core.stuff;

import ecomod.api.EcomodStuff;
import ecomod.client.gui.EMGuiHandler;
import ecomod.common.utils.EMUtils;
import ecomod.common.world.FluidPollution;
import ecomod.common.world.gen.BiomeWasteland;
import ecomod.core.EcologyMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MainRegistry
{
	public static BiomeWasteland biome_wasteland = new BiomeWasteland();
	
	public static void doPreInit()
	{
		EcomodStuff.concentrated_pollution = new FluidPollution();
		
		FluidRegistry.registerFluid(EcomodStuff.concentrated_pollution);
		
		FluidRegistry.addBucketForFluid(EcomodStuff.concentrated_pollution);
		
		EMBlocks.doPreInit();
		EMTiles.doPreInit();
		
		//Biome.REGISTRY.putObject(new ResourceLocation("ecomod:wasteland"), biome_wasteland);
		GameRegistry.register(biome_wasteland);
	}
	
	public static void doInit()
	{
		SoundEvent.REGISTRY.putObject(EMUtils.resloc("advanced_filter_working"), EcomodStuff.advanced_filter_working = new SoundEvent(EMUtils.resloc("advanced_filter_working")));
		SoundEvent.REGISTRY.putObject(EMUtils.resloc("analyzer"), EcomodStuff.analyzer = new SoundEvent(EMUtils.resloc("analyzer")));
		
		IGuiHandler igh = new EMGuiHandler();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(EcologyMod.instance, igh);
		
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(biome_wasteland, 50));
		BiomeDictionary.addTypes(biome_wasteland, BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.RARE, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.WET, BiomeDictionary.Type.COLD);
		BiomeManager.addStrongholdBiome(biome_wasteland);
		
		BiomeProvider.allowedBiomes.add(biome_wasteland);
		
		EcologyMod.log.info("Wasteland ID : "+Biome.getIdForBiome(biome_wasteland));
	}
	
	public static void doPostInit()
	{
		
	}
	
	
	public static void initAnalyzerPollutionEffects()
	{
		
	}
}
