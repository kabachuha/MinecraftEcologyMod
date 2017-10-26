package ecomod.api.pollution;

/**
 * A TileEntity interface that determines the amount of pollution created by the TE (in pollution per WPT runs).<br>
 * <small>A delay between runs is 3 minutes by default</small> 
 * <br><br>
 * Also, the amount can depend on conditions.
 * <br>
 * If a value is negative then the TE will reduce the pollution in its chunk. <br><small>Although, It's not recommended to make pollution reducers using this</small>
 *
 * @since 1.0.1112.0
 */
public interface IPollutionEmitter
{
	public PollutionData pollutionEmission(boolean simulate);
}
