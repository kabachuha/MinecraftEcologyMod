package ccpm.utils;

import java.util.HashMap;
import java.util.Iterator;
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
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
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
		
		if(chunk.worldObj == null || chunk.worldObj.isRemote || chunk.worldObj.provider.dimensionId !=0)
			return;
		
		if(WorldHandler.instance == null)
		{
			FMLLog.bigWarning("[CCPM]World handler isn't initialized!!!");
			CCPM.addToEx();
			return;
		}
		if(WorldHandler.instance.pm == null)
		{
			FMLLog.bigWarning("[CCPM]Pollution Manager isn't initialized!!!");
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
		
		Map<ChunkPosition, TileEntity> tileMapClone = new HashMap<ChunkPosition, TileEntity>();
		tileMapClone.putAll(c.chunkTileEntityMap);
		Iterator iter = tileMapClone.values().iterator();
		
		float ret = 0;
		
		//if(iter != null)
		//{
			while(iter.hasNext())
			{
				
				TileEntity tile = (TileEntity) iter.next();
				
				//if(o instanceof TileEntity)
				//{
					//TileEntity tile = (TileEntity)o;
					
					if(tile instanceof ITilePollutionProducer)
					{
						ret = ret + (((ITilePollutionProducer)tile).getPollutionProdution() * 60);
						continue;
					}
					
					NBTTagCompound nbt = new NBTTagCompound();
					
					tile.writeToNBT(nbt);
					
					String id = nbt.getString("id");
					
					if(id!=null || id !="")
					{
						Tilez[] tiles= PollutionConfig.cfg.getTiles();
						for(int i = 0; i < tiles.length; i++)
						if(id == tiles[i].getName())
							if(Loader.isModLoaded("BuildCraft|Core") || Loader.isModLoaded("BuildCraft"))
							{
								if(BCIntegration.IsHasWork(tile))
								{
									if(BCIntegration.isWorking(tile))
										ret = ret + (tiles[i].getPollution() * 60);
								}
								else
									ret = ret + (tiles[i].getPollution() * 60);
							}
							else
							{
								ret = ret + (tiles[i].getPollution() * 60);
							}
							
					}
				}
				
				//iter.remove();

		
		return ret;
	}
	
	
	
	public static void doPollutionEffects(Chunk chunk, float pollution)
	{
		if(pollution>=100)
		if(pollution % 5 == 0)
		{
			if(!chunk.worldObj.isRemote)
	    	 {
	    		 if(chunk.isChunkLoaded)
	    		 {
	    			 int p = (int) (pollution/5);
	    			 if(chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition-1, chunk.zPosition).isChunkLoaded)
	    			 PollutionUtils.increasePollution(p, chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition-1, chunk.zPosition));
	    			 if(chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition+1, chunk.zPosition).isChunkLoaded)
	    			 PollutionUtils.increasePollution(p, chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition+1, chunk.zPosition));
	    			 if(chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition-1).isChunkLoaded)
	    			 PollutionUtils.increasePollution(p, chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition-1));
	    			 if(chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition+1).isChunkLoaded)
	    			 PollutionUtils.increasePollution(p, chunk.worldObj.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition+1));
	    			 PollutionUtils.increasePollution(-(p*4), chunk);
	    		 }
	    	 }
		}
		
		
		
	     if(pollution >= 100000)
	     {
	    	 if(!chunk.worldObj.isRemote)
	    	 {
	    		 if(chunk.isChunkLoaded)
	    		 {
	    			 int x = chunk.xPosition * 16 + chunk.worldObj.rand.nextInt(16);
	    			 int z = chunk.zPosition * 16 + chunk.worldObj.rand.nextInt(16);
	    			 MiscUtils.changeBiome(chunk.worldObj, CCPM.wasteland, x, z);
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
			FMLLog.bigWarning("World handler's stuff isn't loaded!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(chunk.worldObj.isRemote)
		{
			FMLLog.bigWarning("Function getChunkPollution called on Client side!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(chunk.worldObj.provider.dimensionId != 0)
		{
			FMLLog.bigWarning("This chunk isn't in overworld dimention(0)!");
			//CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm == null)
		{
			FMLLog.bigWarning("Pollution Manager isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution == null)
		{
			FMLLog.bigWarning("Chunks pollution isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution.getCP() == null)
		{
			FMLLog.bigWarning("CP isn't initialized!!");
			CCPM.addToEx();
			return Float.MIN_VALUE;
		}
		
		if(pm.chunksPollution.getCP().length == 0)
		{
			FMLLog.bigWarning("There are no chunks in Pollution Manager!!");
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
	
	public static NBTTagCompound getNbt(ItemStack stack)
	{
		if(!stack.hasTagCompound())
			return null;
		
		return stack.getTagCompound();
	}
	
	
}
