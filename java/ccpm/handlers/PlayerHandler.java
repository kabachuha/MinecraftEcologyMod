package ccpm.handlers;

import java.util.List;
import java.util.Random;

import ccpm.api.CCPMApi;
import ccpm.api.IRespirator;
import ccpm.api.ITilePollutionProducer;
import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager;
import ccpm.fluids.CCPMFluids;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerHandler {

	public PlayerHandler() {
		// TODO Auto-generated constructor stub
	}
	
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event)
	{
		//FMLLog.info("pte called");
		EntityPlayer player = event.player;
		
		if(player == null)
			return;
		
		if(player.worldObj.isRemote)
			return;
		
		if(!WorldHandler.isLoaded)
			return;
		//FMLLog.info("CKP CKP CKP");
		if(player.ticksExisted % 20 == 0)
		{
			if(/*player.addedToChunk && */!player.isDead)
			{
				
				Chunk chunk = player.worldObj.getChunkFromBlockCoords(player.getPosition());
				
				if(chunk == null || !chunk.isLoaded())
					return;
				
				PollutionManager pm = WorldHandler.instance.pm;
				
				if(pm == null || pm.chunksPollution == null || pm.chunksPollution.getCP() == null || pm.chunksPollution.getCP().length <=0)
					return;
				
				float pollution = PollutionUtils.getChunkPollution(chunk);
				
				if(pollution >= CCPMConfig.smogPoll)
				{
					//if(!player.isPotionActive(CCPM.smog))
					//{
						//PotionEffect pe = new PotionEffect(CCPM.smog.id, 10, 1);
						//List<ItemStack> ci = pe.getCurativeItems();
						//ci.clear();
						//pe.setCurativeItems(ci);
					
						if(player.worldObj.canSeeSky(player.getPosition()) || player.worldObj.canSeeSky(player.getPosition().east())||player.worldObj.canSeeSky(player.getPosition().west())||player.worldObj.canSeeSky(player.getPosition().north())||player.worldObj.canSeeSky(player.getPosition().south()))
						player.addPotionEffect(/*pe*/new PotionEffect(CCPM.smog, 120, 1));
					//}
				}
			}
		}
		if(player.ticksExisted % 30 == 0)
		{
			if(!player.isDead)
			{
				
				
				if(player.isPotionActive(CCPM.smog))
				{
					if(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null||!(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof IRespirator)||!((IRespirator)player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem()).isFiltering(player, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)))
					{
					   player.attackEntityFrom(CCPMApi.damageSourcePollution, 1);
					
					if(player.worldObj.rand.nextInt(5) == 0)
					{
						player.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1));
						if(player.worldObj.rand.nextInt(8) == 0)
						{
							player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20,1));
							if(player.worldObj.rand.nextInt(6) == 0)
							{
								player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100,1));
								if(player.worldObj.rand.nextInt(10) == 0)
								{
									player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 120, 2));
									if(player.worldObj.rand.nextInt(4) == 0)
									{
										player.attackEntityFrom(CCPMApi.damageSourcePollution, 10F);
									}
								}
							}
						}
					}
				}
				}
			}
		}
	}

	
	
	@SubscribeEvent
	public void playerClick(PlayerInteractEvent event)
	{

		
		if(!event.getWorld().isRemote)
		{
			EntityPlayer player = event.getEntityPlayer();
			ItemStack is = player.getHeldItemMainhand();
			if(is == null)
				return;
			if(!is.hasTagCompound())
				return;
			NBTTagCompound nbt = is.getTagCompound();
			if(!nbt.hasKey("ccpmTest"))
				return;
			
			TileEntity tile = event.getWorld().getTileEntity(event.getPos());
			
			if(tile == null || tile.isInvalid())
				return;
			
			
			NBTTagCompound tag = new NBTTagCompound();
			
			tile.writeToNBT(tag);
			
			if(!tag.hasKey("id"))
			{
				is.setStackDisplayName("¯\\_(ツ)_/¯");
				return;
			}
			
			String id = tag.getString("id");
			is.setStackDisplayName(id);
			player.addChatMessage(new TextComponentString("The tile entity id is " +tag.getString("id")));
			if(tile instanceof ITilePollutionProducer)
			{
				player.addChatMessage(new TextComponentString("This TE produces "+((ITilePollutionProducer)tile).getPollutionProdution() + " pollution"));
			}
			
			Tilez[] tiles= PollutionConfig.cfg.getTiles();
			for(int i = 0; i < tiles.length; i++)
			if(id == tiles[i].getName())
			{
				player.addChatMessage(new TextComponentString("This TE produces "+tiles[i].getPollution()+" pollution"));
			}
		    player.swingArm(EnumHand.MAIN_HAND);
		    event.setCanceled(true);
		}
	}
	
	
	@SubscribeEvent
	public void onItemExpite(ItemExpireEvent event)
	{
		if(WorldHandler.isLoaded && event.getEntityItem()!=null && !event.getEntityItem().worldObj.isRemote && PollutionUtils.isValidDim(event.getEntityItem().worldObj))
		PollutionUtils.increasePollution((event.getEntityItem().getEntityItem().getItem() == CCPM.pollArmor[0] || event.getEntityItem().getEntityItem().getItem() == CCPM.pollArmor[1] || event.getEntityItem().getEntityItem().getItem() == CCPM.pollArmor[2] || event.getEntityItem().getEntityItem().getItem() == CCPM.pollArmor[3] || event.getEntityItem().getEntityItem().getItem() == CCPM.pollutionBrick || event.getEntityItem().getEntityItem().getItem() == CCPM.buckPw || event.getEntityItem().getEntityItem().getItem() == Item.getItemFromBlock(CCPM.pollutionBricks)) ? 1000*event.getEntityItem().getEntityItem().stackSize : 2*event.getEntityItem().getEntityItem().stackSize, event.getEntityItem().worldObj.getChunkFromBlockCoords(event.getEntityItem().getPosition()));
	}
	
	public static boolean firstPlayerJoinedWorld = false;
	
	@SubscribeEvent
	public void onPlayerJoinGame(EntityJoinWorldEvent event)
	{
			if(event.getWorld() == null || event.getWorld().isRemote || event.getEntity() == null)
				return;
			
			if(event.getEntity() instanceof EntityPlayer)
			{
				if(!firstPlayerJoinedWorld)
				{
				CCPM.log.info("First Player joined game!");
				
			
				firstPlayerJoinedWorld = true;
				}
				
				TextComponentString cct1 = new TextComponentString(CCPM.NAME+" is alpha version now. It may contain a lot of bugs! Please, report all issues and suggestions to my GitHub! "+CCPM.githubURL);
				cct1.setStyle(cct1.getStyle().setColor(TextFormatting.RED));
				
				TextComponentString cct2 = new TextComponentString(CCPM.NAME+" is beta version now. It may contain few bugs. Please, report all issues and suggestions to my GitHub! "+CCPM.githubURL);
				cct2.setStyle(cct2.getStyle().setColor(TextFormatting.BLUE));
					
				StringBuilder sb = new StringBuilder();
				
				for(String c : PollutionConfig.modidList())
				{
					sb.append(c);
					sb.append(", ");
				}
				
				TextComponentString supMods = new TextComponentString(CCPM.NAME+" now supports these mods: "+sb.toString());
				
				((EntityPlayer)event.getEntity()).addChatMessage(supMods);
				
				if(CCPM.version.endsWith("A"))
					((EntityPlayer)event.getEntity()).addChatMessage(cct1);
				if(CCPM.version.endsWith("B"))
					((EntityPlayer)event.getEntity()).addChatMessage(cct2);
			}
		
		
	}
	
	@SubscribeEvent
	public void fillBucketEvent(FillBucketEvent event)
	{	
		if(event.getTarget().typeOfHit != RayTraceResult.Type.BLOCK)
			return;
		BlockPos bp = new BlockPos(event.getTarget().hitVec);
		IBlockState state = event.getWorld().getBlockState(bp);
		Fluid f = FluidRegistry.lookupFluidForBlock(state.getBlock());
		
		if(f == null)
			return;
		
		if(f.isGaseous())
			return;
		
		if(f == FluidRegistry.WATER)
		{
			if(!event.getWorld().isRemote)
			{
				if((Integer)state.getValue(BlockLiquid.LEVEL) == 0)
				{
					if(PollutionUtils.getChunkPollution(event.getWorld(), bp) >= CCPMConfig.waterPoll)
					{
						ItemStack ret = FluidContainerRegistry.fillFluidContainer(new FluidStack(CCPMFluids.pollutedWater, FluidContainerRegistry.BUCKET_VOLUME), event.getEmptyBucket());
						
						if(ret != null)
						{
							event.setFilledBucket(ret);
							event.setResult(Result.ALLOW);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttacked(LivingAttackEvent event)
	{
		if(event.getEntity().worldObj.isRemote)return;
		
		if(event.getSource() == null || event.getEntity() == null || event.getAmount() <= 0)
			return;
		
		if(event.getSource().isUnblockable())
			return;
		
		
		ItemStack armor[] = new ItemStack[]{event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD),event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD), event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD), event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD)};
		
		int multiplier = 0;
		
		for(int i = 0; i<4; i++)
		{
			if(armor[i] != null)
			{
				if(armor[i].getItem()==CCPM.pollArmor[i])
				{
					++multiplier;
				}
			}
		}
		
		if(multiplier == 0)return;
		
		List<Entity> elb = event.getEntity().worldObj.getEntitiesWithinAABBExcludingEntity(event.getEntity(), new AxisAlignedBB(event.getEntity().posX-(3*multiplier), event.getEntity().posY-(3*multiplier), event.getEntity().posZ-(3*multiplier), event.getEntity().posX+(3*multiplier), event.getEntity().posY+(3*multiplier), event.getEntity().posZ+(3*multiplier)));
		
		if(elb == null || elb.size() <= 0)
			return;
		
		BlockPos pos = event.getEntity().getPosition();
		
		
		for(Entity e : elb)
		{
			if(e instanceof EntityLivingBase)
			{
				EntityLivingBase el = (EntityLivingBase)e;
				if(e instanceof EntityPlayer)
				{
				if(el.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null||!(el.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof IRespirator)||!((IRespirator)el.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem()).isFiltering((EntityPlayer)el, el.getItemStackFromSlot(EntityEquipmentSlot.HEAD)))
				{
				el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
				
				el.addPotionEffect(new PotionEffect(MobEffects.POISON,10*multiplier, multiplier));
				}
				}
				else
				{
					el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
					
					el.addPotionEffect(new PotionEffect(MobEffects.POISON,10*multiplier, multiplier));
				}
				
				event.getEntity().getEntityWorld().playSound(null, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, SoundCategory.PLAYERS, multiplier*2, 2.6F + (event.getEntity().worldObj.rand.nextFloat() - event.getEntity().worldObj.rand.nextFloat()) * 0.8F);
				
				for(int i = -2; i <= 2; i++)
					for(int j = -2; j <=2; j++)
						for(int k = -2; k <=2; k++)
						{
							event.getEntity().getEntityWorld().spawnParticle(event.getEntity().getEntityWorld().rand.nextBoolean() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + event.getEntity().getEntityWorld().rand.nextFloat()) - 0.5D, (double)((float)k - event.getEntity().getEntityWorld().rand.nextFloat() - 1.0F), (double)((float)j + event.getEntity().getEntityWorld().rand.nextFloat()) - 0.5D, new int[0]);
						}
				
				PollutionUtils.increasePollution(event.getAmount()*multiplier, event.getEntity().getEntityWorld().getChunkFromBlockCoords(pos));
			}
		}
		
		
		if(event.getEntity().worldObj.rand.nextInt(Math.round(100/multiplier)) == 1)
		{
			PollutionUtils.increasePollution(multiplier*100, event.getEntity().getEntityWorld().getChunkFromBlockCoords(pos));
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void livingTick(LivingUpdateEvent event)
	{
		if(event.getEntity()==null)
			return;
		
		if(event.getEntity().worldObj.isRemote)return;
		
		if(event.getEntity() instanceof EntityPlayer || event.getEntity() instanceof IProjectile)
			return;
		
		if(event.getEntityLiving() instanceof EntityCreature){}else return;
		
		EntityCreature en = (EntityCreature)event.getEntityLiving();
		
		if(!CCPMConfig.mobsScared)
		if(en instanceof IMob)
			return;
		
		if(en.worldObj.getWorldTime() % 20 == 0)
		if(PollutionUtils.getChunkPollution(en) >= CCPMConfig.noPlanting/2)
		if(wtf(en.getRNG(), PollutionUtils.getChunkPollution(en)))
		en.tasks.addTask(1, new EntityAIPanic(en, 2));
	}
	
	private static boolean wtf(Random rand, double in)
	{
		double d = Math.log10(in);
		if(d == Double.NaN || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY)
		return false;
		
		int d1 = (int) Math.round(d);
		
		if(d1 >=10)
			d1=10;
		
		if(d1 < 0)
			d1 = 0;
		
		int i = rand.nextInt(20-d1);
		
		return i==1;
	}
	
	@SubscribeEvent
	public void checkSpawn(CheckSpawn event)
	{
		if(event.getEntity()!=null)
		if(!event.getWorld().isRemote)
		if(event.getEntityLiving() instanceof IAnimals)
		{
			if(PollutionUtils.getChunkPollution(event.getWorld(), new BlockPos(event.getX(),event.getY(),event.getZ()))>=CCPMConfig.smogPoll)
			{
				event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if(event.getWorld() == null || event.getState() == null)return;
		
		if(event.getWorld().isRemote)return;
		
		if(event.getState() == CCPM.compressor.getDefaultState() || event.getState() == CCPM.baf.getDefaultState())
		{
			TileEntity te = event.getWorld().getTileEntity(event.getPos());
			
			if(te == null) return;
			
			if(te instanceof IFluidHandler)
			{
				FluidTankInfo[] fti = ((IFluidHandler)te).getTankInfo(EnumFacing.UP);
				
				for(FluidTankInfo f : fti)
				{
					if(f.fluid.amount == 0)continue;
					FluidEvent.FluidSpilledEvent fse = new FluidEvent.FluidSpilledEvent(f.fluid, event.getWorld(), event.getPos());
					FluidEvent.fireEvent(fse);
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void onFluidSpilled(FluidEvent.FluidSpilledEvent event)
	{
		if(event.getFluid() != null && event.getFluid().amount > 0 && event.getFluid().getFluid() == CCPMFluids.concentratedPollution)
			PollutionUtils.increasePollution(event.getFluid().amount*100, event.getWorld().getChunkFromBlockCoords(event.getPos()));
	}
}
