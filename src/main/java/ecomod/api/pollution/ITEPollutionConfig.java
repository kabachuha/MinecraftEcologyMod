package ecomod.api.pollution;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

/**
 * Used to provide API access to TEPollutionConfig values.
 * <br>
 * Use on *server* side
 * <br>
 */
public interface ITEPollutionConfig {
	public boolean containsTile(@Nonnull String id);
	
	public PollutionData getTilePollution(@Nonnull String id);
	
	public boolean removeTilePollution(@Nonnull String id);
	
	public boolean addTilePollution(@Nonnull String id, @Nonnull PollutionData emission, boolean override);
	
	public String getVersion();
}
