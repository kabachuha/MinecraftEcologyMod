package ccpm.utils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import DummyCore.Utils.MiscUtils;
import ccpm.api.ITilePollutionProducer;
import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager;
import ccpm.ecosystem.PollutionManager.ChunksPollution.ChunkPollution;
import ccpm.handlers.WorldHandler;
import ccpm.integration.buildcraft.BCIntegration;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class PollutionUtils {

	public PollutionUtils() {
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
		
		if(chunk.getWorld() == null || chunk.getWorld().isRemote || chunk.getWorld().provider.getDimensionId() !=0)
			return;
		
		if(WorldHandler.instance == null)
		{
			CCPM.log.warning("World handler isn't initialized!!!");
			CCPM.addToEx();
			return;
		}
		if(WorldHandler.instance.pm == null)
		{
			CCPM.log.warning("[CCPM]Pollution Manager isn't initialized!!!");
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
			 cpoll.setPollution(amount);
			 cp[0] = cpoll;
		 }
		 
		 
		for(ChunkPollution c : cp)
		{
			if(c!=null)
			if(chunk.xPosition == c.getX() && chunk.zPosition == c.getZ())
			{
				hasAlready = true;
				c.setPollution(c.getPollution() + amount);
				break;
			}
		}
		
		if(!hasAlready)
		{
			ChunkPollution[] newCP = new ChunkPollution[cp.length + 1];
			ChunkPollution ccp = new ChunkPollution();
			 ccp.setX(chunk.xPosition);
			 ccp.setZ(chunk.zPosition);
			 ccp.setPollution(amount);
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
							CCPM.log.warning("No tiles in cfg!!!");
						}
						
						Hashtable<String, Float> th = PollutionConfig.toHashNoModid();
						
						if(th.containsKey(id))
							//if(Loader.isModLoaded("BuildCraft|Core") || Loader.isModLoaded("BuildCraft"))
							//{
							//	if(BCIntegration.IsHasWork(tile))
							//	{
							//		if(BCIntegration.isWorking(tile))
							//			ret = ret + th.get(id) * 60;
							//	}
							//	else
							//		ret = ret + th.get(id) * 60;
							//}
							//else
							{
								//FMLLog.info("Tile "+ id +" at "+tile.xCoord+","+tile.yCoord+","+tile.zCoord+" produces "+th.get(id)+" pollution");
								ret = ret + th.get(id) * 60;
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
	    			 MiscUtils.changeBiome(chunk.getWorld(), CCPM.wasteland, x, z);
	    		 }
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
			CCPM.log.warning("World handler's stuff isn't loaded!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(chunk.getWorld().isRemote)
		{
			CCPM.log.warning("Function getChunkPollution called on Client side!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(chunk.getWorld().provider.getDimensionId() != 0)
		{
			CCPM.log.warning("This chunk isn't in overworld dimention(0)!");
			//CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm == null)
		{
			CCPM.log.warning("Pollution Manager isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution == null)
		{
			CCPM.log.warning("Chunks pollution isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution.getCP() == null)
		{
			CCPM.log.warning("CP isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution.getCP().length == 0)
		{
			CCPM.log.warning("There are no chunks in Pollution Manager!!");
			CCPM.addToEx();
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
	
	public static float getChunkPollution(World w, int chunkPosX, int chunkPosZ)
	{
		return getChunkPollution(w.getChunkFromChunkCoords(chunkPosX, chunkPosZ));
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




	

}
