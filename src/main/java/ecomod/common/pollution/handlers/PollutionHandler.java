package ecomod.common.pollution.handlers;

import ecomod.core.*;
import ecomod.core.stuff.EMConfig;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionAbsorption;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ecomod.api.EcomodAPI;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.IGarbage;
import ecomod.api.pollution.IPollutionGetter;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionEmissionEvent;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.*;
import ecomod.common.pollution.PollutionManager.WorldPollution;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.utils.EMUtils;



public class PollutionHandler implements IPollutionGetter
{
	public Map<String, WorldProcessingThread> threads = new HashMap<String, WorldProcessingThread>();
	
	private Gson gson = new GsonBuilder().serializeNulls().create();
	
	public EMPacketString formCachedPollutionToSend(int dim, UUID id, int radius)
	{
		WorldProcessingThread wpt = getWPT(DimensionManager.getWorld(dim));
		
		if(wpt != null)
		{
			EntityPlayer p = wpt.getPM().getWorld().getPlayerEntityByUUID(id);
			
			if(p == null)
			{
				EcologyMod.log.error("Player not found when forming pollution data to be cached!");
				
				return null;
			}
			
			return formCachedPollutionToSend(p, radius);
		}
		else
			return null;
	}
	
	public EMPacketString formCachedPollutionToSend(EntityPlayer player, int radius)
	{
		if(player == null)
		{
			EcologyMod.log.error("Player not found when forming pollution data to be cached!");
			
			return null;
		}
		
		WorldProcessingThread wpt = getWPT(player.world);
		
		ChunkPos chpos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);
		
		List<ChunkPollution> list = new ArrayList<ChunkPollution>();
		
		for (int i = chpos.chunkXPos - radius; i <= chpos.chunkXPos + radius; i++) 
			for (int j = chpos.chunkZPos - radius; j <= chpos.chunkZPos + radius; j++)
			{
				list.add(wpt.getPM().getChunkPollution(Pair.of(i, j)));
			}
		
		if(EMUtils.isSquareChunkPollution(list))
		{
			WorldPollution wp = new WorldPollution();
			
			wp.setData(list.toArray(new ChunkPollution[list.size()]));
			
			String json = gson.toJson(wp, WorldPollution.class);
			
			json = json.replace('\"', 'Q');
			
			json = json.replace(':', 'k');
			
			json = json.replace('{', 'O');
			
			json = json.replace('}', 'C');
			
			json = json.substring(9, json.length() - 2);
			
			json = json.replaceAll("QpollutionQkOQairQk0.0,QwaterQk0.0,QsoilQk0.0C", "pn");
			
			json = json.replaceAll("chunkX", "N");
			
			json = json.replaceAll("chunkZ", "M");
			
			json = "P"+json;
			
			EcologyMod.log.info(json);
			
			if(json != "P")
				return new EMPacketString(json);
		}
		else
		{
			EcologyMod.log.error("Unable to form pollution data to be send to a client!");
		}
		
