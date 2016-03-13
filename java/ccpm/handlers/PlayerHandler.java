package ccpm.handlers;

import java.util.List;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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
						player.addPotionEffect(/*pe*/new PotionEffect(CCPM.smog.id, 120, 1));
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
					if(player.getEquipmentInSlot(4) == null||!(player.getEquipmentInSlot(4).getItem() instanceof IRespirator)||!((IRespirator)player.getEquipmentInSlot(4).getItem()).isFiltering(player, player.getEquipmentInSlot(4)))
					{
					   player.attackEntityFrom(CCPMApi.damageSourcePollution, 1);
					
					if(player.worldObj.rand.nextInt(5) == 0)
					{
						player.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));
						if(player.worldObj.rand.nextInt(8) == 0)
						{
							player.addPotionEffect(new PotionEffect(Potion.blindness.id, 20,1));
							if(player.worldObj.rand.nextInt(6) == 0)
							{
								player.addPotionEffect(new PotionEffect(Potion.hunger.id, 100,1));
								if(player.worldObj.rand.nextInt(10) == 0)
								{
									player.addPotionEffect(new PotionEffect(Potion.wither.id, 120, 2));
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
		if(event.action == Action.RIGHT_CLICK_AIR)
			return;

		
		if(!event.world.isRemote)
		{
			EntityPlayer player = event.entityPlayer;
			ItemStack is = player.getCurrentEquippedItem();
			if(is == null)
				return;
			if(!is.hasTagCompound())
				return;
			NBTTagCompound nbt = is.getTagCompound();
			if(!nbt.hasKey("ccpmTest"))
				return;
			
			TileEntity tile = event.world.getTileEntity(event.pos);
			
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
			player.addChatMessage(new ChatComponentText("The tile entity id is " +tag.getString("id")));
			if(tile instanceof ITilePollutionProducer)
			{
				player.addChatMessage(new ChatComponentText("This TE produces "+((ITilePollutionProducer)tile).getPollutionProdution() + " pollution"));
			}
			
			Tilez[] tiles= PollutionConfig.cfg.getTiles();
			for(int i = 0; i < tiles.length; i++)
			if(id == tiles[i].getName())
			{
				player.addChatMessage(new ChatComponentText("This TE produces "+tiles[i].getPollution()+" pollution"));
			}
		    player.swingItem();
		    event.setCanceled(true);
		}
	}
	
	
	@SubscribeEvent
	public void onItemExpite(ItemExpireEvent event)
	{
		if(WorldHandler.isLoaded && event.entityItem!=null && !event.entityItem.worldObj.isRemote && event.entityItem.worldObj.provider.getDimensionId() == 0)
		PollutionUtils.increasePollution((event.entityItem.getEntityItem().getItem() == CCPM.pollArmor[0] || event.entityItem.getEntityItem().getItem() == CCPM.pollArmor[1] || event.entityItem.getEntityItem().getItem() == CCPM.pollArmor[2] || event.entityItem.getEntityItem().getItem() == CCPM.pollArmor[3] || event.entityItem.getEntityItem().getItem() == CCPM.pollutionBrick || event.entityItem.getEntityItem().getItem() == CCPM.buckPw || event.entityItem.getEntityItem().getItem() == Item.getItemFromBlock(CCPM.pollutionBricks)) ? 1000*event.entityItem.getEntityItem().stackSize : 2*event.entityItem.getEntityItem().stackSize, event.entityItem.worldObj.getChunkFromBlockCoords(event.entityItem.getPosition()));
	}
	
	public static boolean firstPlayerJoinedWorld = false;
	
	@SubscribeEvent
	public void onPlayerJoinGame(EntityJoinWorldEvent event)
	{
			if(event.world == null || event.world.isRemote || event.entity == null)
				return;
			
			if(event.entity instanceof EntityPlayer)
			{
				if(!firstPlayerJoinedWorld)
				{
				CCPM.log.info("First Player joined game!");
				
			
				firstPlayerJoinedWorld = true;
				}
				
				ChatComponentText cct1 = new ChatComponentText(CCPM.NAME+" is alpha version now. It may contain a lot of bugs! Please, report all issues and suggestions to my GitHub! "+CCPM.githubURL);
				cct1.setChatStyle(cct1.getChatStyle().setColor(EnumChatFormatting.RED));
				
				ChatComponentText cct2 = new ChatComponentText(CCPM.NAME+" is beta version now. It may contain few bugs. Please, report all issues and suggestions to my GitHub! "+CCPM.githubURL);
				cct2.setChatStyle(cct2.getChatStyle().setColor(EnumChatFormatting.BLUE));
						
				if(CCPM.version.endsWith("A"))
					((EntityPlayer)event.entity).addChatMessage(cct1);
				if(CCPM.version.endsWith("B"))
					((EntityPlayer)event.entity).addChatMessage(cct2);
			}
		
		
	}
	
	@SubscribeEvent
	public void fillBucketEvent(FillBucketEvent event)
	{
		if(event.target.typeOfHit != MovingObjectType.BLOCK)
			return;
		BlockPos bp = new BlockPos(event.target.hitVec);
		IBlockState state = event.world.getBlockState(bp);
		Fluid f = FluidRegistry.lookupFluidForBlock(state.getBlock());
		
		if(f == null)
			return;
		
		if(f.isGaseous())
			return;
		
		if(f == FluidRegistry.WATER)
		{
			if(!event.world.isRemote)
			{
				if((Integer)state.getValue(BlockLiquid.LEVEL) == 0)
				{
					if(PollutionUtils.getChunkPollution(event.world, bp) >= CCPMConfig.waterPoll)
					{
						ItemStack ret = FluidContainerRegistry.fillFluidContainer(new FluidStack(CCPMFluids.pollutedWater, FluidContainerRegistry.BUCKET_VOLUME), event.current);
						
						if(ret != null)
						{
							event.result = ret;
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
		if(event.source == null || event.entity == null || event.ammount <= 0)
			return;
		
		if(event.source.isUnblockable())
			return;
		
		
		ItemStack armor[] = new ItemStack[]{event.entityLiving.getEquipmentInSlot(4),event.entityLiving.getEquipmentInSlot(3), event.entityLiving.getEquipmentInSlot(2), event.entityLiving.getEquipmentInSlot(1)};
		
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
		
		List<Entity> elb = event.entity.worldObj.getEntitiesWithinAABBExcludingEntity(event.entity, AxisAlignedBB.fromBounds(event.entity.posX-(3*multiplier), event.entity.posY-(3*multiplier), event.entity.posZ-(3*multiplier), event.entity.posX+(3*multiplier), event.entity.posY+(3*multiplier), event.entity.posZ+(3*multiplier)));
		
		if(elb == null || elb.size() <= 0)
			return;
		
		BlockPos pos = event.entity.getPosition();
		
		
		for(Entity e : elb)
		{
			if(e instanceof EntityLivingBase)
			{
				EntityLivingBase el = (EntityLivingBase)e;
				if(e instanceof EntityPlayer)
				{
				if(event.entityLiving.getEquipmentInSlot(4) == null||!(event.entityLiving.getEquipmentInSlot(4).getItem() instanceof IRespirator)||!((IRespirator)event.entityLiving.getEquipmentInSlot(4).getItem()).isFiltering((EntityPlayer)event.entityLiving, event.entityLiving.getEquipmentInSlot(4)))
				{
				el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
				
				el.addPotionEffect(new PotionEffect(Potion.poison.id,10*multiplier, multiplier));
				}
				}
				else
				{
					el.attackEntityFrom(CCPMApi.damageSourcePollution, multiplier);
					
					el.addPotionEffect(new PotionEffect(Potion.poison.id,10*multiplier, multiplier));
				}
				
				event.entity.getEntityWorld().playSoundAtEntity(event.entity, "random.fizz", multiplier*2, 2.6F + (event.entity.worldObj.rand.nextFloat() - event.entity.worldObj.rand.nextFloat()) * 0.8F);
				
				for(int i = -2; i <= 2; i++)
					for(int j = -2; j <=2; j++)
						for(int k = -2; k <=2; k++)
						{
							event.entity.getEntityWorld().spawnParticle(event.entity.getEntityWorld().rand.nextBoolean() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double)((float)i + event.entity.getEntityWorld().rand.nextFloat()) - 0.5D, (double)((float)k - event.entity.getEntityWorld().rand.nextFloat() - 1.0F), (double)((float)j + event.entity.getEntityWorld().rand.nextFloat()) - 0.5D, new int[0]);
						}
				
				PollutionUtils.increasePollution(event.ammount*multiplier, event.entity.getEntityWorld().getChunkFromBlockCoords(pos));
			}
		}
		
		
		if(event.entity.worldObj.rand.nextInt(Math.round(100/multiplier)) == 1)
		{
			PollutionUtils.increasePollution(multiplier*100, event.entity.getEntityWorld().getChunkFromBlockCoords(pos));
			event.setCanceled(true);
		}
	}
}
