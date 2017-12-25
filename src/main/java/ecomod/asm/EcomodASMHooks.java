package ecomod.asm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ecomod.api.EcomodAPI;
import ecomod.api.EcomodStuff;
import ecomod.api.client.IRenderableHeadArmor;
import ecomod.api.client.IAnalyzerPollutionEffect.TriggeringType;
import ecomod.api.pollution.PollutionData;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionEffectsConfig;
import ecomod.common.pollution.PollutionSourcesConfig;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

public class EcomodASMHooks
{
/*FIXME FIXME FIXME!
	//ASM Hooks:
	
		public static void updateTickAddition(World worldIn, BlockPos pos)
		{
			if(!worldIn.isRemote)
			{
				if(worldIn.canSeeSky(pos.up()))
				{
					PollutionData pd = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight());
					if(pd != null)
					if(PollutionEffectsConfig.isEffectActive("wasteland", pd))
					{
						if(worldIn.rand.nextInt(10)==0)
						{
							worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
						}
						else
						{
							if(worldIn.rand.nextInt(50) == 0)
							{
								if(worldIn.rand.nextInt(8) == 0)
									worldIn.setBlockState(pos, Blocks.CLAY.getDefaultState());
								else
									worldIn.setBlockState(pos, Blocks.SAND.getDefaultState());
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
		
		public static void fireTickAddition(World worldIn, BlockPos pos)
		{
			if(!worldIn.isRemote)
			{
				if(worldIn.getGameRules().getBoolean("doFireTick"))
				{
					EcomodAPI.emitPollution(worldIn, EMUtils.blockPosToPair(pos), PollutionSourcesConfig.getSource("fire_pollution"), true);
				}
			}
		}
		
		public static void leavesTickAddition(World worldIn, BlockPos pos)
		{
			if(!worldIn.isRemote)
			{
					if(worldIn.isRainingAt(pos.up()))
					{
						if(worldIn.rand.nextInt(30) == 0)
						{
							PollutionData pollution = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight());
				
							if(pollution!=null && pollution.compareTo(PollutionData.getEmpty()) != 0)
								if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
										worldIn.setBlockToAir(pos);
						}
					}
					else
					{
						if(worldIn.rand.nextInt(10) == 0)
						{
							if(PollutionUtils.hasSurfaceAccess(worldIn, pos))
							{
								PollutionData pollution = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight());
				
								if(pollution!=null && pollution.compareTo(PollutionData.getEmpty()) != 0)
									if(PollutionEffectsConfig.isEffectActive("dead_trees", pollution))
											worldIn.setBlockToAir(pos);
									else
										if(worldIn.rand.nextInt(50)==0)
											EcomodAPI.emitPollution(worldIn, EMUtils.blockPosToPair(pos), PollutionSourcesConfig.getSource("leaves_redution"), true);
							}
						}
					}
			}
		}
		
		public static void farmlandTickAddition(World worldIn, BlockPos pos)
		{
			if(!worldIn.isRemote)
			{
				PollutionData pollution = EcomodAPI.getPollution(worldIn, EMUtils.blockPosToPair(pos).getLeft(), EMUtils.blockPosToPair(pos).getRight());
				
				if(PollutionEffectsConfig.isEffectActive("no_plowing", pollution))
				{
					if(worldIn.rand.nextInt(3) == 0)
					{
						boolean sealed = true;
						for(EnumFacing f : EnumFacing.VALUES)
							sealed &= worldIn.getBlockState(pos.offset(f)).getBlock() != Blocks.DIRT;
						
						sealed &= !PollutionUtils.hasSurfaceAccess(worldIn, pos);
						
						//Turn to dirt
						IBlockState iblockstate = Blocks.DIRT.getDefaultState();
						worldIn.setBlockState(pos, iblockstate);
						AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(worldIn, pos).offset(pos);

						for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb))
						{
					       	entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
						}
						
						return;
					}
				}
				
				if(PollutionEffectsConfig.isEffectActive("acid_rain", pollution))
				{
					if(worldIn.rand.nextInt(3) == 0)
					{
						if(worldIn.isRainingAt(pos.up()))
						{
							//Turn to dirt
							IBlockState iblockstate = Blocks.DIRT.getDefaultState();
							worldIn.setBlockState(pos, iblockstate);
							AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(worldIn, pos).offset(pos);

							for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb))
							{
						       	entity.setPosition(entity.posX, axisalignedbb.maxY, entity.posZ);
							}
						}
					}
				}
			}
		}
		
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
		
		public static void lchAddition(ModelRenderer modelRenderer, EntityLivingBase entitylivingbaseIn, float scale)
		{
			ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

	        if (itemstack != null && itemstack.stackSize > 0)
	        {
	        	if(itemstack.getItem() instanceof IRenderableHeadArmor)
	        	{
	        		Minecraft minecraft = Minecraft.getMinecraft();
	                GlStateManager.pushMatrix();
	                
	                if (entitylivingbaseIn.isSneaking())
	                {
	                    GlStateManager.translate(0.0F, 0.2F, 0.0F);
	                }
	                
	                boolean flag = entitylivingbaseIn instanceof EntityVillager || (entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager());
	                
	                if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof EntityVillager))
	                {
	                    float f = 2.0F;
	                    float f1 = 1.4F;
	                    GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
	                    GlStateManager.scale(0.7F, 0.7F, 0.7F);
	                    GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
	                }
	                
	                modelRenderer.postRender(0.0625F);
	                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	                
	                float f3 = 0.625F;
	                GlStateManager.translate(0.0F, -0.25F, 0.0F);
	                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
	                GlStateManager.scale(0.625F, -0.625F, -0.625F);

	                if (flag)
	                {
	                    GlStateManager.translate(0.0F, -0.1F, 0.0F);
	                }

	                minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
	                
	                GlStateManager.popMatrix();
	        	}
	        }
		}
		
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
						if(is.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
						{
							PollutionData pd = EcologyMod.ph.getPollution(item.getEntityWorld(), EMUtils.blockPosToPair(item.getPosition()));
							
							PollutionData trig = EcomodStuff.pollution_effects.get("food_pollution").getTriggerringPollution();
							
							if(pd != null && !pd.equals(PollutionData.getEmpty()))
							if(EcomodStuff.pollution_effects.get("food_pollution").getTriggeringType() == TriggeringType.AND ? pd.compareTo(trig) >= 0 : pd.compareOR(trig) >= 0)
							{
								PollutionData delta = pd.clone().add(trig.clone().multiplyAll(-1));
								
								boolean in = item.getEntityWorld().getBlockState(item.getPosition()).getMaterial() == Material.WATER;
								
								if(!in)
								for(EnumFacing dir : EnumFacing.VALUES)
									if(!in)
										in |= item.getEntityWorld().getBlockState(item.getPosition().offset(dir)).getMaterial() == Material.WATER;
								
								
								delta.multiply(PollutionType.WATER, in ? 1F : 0.25F);
								
								in = PollutionUtils.hasSurfaceAccess(item.getEntityWorld(), item.getPosition());
								
								delta.multiply(PollutionType.AIR, in ? 1F : 0.4F);
								
								in = item.getEntityWorld().getBlockState(item.getPosition()).getMaterial() == Material.GRASS || item.getEntityWorld().getBlockState(item.getPosition()).getMaterial() == Material.GROUND;
								
								if(!in)
								for(EnumFacing dir : EnumFacing.VALUES)
									if(!in)
										in |= item.getEntityWorld().getBlockState(item.getPosition().offset(dir)).getMaterial() == Material.GRASS || item.getEntityWorld().getBlockState(item.getPosition().offset(dir)).getMaterial() == Material.GROUND;
								
								delta.multiply(PollutionType.SOIL, in ? 1F : 0.2F);
								
								is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).setPollution(is.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution().add(delta.multiplyAll(EMConfig.food_polluting_factor)));
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
				if(stack.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
				{
					PollutionData pollution = stack.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution();
					
					int a = (int)(pollution.getAirPollution() * EMConfig.pollution_to_food_poison[0]);
					int b = (int)(pollution.getWaterPollution() * EMConfig.pollution_to_food_poison[1]);
					int c = (int)(pollution.getSoilPollution() * EMConfig.pollution_to_food_poison[2]);
					
					int m = (a+b+c)/3;
					int k = (int) Math.sqrt(a * b * c) / 300;
					
					if(m > 0)
						player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), Math.min(m*20, 1200), Math.min(k, 2)));
					
					if(m >= 60)
					{
						player.addChatMessage(new TextComponentTranslation("msg.ecomod.polluted_food", new Object[0]).setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
						player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("slowness"), m, Math.min(k, 2)));
						player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), m, Math.min(k, 2)));
						
						player.addStat(EMAchievements.ACHS.get("polluted_food"));
					}
					
					if(m >= 200)
					{
						player.addStat(EMAchievements.ACHS.get("very_polluted_food"));
						
						player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("wither"), m, Math.min(k, 2)));
					}
				}
			}
		}
		
		public static void smeltItemFurnaceAddition(TileEntityFurnace furnace, ItemStack result)
		{
			if(!furnace.getWorld().isRemote)
			{
				/* FIXME!!! Not seeing Capablilties in result!
				if(ingr.getItem() instanceof ItemFood && result.getItem() instanceof ItemFood)
				{
					if(ingr.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null) && result.hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
					{
						result.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).setPollution(result.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution().clone().add(ingr.getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution()));
					}
				}
				
				EcomodAPI.emitPollution(furnace.getWorld(), EMUtils.blockPosToPair(furnace.getPos()), PollutionSourcesConfig.getSmeltedItemStackPollution(furnace.getStackInSlot(0)), true);
			}
		}
		
		public static EntityItem handleFishing(EntityFishHook hook, EntityItem is)
		{
			if(hook == null || is == null)
				return is;
			
			BlockPos pos = hook.getPosition();
			
			World w = hook.worldObj;
			
			if(w.isRemote)return is;
			
			Pair<Integer, Integer> chunkCoords = EMUtils.blockPosToPair(pos);
			
			PollutionData data = EcologyMod.ph.getPollution(w, chunkCoords.getLeft(), chunkCoords.getRight());
			
			if(PollutionEffectsConfig.isEffectActive("no_fish", data))
			{
				if(hook.angler != null)
				{
					hook.angler.addStat(EMAchievements.ACHS.get("no_fish"));
					hook.angler.getHeldItemMainhand().attemptDamageItem(5, hook.worldObj.rand);
				}
				
				if(w.rand.nextInt(4) == 0)
				{
					List<ItemStack> drops = new ArrayList<ItemStack>();
					drops.add(new ItemStack(Items.ROTTEN_FLESH));
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
		}*/
}
