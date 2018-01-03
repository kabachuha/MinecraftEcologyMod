package ecomod.asm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.common.utils.newmc.EMBlockPos;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMAchievements;
import ecomod.core.stuff.EMConfig;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import scala.actors.threadpool.Arrays;

public class EcomodASMHooks
{
	//ASM Hooks:
	
		public static void updateTickAddition(World worldIn, int x, int y, int z)
		{
			if(!worldIn.isRemote)
			{
				if(worldIn.canBlockSeeTheSky(x, y+1, z))
				{
					PollutionData pd = EcomodAPI.getPollution(worldIn, x >> 4, z >> 4);
					if(pd != null)
					if(PollutionEffectsConfig.isEffectActive("wasteland", pd))
					{
						if(worldIn.rand.nextInt(10)==0)
						{
							worldIn.setBlock(x, y, z, Blocks.dirt, 0, 1 | 2);
						}
						else
						{
							if(worldIn.rand.nextInt(50) == 0)
							{
								if(worldIn.rand.nextInt(8) == 0)
									worldIn.setBlock(x, y, z, Blocks.clay, 0, 1 | 2);
								else
									worldIn.setBlock(x, y, z, Blocks.sand, 0, 1 | 2);
							}
						}
					}
				}
			}
		}
		
		private static final ResourceLocation rain_texture = new ResourceLocation("ecomod:textures/environment/rain.png");
		
		public static void renderRainAddition()
		{
			if(EcologyMod.proxy.getClientHandler() != null)
			if(EcologyMod.proxy.getClientHandler().acid_rain)
				Minecraft.getMinecraft().getTextureManager().bindTexture(rain_texture);
		}
		
		public static void fireTickAddition(World worldIn, int x, int y, int z)
		{
			if(!worldIn.isRemote)
			{
				if(worldIn.getGameRules().getGameRuleBooleanValue("doFireTick"))
				{
					EcomodAPI.emitPollution(worldIn, Pair.of(x >> 4, z >> 4), PollutionSourcesConfig.getSource("fire_pollution"), true);
				}
			}
		}
		
		public static void leavesTickAddition(World worldIn, int x, int y, int z)
		{
			if(!worldIn.isRemote)
			{
					if(EMUtils.isRainingAt(worldIn, new EMBlockPos(x, y+1, z)))
					{
						if(worldIn.rand.nextInt(30) == 0)
						{
							PollutionData pollution = EcomodAPI.getPollution(worldIn, x >> 4, z >> 4);
				
							if(pollution!=null && pollution.compareTo(PollutionData.getEmpty()) != 0)
								if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
										worldIn.setBlockToAir(x, y, z);
						}
					}
					else
					{
						if(worldIn.rand.nextInt(10) == 0)
						{
							if(PollutionUtils.hasSurfaceAccess(worldIn, new EMBlockPos(x, y, z)))
							{
								PollutionData pollution = EcomodAPI.getPollution(worldIn, x >> 4, z >> 4);
				
								if(pollution!=null && pollution.compareTo(PollutionData.getEmpty()) != 0)
									if(PollutionEffectsConfig.isEffectActive("dead_trees", pollution))
											worldIn.setBlockToAir(x, y, z);
									else
										if(worldIn.rand.nextInt(50)==0)
											EcomodAPI.emitPollution(worldIn, Pair.of(x >> 4, z >> 4), PollutionSourcesConfig.getSource("leaves_redution"), true);
							}
						}
					}
			}
		}
		
