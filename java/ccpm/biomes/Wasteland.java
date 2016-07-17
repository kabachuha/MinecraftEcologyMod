package ccpm.biomes;

import java.awt.Color;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class Wasteland extends Biome {

	public static int wastelandColor = new Color(49,42,31).getRGB();
	
	public Wasteland(BiomeProperties id)
	{
		super(id);
		this.spawnableCreatureList.clear();
		this.flowers.clear();
		this.theBiomeDecorator.treesPerChunk = -999;
		this.topBlock = Blocks.DIRT.getDefaultState();
		this.fillerBlock = Blocks.DIRT.getDefaultState();
		this.spawnableWaterCreatureList.clear();
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
