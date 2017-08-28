package ecomod.client.advancements.triggers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import ecomod.client.advancements.predicates.PollutionPredicate;
import ecomod.client.advancements.util.ITestable;
import ecomod.client.advancements.util.TriggerBase;
import ecomod.common.utils.EMUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class PlayerInPollutionTrigger extends TriggerBase<PlayerInPollutionTrigger.Instance> {
	
	public PlayerInPollutionTrigger() {
		super(EMUtils.resloc("player_in_pollution"));
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new Instance(this.id, PollutionPredicate.deserialize(json.get("chunk_pollution")));
	}

	public static class Instance extends AbstractCriterionInstance implements ITestable
	{
		private final PollutionPredicate pollution_predicate;
		
		public Instance(ResourceLocation criterionIn, PollutionPredicate poll_pred) {
			super(criterionIn);
			pollution_predicate = poll_pred;
		}

		@Override
		public boolean test(EntityPlayerMP player, Object[] args) {
			return pollution_predicate.test(player, args);
		}
		
		public boolean test(EntityPlayerMP player)
		{
			return test(player, new Object[]{});
		}
	}
}
