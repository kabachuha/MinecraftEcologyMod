package ecomod.api.pollution;

import javax.annotation.Nullable;

import net.minecraft.world.World;

/**
 * Used to provide API access to pollution values. For semi-internal usage.
 * <br>
 * Use on *server* side
 * <br>
 * <strong>Do not implement!!!</strong>
 */
public interface IPollutionGetter
{
	@Nullable
	public PollutionData getPollution(World w, int chunkx, int chunkz);
}