		public static void farmlandTickAddition(World worldIn, int x, int y, int z)
		{
			if(!worldIn.isRemote)
			{
				PollutionData pollution = EcomodAPI.getPollution(worldIn, x >> 4, z >> 4);
				
				if(PollutionEffectsConfig.isEffectActive("no_plowing", pollution))
				{
					if(worldIn.rand.nextInt(3) == 0)
					{
						boolean sealed = true;
						for(ForgeDirection f : ForgeDirection.VALID_DIRECTIONS)
							sealed &= worldIn.getBlock(x + f.offsetX, y + f.offsetY, z + f.offsetZ) != Blocks.dirt;
						
						sealed &= !PollutionUtils.hasSurfaceAccess(worldIn, new EMBlockPos(x, y, z));
						
						//Turn to dirt
						worldIn.setBlock(x, y, z, Blocks.dirt, 0, 3);
						return;
					}
				}
				
				if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
				{
					if(worldIn.rand.nextInt(3) == 0)
					{
						if(EMUtils.isRainingAt(worldIn, new EMBlockPos(x, y + 1, z)))
						{
							//Turn to dirt
							worldIn.setBlock(x, y, z, Blocks.dirt, 0, 3);
						}
					}
				}
			}
		}
		/*
		public static void entityItemAttackedAddition(EntityItem entity_item, DamageSource dmg_source)
		{
			if(entity_item == null || entity_item.getEntityWorld() == null || entity_item.getEntityWorld().isRemote || dmg_source == null)
				return;
			
			if(!PollutionSourcesConfig.hasSource("expired_item"))
				return;
			
			PollutionData to_emit = PollutionData.getEmpty();
			
			if(dmg_source == DamageSource.lava)
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem());
				to_emit.add(PollutionType.AIR, to_emit.getSoilPollution() * 0.8).add(PollutionType.AIR, to_emit.getWaterPollution() * 0.9D).multiply(PollutionType.WATER, 0.1F).multiply(PollutionType.SOIL, 0.2F);
			}
			else if(dmg_source.isFireDamage())
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem());
				to_emit.add(PollutionType.AIR, to_emit.getSoilPollution() * 0.7).add(PollutionType.AIR, to_emit.getWaterPollution() * 0.8D).multiply(PollutionType.WATER, 0.2F).multiply(PollutionType.SOIL, 0.3F);
				
				FluidStack fs = FluidUtil.getFluidContained(entity_item.getEntityItem());
				
				if(fs != null)
				{
					if(EMConfig.isConcentratedPollutionExplosive)
					if(fs.getFluid() == EcomodStuff.concentrated_pollution)
					{
						if(PollutionSourcesConfig.hasSource("concentrated_pollution_explosion_pollution"))
						{
							to_emit.add(PollutionSourcesConfig.getSource("concentrated_pollution_explosion_pollution").multiplyAll(fs.amount / Fluid.BUCKET_VOLUME));
						}
						
						entity_item.getEntityWorld().newExplosion(entity_item, entity_item.posX, entity_item.posY, entity_item.posZ, 3F * (fs.amount / Fluid.BUCKET_VOLUME), true, true);
					}
				}
			}
			else if(dmg_source.isExplosion())
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem());
				to_emit.add(PollutionType.AIR, to_emit.getSoilPollution() * 0.5).add(PollutionType.AIR, to_emit.getWaterPollution() * 0.4D).multiply(PollutionType.WATER, 0.6F).multiply(PollutionType.SOIL, 0.5F);
				
				FluidStack fs = FluidUtil.getFluidContained(entity_item.getEntityItem());
				
				if(fs != null)
				{
					if(EMConfig.isConcentratedPollutionExplosive)
					if(fs.getFluid() == EcomodStuff.concentrated_pollution)
					{
						if(PollutionSourcesConfig.hasSource("concentrated_pollution_explosion_pollution"))
						{
							to_emit.add(PollutionSourcesConfig.getSource("concentrated_pollution_explosion_pollution").multiplyAll(fs.amount / Fluid.BUCKET_VOLUME));
						}
						
						entity_item.getEntityWorld().newExplosion(dmg_source.getSourceOfDamage() == null ? entity_item : dmg_source.getSourceOfDamage(), entity_item.posX, entity_item.posY, entity_item.posZ, 3F * (fs.amount / Fluid.BUCKET_VOLUME), true, true);
					}
				}
			}
			else
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem()).multiplyAll(0.95F);
			}
			
			if(!to_emit.equals(PollutionData.getEmpty()))
			{
				EcomodAPI.emitPollution(entity_item.getEntityWorld(), EMUtils.blockPosToPair(entity_item.getPosition()), to_emit, true);
			}
		}
		*/
		public static void itemEntityUpdateAddition(EntityItem item)
		{
			if(item != null)
			if(!item.worldObj.isRemote)
			{
				if(item.ticksExisted > 0 && item.ticksExisted % 100 == 0)
				{
					ItemStack is = item.getEntityItem();
				
					if(is != null && is.stackSize > 0)
					if(is.getItem() instanceof ItemFood)
					{
						if(EcomodStuff.pollution_effects.containsKey("food_pollution"))
						{
							NBTTagCompound tag = is.getTagCompound();
							PollutionData pd = EcologyMod.ph.getPollution(item.worldObj, Pair.of((int)item.posX >> 4, (int)item.posZ >> 4));
							
							PollutionData trig = EcomodStuff.pollution_effects.get("food_pollution").getTriggerringPollution();
							
							if(pd != null && !pd.equals(PollutionData.getEmpty()))
							if(EcomodStuff.pollution_effects.get("food_pollution").getTriggeringType() == TriggeringType.AND ? pd.compareTo(trig) >= 0 : pd.compareOR(trig) >= 0)
							{
								PollutionData delta = pd.clone().add(trig.clone().multiplyAll(-1));
								
								boolean in = item.worldObj.getBlock((int)Math.floor(item.posX), (int)Math.floor(item.posY), (int)Math.floor(item.posZ)).getMaterial() == Material.water;
								
								if(!in)
								for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
									if(!in)
										in |= item.worldObj.getBlock((int)Math.floor(item.posX) + dir.offsetX, (int)Math.floor(item.posY) + dir.offsetY, (int)Math.floor(item.posZ) + dir.offsetZ).getMaterial() == Material.water;
								
								
								delta.multiply(PollutionType.WATER, in ? 1F : 0.25F);
								
								in = PollutionUtils.hasSurfaceAccess(item.worldObj, new EMBlockPos(item));
								
								delta.multiply(PollutionType.AIR, in ? 1F : 0.4F);
								
								in = item.worldObj.getBlock((int)Math.floor(item.posX), (int)Math.floor(item.posY), (int)Math.floor(item.posZ)).getMaterial() == Material.grass || item.worldObj.getBlock((int)Math.floor(item.posX), (int)Math.floor(item.posY), (int)Math.floor(item.posZ)).getMaterial() == Material.ground;
								
								if(!in)
								for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
									if(!in)
										in |= item.worldObj.getBlock((int)Math.floor(item.posX) + dir.offsetX, (int)Math.floor(item.posY) + dir.offsetY, (int)Math.floor(item.posZ) + dir.offsetZ).getMaterial() == Material.grass || item.worldObj.getBlock((int)Math.floor(item.posX) + dir.offsetX, (int)Math.floor(item.posY) + dir.offsetY, (int)Math.floor(item.posZ) + dir.offsetZ).getMaterial() == Material.ground;
								
								delta.multiply(PollutionType.SOIL, in ? 1F : 0.2F);
								
								if(tag == null)
									tag = new NBTTagCompound();
								
								NBTTagCompound p_tag = new NBTTagCompound();
								
								if(tag.hasKey("food_pollution"))
									p_tag = tag.getCompoundTag("food_pollution");
								
								PollutionData itempollution = new PollutionData();
								itempollution.readFromNBT(p_tag);
								itempollution.add(delta.multiplyAll(EMConfig.food_polluting_factor));
								itempollution.writeToNBT(p_tag);
								
								tag.setTag("food_pollution", p_tag);
								
								item.getEntityItem().setTagCompound(tag);
							}
						}
					}
				}
			}
		}
		
