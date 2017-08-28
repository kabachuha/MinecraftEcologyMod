package ecomod.client.advancements.triggers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import ecomod.client.advancements.predicates.PollutionEffectPredicate;
import ecomod.client.advancements.util.ITestable;
import ecomod.client.advancements.util.TriggerBase;
import ecomod.common.utils.EMUtils;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class PlayerPollutionEffectTrigger extends TriggerBase<PlayerPollutionEffectTrigger.Instance> {

	public PlayerPollutionEffectTrigger() {
		super(EMUtils.resloc("pollution_effect"));
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new Instance(id, PollutionEffectPredicate.deserialize(json.get("pollution_effect")));
	}
	
	public static class Instance extends AbstractCriterionInstance implements ITestable
	{
		private final PollutionEffectPredicate effect_predicate;
		
		public Instance(ResourceLocation criterionIn, PollutionEffectPredicate effect) {
			super(criterionIn);
			effect_predicate = effect;
		}

		@Override
		public boolean test(EntityPlayerMP player, Object[] args) {
			return effect_predicate.test(player, args);
		}
		
	}
}
