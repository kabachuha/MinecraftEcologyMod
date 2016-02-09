package ccpm.handlers;

import java.util.List;

import ccpm.api.CCPMApi;
import ccpm.api.IRespirator;
import ccpm.api.ITilePollutionProducer;
import ccpm.core.CCPM;
import ccpm.ecosystem.PollutionManager;
import ccpm.utils.PollutionUtils;
import ccpm.utils.config.CCPMConfig;
import ccpm.utils.config.PollutionConfig;
import ccpm.utils.config.PollutionConfig.PollutionProp.Tilez;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
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
				
				Chunk chunk = player.worldObj.getChunkFromBlockCoords((int)player.posX, (int)player.posZ);
				
				if(chunk == null || !chunk.isChunkLoaded)
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
					
					if(player.worldObj.rand.nextInt(10) == 5)
					{
						player.addPotionEffect(new PotionEffect(Potion.poison.id, 10, 1));
						if(player.worldObj.rand.nextInt(10) == 5)
						{
							player.addPotionEffect(new PotionEffect(Potion.blindness.id, 20,1));
							if(player.worldObj.rand.nextInt(10) == 5)
							{
								player.addPotionEffect(new PotionEffect(Potion.hunger.id, 100,1));
								if(player.worldObj.rand.nextInt(10) == 5)
								{
									player.addPotionEffect(new PotionEffect(Potion.wither.id, 120, 2));
									if(player.worldObj.rand.nextInt(10) == 5)
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
			
			TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
			
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
		if(WorldHandler.isLoaded && event.entityItem!=null && !event.entityItem.worldObj.isRemote && event.entityItem.worldObj.provider.dimensionId == 0)
		PollutionUtils.increasePollution(10*event.entityItem.getEntityItem().stackSize, event.entityItem.worldObj.getChunkFromBlockCoords((int)event.entityItem.posX, (int)event.entityItem.posZ));
	}
	
	public static boolean firstPlayerJoinedWorld = false;
	
	@SubscribeEvent
	public void onPlayerJoinGame(EntityJoinWorldEvent event)
	{
		if(!firstPlayerJoinedWorld)
		{
			if(event.world == null || event.world.isRemote || event.entity == null)
				return;
			
			if(event.entity instanceof EntityPlayer)
			{
				FMLLog.info("First Player joined game!");
				firstPlayerJoinedWorld = true;
			}
		}
	}
}
