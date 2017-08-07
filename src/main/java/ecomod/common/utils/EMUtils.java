package ecomod.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.PollutionData;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.network.EMPacketHandler;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class EMUtils 
{
	public static ResourceLocation resloc(String path)
	{
		return new ResourceLocation(EMConsts.modid, path);
	}
	
	public static ResourceLocation MC_resloc(String path)
	{
		return new ResourceLocation(path);
	}
	
	public static String getString(URL url) throws IOException
	{
		StringBuffer buffer = null;
		EcologyMod.log.info("Connecting to: "+url.toString());
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine;
		buffer = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) 
		{
			buffer.append(inputLine);
			buffer.append('\n');
		}
		buffer.deleteCharAt(buffer.length()-1);
		in.close();
		
		return buffer.toString(); 
	}
	
	public static boolean shouldTEPCupdate(String thi/*s*/, String ne/*w*/)
	{
		if(thi.toLowerCase().contentEquals("custom")) return false;
		
		if(Integer.parseInt(String.valueOf(ne.charAt(0))) < Integer.parseInt(String.valueOf(thi.charAt(0))))
			return false;
		
		if(Integer.parseInt(String.valueOf(ne.charAt(0))) > Integer.parseInt(String.valueOf(thi.charAt(0))))
			return true;
		
		return Integer.parseInt(String.valueOf(ne.charAt(2))) > Integer.parseInt(String.valueOf(thi.charAt(2)));
	}
	
	public static Pair<Integer, Integer> chunkPosToPair(ChunkPos pos)
	{
		return Pair.of(pos.chunkXPos, pos.chunkZPos);
	}
	
	public static Pair<Integer, Integer> blockPosToPair(BlockPos pos)
	{
		return chunkPosToPair(new ChunkPos(pos));
	}
	
	public static ChunkPos pairToChunkPos(Pair<Integer, Integer> pair)
	{
		return new ChunkPos(pair.getLeft(), pair.getRight());
	}
	
	public static boolean isBlockWater(World w, BlockPos pos)
	{	
		return w.getBlockState(pos).getMaterial() == Material.WATER;
	}
	
	public static int countWaterInRadius(World w, BlockPos center, int radius)
	{
		Iterable<BlockPos> blocks = BlockPos.getAllInBox(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
		
		int ret = 0;
		
		for(BlockPos bp : blocks)
			if(isBlockWater(w, bp))
				ret++;
		
		return ret;
	}
	
	public static PollutionData pollutionDataFromJSON(String json, PollutionData failture_data)
	{
		return pollutionDataFromJSON(json, failture_data, "Failed to parse "+json+" into PollutionData!");
	}
	
	static Gson gson = new GsonBuilder().create();
	
	public static PollutionData pollutionDataFromJSON(String json, PollutionData failture_data, String fail_message)
	{
		PollutionData ret;
		
		try
		{
			ret = gson.fromJson(json, PollutionData.class);
		}
		catch (JsonSyntaxException e)
		{
			EcologyMod.log.error(fail_message);
			ret = failture_data;
		}
		
		return ret;
	}
	
	public static boolean isSquareChunkPollution(Collection<ChunkPollution> points)
	{
		ArrayList<Pair<Integer, Integer>> al = new ArrayList<Pair<Integer, Integer>>();
		
		for(ChunkPollution c : points)
			al.add(c.getLeft());
		
		return isSquare(al);
	}
	
	public static boolean isSquare(Collection<Pair<Integer, Integer>> points)
	{
		/*
			x_min, y_min - position of the first vertex
		*/
		int x_min = Integer.MAX_VALUE, y_min = Integer.MAX_VALUE;
		/*
			x_max, y_max - position of the opposite vertex
		 */
		int x_max = Integer.MIN_VALUE, y_max = Integer.MIN_VALUE;
		
		boolean flag = false;
		
		//Finding the two opposite vertices
		for(Pair<Integer, Integer> point : points)
		{
			if(!flag)
			{
				x_min = point.getLeft();
				y_min = point.getRight();
				flag = true;
			}
			else
			{
				if(point.getLeft() < x_min)
					x_min = point.getLeft();
				
				if(point.getRight() < y_min)
					y_min = point.getRight();
			}
			
			if(point.getLeft() > x_max)
				x_max = point.getLeft();
			
			if(point.getRight() > y_max)
				y_max = point.getRight();
		}
		
		if(x_min == Integer.MAX_VALUE || y_min == Integer.MAX_VALUE || x_max == Integer.MIN_VALUE || y_max == Integer.MIN_VALUE)
			return false;
		
		int size_x, size_y;
				
		size_x = x_max - x_min;
		size_y = y_max - y_min;
		
		if(size_x != size_y)//Thus the figure isn't square
			return false;
		
		if(size_x/2 > EMConsts.max_cached_pollution_radius || size_y/2 > EMConsts.max_cached_pollution_radius)
			return false;
		
		
		for(int i = x_min; i <= x_max; i++)//I is X
			for(int j = y_min; j <= y_max; j++)//J is Y(z)
			{
				while(points.contains(Pair.of(i, j)))
					points.remove(Pair.of(i, j));//Getting rid of any duplicates if there are.
			}
		
		
		return points.isEmpty();
	}
	
	//Borrowed from Buildcraft(https://github.com/BuildCraft/BuildCraft) and slightly modified
	public static void pushFluidAround(IBlockAccess world, BlockPos pos, IFluidTank tank) 
	{
        FluidStack potential = tank.drain(tank.getFluidAmount(), false);
        
        int drained = 0;
        
        if (potential == null || potential.amount <= 0)
        {
            return;
        }
        FluidStack working = potential.copy();
        
        for (EnumFacing side : EnumFacing.VALUES)
        {
            if (potential.amount <= 0)
                break;
            
            TileEntity target = world.getTileEntity(pos.offset(side));
            if (target == null) {
                continue;
            }
            IFluidHandler handler = target.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
            if (handler != null) {
                int used = handler.fill(potential, true);

                if (used > 0) {
                    drained += used;
                    potential.amount -= used;
                }
            }
        }
        if (drained > 0) {
            FluidStack actuallyDrained = tank.drain(drained, true);
        }
	}
}
