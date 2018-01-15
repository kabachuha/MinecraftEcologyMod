package ecomod.client.advancements.util;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public abstract class TriggerBase<I extends ICriterionInstance> implements ICriterionTrigger<I>
{
	public final ResourceLocation id;
	
	private final Map<PlayerAdvancements, ListenersBase<I>> listeners = Maps.<PlayerAdvancements, ListenersBase<I>>newHashMap();
	
	public TriggerBase(ResourceLocation criterionIn) {
		id = criterionIn;
	}
	
	public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<I> listener)
    {
		ListenersBase<I> triglisteners = this.listeners.computeIfAbsent(playerAdvancementsIn, ListenersBase::new);

		triglisteners.add(listener);
    }

    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<I> listener)
    {
    	ListenersBase<I> triglisteners = this.listeners.get(playerAdvancementsIn);

        if (triglisteners != null)
        {
        	triglisteners.remove(listener);

            if (triglisteners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.listeners.remove(playerAdvancementsIn);
    }

	@Override
	public abstract I deserializeInstance(JsonObject json, JsonDeserializationContext context);
	
	public void trigger(EntityPlayerMP player, Object... args)
	{
		ListenersBase<I> triglisteners = this.listeners.get(player.getAdvancements());
		
		if (triglisteners != null)
        {
			triglisteners.trigger(player, args);
        }
	}
	
	public void trigger(EntityPlayerMP player)
	{
		trigger(player, new Object[]{});
	}
	
	@Override
	public ResourceLocation getId() {
		return id;
	}
}
