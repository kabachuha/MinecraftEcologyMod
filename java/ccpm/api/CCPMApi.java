package ccpm.api;

import java.util.Random;

import ccpm.api.events.CCPMFireTickEvent;
import ccpm.api.events.CCPMPlantGrowEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public abstract class CCPMApi {

	public static ToolMaterial pollMaterial = EnumHelper.addToolMaterial("ccpm.pollution", 3, 750, 2.5F, 7, 22);
	
	private CCPMApi(){};

	public static DamageSource damageSourcePollution = new DamageSource("pollution").setDamageBypassesArmor();
	
	public static boolean postEvent(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
	{
		//System.out.println("CKP CKP CKP");
		CCPMPlantGrowEvent event = new CCPMPlantGrowEvent(worldIn, pos, state, isClient);
		MinecraftForge.EVENT_BUS.post(event);
		return (event.getResult() != Result.DENY) && !event.isCanceled();
	}
	
	public static void postFireTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		CCPMFireTickEvent event = new CCPMFireTickEvent(worldIn, pos, state, rand);
		
		MinecraftForge.EVENT_BUS.post(event);
	}
}
