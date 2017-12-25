package ecomod.common.world.gen;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import ecomod.core.stuff.EMConfig;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeWasteland extends BiomeGenBase
{
	public static final int wastelandColor = new Color(49,42,31).getRGB();
	public static final int wastelandWaterColor = new Color(150, 108, 74).getRGB();
	
	public BiomeWasteland() {
		super(EMConfig.wasteland_id, true);
		
		this.setBiomeName("Wasteland");
		this.setColor(wastelandColor);
		
		this.fillerBlock = Blocks.dirt;
		this.flowers.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCreatureList.clear();
		this.topBlock = Blocks.dirt;
		
		this.setTemperatureRainfall(1F, 1F);
		this.heightVariation = 0.05F;
		this.setHeight(height_Default);
		
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		
		this.theBiomeDecorator.treesPerChunk = -999;
		this.theBiomeDecorator.bigMushroomsPerChunk = -999;
		this.theBiomeDecorator.cactiPerChunk = -999;
		this.theBiomeDecorator.grassPerChunk = -999;
		this.theBiomeDecorator.mushroomsPerChunk = 8;
		this.theBiomeDecorator.waterlilyPerChunk = -999;
		this.theBiomeDecorator.reedsPerChunk = -999;
	}

	@Override
	public int getWaterColorMultiplier()
	{
		return wastelandWaterColor;
	}

	@Override
	public int getModdedBiomeGrassColor(int original) {
		return wastelandColor;
	}

	@Override
	public int getModdedBiomeFoliageColor(int original) {
		return wastelandColor;
	}
	
	public void plantFlower(World world, Random rand, int x, int y, int z)
    {
		//
    }
}
