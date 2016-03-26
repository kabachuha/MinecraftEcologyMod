package ccpm.tiles;

import ccpm.integration.thaumcraft.TCUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectSourceHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.crafting.IInfusionStabiliser;

public class TileAdvThaum extends TileEnergyCellBasic implements IInfusionStabiliser {

	public TileAdvThaum(String name, int maxEnergy) {
		super(name, maxEnergy);
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos pos) {
		return true;
	}

	
	@Override
	public boolean useEnergy(int amount, TileEntity user) {
		
		boolean ret = true;
		
		for(int i = 0; i<=amount/100; i++)
		{
			ret = ret && user.getWorld().rand.nextBoolean() ? TCUtils.drainEssentia(user, Aspect.AIR, null, 30, 8) && TCUtils.drainEssentia(user, Aspect.ENERGY, null, 30, 8) : true;
		}
		
		return ret;
	}
}
