package ecomod.common.pollution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import buildcraft.api.tiles.IHasWork;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.api.EcomodStuff;
import ecomod.api.pollution.IRespirator;
import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.core.EMConsts;
import ecomod.core.stuff.EMIntermod;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

public class PollutionUtils
{
	public static String genPMid(World w)
	{
		return w.getWorldInfo().getWorldName()+"_"+w.provider.dimensionId;
	}
	
	public static String genPMid(PollutionManager pm)
	{
		return pm == null ? "null" : pm.getWorld().getWorldInfo().getWorldName()+"_"+pm.getWorld().provider.dimensionId;
	}
	
	public static Chunk coordsToChunk(World w, Pair<Integer, Integer> coord)
	{
		return w.getChunkFromChunkCoords(coord.getLeft(), coord.getRight());
	}
	
	public static boolean isTEWorking(World w, TileEntity te)
	{	
		TileEntity tile = w.getTileEntity(te.xCoord, te.yCoord, te.zCoord);
		
		//TODO add more checks
		if(te instanceof TileEntityFurnace)
		{
			return ((TileEntityFurnace)tile).isBurning();
		}
		
		if(tile instanceof IHasWork)
		{
			return ((IHasWork)tile).hasWork();
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
	
	public static final ForgeDirection horizontal_facings[] = new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.NORTH, ForgeDirection.WEST, ForgeDirection.SOUTH};
	
	private static boolean hasSurfaceAccess(World w, EMBlockPos bp, List<EMBlockPos> was_at)
	{
		if(was_at.contains(bp))
			return false;
		
		if(w.canBlockSeeTheSky(bp.getX(), bp.getY(), bp.getZ()) && isBlockHollow(w, bp.up(), ForgeDirection.UP))
			return true;
		
		if(isBlockHollow(w, bp.up(), ForgeDirection.UP))
			if(hasSurfaceAccess(w, bp.up(), was_at))
				return true;
		
		for(ForgeDirection facing : horizontal_facings)
		{
			EMBlockPos b = bp.offset(facing);
			if(isBlockHollow(w, b, facing))
			{
					if(w.canBlockSeeTheSky(b.getX(), b.getY(), b.getZ()))
					{
						return true;
					}
					else if(isBlockHollow(w, b.up(), ForgeDirection.UP))
					{
						if(hasSurfaceAccess(w, b.up(), was_at))
							return true;
					}
			}
		}
			
		was_at.add(bp);
		return false;
	}
	
	public static boolean isBlockHollow(World world, EMBlockPos pb, ForgeDirection direction)
	{
		int rv3 = isBlockAirPenetratorCFG(EMUtils.getBlock(world, pb), world.getBlockMetadata(pb.getX(), pb.getY(), pb.getZ()));
		
		if(rv3 > 0)
			return true;
		else if(rv3 < 0)
			return false;
		
		AxisAlignedBB aabb = world.getBlock(pb.getX(), pb.getY(), pb.getZ()).getCollisionBoundingBoxFromPool(world, pb.getX(), pb.getY(), pb.getZ());
		
		if(aabb == null)
			return true;
		
		if(aabb == AxisAlignedBB.getBoundingBox(-1, -1, -1, 1, 1, 1))
			return false;
		
		double l = aabb.maxX - aabb.minX;
        double h = aabb.maxY - aabb.minY;
        double w = aabb.maxZ - aabb.minZ;
		

		if(direction == ForgeDirection.WEST || direction == ForgeDirection.EAST)
		{
			return h < 0.9D || w < 0.9D;
		}
		else if(direction == ForgeDirection.DOWN || direction == ForgeDirection.UP)
		{
			return l < 0.9D || w < 0.9D;
		}
		else if(direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH)
		{
			return h < 0.9D || l < 0.9D;
		}

		return false;
	}
	
	
	/**
	 * @return 0 if the block custom air penetration value is undefined. 1 if block is penetrating air, -1 if not.
	 */
	public static int isBlockAirPenetratorCFG(Block block, int meta)
	{
		if(block == null)
			return 0;
		
		String searchkey = GameRegistry.findUniqueIdentifierFor(block).toString();
		String searchmeta ="@"+meta;
		
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
	
	public static boolean hasSurfaceAccess(World w, EMBlockPos bp)
	{	
		return hasSurfaceAccess(w, bp, new ArrayList<EMBlockPos>());
	}
	
	public static boolean isEntityRespirating(EntityLivingBase entity)
	{
		return isEntityRespirating(entity, true);
	}
	
	public static boolean isEntityRespirating(EntityLivingBase entity, boolean decr)
	{
		ItemStack is = entity.getEquipmentInSlot(4);
		
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
