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
	boolean containsTile(@Nonnull ResourceLocation id);
	
	PollutionData getTilePollution(@Nonnull ResourceLocation id);
	
	boolean removeTilePollution(@Nonnull ResourceLocation id);
	
	boolean addTilePollution(@Nonnull ResourceLocation id, @Nonnull PollutionData emission, boolean override);
	
	String getVersion();
}
