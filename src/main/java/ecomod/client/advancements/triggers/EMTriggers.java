package ecomod.client.advancements.triggers;

import ecomod.client.advancements.util.SimpleTrigger;
import ecomod.common.utils.EMUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;

public class EMTriggers
{
	public static PlayerInPollutionTrigger PLAYER_IN_POLLUTION;
	public static PlayerPollutionEffectTrigger POLLUTION_EFFECT;
	public static SimpleTrigger BREATHE_SMOG;
	public static SimpleTrigger BAD_SLEEP;
	public static SimpleTrigger POLLUTED_FOOD;
	public static SimpleTrigger VERY_POLLUTED_FOOD;
	public static SimpleTrigger POISONOUS_SLEEP;
	public static SimpleTrigger NO_FISH;
	public static SimpleTrigger ACID_RAIN;
	public static SimpleTrigger NO_BONEMEAL;
	
	public static void setup()
	{
		PLAYER_IN_POLLUTION = register(new PlayerInPollutionTrigger());
		POLLUTION_EFFECT = register(new PlayerPollutionEffectTrigger());
		BREATHE_SMOG = register(new SimpleTrigger("breathe_smog"));
		BAD_SLEEP = register(new SimpleTrigger("bad_sleep"));
		POISONOUS_SLEEP = register(new SimpleTrigger("poisonous_sleep"));
		POLLUTED_FOOD = register(new SimpleTrigger("polluted_food"));
		VERY_POLLUTED_FOOD = register(new SimpleTrigger("very_polluted_food"));
		NO_FISH = register(new SimpleTrigger("no_fish"));
		ACID_RAIN = register(new SimpleTrigger("acid_rain"));
		NO_BONEMEAL = register(new SimpleTrigger("no_bonemeal"));
	}
	
	private static <T extends ICriterionTrigger> T register(T criterion)
    {
        if (CriteriaTriggers.REGISTRY.containsKey(criterion.getId()))
        {
            throw new IllegalArgumentException("Duplicate criterion id " + criterion.getId());
        }
        else
        {
        	CriteriaTriggers.REGISTRY.put(criterion.getId(), criterion);
            return criterion;
        }
    }
}
