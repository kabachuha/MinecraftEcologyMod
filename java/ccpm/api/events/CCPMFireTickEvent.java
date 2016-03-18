package ccpm.api.events;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CCPMFireTickEvent extends Event {
	
	public World world;
	public BlockPos pos;
	public IBlockState state;
	public Random rand;

	public CCPMFireTickEvent(World worldIn, BlockPos posIn, IBlockState stateIn, Random randIn) {
		world = worldIn;
		pos = posIn;
		state=stateIn;
		rand=randIn;
	}

}
