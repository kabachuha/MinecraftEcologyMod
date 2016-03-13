package ccpm.api;

import ccpm.api.events.CCPMPlantGrowEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public abstract class CCPMApi {



	public static DamageSource damageSourcePollution = new DamageSource("pollution").setDamageBypassesArmor();
	
	public static boolean postEvent(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
	{
		System.out.println("CKP CKP CKP");
		CCPMPlantGrowEvent event = new CCPMPlantGrowEvent(worldIn, pos, state, isClient);
		MinecraftForge.EVENT_BUS.post(event);
		return (event.getResult() != Result.DENY) && !event.isCanceled();
	}
	
	
}
