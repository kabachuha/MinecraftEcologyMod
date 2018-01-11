package ecomod.api.pollution;

import net.minecraft.util.math.BlockPos;

/**
 * A <i>tile entity</i>, which implements this interface can affect nearby pollution emissions, including emissions from other tiles.
 * <br>
 * I.e. filters should implement this.
 */
public interface IPollutionAffector
{
	public PollutionData handleEmission(BlockPos pos, PollutionData emission);
}
