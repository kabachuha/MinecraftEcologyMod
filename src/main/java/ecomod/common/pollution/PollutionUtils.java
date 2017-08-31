package ecomod.common.pollution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import buildcraft.api.tiles.IHasWork;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.IRespirator;
import ecomod.api.pollution.PollutionData;
import ecomod.core.stuff.EMIntermod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
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
	
	public static boolean isTEWorking(World w, TileEntity te)
	{	
		TileEntity tile = w.getTileEntity(te.getPos());
		
		//TODO add more checks
		if(te instanceof TileEntityFurnace)
		{
			return ((TileEntityFurnace)tile).isBurning();
		}
		
		if(ModAPIManager.INSTANCE.hasAPI("BuildCraftAPI|tiles"))
		{
			if(EMIntermod.CAP_HAS_WORK != null)
			if(tile.hasCapability(EMIntermod.CAP_HAS_WORK, null))
			{
				IHasWork ihw = tile.getCapability(EMIntermod.CAP_HAS_WORK, null);
				return ihw.hasWork();
			}
			else
			{
				if(tile instanceof IHasWork)
				{
					return ((IHasWork)tile).hasWork();
				}
			}
		}
		
		return true;
	}
	
	public static PollutionData pollutionMaxFrom(PollutionData a, PollutionData b)
	{
		for(PollutionData.PollutionType type : PollutionData.PollutionType.values())
		{
			if(b.get(type) > a.get(type))
				a.set(type, b.get(type));
		}
		
		return a;
	}
	
	
	private static boolean hasSurfaceAccess(World w, BlockPos bp, List<BlockPos> was_at)
	{
		if(was_at.contains(bp))
			return false;
		
		if(w.canSeeSky(bp) && isBlockHollow(w, bp.up(), EnumFacing.UP))
			return true;
		
		if(isBlockHollow(w, bp.up(), EnumFacing.UP))
			if(hasSurfaceAccess(w, bp.up(), was_at))
				return true;
		
		for(EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			BlockPos b = bp.offset(facing);
			if(isBlockHollow(w, b, facing))
			{
					if(w.canSeeSky(b))
					{
						return true;
					}
					else if(isBlockHollow(w, b.up(), EnumFacing.UP))
					{
						if(hasSurfaceAccess(w, b.up(), was_at))
							return true;
					}
			}
		}
			
		was_at.add(bp);
		return false;
	}
	
	public static boolean isBlockHollow(World world, BlockPos pb, EnumFacing direction)
	{
		int rv3 = isBlockAirPenetratorCFG(world.getBlockState(pb));
		
		if(rv3 > 0)
			return true;
		else if(rv3 < 0)
			return false;
		
		AxisAlignedBB aabb = world.getBlockState(pb).getCollisionBoundingBox(world, pb);
		
		if(aabb == null || aabb == Block.NULL_AABB)
			return true;
		
		if(aabb == Block.FULL_BLOCK_AABB)
			return false;
		
		double l = aabb.maxX - aabb.minX;
        double h = aabb.maxY - aabb.minY;
        double w = aabb.maxZ - aabb.minZ;
		

		if(direction.getAxis() == Axis.X)
		{
			return h < 0.9D || w < 0.9D;
		}
		else if(direction.getAxis() == Axis.Y)
		{
			return l < 0.9D || w < 0.9D;
		}
		else if(direction.getAxis() == Axis.Z)
		{
			return h < 0.9D || l < 0.9D;
		}

		return false;
	}
	
	
	/**
	 * @param ibs BlockState
	 * @return 0 if the block custom air penetration value is undefined. 1 if block is penetrating air, -1 if not.
	 */
	public static int isBlockAirPenetratorCFG(IBlockState ibs)
	{
		if(ibs == null || ibs.getBlock() == null)
			return 0;
		
		String searchkey = ibs.getBlock().getRegistryName().toString();
		String searchmeta ="@"+ibs.getBlock().getMetaFromState(ibs);
		
		if(EcomodStuff.additional_blocks_air_penetrating_state == null)
			return 0;
		
		if(EcomodStuff.additional_blocks_air_penetrating_state.containsKey(searchkey))
		{
			if(EcomodStuff.additional_blocks_air_penetrating_state.get(searchkey))
				return 1;
			else
				return -1;
		}
		else if(EcomodStuff.additional_blocks_air_penetrating_state.containsKey(searchkey+searchmeta))
		{
			if(EcomodStuff.additional_blocks_air_penetrating_state.get(searchkey+searchmeta))
				return 1;
			else
				return -1;
		}
		
		return 0;
	}
	
	public static boolean hasSurfaceAccess(World w, BlockPos bp)
	{	
		return hasSurfaceAccess(w, bp, new ArrayList<BlockPos>());
	}
	
	public static boolean isEntityRespirating(EntityLivingBase entity)
	{
		return isEntityRespirating(entity, true);
	}
	
	public static boolean isEntityRespirating(EntityLivingBase entity, boolean decr)
	{
		ItemStack is = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		
		if(is != null)
		{
			if(is.getItem() instanceof IRespirator)
			{
				return ((IRespirator)is.getItem()).isRespirating(entity, is, decr);
			}
		}
		
		return false;
	}
}
