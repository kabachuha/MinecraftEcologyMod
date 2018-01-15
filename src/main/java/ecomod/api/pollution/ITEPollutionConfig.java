package ecomod.api.pollution;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Used to provide API access to TEPollutionConfig values.
 * <br>
 * Use on *server* side
 * <br>
 */
public interface ITEPollutionConfig {
	public boolean containsTile(@Nonnull ResourceLocation id);
	
	public PollutionData getTilePollution(@Nonnull ResourceLocation id);
	
	public boolean removeTilePollution(@Nonnull ResourceLocation id);
	
	public boolean addTilePollution(@Nonnull ResourceLocation id, @Nonnull PollutionData emission, boolean override);
	
	public String getVersion();
}
