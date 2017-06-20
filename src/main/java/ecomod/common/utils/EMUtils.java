package ecomod.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

public class EMUtils 
{
	public static ResourceLocation resloc(String path)
	{
		return new ResourceLocation(EMConsts.modid, path);
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
	
	public static boolean isBlockWater(World w, BlockPos pos)
	{	
		return w.getBlockState(pos).getBlock() instanceof IFluidBlock ? ((IFluidBlock)w.getBlockState(pos).getBlock()).getFluid() == FluidRegistry.WATER : false;
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
	
	
}
