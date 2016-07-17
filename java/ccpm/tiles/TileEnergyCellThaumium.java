package ccpm.tiles;

import ccpm.core.CCPM;
import ccpm.utils.config.CCPMConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEnergyCellThaumium extends TileEnergyCellBasic/* implements IInfusionStabiliser */{

	public TileEnergyCellThaumium(String name, int maxEnergy) {
		super(name, maxEnergy);
	}

/*	@Override
	public boolean useEnergy(int amount, TileEntity user) {
		
		//boolean ret = true;
		
		int i = AuraHelper.drainAuraAvailable(getWorld(), getPos(), Aspect.AIR, amount/100);
		
		AuraHelper.pollute(getWorld(), getPos(), amount/100, true);
		
		return i >= amount / 100;
		//CCPM.log.info("Essentia drained "+(ret ? "successfully" : "unsuccessfully"));
		//return ret;
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos p) {
		return true;
	}
*/
}
