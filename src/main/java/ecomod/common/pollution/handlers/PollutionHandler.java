package ecomod.common.pollution.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ecomod.api.EcomodAPI;
import ecomod.api.EcomodItems;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.ChunkPollution;
import ecomod.api.pollution.IGarbage;
import ecomod.api.pollution.IPollutionEmitter;
import ecomod.api.pollution.IPollutionGetter;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.api.pollution.PollutionEmissionEvent;
import ecomod.asm.EcomodClassTransformer;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionEffectsConfig.Effects;
import ecomod.common.pollution.TEPollutionConfig.TEPollution;
import ecomod.common.pollution.PollutionManager;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.pollution.thread.WorldProcessingThread;
import ecomod.common.tiles.TileAnalyzer;
import ecomod.common.tiles.TileFilter;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.Percentage;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.core.EMConsts;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMAchievements;
import ecomod.core.stuff.EMConfig;
import ecomod.network.EMPacketHandler;
import ecomod.network.EMPacketString;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;



public class PollutionHandler implements IPollutionGetter
{
	public Map<String, WorldProcessingThread> threads = new HashMap<String, WorldProcessingThread>();
	
	private Gson gson = new GsonBuilder().serializeNulls().create();
	
	public WorldProcessingThread getWPT(World key)
	{	
		if(key == null || key.isRemote)
			return null;
		
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
		World w = event.world;
		
		if(w.isRemote)
		{
			return;
		}
		
		
		boolean b1 = false;
		
		for(int i : EMConfig.allowedDims)
			if(i == w.provider.dimensionId)
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
		
	}
	
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World w = event.world;
		
