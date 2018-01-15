package ecomod.client.advancements.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import ecomod.common.utils.EMUtils;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class SimpleTrigger extends TriggerBase<SimpleTrigger.Instance> {

	public SimpleTrigger(String i_d) {
		super(EMUtils.resloc(i_d));
	}

	public static class Instance extends AbstractCriterionInstance implements ITestable
	{
		public Instance(ResourceLocation criterionIn) {
			super(criterionIn);
		}

		@Override
		public boolean test(EntityPlayerMP player, Object[] args) {
			return true;
		}
	}

	@Override
	public SimpleTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new Instance(id);
	}
}
