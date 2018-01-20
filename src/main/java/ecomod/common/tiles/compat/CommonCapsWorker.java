package ecomod.common.tiles.compat;

import ecomod.common.tiles.TileAdvancedFilter;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMIntermod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.commoncapabilities.api.capability.work.IWorker;

public class CommonCapsWorker
{
	@CapabilityInject(IWorker.class)
	public static final Capability<IWorker> CAP_WORKER = null;
	
	@CapabilityInject(IWorker.class)
	public static void onCapWorkerInjection(Capability<IWorker> cap)
	{
		EMConsts.common_caps_compat$IWorker = true;
		EMIntermod.log.info("Injecting commoncapabilities IWorker Capability!");
	}
	
	public static class AdvancedFilterWorker implements IWorker
	{
		private final TileAdvancedFilter filter;
		
		public AdvancedFilterWorker(TileAdvancedFilter tile)
		{
			filter = tile;
		}
		
		@Override
		public boolean hasWork()
		{
			return filter.getProduction() != null;
		}

		@Override
		public boolean canWork()
		{
			return filter.hasWork();
		}
		
		/**
		 * For testing caps
		 */
		@Deprecated
		public static boolean hasWorkByCap(TileAdvancedFilter tile)
		{
			if(tile.hasCapability(CAP_WORKER, null))
				return tile.getCapability(CAP_WORKER, null).hasWork() && tile.getCapability(CAP_WORKER, null).canWork();
			
			return false;
		}
	}
}
