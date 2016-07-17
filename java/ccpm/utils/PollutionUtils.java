package ccpm.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import ccpm.api.ITilePollutionProducer;
import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.handlers.PlayerHandler;
import ccpm.handlers.WorldHandler;
import ccpm.utils.config.CCPMConfig;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PollutionUtils {
	
	static Field temp = Biome.class.getDeclaredFields()[21];

	public PollutionUtils() 
	{
		
	}

	static
	{
		temp.setAccessible(true);
	}
	
	public static void increasePollution(float amount, Chunk chunk)
	{
		//<Checks>
		if(!WorldHandler.isLoaded)
		{
			return;
		}
		
		if(chunk == null)
			return;
		
		if(chunk.getWorld() == null || chunk.getWorld().isRemote || chunk.getWorld().provider.getDimension() !=0)
			return;
		
		if(WorldHandler.instance == null)
		{
			CCPM.log.warn("World handler isn't initialized!!!");
			CCPM.addToEx();
			return;
		}
		if(WorldHandler.instance.pm == null)
		{
			CCPM.log.warn("[CCPM]Pollution Manager isn't initialized!!!");
			CCPM.addToEx();
			return;
		}
		
		if(WorldHandler.instance.pm.chunksPollution ==null)
		{
			WorldHandler.instance.pm.load();
		}
		
		
		//</Checks>
		ChunkPollution[] cp = WorldHandler.instance.pm.chunksPollution.getCP();
		
		
	     boolean hasAlready = false;
		 
		
		 if(cp == null || cp.length == 0)
		 {
			 cp = new ChunkPollution[1];
			 ChunkPollution cpoll = new ChunkPollution();
			 cpoll.setX(chunk.xPosition);
			 cpoll.setZ(chunk.zPosition);
			 cpoll.setPollution(amount >=0 ? amount : 0);
			 cp[0] = cpoll;
		 }
		 
		 
		for(ChunkPollution c : cp)
		{
			if(c!=null)
			if(chunk.xPosition == c.getX() && chunk.zPosition == c.getZ())
			{
				hasAlready = true;
				c.setPollution((c.getPollution() + amount) >= 0 ? c.getPollution()+amount : 0);
				break;
			}
		}
		
		if(!hasAlready)
		{
			ChunkPollution[] newCP = new ChunkPollution[cp.length + 1];
			ChunkPollution ccp = new ChunkPollution();
			 ccp.setX(chunk.xPosition);
			 ccp.setZ(chunk.zPosition);
			 ccp.setPollution(amount >= 0 ? amount : 0);
			 for(int i = 0; i <= cp.length-1; i++)
			 {
				 newCP[i] = cp[i];
			 }
			 newCP[newCP.length - 1] = ccp;
			 WorldHandler.instance.pm.chunksPollution.setCP(newCP);
		}
	}
	
	
	public static float processChunk(Chunk c)
	{
		if(c==null)
			return 0;
		
		Map<BlockPos, TileEntity> tileMapClone = new LinkedHashMap<BlockPos, TileEntity>(c.getTileEntityMap());
		
		Iterator iter = tileMapClone.values().iterator();
		
		float ret = 0;
		
		//if(iter != null)
		//{
			while(iter.hasNext())
			{
				//FMLLog.info("CKP CKP CKP "+c.xPosition*16+","+c.zPosition*16);
				TileEntity tile = (TileEntity) iter.next();
				
				//if(o instanceof TileEntity)
				//{
					//TileEntity tile = (TileEntity)o;
					
					if(tile == null || tile.isInvalid() || !tile.hasWorldObj())
						continue;
					//FMLLog.info("***CKP CKP CKP***");
					if(tile instanceof ITilePollutionProducer)
					{
						ret = ret + (((ITilePollutionProducer)tile).getPollutionProdution() * 60);
						continue;
					}
					
					NBTTagCompound nbt = new NBTTagCompound();
					
					tile.writeToNBT(nbt);
					//FMLLog.info(nbt.toString());
					
					String id = nbt.getString("id");
					
					if(id.length() > 0)
					{
						//FMLLog.info("CKP CKP CKP");
						Tilez[] tiles= PollutionConfig.cfg.getTiles();
						if(tiles == null||tiles.length == 0)
						{
							CCPM.log.warn("No tiles in cfg!!!");
						}
						
						Hashtable<String, Float> th = PollutionConfig.toHashNoModid();
						
						if(th.containsKey(id))
							/*if(Loader.isModLoaded("BuildCraft|Core") || Loader.isModLoaded("BuildCraft"))
							{
								if(BCIntegration.IsHasWork(tile))
								{
									if(BCIntegration.isWorking(tile))
										ret = ret + th.get(id) * 60;
								}
								else
									ret = ret + th.get(id) * 60;
							}
							else*/
							{
								//FMLLog.info("Tile "+ id +" at "+tile.xCoord+","+tile.yCoord+","+tile.zCoord+" produces "+th.get(id)+" pollution");
								ret = ret + th.get(id) * CCPMConfig.pollutionMultiplier;
							}
							
					}
				}
				
				//iter.remove();
		//if(ret>0)
		//FMLLog.info("Chunk at"+c.xPosition+","+c.zPosition+" produces "+ret);
		return ret;
	}
	
	
	
	public static void doPollutionEffects(Chunk chunk, float pollution)
	{
		if(pollution>=100)
		if(chunk.getWorld().rand.nextInt(50) == 5)
		if(pollution % 5 == 0)
		{
			if(!chunk.getWorld().isRemote)
	    	 {
				
	    		 if(chunk.isLoaded())
	    		 {
	    			 int p = (int) (pollution/5);
	    			 int ch = 0;
	    			 if(chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition-1, chunk.zPosition).isLoaded())
	    			 {
	    				 PollutionUtils.increasePollution(p, chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition-1, chunk.zPosition));
	    				 ch++;
	    			 }
	    			 if(chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition+1, chunk.zPosition).isLoaded())
	    			 {
	    				 PollutionUtils.increasePollution(p, chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition+1, chunk.zPosition));
	    				 ch++;
	    			 }
	    			 if(chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition-1).isLoaded())
	    			 {
	    				 PollutionUtils.increasePollution(p, chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition-1));
	    				 ch++;
	    			 }
	    			 if(chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition+1).isLoaded())
	    			 {
	    				 PollutionUtils.increasePollution(p, chunk.getWorld().getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition+1));
	    				 ch++;
	    			 }
	    			 PollutionUtils.increasePollution(-(p*ch), chunk);
	    		 }
	    	 }
		}
		
		
		
	     if(pollution >= 100000)
	     {
	    	 if(!chunk.getWorld().isRemote)
	    	 {
	    		 if(chunk.isLoaded())
	    		 {
	    			 int x = chunk.xPosition * 16 + chunk.getWorld().rand.nextInt(16);
	    			 int z = chunk.zPosition * 16 + chunk.getWorld().rand.nextInt(16);
	    			 changeBiome(chunk.getWorld(), CCPM.wasteland, x, z);
	    		 }
	    	 }
	     }
	     
	     //Global warming! Aaaaaahhh!
	     if(pollution >= 10000)
	     {
	    	 if(!chunk.getWorld().isRemote)
	    	 if(chunk.isLoaded())
	    	 for(int i = chunk.xPosition*16; i<=chunk.xPosition*16+16; i++)
	    		 for(int j = chunk.zPosition*16; j<=chunk.zPosition*16+16; j++)
					try 
	    	 		{
						temp.setFloat(chunk.getWorld().getBiomeGenForCoords(new BlockPos(i, 16, j)), temp.getFloat(chunk.getWorld().getBiomeGenForCoords(new BlockPos(i, 16, j)))+0.01F);
					} 
	    	 		catch (IllegalArgumentException e)
	    	 		{
						e.printStackTrace();
					} 
	    	 		catch (IllegalAccessException e)
	    	 		{
						e.printStackTrace();
					}
	     }
	    	 
	     //TODO Add more effects
	}
	
	
	public static float getChunkPollution(Chunk chunk)
	{
		PollutionManager pm = WorldHandler.instance.pm;
	   // if(pm == null || chunk.worldObj.isRemote || chunk.worldObj.provider.dimensionId != 0 || pm.chunksPollution == null || pm.chunksPollution.getCP() == null || pm.chunksPollution.getCP().length <=0)
	    //	return Float.MIN_VALUE;
		
		//<Checks>
		
		if(!WorldHandler.isLoaded)
		{
			CCPM.log.warn("World handler's stuff isn't loaded!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(chunk.getWorld().isRemote)
		{
			CCPM.log.warn("Function getChunkPollution called on Client side!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(chunk.getWorld().provider.getDimension() != 0)
		{
			CCPM.log.warn("This chunk isn't in overworld dimention(0)!");
			//CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm == null)
		{
			CCPM.log.warn("Pollution Manager isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution == null)
		{
			CCPM.log.warn("Chunks pollution isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution.getCP() == null)
		{
			CCPM.log.warn("CP isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution.getCP().length == 0)
		{
			if(PlayerHandler.firstPlayerJoinedWorld)
			{
			CCPM.log.warn("There are no chunks in Pollution Manager!!");
			CCPM.addToEx();
			}
			return Float.MIN_VALUE;
		}
	    
		//</Checks>
		
	    for(ChunkPollution c : pm.chunksPollution.getCP())
		{
			if(chunk.xPosition == c.getX() && chunk.zPosition == c.getZ())
			{
				return c.getPollution();
			}
		}
	    
	    return Float.MIN_VALUE;
	}
	
	public static float getChunkPollution(World w, double posX, double posZ)
	{
		return getChunkPollution(w.getChunkFromChunkCoords((int)posX, (int)posZ));
	}
	
	public static float getChunkPollution(World w, int posX, int posZ)
	{
		return getChunkPollution(w.getChunkFromChunkCoords(posX, posZ));
	}
	
	public static float getChunkPollution(Entity en)
	{
		return getChunkPollution(en.getEntityWorld(), en.posX/16, en.posZ/16);
	}
	
	public static float getChunkPollution(World w, BlockPos pos)
	{
		return getChunkPollution(w.getChunkFromBlockCoords(pos));
	}
	
	public static NBTTagCompound getNbt(ItemStack stack)
	{
		if(!stack.hasTagCompound())
			return null;
		
		return stack.getTagCompound();
	}

	public static void changeBiome(World w, Biome biome, int x, int z)
	{
		Chunk chunk = w.getChunkFromBlockCoords(new BlockPos(x,w.getActualHeight(),z));
		byte[] b = chunk.getBiomeArray();
		byte cbiome = b[(z & 0xf) << 4 | x & 0xf]; //What is even going on here? Can this code be a little bit more readable?
		cbiome = (byte)(Biome.getIdForBiome(biome) & 0xff);
		b[(z & 0xf) << 4 | x & 0xf] = cbiome; //Looks like not.
		chunk.setBiomeArray(b);
		//notifyBiomeChange(x,z,Biome.getIdForBiome(biome));
	}


	

}
