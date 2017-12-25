package ecomod.common.utils;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.versioning.ComparableVersion;
import cpw.mods.fml.relauncher.Side;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.PollutionData;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.common.utils.newmc.EMChunkPos;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

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
	
	public static String parseMINECRAFTURL(String mcurl)
	{
		if(mcurl.contains("<MINECRAFT>"))
		{
			String mcpath = "";
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
				mcpath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
			else
				mcpath = FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName();
			mcpath = mcpath.substring(0, mcpath.length()-2);
			mcurl = mcurl.replace("<MINECRAFT>", mcpath);
		}
		
		return mcurl;
	}
	
	public static boolean shouldTEPCupdate(String thiz, String nev)
	{
		if(thiz.toLowerCase().contentEquals("custom")) return false;
		
		ComparableVersion ver1 = new ComparableVersion(thiz);
		ComparableVersion ver2 = new ComparableVersion(nev);
		
		return  ver2.compareTo(ver1) > 0;
	}
	
	public static Pair<Integer, Integer> chunkPosToPair(EMChunkPos pos)
	{
		return Pair.of(pos.chunkXPos, pos.chunkZPos);
	}
	
	public static Pair<Integer, Integer> blockPosToPair(EMBlockPos pos)
	{
		return chunkPosToPair(new EMChunkPos(pos));
	}
	
	public static Pair<Integer, Integer> blockPosToPair(Entity entity)
	{
		return blockPosToPair(new EMBlockPos(entity));
	}
	
	public static EMChunkPos pairToChunkPos(Pair<Integer, Integer> pair)
	{
		return new EMChunkPos(pair.getLeft(), pair.getRight());
	}
	
	public static boolean isBlockWater(World w, EMBlockPos pos)
	{	
		return getBlock(w, pos) == Blocks.water;
	}
	
	public static int countWaterInRadius(World w, EMBlockPos center, int radius)
	{
		Iterable<EMBlockPos> blocks = EMBlockPos.getAllInBox(center.add(-radius, -radius, -radius), center.add(radius, radius, radius));
		
		int ret = 0;
		
		for(EMBlockPos bp : blocks)
			if(isBlockWater(w, bp))
				ret++;
		
		return ret;
	}
	
	public static Chunk getChunkFromBlockCoords(World world, EMBlockPos pos)
	{
		return world.getChunkFromBlockCoords(pos.getX(), pos.getZ());
	}
	
	public static Block getBlock(World world, EMBlockPos pos)
	{
		return world.getBlock(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static TileEntity getTile(IBlockAccess world, EMBlockPos pos)
	{
		return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static void setBiome(World w, BiomeGenBase biome, int x, int z)
	{
		Chunk chunk = getChunkFromBlockCoords(w, new EMBlockPos(x,w.getActualHeight(),z));
		setBiome(chunk, biome, x, z);
	}
	
	public static void setBiome(Chunk c, BiomeGenBase biome, int x, int z)
	{
		byte[] b = c.getBiomeArray();
		byte cbiome = b[(z & 0xf) << 4 | x & 0xf];
		cbiome = (byte)(biome.biomeID & 0xff);
		b[(z & 0xf) << 4 | x & 0xf] = cbiome;
		c.setBiomeArray(b);
		
		EMPacketHandler.WRAPPER.sendToDimension(new EMPacketString("*"+x+";"+z+";"+biome.biomeID), c.worldObj.provider.dimensionId);
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
		
		
		for(int i = x_min; i <= x_max; i++)//I is X
			for(int j = y_min; j <= y_max; j++)//J is Y(z)
			{
				while(points.contains(Pair.of(i, j)))
					points.remove(Pair.of(i, j));//Getting rid of points within the square.
			}
		
		
		return points.isEmpty();
	}
	
	public static Vec3 vec(double x, double y, double z)
	{
		return Vec3.createVectorHelper(x, y, z);
	}
	
	//Linear interpolation
	public static double lerp(double a, double b, double v)
	{
		if (v == 0)
			return a;
		else if (v == 1)
			return b;

		return a + (b - a) * v;
	}

	public static float lerp(float a, float b, float v)
	{
		if (v == 0)
			return a;
		else if (v == 1)
			return b;

		return a + (b - a) * v;
	}

	public static int lerp(int a, int b, float v)
	{
		if (v == 0)
			return a;
		else if (v == 1)
			return b;

		return a + Math.round((b - a) * v);
	}
	
	public static Vec3 lerp(Vec3 a, Vec3 b, float v)
	{
		if (v == 0)
			return a;
		else if (v == 1)
			return b;

		return vec(lerp(a.xCoord, b.xCoord, v),
						lerp(a.yCoord, b.yCoord, v),
						lerp(a.zCoord, b.zCoord, v));
	}
	
	public static Color lerp(Color a, Color b, float v)
	{
		if (v == 0)
			return a;
		else if (v == 1)
			return b;
		
		
		return new Color(lerp(a.getRed(), b.getRed(), v),
						lerp(a.getGreen(), b.getGreen(), v),
						lerp(a.getBlue(), b.getBlue(), v));
	}
	
	//Horizontal gradient
	public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double)right, (double)top, (double)zLevel);
        tessellator.addVertex((double)left, (double)top, (double)zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double)left, (double)bottom, (double)zLevel);
        tessellator.addVertex((double)right, (double)bottom, (double)zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
	
	public static EnumFacing getOppositeFacing(EnumFacing f)
	{
		if(f == EnumFacing.DOWN)
			return EnumFacing.UP;
		else if(f == EnumFacing.UP)
			return EnumFacing.DOWN;
		else if(f == EnumFacing.EAST)
			return EnumFacing.WEST;
		else if(f == EnumFacing.WEST)
			return EnumFacing.EAST;
		else if(f == EnumFacing.NORTH)
			return EnumFacing.SOUTH;
		else if(f == EnumFacing.SOUTH)
			return EnumFacing.NORTH;
			
		return null;
	}
	
	public static ForgeDirection facingToDirection(EnumFacing f)
	{
		return f == EnumFacing.DOWN ? ForgeDirection.DOWN :
			f == EnumFacing.UP ? ForgeDirection.UP :
			f == EnumFacing.EAST ? ForgeDirection.EAST :
			f == EnumFacing.WEST ? ForgeDirection.WEST :
			f == EnumFacing.NORTH ? ForgeDirection.NORTH :
			f == EnumFacing.SOUTH ? ForgeDirection.SOUTH :
			ForgeDirection.UNKNOWN;
	}
	
	public static void pushFluidAround(IBlockAccess world, EMBlockPos pos, IFluidTank tank) 
	{
        FluidStack potential = tank.drain(tank.getFluidAmount(), false);
        
        int drained = 0;
        
        if (potential == null || potential.amount <= 0)
        {
            return;
        }
        FluidStack working = potential.copy();
        
        for (ForgeDirection side : ForgeDirection.values())
        {
            if (potential.amount <= 0)
                break;
            
            TileEntity target = getTile(world, pos.offset(side));
            if (target == null || !(target instanceof IFluidHandler)) {
                continue;
            }
            IFluidHandler handler = (IFluidHandler) target;
            if (handler != null) {
                int used = handler.fill(side.getOpposite() ,potential, true);

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
	
	public static TileEntity get1NearbyTileEntity(@Nullable String id, World w, EMBlockPos pos)
	{
		TileEntity ret = null;

		for(EnumFacing ef : EnumFacing.values())
		{
			TileEntity te = getTile(w, pos.offset(ef));
			if(te != null)
			{
				if(ret == null)
				{
					if(id == null || id == "" || getTileEntityId(te.getClass()).toString().equals(id))
						ret = te;
				}
				else
				{
					if(id == null || id == "" || getTileEntityId(te.getClass()).toString().equals(id))
					{
						return null;
					}
				}
			}
		}
			
		return ret;
	}
	
	public static TileEntity get1NearbyTileEntity(World w, EMBlockPos pos)
	{
		return get1NearbyTileEntity(null, w, pos);
	}
	
	public static double mean(Number...numbers)
	{
		if(numbers.length == 0)
			return Double.NaN;
		double sum = 0;
		for(Number n : numbers)
		{
			sum += n.doubleValue();
		}
		
		return sum/numbers.length;
	}
	
	public static Pair<Integer, Integer> offsetPair(Pair<Integer, Integer> pair, Pair<Integer, Integer> offset)
	{
		return Pair.of(pair.getLeft() + offset.getLeft(), pair.getRight() + offset.getRight());
	}
	
	public static Pair<Integer, Integer> offsetPair(Pair<Integer, Integer> pair, int offx, int offy)
	{
		return Pair.of(pair.getLeft() + offx, pair.getRight() + offy);
	}
	
	public static <T> T retLog(T o, Logger log, Level level)
	{
		log.log(level, o);
		return o;
	}
	
	public static <T> T retLog(T o, Logger log)
	{
		log.info(o);
		return o;
	}
	
	public static <T> T retLog(T o)
	{
		return retLog(o, EcologyMod.log);
	}
	
	public static <T> T retLog(T o, Level lvl)
	{
		return retLog(o, EcologyMod.log, lvl);
	}
	
	public static int square(int x)
	{
		return x * x;
	}
	
	public static double square(float x)
	{
		return x * x;
	}
	
	public static double square(double x)
	{
		return x * x;
	}
	
	public static void logIfNotNull(Object o, Logger log, Level lvl)
	{
		if(o != null)
			log.log(lvl, o);
	}
	
	public static void logIfNotNull(PollutionData o, Logger log, Level lvl)
	{
		if(o != null && !o.isEmpty())
			log.log(lvl, o);
	}
	
	public static Class classForNameOrNull(String className)
	{
		try
		{
			return Class.forName(className);
		}
		catch(Exception e)
		{
			EcologyMod.log.error("Unable to find the class: "+className);
			EcologyMod.log.error(e.toString());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void repeat(int times, java.util.function.IntConsumer func)
	{
		for(int i = 0; i < times; i++)
			func.accept(i);
	}
	
	public static String getTileEntityId(Class tile_class)
	{
		return (String) TileEntity.classToNameMap.get(tile_class);
	}
	
	public static boolean isRainingAt(World w, EMBlockPos pos)
    {
        if (!w.isRaining())
        {
            return false;
        }
        else if (!w.canBlockSeeTheSky(pos.getX(), pos.getY(), pos.getZ()))
        {
            return false;
        }
        else if (w.getPrecipitationHeight(pos.getX(), pos.getZ()) > pos.getY())
        {
            return false;
        }
        else
        {
            BiomeGenBase biome = w.getBiomeGenForCoords(pos.getX(), pos.getZ());
            return biome.getEnableSnow() ? false : (w.canSnowAtBody(pos.getX(), pos.getY(), pos.getZ(), false) ? false : biome.rainfall > 0);
        }
    }
}
