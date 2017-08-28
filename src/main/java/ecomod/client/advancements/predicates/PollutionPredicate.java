package ecomod.client.advancements.predicates;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ecomod.api.EcomodAPI;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.PollutionData;
import ecomod.client.advancements.util.ITestable;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class PollutionPredicate implements ITestable {

	public static final PollutionPredicate ANY = new PollutionPredicate(new PollutionData(-1,-1,-1), TriggeringType.AND);
	
	@Nullable
	private final PollutionData trig_pollution;
	
	private final TriggeringType trig_type;
	
	public PollutionPredicate(PollutionData triggering_pollution, TriggeringType type)
	{
		trig_pollution = triggering_pollution;
		if(type == null)
			type = TriggeringType.AND;
		trig_type = type;
	}
	
	public PollutionPredicate(PollutionData triggering_pollution)
	{
		this(triggering_pollution, TriggeringType.AND);
	}

	@Override
	public boolean test(EntityPlayerMP player, Object[] args) 
	{
		PollutionData pd = null;
		
		if(args.length > 0)
		{
			if(args[0]!=null)
			{
				if(args[0] instanceof PollutionData)
					pd = (PollutionData)args[0];
				else if(args[0] instanceof Pair<?, ?>)
				{
					pd = EcologyMod.ph.getPollution(player.getEntityWorld(), (Pair<Integer, Integer>)args[0]);
				}
				else if(args[0] instanceof BlockPos)
					pd = EcologyMod.ph.getPollution(player.getEntityWorld(), EMUtils.blockPosToPair((BlockPos)args[0]));
				else if(args[0] instanceof ChunkPos)
					pd = EcologyMod.ph.getPollution(player.getEntityWorld(), EMUtils.chunkPosToPair((ChunkPos)args[0]));
			}
		}
		else
		{
			pd = EcologyMod.ph.getPollution(player.world, EMUtils.blockPosToPair(player.getPosition()));
		}
		
		if(pd == null)
			return false;
		
		if(trig_type == TriggeringType.AND)
			return pd.compareTo(trig_pollution) >= 0;
			
		if(trig_type == TriggeringType.OR)
			return pd.compareOR(trig_pollution) >= 0;
		
		return false;
	}
	
	public static PollutionPredicate deserialize(@Nullable JsonElement json)
	{
		if (json != null && !json.isJsonNull())
        {
			JsonObject jsonobject = JsonUtils.getJsonObject(json, "chunk_pollution");
			
			PollutionData data = new PollutionData(-1,-1,-1);
			TriggeringType tt = TriggeringType.AND;
			
			if(jsonobject.has("pollution"))
				data = EMUtils.pollutionDataFromJSON(jsonobject.get("pollution").toString(), PollutionData.getEmpty(), "Failed to deserialize PollutionPredicate from JSON! Using ANY");
			
			try{
			if(JsonUtils.hasField(jsonobject, "triggering_type"))
				tt = TriggeringType.valueOf(JsonUtils.getString(jsonobject, "triggering_type"));
			}
			catch(Exception e)
			{
				EcologyMod.log.error(e.toString());
				tt = TriggeringType.AND;
			}
			
			return new PollutionPredicate(data, tt);
        }
		else
			return ANY;
	}
}
