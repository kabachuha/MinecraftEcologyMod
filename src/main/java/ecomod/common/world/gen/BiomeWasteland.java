package ecomod.common.world.gen;

import java.awt.Color;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BiomeWasteland extends Biome
{
	public static final int wastelandColor = new Color(49,42,31).getRGB();
	
	public BiomeWasteland() {
		super(new BiomeProperties("Wasteland").setWaterColor(wastelandColor).setBaseHeight(0.125F).setHeightVariation(0.05F).setTemperature(1F).setRainfall(1F));
		
		this.setRegistryName(new ResourceLocation("ecomod:wasteland"));
		
		this.fillerBlock = Blocks.DIRT.getDefaultState();
		this.flowers.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCreatureList.clear();
		this.topBlock = Blocks.DIRT.getDefaultState();
		
		this.modSpawnableLists.remove(EnumCreatureType.CREATURE);
		this.modSpawnableLists.remove(EnumCreatureType.WATER_CREATURE);
		
		this.theBiomeDecorator.treesPerChunk = -999;
		this.theBiomeDecorator.bigMushroomsPerChunk = -999;
		this.theBiomeDecorator.cactiPerChunk = -999;
		this.theBiomeDecorator.generateLakes = false;
		this.theBiomeDecorator.grassPerChunk = -999;
		this.theBiomeDecorator.mushroomsPerChunk = 8;
		this.theBiomeDecorator.waterlilyPerChunk = -999;
		this.theBiomeDecorator.reedsPerChunk = -999;
	}

	

	@Override
	public int getGrassColorAtPos(BlockPos p)
	{
		return wastelandColor;
	}
	
	@Override
	public int getFoliageColorAtPos(BlockPos p)
	{
		return wastelandColor;
	}
	
	@Override
	public int getWaterColorMultiplier()
	{
		return wastelandColor;
	}
}
