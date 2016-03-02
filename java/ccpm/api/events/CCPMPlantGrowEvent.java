package ccpm.api.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;;

@Cancelable
@HasResult
public class CCPMPlantGrowEvent extends Event {

	public final World world;
	public final BlockPos pos;
	public final IBlockState state;
	public final boolean isClient;
	
	public CCPMPlantGrowEvent(World w, BlockPos posi, IBlockState Bstate, boolean isClientSide) {
		world = w;
		pos = posi;
		state = Bstate;
		isClient = isClientSide;
	}

}
