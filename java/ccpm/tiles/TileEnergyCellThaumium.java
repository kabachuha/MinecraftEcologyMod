package ccpm.tiles;

import ccpm.utils.config.CCPMConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectSourceHelper;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class TileEnergyCellThaumium extends TileEnergyCellBasic implements IInfusionStabiliser {

	public TileEnergyCellThaumium(String name, int maxEnergy) {
		super(name, maxEnergy);
	}

	@Override
	public boolean useEnergy(int amount, TileEntity user) {
		
		boolean ret = true;
		
		for(int i = 0; i <= amount / 100; i++)
		{
			ret = ret && AspectSourceHelper.drainEssentia(user, Aspect.ENERGY, null, 30);
		}
		
		return ret;
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos p) {
		return true;
	}

}