		return null;
	}
	
	public WorldProcessingThread getWPT(World key)
	{	
		return getWPT(PollutionUtils.genPMid(key));
	}
	
	public WorldProcessingThread getWPT(String key)
	{
		if(threads.containsKey(key))
		{
			return threads.get(key);
		}
		
		return null;
	}
	
	//World handlers
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldLoad(WorldEvent.Load event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)
		{
			return;
		}
		
		
		boolean b1 = false;
		
		for(int i : EMConfig.allowedDims)
			if(i == w.provider.getDimension())
				b1 = true;
		
		if(!b1)
			return;
		
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key) && PollutionUtils.genPMid(threads.get(key).getPM()) == key)
		{
			return;
		}
		
		PollutionManager pm = new PollutionManager(w);
		
		EcologyMod.log.debug("Creating PollutionManager for "+key);
		
		if(pm.load())
		{
			WorldProcessingThread thr = new WorldProcessingThread(pm);
			threads.put(PollutionUtils.genPMid(pm), thr);
			thr.start();
		}
		else
		{
			//EcologyMod.log.error("Unable to load the pollution manager and start the thread for dim "+w.provider.getDimension());
			pm = new PollutionManager(w);
			WorldProcessingThread thr = new WorldProcessingThread(pm);
			threads.put(PollutionUtils.genPMid(pm), thr);
			thr.start();
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldSave(WorldEvent.Save event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key) && threads.get(key) != null && !threads.get(key).isWorking() && PollutionUtils.genPMid(threads.get(key).getPM()) == key)
		{
			WorldProcessingThread t = threads.get(key);
			
			try
			{
				t.wait();
			
				t.getPM().save();
			
				t.notify();
			}
			catch(InterruptedException e)
			{
				EcologyMod.log.error(e.toString());
				e.printStackTrace();
			}
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key) && PollutionUtils.genPMid(threads.get(key).getPM()) == key)
		{
			threads.get(key).forceSE();
			
			threads.get(key).interrupt();
			
			threads.get(key).getPM().save();
			
			threads.remove(key);
		}
	}
	
	//Chunk handlers
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
		
			Pair<Integer, Integer> coord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);
		
			synchronized(wpt.getLoadedChunks())
			{
				if(!wpt.getLoadedChunks().contains(coord))
					wpt.getLoadedChunks().add(coord);
			}
		}
	}
	
	
	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
		
			Pair<Integer, Integer> coord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);
		
			synchronized(wpt.getLoadedChunks())
			{
				if(wpt.getLoadedChunks().contains(coord))
					wpt.getLoadedChunks().remove(coord);
			}
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEmission(PollutionEmissionEvent event)
	{		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onEmission");
		
		String key = PollutionUtils.genPMid(w);
		
		//EcologyMod.log.info(event.getEmission().toString());
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
			
			if(event.isScheduled())
			{
				synchronized(wpt.getScheduledEmissions())
				{
					wpt.getScheduledEmissions().add(new ChunkPollution(event.getChunkX(), event.getChunkZ(), event.getEmission()));
				}
			}
			else
			{
				wpt.getPM().setChunkPollution(new ChunkPollution(event.getChunkX(), event.getChunkZ(), event.getEmission().clone().add(wpt.getPM().getChunkPollution(Pair.of(event.getChunkX(), event.getChunkZ())).getPollution())));
			}
		}
	}
	
	
	//Pollution sources

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemExpire(ItemExpireEvent event)
	{
		if(event.isCanceled())return;
		
		EntityItem ei = event.getEntityItem();
		
		World w = ei.getEntityWorld();
		
		if(w.isRemote)return;
		
		EcologyMod.log.debug("PollutionHandler#onItemExpire");
		
		ItemStack is = ei.getEntityItem();
		
		if(EMConfig.item_blacklist.contains(is.getItem().getRegistryName().toString()))
			return;
		
		boolean isInWater = EMUtils.countWaterInRadius(w, ei.getPosition(), 1) >= 1;
		
		if(!is.hasTagCompound() || !is.getTagCompound().hasKey("ECO_PH_ONITEMEXPIRE"))
		{
			//EcologyMod.log.info(is.toString());
			
			if(is.getItem() instanceof IGarbage)
			{
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(ei.getPosition()), ((IGarbage)is.getItem()).getPollutionOnDecay().clone().multiplyAll(is.getCount()).multiply(PollutionType.WATER, isInWater ? 2 : 1), true);
			}
			else
			{
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(ei.getPosition()), EMConfig.pp2di.clone().multiply(PollutionType.WATER, isInWater ? 2 : 1).multiplyAll(is.getCount()), true);
			}
			
			if(is.hasTagCompound())
			{
				is.getTagCompound().setBoolean("ECO_PH_ONITEMEXPIRE", true);
			}
			else
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean("ECO_PH_ONITEMEXPIRE", true);
				is.setTagCompound(tag);
			}
		}
	}
	
	@SubscribeEvent
	public void onExplosion(ExplosionEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		Explosion expl = event.getExplosion();
		
		int water_affected = EMUtils.countWaterInRadius(w, new BlockPos(expl.getPosition()), (int)expl.explosionSize);
		
		PollutionData emission = EMConfig.explosion_pollution.clone();
		
		emission.multiply(PollutionType.AIR, expl.isFlaming ? 1.5F : 1);	
		emission.multiply(PollutionType.SOIL, expl.isFlaming ? 1.5F : 1);
		
		emission.multiply(PollutionType.WATER, water_affected > 0 ? 2 : 1);
		
		emission.multiplyAll(expl.explosionSize);
		
		EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new BlockPos(expl.getPosition())), emission, true);
	}
	
	@SubscribeEvent
	public void onBonemeal(BonemealEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
			
			PollutionData data = wpt.getPM().getChunkPollution(EMUtils.blockPosToPair(event.getPos())).getPollution();
			
			if((data.getWaterPollution() + data.getSoilPollution()) >= EMConfig.bonemeal_limiting_soil_pollution)
			{
				if(w.getBlockState(event.getPos()).getBlock() == Blocks.SAPLING)
				{
					if(data.compareOR(EMConfig.no_trees_pollution) >= 0)
					{
						event.setResult(Result.DENY);
						event.setCanceled(true);
					}
					else
					{
						
					}
				}
				else
				{
					event.setResult(Result.DENY);
					event.setCanceled(true);
				}
			}
			else
			{
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getPos()), EMConfig.bonemeal_pollution, true);
			}
		}
	}
	
	@SubscribeEvent
	public void onHoe(UseHoeEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		WorldProcessingThread wpt = getWPT(key);
		
		if(wpt == null)return;
		
		PollutionData data = wpt.getPM().getChunkPollution(EMUtils.blockPosToPair(event.getPos())).getPollution();
		
		if((data.getWaterPollution() + data.getSoilPollution()) >= EMConfig.useless_hoe_pollution)
		{
			event.setResult(Result.DENY);
			event.setCanceled(true);
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getPos()), new PollutionData(0,0,-0.5), true);
		}
		
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPlayerSleep(PlayerWakeUpEvent event)
	{
		if(event.isCanceled())return;
		
		EntityPlayer player = event.getEntityPlayer();
		
		World w = player.getEntityWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		WorldProcessingThread wpt = getWPT(key);
		
		if(wpt == null)return;
		
		PollutionData data = wpt.getPM().getChunkPollution(EMUtils.blockPosToPair(player.getPosition())).getPollution();
		
		if(data.getAirPollution() >= EMConfig.bad_sleep_pollution)
		{
			int f = (int) (data.getAirPollution()/EMConfig.bad_sleep_pollution + 1);
			
			player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("nausea").toString()), f<10 ? 250*f : 2500, 1));
			player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("weakness").toString()), 2000, f));
			if(f >= 2)
				player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("hunger").toString()), 2000, 2));
			
			player.sendMessage(new TextComponentString("You feels ill. It seems the air is not clean enough."));
			
			if(data.getAirPollution() >= EMConfig.poisonous_sleep_pollution)
			{
				player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("poison").toString()), 1000, f));
			}
		}
	}
	
	@SubscribeEvent
	public void onPotionBrewed(PlayerBrewedPotionEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.getEntityPlayer().getEntityWorld();
		
		if(w.isRemote)return;
		
		EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getEntityPlayer().getPosition()), EMConfig.pollution_per_potion_brewed, true);
	}
	
	@SubscribeEvent
	public void onTreeGrow(SaplingGrowTreeEvent event)
	{
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		WorldProcessingThread wpt = getWPT(key);
		
		if(wpt == null)return;
		
		PollutionData data = wpt.getPM().getChunkPollution(EMUtils.blockPosToPair(event.getPos())).getPollution();
		
		EcologyMod.log.info(data.toString());
		
		if(data.compareOR(EMConfig.no_trees_pollution) >= 0)
		{
			EcologyMod.log.info(1);
			event.setResult(Result.DENY);
		}
		else
		{/*
			FIXME!!!
			if(data.compareOR(EMConfig.dead_trees_pollution) >= 0)
			{
				EcologyMod.log.info(2);
				event.setResult(Result.DENY);
				
				//WorldGenerator worldgenerator = new WorldGenHugeTrees();
				
				//worldgenerator.generate(w, w.rand, event.getPos());
			}
			else
			{*/
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getPos()), EMConfig.pollution_reduced_by_tree, true);
			//}
		}
	}
	
	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		WorldProcessingThread wpt = getWPT(key);
		
		if(wpt == null)return;
		
		PollutionData data = wpt.getPM().getChunkPollution(EMUtils.blockPosToPair(pos)).getPollution();
		
		if(event.getEntityLiving() instanceof IAnimals)
		{
			if(data.compareOR(data) >= 0)
			{
				event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote || event.getEntity() == null)
			return;
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			try
			{
				EMPacketString to_send = formCachedPollutionToSend((EntityPlayer)event.getEntity(), EMConfig.cached_pollution_radius);
				
				if(to_send == null)
				{
					EcologyMod.log.error("Unable to make EMPacketString with mark 'P'!!! Unable to form cached pollution for player "+((EntityPlayer)event.getEntity()).getName()+"("+((EntityPlayer)event.getEntity()).getUniqueID().toString()+")");
				}
				else
				{
					EMPacketHandler.WRAPPER.sendTo(to_send, (EntityPlayerMP)event.getEntity());
				}
			}
			catch (Exception e)
			{
				EcologyMod.log.error("Error while sending EMPacketString with mark 'P' to the client!");
				EcologyMod.log.error(e.toString());
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		
		if(entity == null)
			return;
		
		World world = entity.getEntityWorld();
		
		if(world.isRemote)//client side actions are handled in ClientHandler
			return;
		
		if(entity instanceof EntityPlayer)
		{
			//1.5 min
			if((entity.ticksExisted + 1) % 1800 == 0)
			{
				EMPacketString to_send = EcologyMod.ph.formCachedPollutionToSend((EntityPlayer)event.getEntity(), EMConfig.cached_pollution_radius);
				
				if(to_send == null)
				{
					EcologyMod.log.error("Unable to make EMPacketString with mark 'P'!!! Unable to form cached pollution for player "+((EntityPlayer)event.getEntity()).getName()+"("+((EntityPlayer)event.getEntity()).getUniqueID().toString()+")");
				}
				else
				{
					EMPacketHandler.WRAPPER.sendTo(to_send, (EntityPlayerMP)event.getEntity());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onStrEventReceived(EMPacketString.EventReceived event)
	{
		String str = event.getContent();
		EcologyMod.log.info(str);
	}

	@Nullable
	@Override
	public PollutionData getPollution(World w, int chunkx, int chunkz)
	{
		WorldProcessingThread wpt = getWPT(w);
		
		if(wpt == null)
			return null;
		
		return wpt.getPM().getPollution(chunkx, chunkz).clone();
	}
}