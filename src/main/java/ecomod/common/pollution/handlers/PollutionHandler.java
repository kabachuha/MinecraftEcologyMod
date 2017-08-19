package ecomod.common.pollution.handlers;

import ecomod.core.*;
import ecomod.core.stuff.EMConfig;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionAbsorption;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenHugeTrees;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
import ecomod.api.EcomodStuff;
import ecomod.api.capabilities.PollutionProvider;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.IGarbage;
import ecomod.api.pollution.IPollutionGetter;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionEmissionEvent;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.items.ItemRespirator;
import ecomod.common.pollution.*;
import ecomod.common.pollution.PollutionManager.WorldPollution;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.tiles.TileAnalyzer;
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
			
			//EcologyMod.log.info(json);
			
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
		
		EcologyMod.log.info("Creating PollutionManager for "+key);
		
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
		
///		EcologyMod.log.debug("PollutionHandler#onEmission");
		
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
				wpt.getPM().addPollution(event.getChunkX(), event.getChunkZ(), event.getEmission());
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
		
//		EcologyMod.log.debug("PollutionHandler#onItemExpire");
		
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
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(ei.getPosition()), PollutionSourcesConfig.getItemStackPollution(is).multiply(PollutionType.WATER, isInWater ? 2 : 1), true);
			}
			
			if(is.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
			{
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(ei.getPosition()), is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution(), true);
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
		
		PollutionData emission = PollutionSourcesConfig.getSource("explosion_pollution_per_power");
		
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
		
		PollutionData data = getPollution(w, EMUtils.blockPosToPair(event.getPos()).getLeft(), EMUtils.blockPosToPair(event.getPos()).getRight());
		
		if(PollutionEffectsConfig.isEffectActive("no_bonemeal", data))
		{
			if(w.getBlockState(event.getPos()).getBlock() == Blocks.SAPLING)
			{
				if(PollutionEffectsConfig.isEffectActive("no_trees", data))
				{
					event.setResult(Result.DENY);
					event.setCanceled(true);
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
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getPos()), PollutionSourcesConfig.getSource("bonemeal_pollution"), true);
		}
	}
	
	@SubscribeEvent
	public void onHoe(UseHoeEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		PollutionData data = getPollution(w, EMUtils.blockPosToPair(event.getPos()).getLeft(), EMUtils.blockPosToPair(event.getPos()).getRight());
		
		if(PollutionEffectsConfig.isEffectActive("no_plowing", data))
		{
			event.setResult(Result.DENY);
			event.setCanceled(true);
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getPos()), PollutionSourcesConfig.getSource("hoe_plowing_reducion"), true);
		}
		
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPlayerSleep(PlayerWakeUpEvent event)
	{
		if(event.isCanceled())return;
		
		EntityPlayer player = event.getEntityPlayer();
		
		World w = player.getEntityWorld();
		
		if(w.isRemote)return;

		
		PollutionData data = getPollution(w, EMUtils.blockPosToPair(player.getPosition()).getLeft(), EMUtils.blockPosToPair(player.getPosition()).getRight());
		
		if(PollutionEffectsConfig.isEffectActive("bad_sleep", data))
		{
			if(!PollutionUtils.isEntityRespirating(player))
			{
				float f = (float) (data.getAirPollution()/EcomodStuff.pollution_effects.get("bad_sleep").getTriggerringPollution().getAirPollution() + 1);
			
				player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("nausea").toString()), f<10 ? (int)(250*f) : 2500, 1));
				player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("weakness").toString()), 2000, (int)f));
				if(f >= 2)
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("hunger").toString()), 2000, 2));
			
				player.sendMessage(new TextComponentString("You are feeling ill. Perhaps the air is not clean enough."));
			
				if(PollutionEffectsConfig.isEffectActive("poisonous_sleep", data))
				{
					player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(new ResourceLocation("poison").toString()), 1000, (int)f));
				}
			}
			else
			{
				ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				
				NBTTagCompound nbt = stack.getTagCompound();
				
				if(nbt != null)
				if(nbt.hasKey("filter"))
				{
					nbt.setInteger("filter", Math.max(0, nbt.getInteger("filter") - EMConfig.filter_durability/2));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPotionBrewed(PlayerBrewedPotionEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.getEntityPlayer().getEntityWorld();
		
		if(w.isRemote)return;
		
		EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getEntityPlayer().getPosition()), PollutionSourcesConfig.getSource("brewing_potion_pollution"), true);
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
		
		if(PollutionEffectsConfig.isEffectActive("no_trees", data))
		{
			event.setResult(Result.DENY);
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(event.getPos()), PollutionSourcesConfig.getSource("tree_growing_pollution_redution"), true);
		}
	}
	
	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		Pair<Integer, Integer> chunkCoords = EMUtils.blockPosToPair(pos);
		
		PollutionData data = getPollution(w, chunkCoords.getLeft(), chunkCoords.getRight());
		
		if(event.getEntityLiving() instanceof IAnimals)
		{
			if(PollutionEffectsConfig.isEffectActive("no_animals", data))
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
				
				boolean inSmog = isEntityInSmog((EntityPlayerMP)event.getEntity());
				
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString(">"+(inSmog ? 1 : 0)), (EntityPlayerMP)event.getEntity());
				
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString("R"+(isPlayerInAcidRainZone((EntityPlayer)event.getEntity()) ? 1 : 0)), (EntityPlayerMP)event.getEntity());
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
		
		
			//1.5 min
			if((entity.ticksExisted + 1) % 1800 == 0)
			{
				if(entity instanceof EntityPlayer)
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
			
			if(((entity.ticksExisted) % 300 == 0))
			{
				if(entity instanceof EntityLivingBase)
				{
					if(isPlayerInAcidRainZone(entity))
					{
						if(entity.world.isRainingAt(entity.getPosition()))
						{
							ItemStack is = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
						
							if(is != null && !is.isEmpty())
							{
								if(is.isItemStackDamageable())
									is.damageItem((int) (EMConfig.acid_rain_item_deterioriation_factor * is.getMaxDamage()), entity);
							}
							else
							{
								entity.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), 300, 1));
							}
						}
					}
					
					if(entity instanceof EntityPlayer)
					{
						EMPacketHandler.WRAPPER.sendTo(new EMPacketString("R"+(isPlayerInAcidRainZone(entity) ? 1 : 0)), (EntityPlayerMP)entity);
						
						BlockPos bp = new BlockPos(entity.posX, entity.posY, entity.posZ);
						
						boolean inSmog = isEntityInSmog((EntityPlayerMP)event.getEntity());
				
						EMPacketHandler.WRAPPER.sendTo(new EMPacketString(">"+(inSmog ? 1 : 0)), (EntityPlayerMP)event.getEntity());
						
						if(inSmog && PollutionUtils.hasSurfaceAccess(entity.getEntityWorld(), bp))
						{
							if(!PollutionUtils.isEntityRespirating(entity))
							{
								((EntityPlayerMP)event.getEntity()).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), 200, 0));
								
								if(getPollution(world, EMUtils.blockPosToPair(bp).getLeft(), EMUtils.blockPosToPair(bp).getRight()).clone().getAirPollution() / EcomodStuff.pollution_effects.get("smog").getTriggerringPollution().getAirPollution()  >= 2)
									((EntityPlayerMP)event.getEntity()).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("wither"), 140, 1));
							}
						}
					}
					else
					{
						if(entity.getEntityWorld().rand.nextInt(10) == 0)
						{
							if(isEntityInSmog(entity))
							{
								if(!PollutionUtils.isEntityRespirating(entity))
								{
									((EntityLivingBase)event.getEntity()).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), 200, 1));
								}
							}
						}
					}
				}
			}
	}
	
	@SubscribeEvent
	public void onFished(ItemFishedEvent event)
	{
		BlockPos pos = new BlockPos(event.getHookEntity().posX, event.getHookEntity().posY, event.getHookEntity().posZ);
		
		World w = event.getHookEntity().world;
		
		if(w.isRemote)return;
		
		Pair<Integer, Integer> chunkCoords = EMUtils.blockPosToPair(pos);
		
		PollutionData data = getPollution(w, chunkCoords.getLeft(), chunkCoords.getRight());
		
		if(PollutionEffectsConfig.isEffectActive("no_fish", data))
		{
			event.damageRodBy(5);
			event.setCanceled(true);
		}
	}
	
	public boolean isEntityInSmog(EntityLivingBase player)
	{
		BlockPos bp = new BlockPos(player.posX, player.posY, player.posZ);
		
		PollutionData pollution = EcomodAPI.getPollution(player.getEntityWorld(), EMUtils.blockPosToPair(bp).getLeft(), EMUtils.blockPosToPair(bp).getRight()).clone();
		
		if(pollution!=null && pollution != PollutionData.getEmpty())
			if(PollutionEffectsConfig.isEffectActive("smog", pollution))
			{
					return true;
			}
		
		return false;
	}

	public boolean isPlayerInAcidRainZone(EntityLivingBase player)
	{
		BlockPos bp = new BlockPos(player.posX, player.posY, player.posZ);
		
		if(player.world.isRaining())
		{
			PollutionData pollution = EcomodAPI.getPollution(player.getEntityWorld(), EMUtils.blockPosToPair(bp).getLeft(), EMUtils.blockPosToPair(bp).getRight()).clone();
		
			if(pollution!=null && pollution != PollutionData.getEmpty())
				if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
				{
						return true;
				}
		}
		
		return false;
	}
	
	
	@SubscribeEvent
	public void onStrEventReceived(EMPacketString.EventReceived event)
	{
		String str = event.getContent();
		//EcologyMod.log.info(str);
		char TYPE = str.charAt(0);
		
		if(str.length() >= 1)
			str = str.substring(1);
		
		switch(TYPE)
		{
			case 'A':
				makeAnalysis(str);
				break;
				
			case '0':
			case '\0'://So if the string is empty
			default:
				return;
		}
	}
	
	public void makeAnalysis(String str)
	{
		String strs[] = str.split(";");
		
		//strs[0] - x
		//strs[1] - y
		//strs[2] - z
		//strs[3] - dim
		
		if(strs.length < 4)
			return;
		
		BlockPos bp;
		int dim;
		
		try{
			 bp = new BlockPos(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
			 dim = Integer.parseInt(strs[3]);
		}
		catch (NumberFormatException nfe)
		{
			EcologyMod.log.info(nfe.toString());
			return;
		}
		
		if(bp != null)
		{
			MinecraftServer mcserver = FMLCommonHandler.instance().getMinecraftServerInstance();
			
			WorldServer ws = mcserver.worldServerForDimension(dim);
			
			TileEntity te = ws.getTileEntity(bp);
			
			if(te != null)
			if(te instanceof TileAnalyzer)
			{
				((TileAnalyzer)te).analyze();
			}
		}
	}
	
	public static final ResourceLocation POLLUTION_CAPABILITY_RESLOC = EMUtils.resloc("pollution");
	
	@SubscribeEvent
	public void onCapabilityAttachment(AttachCapabilitiesEvent<Item> event)
	{
		if(event.getObject() instanceof ItemFood)
		{
			event.addCapability(POLLUTION_CAPABILITY_RESLOC, new PollutionProvider());
		}
	}
	
	@SubscribeEvent
	public void onCropGrow(BlockEvent.CropGrowEvent.Pre event)
	{
		if(!event.getWorld().isRemote)
		{
			PollutionData pollution = getPollution(event.getWorld(), EMUtils.blockPosToPair(event.getPos()));
			
			if(pollution != null && pollution.compareTo(PollutionData.getEmpty()) != 0 && pollution.getSoilPollution() > 1)
			if(PollutionEffectsConfig.isEffectActive("no_plowing", pollution))
			{
				if(PollutionUtils.hasSurfaceAccess(event.getWorld(), event.getPos()))
					event.setResult(Result.DENY);
			}
			else
			{
				if(PollutionUtils.hasSurfaceAccess(event.getWorld(), event.getPos()))
				if(EcomodStuff.pollution_effects.containsKey("no_crops_growing"))
				{
					PollutionData effectPoll = EcomodStuff.pollution_effects.get("no_crops_growing").getTriggerringPollution();
					
					for(PollutionType type : PollutionType.values())
					{
						if(effectPoll.get(type) > 1 && pollution.get(type) > 1)
						{
							double k = effectPoll.get(type) / pollution.get(type);
							
							k = Math.max(k, 1);
					
							if(event.getWorld().rand.nextInt((int)k) == 0)
							{
								event.setResult(Result.DENY);
								return;
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockDrops(BlockEvent.HarvestDropsEvent event)
	{
		if(!event.getWorld().isRemote)
		{
			if(event.getState().getBlock() instanceof IGrowable)
			{
				dropHandler(event.getWorld(), event.getPos(), event.getDrops());
			}
		}
	}
	
	public void dropHandler(World w, BlockPos pos, List<ItemStack> drops)
	{
		PollutionData pd = getPollution(w, EMUtils.blockPosToPair(pos));
		
		if(PollutionEffectsConfig.isEffectActive("food_pollution", pd))
		{
			PollutionData trig = EcomodStuff.pollution_effects.get("food_pollution").getTriggerringPollution();
		
			PollutionData delta = pd.clone().add(trig.clone().multiplyAll(-1));
		
			if(EcomodStuff.pollution_effects.get("food_pollution").getTriggeringType() == TriggeringType.AND ? pd.compareTo(trig) >= 0 : pd.compareOR(trig) >= 0)
			{	
				boolean in = w.getBlockState(pos).getMaterial() == Material.WATER;
			
				if(!in)
				for(EnumFacing dir : EnumFacing.VALUES)
					if(!in)
						in |= w.getBlockState(pos.offset(dir)).getMaterial() == Material.WATER;
			
			
				delta.multiply(PollutionType.WATER, in ? 1F : 0.25F);
			
				in = PollutionUtils.hasSurfaceAccess(w, pos);
			
				delta.multiply(PollutionType.AIR, in ? 1F : 0.4F);
			
				in = w.getBlockState(pos).getMaterial() == Material.GRASS || w.getBlockState(pos).getMaterial() == Material.GROUND;
			
				if(!in)
				for(EnumFacing dir : EnumFacing.VALUES)
					if(!in)
						in |= w.getBlockState(pos.offset(dir)).getMaterial() == Material.GRASS || w.getBlockState(pos.offset(dir)).getMaterial() == Material.GROUND;
			
				delta.multiply(PollutionType.SOIL, in ? 1F : 0.2F);
			}
			
			for(ItemStack is : drops)
			{
				if(is.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
				{
					is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).setPollution(is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution().add(delta.multiplyAll(EMConfig.food_polluting_factor * 2)));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		if(event.getEntityLiving() != null)
		if(!event.getEntityLiving().getEntityWorld().isRemote)
		{
			List<ItemStack> drps = new ArrayList<ItemStack>();
			for(EntityItem ei : event.getDrops())
			{
				if(ei.getEntityItem().getItem() instanceof ItemFood)
					drps.add(ei.getEntityItem());
			}
			
			dropHandler(event.getEntityLiving().getEntityWorld(), event.getEntityLiving().getPosition(), drps);
		}
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
	
	public PollutionData getPollution(World w, Pair<Integer, Integer> pair)
	{
		return getPollution(w, pair.getLeft(), pair.getRight());
	}
}