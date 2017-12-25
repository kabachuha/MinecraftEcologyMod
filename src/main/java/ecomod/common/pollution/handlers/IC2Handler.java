package ecomod.common.pollution.handlers;


import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.common.utils.newmc.EMChunkPos;
import ecomod.core.stuff.EMIntermod;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class IC2Handler
{
	private Class NUKE_CLASS = null;
	
	public IC2Handler()
	{
		try{
			NUKE_CLASS = Class.forName("ic2.core.block.EntityNuke");
		}
		catch(Exception e)
		{
			EMIntermod.log.error("Not found ic2.core.block.EntityNuke");
		}
	}
	
	@SubscribeEvent
	public void onIC2Explosion(ic2.api.event.ExplosionEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.world;
		
		if(w.isRemote)return;
		
		if(!PollutionSourcesConfig.hasSource("explosion_pollution_per_power"))
			return;
		
		PollutionData emission = PollutionSourcesConfig.getSource("explosion_pollution_per_power");
		
		emission.multiply(PollutionType.WATER, EMUtils.isRainingAt(w, new EMBlockPos(event.x, event.y, event.z)) ? 3 : 1);
		
		emission.multiplyAll((float) event.power);
		
		EcomodAPI.emitPollution(w, Pair.of((int)event.x, (int)event.z), emission, true);
		
		if(PollutionSourcesConfig.hasSource("nuclear_explosion_pollution_per_power"))
		if((event.power >= 30 && (event.radiationRange >= 67 || event.rangeLimit > 67)) || (event.entity != null && event.entity.getClass() == NUKE_CLASS))
		{
			PollutionData emission_nuke = PollutionSourcesConfig.getSource("nuclear_explosion_pollution_per_power");
			
			emission_nuke.multiplyAll((float)event.power * (PollutionUtils.hasSurfaceAccess(w, new EMBlockPos(event.x, event.y, event.z).up(30)) ? 1F : 0.375F));
			
			int max_range = ((int) event.rangeLimit) >> 4;
			
			float nuke_rad_mult = 0.75F;
			
			EMChunkPos pos = new EMChunkPos(new EMBlockPos(event.x, event.y, event.z));
			
			for(int offset = 0; offset <= max_range; offset++)
			{
				for(int i = pos.chunkXPos - offset; i <= pos.chunkXPos + offset; i++)
					for(int j = pos.chunkZPos - offset; j <= pos.chunkZPos + offset; j++)
					{
						EcomodAPI.emitPollution(w, Pair.of(i, j), emission_nuke.clone().multiplyAll(nuke_rad_mult), true);
					}
				
				nuke_rad_mult *= 0.75F;
			}
		}
	}
}
