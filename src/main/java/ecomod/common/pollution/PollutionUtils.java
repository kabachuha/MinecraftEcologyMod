package ecomod.common.pollution;

import org.apache.commons.lang3.tuple.Pair;

import buildcraft.api.tiles.IHasWork;
import buildcraft.api.tiles.TilesAPI;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.ModAPIManager;

public class PollutionUtils
{
	public static String genPMid(World w)
	{
		return w.getWorldInfo().getWorldName()+"_"+w.provider.getDimension();
	}
	
	public static String genPMid(PollutionManager pm)
	{
		return pm == null ? "null" : pm.getWorld().getWorldInfo().getWorldName()+"_"+pm.getWorld().provider.getDimension();
	}
	
	public static Chunk coordsToChunk(World w, Pair<Integer, Integer> coord)
	{
		return w.getChunkFromChunkCoords(coord.getLeft(), coord.getRight());
	}
	
	public static boolean isTEWorking(TileEntity te)
	{	
		if(te instanceof TileEntityFurnace)
		{
			return ((TileEntityFurnace)te).isBurning();
		}
		
		if(ModAPIManager.INSTANCE.hasAPI("BuildCraftAPI|tiles"))
		{
			if(te.hasCapability(TilesAPI.CAP_HAS_WORK, null))
			{
				IHasWork ihw = te.getCapability(TilesAPI.CAP_HAS_WORK, null);
				return ihw.hasWork();
			}
			else
			{
				if(te instanceof IHasWork)
				{
					return ((IHasWork)te).hasWork();
				}
			}
		}
		
		return true;
	}
}