		public static void itemFoodOnEatenAddition(ItemStack stack, World worldIn, EntityPlayer player)
		{
			if(!worldIn.isRemote)
			{
				if(stack.hasTagCompound())
				{
					NBTTagCompound tag = stack.getTagCompound();
					
					if(tag == null || !tag.hasKey("food_pollution"))
						return;	
					
					NBTTagCompound p_tag = tag.getCompoundTag("food_pollution");

					PollutionData pollution = new PollutionData();
					pollution.readFromNBT(p_tag);
					
					int a = (int)(pollution.getAirPollution() * EMConfig.pollution_to_food_poison[0]);
					int b = (int)(pollution.getWaterPollution() * EMConfig.pollution_to_food_poison[1]);
					int c = (int)(pollution.getSoilPollution() * EMConfig.pollution_to_food_poison[2]);
					
					int m = (a+b+c)/3;
					int k = (int) Math.sqrt(a * b * c) / 300;
					
					if(m > 0)
						player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), Math.min(m*20, 1200), Math.min(k, 2)));
					
					if(m >= 60)
					{
						player.addChatMessage(new ChatComponentTranslation("msg.ecomod.polluted_food", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
						player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), m, Math.min(k, 2)));
						player.addPotionEffect(new PotionEffect(Potion.poison.getId(), m, Math.min(k, 2)));
						
