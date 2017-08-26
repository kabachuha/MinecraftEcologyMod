package ecomod.common.pollution.handlers;


import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.stuff.EMIntermod;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		if(!PollutionSourcesConfig.hasSource("explosion_pollution_per_power"))
			return;
		
		PollutionData emission = PollutionSourcesConfig.getSource("explosion_pollution_per_power");
		
		emission.multiply(PollutionType.WATER, w.isRainingAt(new BlockPos(event.pos)) ? 3 : 1);
		
		emission.multiplyAll((float) event.power);
		
		EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new BlockPos(event.pos)), emission, true);
		
		if(PollutionSourcesConfig.hasSource("nuclear_explosion_pollution_per_power"))
		if((event.power >= 30 && (event.radiationRange >= 67 || event.rangeLimit > 67)) || (event.entity != null && event.entity.getClass() == NUKE_CLASS))
		{
			PollutionData emission_nuke = PollutionSourcesConfig.getSource("nuclear_explosion_pollution_per_power");
			
			emission_nuke.multiplyAll((float)event.power * (PollutionUtils.hasSurfaceAccess(w, new BlockPos(event.pos).up(30)) ? 1F : 0.375F));
			
			int max_range = ((int) event.rangeLimit) >> 4;
			
			float nuke_rad_mult = 0.75F;
			
			ChunkPos pos = new ChunkPos(new BlockPos(event.pos));
			
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
