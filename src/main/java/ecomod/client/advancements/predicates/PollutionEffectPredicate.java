package ecomod.client.advancements.predicates;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ecomod.client.advancements.util.ITestable;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;

public class PollutionEffectPredicate implements ITestable
{
	public static final PollutionEffectPredicate ANY = new PollutionEffectPredicate("ANY");
	
	private final String triggering_effect;
	
	public PollutionEffectPredicate(String effect)
	{
		triggering_effect = effect;
	}
	
	@Override
	public boolean test(EntityPlayerMP player, Object[] args) 
	{
		return PollutionEffectsConfig.isEffectActive(triggering_effect, EcologyMod.ph.getPollution(player.getEntityWorld(), EMUtils.blockPosToPair(player.getPosition())));
	}
	
	public static PollutionEffectPredicate deserialize(@Nullable JsonElement json)
	{
		if (json != null && !json.isJsonNull())
        {
			String str = JsonUtils.getString(json, "pollution_effect");
			
			if(str.isEmpty())
				return ANY;
			
			return new PollutionEffectPredicate(str);
        }
		
		return ANY;
	}
}
