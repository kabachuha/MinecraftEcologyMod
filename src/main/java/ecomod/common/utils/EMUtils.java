package ecomod.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.PollutionData;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

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
		StringBuffer buffer;
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
			String mcpath;
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
				mcpath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
			else
				mcpath = FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory().getAbsolutePath();
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
	
	public static Pair<Integer, Integer> chunkPosToPair(ChunkPos pos)
	{
		return Pair.of(pos.x, pos.z);
	}
	
	public static Pair<Integer, Integer> blockPosToPair(BlockPos pos)
	{
		return chunkPosToPair(new ChunkPos(pos));
	}
	
	public static Pair<Integer, Integer> blockPosToPair(Entity entity)
	{
		return blockPosToPair(entity.getPosition());
	}
	
	public static ChunkPos pairToChunkPos(Pair<Integer, Integer> pair)
	{
		return new ChunkPos(pair.getLeft(), pair.getRight());
	}
	
	public static boolean isBlockWater(World w, BlockPos pos)
	{	
		return w.getBlockState(pos).getBlock() == Blocks.WATER;
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
	
	public static void setBiome(World w, Biome biome, int x, int z)
	{
		Chunk chunk = w.getChunkFromBlockCoords(new BlockPos(x,w.getActualHeight(),z));
		setBiome(chunk, biome, x, z);
	}
	
	public static void setBiome(Chunk c, Biome biome, int x, int z)
	{
		byte[] b = c.getBiomeArray();
		byte cbiome = (byte)(Biome.getIdForBiome(biome) & 0xff);
		b[(z & 0xf) << 4 | x & 0xf] = cbiome;
		c.setBiomeArray(b);
		
		EMPacketHandler.WRAPPER.sendToDimension(new EMPacketString("*"+x+ ';' +z+ ';' +Biome.getIdForBiome(biome)), c.getWorld().provider.getDimension());
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
		ArrayList<Pair<Integer, Integer>> al = new ArrayList<>();
		
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
	
	public static Vec3d lerp(Vec3d a, Vec3d b, float v)
	{
		if (v == 0)
			return a;
		else if (v == 1)
			return b;

		return new Vec3d(lerp(a.x, b.x, v),
						lerp(a.y, b.y, v),
						lerp(a.z, b.z, v));
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
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double)right, (double)top, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        vertexbuffer.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos((double)left, (double)bottom, (double)zLevel).color(f1, f2, f3, f).endVertex();
        vertexbuffer.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
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
	
	public static TileEntity get1NearbyTileEntity(@Nullable ResourceLocation id, World w, BlockPos pos)
	{
		TileEntity ret = null;

		for(EnumFacing ef : EnumFacing.VALUES)
		{
			TileEntity te = w.getTileEntity(pos.offset(ef));
			if(te != null)
			{
				if(ret == null)
				{
					if(id == null || TileEntity.getKey(te.getClass()).toString().equals(id.toString()))
						ret = te;
				}
				else
				{
					if(id == null || TileEntity.getKey(te.getClass()).toString().equals(id.toString()))
					{
						return null;
					}
				}
			}
		}
			
		return ret;
	}
	
	public static TileEntity get1NearbyTileEntity(World w, BlockPos pos)
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
			sum += (double)n;
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
	
	public static <T, V> boolean arePairsEqual(Pair<T, V> par_1, Pair<T, V> par_2)
	{
		return par_1.getLeft() == par_2.getLeft() && par_1.getRight() == par_2.getRight();
	}
	
	/**
     * Renders an item held in hand as a 2D texture with thickness
     */
    public static void renderItemIn2D(float minU, float minV, float maxU, float maxV, int width, int height, float thickness)
    {
    	Tessellator t = Tessellator.getInstance();
    	BufferBuilder tess = Tessellator.getInstance().getBuffer();
        tess.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tess.pos(0.0D, 0.0D, 0.0D).tex((double)minU, (double)maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
        tess.pos(1.0D, 0.0D, 0.0D).tex((double)maxU, (double)maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
        tess.pos(1.0D, 1.0D, 0.0D).tex((double)maxU, (double)minV).normal(0.0F, 0.0F, 1.0F).endVertex();
        tess.pos(0.0D, 1.0D, 0.0D).tex((double)minU, (double)minV).normal(0.0F, 0.0F, 1.0F).endVertex();
        t.draw();
        tess.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        tess.pos(0.0D, 1.0D, (double)(0.0F - thickness)).tex((double)minU, (double)minV).normal(0.0F, 0.0F, -1.0F).endVertex();
        tess.pos(1.0D, 1.0D, (double)(0.0F - thickness)).tex((double)maxU, (double)minV).normal(0.0F, 0.0F, -1.0F).endVertex();
        tess.pos(1.0D, 0.0D, (double)(0.0F - thickness)).tex((double)maxU, (double)maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
        tess.pos(0.0D, 0.0D, (double)(0.0F - thickness)).tex((double)minU, (double)maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
        t.draw();
        float f5 = 0.5F * (minU - maxU) / (float)width;
        float f6 = 0.5F * (maxV - minV) / (float)height;
        tess.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        int k;
        float f7;
        float f8;

        for (k = 0; k < width; ++k)
        {
            f7 = (float)k / (float)width;
            f8 = minU + (maxU - minU) * f7 - f5;
            tess.pos((double)f7, 0.0D, (double)(0.0F - thickness)).tex((double)f8, (double)maxV).normal(-1.0F, 0.0F, 0.0F).endVertex();
            tess.pos((double)f7, 0.0D, 0.0D).tex((double)f8, (double)maxV).normal(-1.0F, 0.0F, 0.0F).endVertex();
            tess.pos((double)f7, 1.0D, 0.0D).tex((double)f8, (double)minV).normal(-1.0F, 0.0F, 0.0F).endVertex();
            tess.pos((double)f7, 1.0D, (double)(0.0F - thickness)).tex((double)f8, (double)minV).normal(-1.0F, 0.0F, 0.0F).endVertex();
        }

        t.draw();
        tess.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        float f9;

        for (k = 0; k < width; ++k)
        {
            f7 = (float)k / (float)width;
            f8 = minU + (maxU - minU) * f7 - f5;
            f9 = f7 + 1.0F / (float)width;
            tess.pos((double)f9, 1.0D, (double)(0.0F - thickness)).tex((double)f8, (double)minV).normal(1.0F, 0.0F, 0.0F).endVertex();
            tess.pos((double)f9, 1.0D, 0.0D).tex((double)f8, (double)minV).normal(1.0F, 0.0F, 0.0F).endVertex();
            tess.pos((double)f9, 0.0D, 0.0D).tex((double)f8, (double)maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
            tess.pos((double)f9, 0.0D, (double)(0.0F - thickness)).tex((double)f8, (double)maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
        }

        t.draw();
        tess.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (k = 0; k < height; ++k)
        {
            f7 = (float)k / (float)height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            f9 = f7 + 1.0F / (float)height;
            tess.pos(0.0D, (double)f9, 0.0D).tex((double)minU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            tess.pos(1.0D, (double)f9, 0.0D).tex((double)maxU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            tess.pos(1.0D, (double)f9, (double)(0.0F - thickness)).tex((double)maxU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
            tess.pos(0.0D, (double)f9, (double)(0.0F - thickness)).tex((double)minU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
        }

        t.draw();
        tess.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

        for (k = 0; k < height; ++k)
        {
            f7 = (float)k / (float)height;
            f8 = maxV + (minV - maxV) * f7 - f6;
            tess.pos(1.0D, (double)f7, 0.0D).tex((double)maxU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            tess.pos(0.0D, (double)f7, 0.0D).tex((double)minU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            tess.pos(0.0D, (double)f7, (double)(0.0F - thickness)).tex((double)minU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
            tess.pos(1.0D, (double)f7, (double)(0.0F - thickness)).tex((double)maxU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
        }

        t.draw();
    }
    
    @Nullable
    public static TileEntity getLoadedTileEntityAt(World world, BlockPos pos)
    {
        for (int j2 = 0; j2 < world.loadedTileEntityList.size(); ++j2)
        {
            TileEntity tileentity2 = world.loadedTileEntityList.get(j2);

            if (!tileentity2.isInvalid() && tileentity2.getPos().equals(pos))
            {
                return tileentity2;
            }
        }

        return null;
    }
}
