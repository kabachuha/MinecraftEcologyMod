package ccpm.biomes;

import java.awt.Color;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

public class Wasteland extends BiomeGenBase {

	public static int wastelandColor = new Color(49,42,31).getRGB();
	
	public Wasteland(int id) {
		super(id);
		this.setBiomeName("wasteland");
		this.setDisableRain();
		this.spawnableCreatureList.clear();
		this.flowers.clear();
		this.theBiomeDecorator.treesPerChunk = -999;
		this.topBlock = (IBlockState) Blocks.dirt;
		this.fillerBlock = (IBlockState) Blocks.dirt;
		this.setHeight(height_Default);
		this.spawnableWaterCreatureList.clear();
		this.temperature = 100;
	}

	@Override
	public boolean canSpawnLightningBolt()
	{
		return true;
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
	
	public float getSpawningChance()
	{
		return 0.01F;
	}
}
