package ecomod.api.pollution;

import net.minecraft.util.math.BlockPos;

/**
 * A tile entity, which implements this interface can affect nearby pollution emissions, including emissions from other tiles.
 * 
 * I.e. filters should implement this.
 */
public interface IPollutionAffector
{
	public PollutionData handleEmission(BlockPos pos, PollutionData emission);
}