						player.addStat(EMAchievements.ACHS.get("polluted_food"), 1);
					}
					
					if(m >= 200)
					{
						player.addStat(EMAchievements.ACHS.get("very_polluted_food"), 1);
						
						player.addPotionEffect(new PotionEffect(Potion.wither.getId(), m, Math.min(k, 2)));
					}
				}
			}
		}
		
		public static void smeltItemFurnaceAddition(TileEntityFurnace furnace, ItemStack result)
		{
			if(!furnace.getWorldObj().isRemote)
			{
				EcomodAPI.emitPollution(furnace.getWorldObj(), Pair.of(furnace.xCoord >> 4, furnace.zCoord >> 4), PollutionSourcesConfig.getSmeltedItemStackPollution(furnace.getStackInSlot(0)), true);
			}
		}
		
		public static EntityItem handleFishing(EntityFishHook hook, EntityItem is)
		{
			if(hook == null || is == null)
				return is;
			
			EMBlockPos pos = new EMBlockPos(hook.posX, hook.posY, hook.posZ);
			
			World w = hook.worldObj;
			
			if(w.isRemote)return is;
			
			Pair<Integer, Integer> chunkCoords = EMUtils.blockPosToPair(pos);
			
			PollutionData data = EcologyMod.ph.getPollution(w, chunkCoords.getLeft(), chunkCoords.getRight());
			
			if(PollutionEffectsConfig.isEffectActive("no_fish", data))
			{
				if(hook.field_146042_b != null)
				{
					hook.field_146042_b.addStat(EMAchievements.ACHS.get("no_fish"), 1);
					hook.field_146042_b.getCurrentEquippedItem().attemptDamageItem(5, hook.worldObj.rand);
				}
				
				if(w.rand.nextInt(4) == 0)
				{
					List<ItemStack> drops = new ArrayList<ItemStack>();
					drops.add(new ItemStack(Items.rotten_flesh));
					EcologyMod.ph.dropHandler(w, pos, drops);
					is.setEntityItemStack(drops.get(0));
				}
				else
				{
					is.setEntityItemStack(null);
					is.setDead();
				}
				return is;
			}
			
			List<ItemStack> drops = new ArrayList<ItemStack>();
			drops.add(is.getEntityItem());
			EcologyMod.ph.dropHandler(w, pos, drops);
			is.setEntityItemStack(drops.get(0));
			
			return is;
		}
		
		public static void entityItemAttackedAddition(EntityItem entity_item, DamageSource dmg_source)
		{
			if(entity_item == null || entity_item.worldObj == null || entity_item.worldObj.isRemote || dmg_source == null || entity_item.isDead)
				return;
			
			if(!PollutionSourcesConfig.hasSource("expired_item"))
				return;
			
			PollutionData to_emit = PollutionData.getEmpty();
			
			if(dmg_source == DamageSource.lava)
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem());
				to_emit.add(PollutionType.AIR, to_emit.getSoilPollution() * 0.8).add(PollutionType.AIR, to_emit.getWaterPollution() * 0.9D).multiply(PollutionType.WATER, 0.1F).multiply(PollutionType.SOIL, 0.2F);
			}
			else if(dmg_source.isFireDamage())
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem());
				to_emit.add(PollutionType.AIR, to_emit.getSoilPollution() * 0.7).add(PollutionType.AIR, to_emit.getWaterPollution() * 0.8D).multiply(PollutionType.WATER, 0.2F).multiply(PollutionType.SOIL, 0.3F);
				
				FluidStack fs = null;
				if(entity_item.getEntityItem().getItem() instanceof IFluidContainerItem)
					fs = ((IFluidContainerItem)entity_item.getEntityItem().getItem()).getFluid(entity_item.getEntityItem());
				else
					fs = FluidContainerRegistry.getFluidForFilledItem(entity_item.getEntityItem());
				
				if(fs != null)
				{
					if(EMConfig.isConcentratedPollutionExplosive)
					if(fs.getFluid() == EcomodStuff.concentrated_pollution)
					{
						if(PollutionSourcesConfig.hasSource("concentrated_pollution_explosion_pollution"))
						{
							to_emit.add(PollutionSourcesConfig.getSource("concentrated_pollution_explosion_pollution").multiplyAll(fs.amount / FluidContainerRegistry.BUCKET_VOLUME));
						}
						
						entity_item.worldObj.newExplosion(entity_item, entity_item.posX, entity_item.posY, entity_item.posZ, 3F * (fs.amount / FluidContainerRegistry.BUCKET_VOLUME), true, true);
						entity_item.worldObj.removeEntity(entity_item);
					}
				}
			}
			else if(dmg_source.isExplosion())
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem());
				to_emit.add(PollutionType.AIR, to_emit.getSoilPollution() * 0.5).add(PollutionType.AIR, to_emit.getWaterPollution() * 0.4D).multiply(PollutionType.WATER, 0.6F).multiply(PollutionType.SOIL, 0.5F);
				
				FluidStack fs = null;
				if(entity_item.getEntityItem().getItem() instanceof IFluidContainerItem)
					fs = ((IFluidContainerItem)entity_item.getEntityItem().getItem()).getFluid(entity_item.getEntityItem());
				else
					fs = FluidContainerRegistry.getFluidForFilledItem(entity_item.getEntityItem());
				
				if(fs != null)
				{
					if(EMConfig.isConcentratedPollutionExplosive)
					if(fs.getFluid() == EcomodStuff.concentrated_pollution)
					{
						if(PollutionSourcesConfig.hasSource("concentrated_pollution_explosion_pollution"))
						{
							to_emit.add(PollutionSourcesConfig.getSource("concentrated_pollution_explosion_pollution").multiplyAll(fs.amount / FluidContainerRegistry.BUCKET_VOLUME));
						}
						
						entity_item.worldObj.newExplosion(dmg_source.getSourceOfDamage() == null ? entity_item : dmg_source.getSourceOfDamage(), entity_item.posX, entity_item.posY, entity_item.posZ, 3F * (fs.amount / FluidContainerRegistry.BUCKET_VOLUME), true, true);
						entity_item.worldObj.removeEntity(entity_item);
					}
				}
			}
			else
			{
				to_emit = PollutionSourcesConfig.getItemStackPollution(entity_item.getEntityItem()).multiplyAll(0.95F);
			}
			
			if(!to_emit.equals(PollutionData.getEmpty()))
			{
				EcomodAPI.emitPollution(entity_item.worldObj, Pair.of((int)Math.floor(entity_item.posX) >> 4, (int)Math.floor(entity_item.posZ) >> 4), to_emit, true);
			}
		}
		
		public static void potionBrewedAddition(TileEntityBrewingStand tile)
		{
			if(tile != null && !tile.getWorldObj().isRemote)
			{
				EcomodAPI.emitPollution(tile.getWorldObj(), Pair.of(tile.xCoord >> 4, tile.zCoord >> 4), PollutionSourcesConfig.getSource("brewing_potion_pollution"), true);
			}
		}
		
		public static boolean canCropGrow(boolean ret, World world, int x, int y, int z)
		{
			if(!world.isRemote)
			{
				PollutionData pollution = EcologyMod.ph.getPollution(world, x >> 4, z >> 4);
				
				if(pollution != null && pollution.compareTo(PollutionData.getEmpty()) != 0 && pollution.getSoilPollution() > 1)
				if(PollutionEffectsConfig.isEffectActive("no_plowing", pollution))
				{
					if(PollutionUtils.hasSurfaceAccess(world, new EMBlockPos(x, y, z)))
						return false;
				}
				else
				{
					if(PollutionUtils.hasSurfaceAccess(world, new EMBlockPos(x, y, z)))
					if(EcomodStuff.pollution_effects.containsKey("no_crops_growing"))
					{
						PollutionData effectPoll = EcomodStuff.pollution_effects.get("no_crops_growing").getTriggerringPollution();
						
						for(PollutionType type : PollutionType.values())
						{
							if(effectPoll.get(type) > 1 && pollution.get(type) > 1)
							{
								double k = effectPoll.get(type) / pollution.get(type);
								
								k = Math.max(k, 1);
						
								if(world.rand.nextInt((int)k) == 0)
								{
									return false;
								}
							}
						}
					}
				}
			}
			
			return ret;
		}
}