		if(w.isRemote)return;
		
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			try
			{
				synchronized(threads.get(key))
				{
					threads.get(key).notify();
					
					threads.get(key).forceSE();
				}
			}
			catch(Exception e)
			{
				EcologyMod.log.error("Unable to force sheduled emissions handling for "+threads.get(key).getName()+" because of" + e.toString());
				e.printStackTrace();
			}
			finally
			{
				threads.get(key).shutdown();
			}
		}
	}
	
	public void onServerStopping()
	{
		EcologyMod.log.info("Server is stopping... Shutting down WorldProcessingThreads...");
		for(String s : threads.keySet())
		{
			if(threads.get(s) != null)
			{
				try
				{
					synchronized(threads.get(s))
					{
						threads.get(s).notify();
				
						threads.get(s).forceSE();
					}
				}
				catch(Exception e)
				{
					EcologyMod.log.error("Unable to force sheduled emissions handling for "+threads.get(s).getName()+" because of " + e.toString());
					e.printStackTrace();
				}
				finally
				{
					threads.get(s).shutdown();
				}
			}
		}
	}
	
	//Chunk handlers
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		World w = event.world;
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
		
			Pair<Integer, Integer> coord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);
		
			if(!wpt.getLoadedChunks().contains(coord))
				wpt.getLoadedChunks().add(coord);
			
		}
	}
	
	
	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		World w = event.world;
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
		
			Pair<Integer, Integer> coord = Pair.of(event.getChunk().xPosition, event.getChunk().zPosition);
		
			if(wpt.getLoadedChunks().contains(coord))
				wpt.getLoadedChunks().remove(coord);
		}
	}
	
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEmission(PollutionEmissionEvent event)
	{		
		World w = event.getWorld();
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		//EcologyMod.log.info(event.getEmission().toString());
		
		if(threads.containsKey(key))
		{
			WorldProcessingThread wpt = threads.get(key);
			
			if(event.isScheduled())
			{
				wpt.getScheduledEmissions().add(new ChunkPollution(event.getChunkX(), event.getChunkZ(), event.getEmission()));
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
		
		EntityItem ei = event.entityItem;
		
		World w = ei.worldObj;
		
		if(w.isRemote)return;
		
		ItemStack is = ei.getEntityItem();
		
		if(EMConfig.item_blacklist.contains(GameRegistry.findUniqueIdentifierFor(is.getItem()).toString()))
			return;
		
		boolean isInWater = EMUtils.countWaterInRadius(w, new EMBlockPos(ei), 1) >= 1;
		
		if(!is.hasTagCompound() || !is.getTagCompound().hasKey("ECO_PH_ONITEMEXPIRE"))
		{
			//EcologyMod.log.info(is.toString());
			
			if(is.getItem() instanceof IGarbage)
			{
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(ei)), ((IGarbage)is.getItem()).getPollutionOnDecay().clone().multiplyAll(is.stackSize).multiply(PollutionType.WATER, isInWater ? 2 : 1), true);
			}
			else	
			{
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(ei)), PollutionSourcesConfig.getItemStackPollution(is).multiply(PollutionType.WATER, isInWater ? 2 : 1), true);
			}
			
			if(is.hasTagCompound() && is.getTagCompound().hasKey("food_pollution"))
			{
				PollutionData f_poll = new PollutionData();
				f_poll.readFromNBT(is.getTagCompound().getCompoundTag("food_pollution"));
				EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(ei)), f_poll, true);
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
		
		World w = event.world;
		
		if(w.isRemote)return;
		
		Explosion expl = event.explosion;
		
		int water_affected = EMUtils.countWaterInRadius(w, new EMBlockPos(expl.explosionX, expl.explosionY, expl.explosionZ), (int)expl.explosionSize);
		
		PollutionData emission = PollutionSourcesConfig.getSource("explosion_pollution_per_power");
		
		emission.multiply(PollutionType.AIR, expl.isFlaming ? 1.5F : 1);	
		emission.multiply(PollutionType.SOIL, expl.isFlaming ? 1.5F : 1);
		
		emission.multiply(PollutionType.WATER, water_affected > 0 ? 2 : 1);
		
		emission.multiplyAll(expl.explosionSize);
		
		EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(expl.explosionX, expl.explosionY, expl.explosionZ)), emission, true);
	}
	
	@SubscribeEvent
	public void onBonemeal(BonemealEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.world;
		
		if(w.isRemote)return;
		
		PollutionData data = getPollution(w, EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)).getLeft(), EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)).getRight());
		
		if(PollutionEffectsConfig.isEffectActive("no_bonemeal", data))
		{
			if(w.getBlock(event.x, event.y, event.z) == Blocks.sapling)
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
			
			if(event.entityPlayer != null)
			{
				event.entityPlayer.addStat(EMAchievements.ACHS.get("no_bonemeal"), 1);
			}
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)), PollutionSourcesConfig.getSource("bonemeal_pollution"), true);
		}
	}
	
	@SubscribeEvent
	public void onHoe(UseHoeEvent event)
	{
		if(event.isCanceled())return;
		
		World w = event.world;
		
		if(w.isRemote)return;
		
		PollutionData data = getPollution(w, EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)).getLeft(), EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)).getRight());
		
		if(PollutionEffectsConfig.isEffectActive("no_plowing", data))
		{
			event.setResult(Result.DENY);
			event.setCanceled(true);
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)), PollutionSourcesConfig.getSource("hoe_plowing_reducion"), true);
		}
		
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPlayerSleep(PlayerWakeUpEvent event)
	{
		if(event.isCanceled())return;
		
		EntityPlayer player = event.entityPlayer;
		
		World w = player.getEntityWorld();
		
		if(w.isRemote)return;

		
		PollutionData data = getPollution(w, EMUtils.blockPosToPair(new EMBlockPos(player)).getLeft(), EMUtils.blockPosToPair(new EMBlockPos(player)).getRight());
		
		if(!event.updateWorld)
		if(PollutionEffectsConfig.isEffectActive("bad_sleep", data))
		{
			if(PollutionUtils.hasSurfaceAccess(w, new EMBlockPos(player)))
			if(!PollutionUtils.isEntityRespirating(player))
			{
				player.addStat(EMAchievements.ACHS.get("bad_sleep"), 1);
				
				float f = (float) (data.getAirPollution()/EcomodStuff.pollution_effects.get("bad_sleep").getTriggerringPollution().getAirPollution() + 1);
			
				player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), f<10 ? (int)(250*f) : 2500, 1));
				player.addPotionEffect(new PotionEffect(Potion.weakness.getId(), 2000, (int)f));
				if(f >= 2)
					player.addPotionEffect(new PotionEffect(Potion.hunger.getId(), 2000, 2));
			
				player.addChatMessage(new ChatComponentTranslation("msg.ecomod.bad_sleep", new Object[0]));
			
				if(PollutionEffectsConfig.isEffectActive("poisonous_sleep", data))
				{
					player.addPotionEffect(new PotionEffect(Potion.poison.getId(), 1000, (int)f));
					
					player.addStat(EMAchievements.ACHS.get("poisonous_sleep"), 1);
				}
			}
			else
			{
				ItemStack stack = player.getEquipmentInSlot(4);
				
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
	public void onTreeGrow(SaplingGrowTreeEvent event)
	{
		World w = event.world;
		
		if(w.isRemote)return;
		
		String key = PollutionUtils.genPMid(w);
		
		WorldProcessingThread wpt = getWPT(key);
		
		if(wpt == null)return;
		
		PollutionData data = wpt.getPM().getChunkPollution(EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z))).getPollution();
		
		EcologyMod.log.info(data.toString());
		
		if(PollutionEffectsConfig.isEffectActive("no_trees", data))
		{
			event.setResult(Result.DENY);
		}
		else
		{
			EcomodAPI.emitPollution(w, EMUtils.blockPosToPair(new EMBlockPos(event.x, event.y, event.z)), PollutionSourcesConfig.getSource("tree_growing_pollution_redution"), true);
		}
	}
	
	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		EMBlockPos pos = new EMBlockPos(event.x, event.y, event.z);
		
		World w = event.world;
		
		if(w.isRemote)return;
		
		Pair<Integer, Integer> chunkCoords = EMUtils.blockPosToPair(pos);
		
		PollutionData data = getPollution(w, chunkCoords.getLeft(), chunkCoords.getRight());
		
		if(event.entityLiving instanceof IAnimals)
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
		if(event.world.isRemote || event.entity == null)
			return;
		
		if(event.entity instanceof EntityPlayer)
		{
			try
			{/*
				EMPacketString to_send = formCachedPollutionToSend((EntityPlayer)event.getEntity(), EMConfig.cached_pollution_radius);
				
				if(to_send == null)
				{
					EcologyMod.log.error("Unable to make EMPacketString with mark 'P'!!! Unable to form cached pollution for player "+((EntityPlayer)event.getEntity()).getName()+"("+((EntityPlayer)event.getEntity()).getUniqueID().toString()+")");
				}
				else
				{
					EMPacketHandler.WRAPPER.sendTo(to_send, (EntityPlayerMP)event.getEntity());
				}
				*/
				
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString(">"+("-" + getVisibleSmogIntensity(event.world, new EMBlockPos(event.entity)))), (EntityPlayerMP)event.entity);
				
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString("R"+(isPlayerInAcidRainZone((EntityPlayer)event.entity) ? 1 : 0)), (EntityPlayerMP)event.entity);
				
				EcologyMod.log.info("Serializing and sending Pollution Effects Config to the Player: "+((EntityPlayerMP)event.entity).getDisplayName()+"("+((EntityPlayerMP)event.entity).getUniqueID() + ")");
				
				Effects t = new Effects("", EcomodStuff.pollution_effects.values().toArray(new IAnalyzerPollutionEffect[EcomodStuff.pollution_effects.values().size()]));
				
				String json = gson.toJson(t, Effects.class);
				
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString("E"+json), (EntityPlayerMP)event.entity);
			}
			catch (Exception e)
			{
				EcologyMod.log.error("Error while sending EMPacketString with mark 'P' to the client!");
				EcologyMod.log.error(e.toString());
			}
			
			if(EcomodClassTransformer.failed_transformers.size() > 0)
			{
				String fails = "";
				
				for(String f : EcomodClassTransformer.failed_transformers)
					fails += f+";";
				
				fails = fails.substring(0, fails.length()-1);
				
				((EntityPlayerMP)event.entity).addChatMessage(new ChatComponentTranslation("msg.ecomod.asm_transformers_failed", fails).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)).appendSibling(new ChatComponentText(EMConsts.githubURL+"/issues").setChatStyle(new ChatStyle().setUnderlined(true).setColor(EnumChatFormatting.BLUE).setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, EMConsts.githubURL+"/issues")))));
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.entityLiving;
		
		if(entity == null)
			return;
		
		World world = entity.worldObj;
		
		if(world.isRemote)//client side actions are handled in ClientHandler
			return;
		
		if((entity.ticksExisted) % 60 == 0)
		{
			if(entity instanceof EntityPlayerMP)
				EMPacketHandler.WRAPPER.sendTo(new EMPacketString(">"+(getVisibleSmogIntensity(world, new EMBlockPos(entity)).intValue())), (EntityPlayerMP)entity);
		}
		
			if((entity.ticksExisted) % 300 == 0)
			{
				if(entity instanceof EntityLivingBase)
				{
					if(isPlayerInAcidRainZone(entity))
					{
						if(EMUtils.isRainingAt(world, new EMBlockPos(entity)))
						{
							ItemStack is = entity.getEquipmentInSlot(4);
						
							if(is != null && is.stackSize > 0)
							{
								if(is.isItemStackDamageable())
									is.damageItem((int) (EMConfig.acid_rain_item_deterioriation_factor * is.getMaxDamage()), entity);
							}
							else
							{
								entity.addPotionEffect(new PotionEffect(Potion.poison.getId(), 300, 1));
							}
							
							if(entity instanceof EntityPlayer)
							{
								((EntityPlayer)entity).addStat(EMAchievements.ACHS.get("acid_rain"), 1);
							}
						}
					}
					
					if(entity instanceof EntityPlayer)
					{
						EMPacketHandler.WRAPPER.sendTo(new EMPacketString("R"+(isPlayerInAcidRainZone(entity) ? 1 : 0)), (EntityPlayerMP)entity);
						
						EMBlockPos bp = new EMBlockPos(entity);
						
						boolean inSmog = isEntityInSmog((EntityPlayerMP)entity);
						
						if(inSmog && PollutionUtils.hasSurfaceAccess(world, bp))
						{
							if(!PollutionUtils.isEntityRespirating(entity))
							{
								((EntityPlayer)entity).addStat(EMAchievements.ACHS.get("smog"), 1);
								
								((EntityPlayerMP)entity).addPotionEffect(new PotionEffect(Potion.confusion.getId(), 200, 0));
								((EntityPlayerMP)entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 180, 0));
								
								if(getPollution(world, EMUtils.blockPosToPair(bp)).clone().getAirPollution() / EcomodStuff.pollution_effects.get("smog").getTriggerringPollution().getAirPollution()  >= 2)
									((EntityPlayerMP)entity).addPotionEffect(new PotionEffect(Potion.wither.getId(), 160, 1));
							}
						}
					}
					else
					{
						if(world.rand.nextInt(10) == 0)
						{
							if(isEntityInSmog(entity))
							{
								if(!PollutionUtils.isEntityRespirating(entity))
								{
									((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.poison.getId(), 200, 1));
								}
							}
						}
					}
				}
			}
	}
	
	/*@SubscribeEvent
	public void onFished(ItemFishedEvent event)
	{
		if(event.getHookEntity() == null)
			return;
		
		BlockPos pos = new BlockPos(event.getHookEntity().posX, event.getHookEntity().posY, event.getHookEntity().posZ);
		
		World w = event.getHookEntity().world;
		
		if(w.isRemote)return;
		
		Pair<Integer, Integer> chunkCoords = EMUtils.blockPosToPair(pos);
		
		PollutionData data = getPollution(w, chunkCoords.getLeft(), chunkCoords.getRight());
		
		if(PollutionEffectsConfig.isEffectActive("no_fish", data))
		{
			if(event.getEntityPlayer() != null)
			{
				EMTriggers.NO_FISH.trigger((EntityPlayerMP)event.getEntityPlayer());
			}
			
			event.damageRodBy(5);
			event.setCanceled(true);
		}
	}*/
	
	public boolean isEntityInSmog(EntityLivingBase player)
	{
		EMBlockPos bp = new EMBlockPos(player);
		
		PollutionData pollution = EcomodAPI.getPollution(player.worldObj, EMUtils.blockPosToPair(bp).getLeft(), EMUtils.blockPosToPair(bp).getRight());
		
		if(pollution!=null && pollution != PollutionData.getEmpty())
			if(PollutionEffectsConfig.isEffectActive("smog", pollution))
			{
					return true;
			}
		
		return false;
	}

	public boolean isPlayerInAcidRainZone(EntityLivingBase player)
	{
		EMBlockPos bp = new EMBlockPos(player.posX, player.posY, player.posZ);
		
		if(player.worldObj.isRaining())
		{
			PollutionData pollution = EcomodAPI.getPollution(player.worldObj, EMUtils.blockPosToPair(bp).getLeft(), EMUtils.blockPosToPair(bp).getRight());
		
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
		
		EMBlockPos bp;
		int dim;
		
		try{
			 bp = new EMBlockPos(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
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
			
			TileEntity te = ws.getTileEntity(bp.getX(), bp.getY(), bp.getZ());
			
			if(te != null)
			if(te instanceof TileAnalyzer)
			{
				((TileAnalyzer)te).analyze();
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockDrops(BlockEvent.HarvestDropsEvent event)
	{
		if(!event.world.isRemote)
		{
			if(event.block instanceof IGrowable)
			{
				dropHandler(event.world, new EMBlockPos(event.x, event.y, event.z), event.drops);
			}
		}
	}
	
	public void dropHandler(World w, EMBlockPos pos, List<ItemStack> drops)
	{
		PollutionData pd = getPollution(w, EMUtils.blockPosToPair(pos));
		
		if(PollutionEffectsConfig.isEffectActive("food_pollution", pd))
		{
			PollutionData trig = EcomodStuff.pollution_effects.get("food_pollution").getTriggerringPollution();
		
			PollutionData delta = pd.clone().add(trig.clone().multiplyAll(-1));
		
			if(EcomodStuff.pollution_effects.get("food_pollution").getTriggeringType() == TriggeringType.AND ? pd.compareTo(trig) >= 0 : pd.compareOR(trig) >= 0)
			{	
				boolean in = w.getBlock(pos.getX(), pos.getY(), pos.getZ()).getMaterial() == Material.water;
			
				if(!in)
				for(EnumFacing dir : EnumFacing.values())
					if(!in)
						in |= EMUtils.getBlock(w, pos.offset(dir)).getMaterial() == Material.water;
			
			
				delta.multiply(PollutionType.WATER, in ? 1F : 0.25F);
			
				in = PollutionUtils.hasSurfaceAccess(w, pos);
			
				delta.multiply(PollutionType.AIR, in ? 1F : 0.4F);
			
				in = EMUtils.getBlock(w, pos).getMaterial() == Material.grass || EMUtils.getBlock(w, pos).getMaterial() == Material.ground;
			
				if(!in)
				for(EnumFacing dir : EnumFacing.values())
					if(!in)
						in |= EMUtils.getBlock(w, pos.offset(dir)).getMaterial() == Material.grass || EMUtils.getBlock(w, pos.offset(dir)).getMaterial() == Material.ground;
			
				delta.multiply(PollutionType.SOIL, in ? 1F : 0.2F);
			}

			for(ItemStack is : drops)
			{
				if(is.getItem() instanceof ItemFood)
				{
					NBTTagCompound tag = is.getTagCompound();
					if(tag == null)
						tag = new NBTTagCompound();
					
					NBTTagCompound p_tag = new NBTTagCompound();
					
					if(tag.hasKey("food_pollution"))
						p_tag = tag.getCompoundTag("food_pollution");
					
					PollutionData itempollution = new PollutionData();
					itempollution.readFromNBT(p_tag);
					itempollution.add(delta.multiplyAll(EMConfig.food_polluting_factor * 2));
					itempollution.writeToNBT(p_tag);
					
					tag.setTag("food_pollution", p_tag);
					
					is.setTagCompound(tag);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerUsesItem(PlayerUseItemEvent.Start event)
	{
		if(event.entityPlayer != null && event.item != null)
			if(!event.entityPlayer.getEntityWorld().isRemote)
			{
				ItemStack is = event.entityPlayer.getEquipmentInSlot(4);
				
				if(is != null && is.stackSize > 0 && is.getItem() == EcomodItems.RESPIRATOR)
				{
					is = event.item;
					if(is.getItem() instanceof ItemFood || is.getItem() instanceof ItemBucketMilk || is.getItem() instanceof ItemPotion)
					{
						event.entityPlayer.addChatMessage(new ChatComponentTranslation("msg.ecomod.no_eat_with_respirator"));
						event.duration = -1;
						event.setCanceled(true);
					}
				}
			}
	}
	
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		if(event.entityLiving != null)
		if(!event.entityLiving.worldObj.isRemote)
		{
			List<ItemStack> drps = new ArrayList<ItemStack>();
			for(EntityItem ei : event.drops)
			{
				if(ei.getEntityItem().getItem() instanceof ItemFood)
					drps.add(ei.getEntityItem());
			}
			
			dropHandler(event.entityLiving.worldObj, new EMBlockPos(event.entityLiving), drps);
		}
	}
	
	private static final int smog_search_radius = 2;
	
	private Percentage getVisibleSmogIntensity(World w, EMBlockPos bp)
	{
		if(!PollutionEffectsConfig.isEffectPresent("smog"))
			return new Percentage(0);
		
		Pair<Integer, Integer> chunkpos = EMUtils.blockPosToPair(bp);
		
		if(PollutionEffectsConfig.isEffectActive("smog", getPollution(w, chunkpos)))
		{
			return new Percentage(100);
		}
		
		int d_x = 0, d_y = 0, r;
		boolean found = false;
		
		for(r = 1; r <= smog_search_radius; r++)
		{
			if(!found)
			for(d_x = -r; d_x <= r; d_x++)
			{
				if(PollutionEffectsConfig.isEffectActive("smog", getPollution(w, EMUtils.offsetPair(chunkpos, d_x, d_y))))
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			for(d_y = -r; d_y <= r; d_y++)
			{
				if(PollutionEffectsConfig.isEffectActive("smog", getPollution(w, EMUtils.offsetPair(chunkpos, d_x, d_y))))
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			for(d_x = r; d_x >= -r; d_x--)
			{
				if(PollutionEffectsConfig.isEffectActive("smog", getPollution(w, EMUtils.offsetPair(chunkpos, d_x, d_y))))
				{
					found = true;
					break;
				}
			}
			
			if(!found)
			for(d_y = r; d_y >= -r; d_y--)
			{
				if(PollutionEffectsConfig.isEffectActive("smog", getPollution(w, EMUtils.offsetPair(chunkpos, d_x, d_y))))
				{
					found = true;
					break;
				}
			}
			
			if(found)
				break;
		}
		
		if(found)
		{
			return new Percentage(100 / (r * r + 1));
		}
		
		return new Percentage(0);
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event)
	{
		World w = event.world;

		if(!w.isRemote)
		{
			WorldProcessingThread wpt = getWPT(w);

			if(wpt != null)
			{
				wpt.isWorldTicking = event.phase == cpw.mods.fml.common.gameevent.TickEvent.Phase.START;
			}
		}
	}
	
	/*
	 * For searching an error from issue #11
	 * 
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.world.isRemote || getWPT(event.world) == null)
			return;
		
		if(event.world.rand.nextInt(40) == 3)
		new Thread(){
			@Override
			public void run() {
				super.run();
				
				getWPT(event.world).getLoadedChunks().forEach((chpos) -> {EMUtils.logIfNotNull(calculateChunkPollution(event.world.getChunkFromChunkCoords(chpos.getLeft(), chpos.getRight())), EcologyMod.log, Level.INFO);});
			}
			
			public PollutionData calculateChunkPollution(Chunk c)
			{
				List<TileEntity> tes = new ArrayList(c.getTileEntityMap().values())
				
				PollutionData ret = new PollutionData();
				
				for(TileEntity te : tes)
				{
					if(te == null || te.isInvalid())
						continue;
					
					int wir = EMUtils.countWaterInRadius(c.getWorld(), te.getPos(), EMConfig.wpr);
					boolean rain = c.getWorld().isRainingAt(te.getPos());
					
					boolean overriden_by_func = false;
					
					for(Function<TileEntity, Object[]> func : EcomodStuff.custom_te_pollution_determinants)
					{
						Object[] func_result = new Object[0];
						
						try
						{
							func_result = func.apply(te);
						}
						catch(Exception e)
						{
							EcologyMod.log.error("Exception while processing a custom TileEntity pollution determining function:");
							EcologyMod.log.info(e.toString());
							e.printStackTrace();
							continue;
						}

						if(func_result.length < 3)
							continue;
						
						ret.add(PollutionType.AIR, (double)func_result[0]);
						ret.add(PollutionType.WATER, (double)func_result[1]);
						ret.add(PollutionType.SOIL, (double)func_result[2]);
						
						if(func_result.length > 3)
						{
							if(func_result[3] != null && func_result[3] instanceof Boolean)
								if(!overriden_by_func)
									overriden_by_func = (Boolean)func_result[3];
						}
					}
					
					if(!overriden_by_func)
					if(te instanceof IPollutionEmitter)
					{
						IPollutionEmitter ipe = (IPollutionEmitter) te;
						
						int filters = 0;
						
						for(EnumFacing f : EnumFacing.VALUES)
						{
							TileEntity tile = c.getWorld().getTileEntity(te.getPos().offset(f));
							
							if(tile instanceof TileFilter && ((TileFilter)tile).isWorking())
								filters++;
						}
						
						ret.add(ipe.pollutionEmission(false).clone()
								.multiply(PollutionType.AIR, 1 - EMConfig.filter_adjacent_tiles_redution * filters).multiply(PollutionType.WATER, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 2).multiply(PollutionType.SOIL, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 3)
								.multiply(PollutionType.WATER, rain ? 2 : 1).multiply(PollutionType.SOIL, rain ? 1.2F : 1).multiply(PollutionType.WATER, wir == 0 ? 1 : wir)
								);
					}
					else
					{
						if(EcologyMod.instance.tepc.hasTile(te))
						{
							if(PollutionUtils.isTEWorking(c.getWorld(), te))
							{
								TEPollution tep = EcologyMod.instance.tepc.getTEP(te);
								if(tep != null)
								{
									int filters = 0;
									
									for(EnumFacing f : EnumFacing.VALUES)
									{
										TileEntity tile = c.getWorld().getTileEntity(te.getPos().offset(f));
										
										if(tile instanceof TileFilter && ((TileFilter)tile).isWorking())
											filters++;
									}
									
									ret.add(tep.getEmission().clone()
											.multiply(PollutionType.AIR, 1 - EMConfig.filter_adjacent_tiles_redution * filters).multiply(PollutionType.WATER, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 2).multiply(PollutionType.SOIL, 1 - EMConfig.filter_adjacent_tiles_redution * filters / 3)
											.multiply(PollutionType.WATER, rain ? 3 : 1).multiply(PollutionType.SOIL, rain ? 1.5F : 1).multiply(PollutionType.WATER, wir == 0 ? 1 : wir)
											);
								}
							}
						}
					}
				}
				return ret.multiplyAll(EMConfig.wptcd/60);
			}
		}.start();
	}
	*/
	@Nullable
	@Override
	public PollutionData getPollution(World w, int chunkx, int chunkz)
	{
		if(w == null)
			return null;
		
		WorldProcessingThread wpt = getWPT(w);
		
		if(wpt == null)
			return null;
		else if(wpt.getPM() == null)
			return null;
		
		if(wpt.getPM().getPollution(chunkx, chunkz) != null)
			return wpt.getPM().getPollution(chunkx, chunkz);
		else
			return null;
	}
	
	public PollutionData getPollution(World w, Pair<Integer, Integer> pair)
	{
		return getPollution(w, pair.getLeft(), pair.getRight());
	}
	
	public static class PollutionHandlerCrashCallable implements ICrashCallable
	{
		@Override
		public String call() throws Exception {
			PollutionHandler ph = EcologyMod.ph;
			if(ph != null)
			{
				String ret = "";
				
				for(String s : ph.threads.keySet())
				{
					WorldProcessingThread wpt = ph.threads.get(s);
					
					if(wpt != null)
					{
						ret += "\n\t"+s+": Working: "+wpt.isWorking()+" |Last Profiler section: "+wpt.profiler.getNameOfLastSection()+"| Interrupted:"+wpt.isInterrupted();
					}
				}
				
				if(!ret.isEmpty())
					return ret + "\n";
			}
			
			return null;
		}

		@Override
		public String getLabel() {
			return "[EcologyMod|PollutionHandler] Active WorldProcessingThreads";
		}
		
	}
}