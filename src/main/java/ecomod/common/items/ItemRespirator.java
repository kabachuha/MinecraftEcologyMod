package ecomod.common.items;

import java.util.List;

import ecomod.api.EcomodStuff;
import ecomod.api.client.IRenderableHeadArmor;
import ecomod.api.pollution.IRespirator;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.EMItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRespirator extends ItemArmor implements IRespirator, IRenderableHeadArmor
{
	public ItemRespirator() {
		super(EMItems.RESPIRATOR_MATERIAL, 1, EntityEquipmentSlot.HEAD);
		
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		
	}

	@Override
	public boolean isRespirating(EntityLivingBase entity, ItemStack stack, boolean decr)
	{
		if(entity instanceof EntityPlayer)
		{		
			NBTTagCompound nbt = stack.getTagCompound();
			
			EntityPlayer player = (EntityPlayer)entity;
			
			if(nbt != null)
			{
				int filter = nbt.getInteger("filter");
				if(filter > 0)
				{
					if(decr)
						nbt.setInteger("filter", filter-(entity.getHealth() >= entity.getMaxHealth()/2 ? 1 : 2));

					return true;
				}
				else
				{
					int k = getFilterInInventory(player);

					if(k != -1)
					{
						ItemStack stk = player.inventory.getStackInSlot(k);

						player.inventory.setInventorySlotContents(k, --stk.stackSize < 1 ? null : stk);

						nbt.setInteger("filter", EMConfig.filter_durability);

						stack.setTagCompound(nbt);

						return true;
					}
				}
			}
			else
			{
				nbt = new NBTTagCompound();
				
				int k = getFilterInInventory(player);
				
				if(k != -1)
				{
					ItemStack stk = player.inventory.getStackInSlot(k);

					player.inventory.setInventorySlotContents(k, --stk.stackSize < 1 ? null : stk);
					
					nbt.setInteger("filter", EMConfig.filter_durability);
				}
				
				stack.setTagCompound(nbt);
			}
		}
		return false;
	}

	
	public int getFilterInInventory(EntityPlayer player)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            
            if(itemstack != null && itemstack.stackSize > 0)
            	if(itemstack.getItem() instanceof ItemCore && itemstack.getMetadata() == 0)
            	{
            		return i;
            	}
        }
		
		return -1;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) 
	{
		super.onArmorTick(world, player, itemStack);
		
		if(!world.isRemote)
		{
			if(player.ticksExisted % (player.getHealth() >= (player.getMaxHealth() / 2) ? 60 : 30) == 0)
			{
				if(PollutionUtils.isEntityRespirating(player, false))
					world.playSound(null, new BlockPos(player.posX, player.posY, player.posZ), SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.PLAYERS, 1.5F, 0.35F+world.rand.nextInt(35)/100F);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean flagIn) {
		super.addInformation(stack, player, tooltip, flagIn);
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if(nbt != null && nbt.hasKey("filter"))
		{
			int i = (int)(((float)Math.max(nbt.getInteger("filter"), 0))/EMConfig.filter_durability * 100);
			if(i == 0)
				tooltip.add(I18n.format("tooltip.ecomod.respirator.insert_filter", new Object[0]));
			tooltip.add(I18n.format("tooltip.ecomod.respirator.filter_capacity", new Object[0]) + ": "+i+"%");
		}
		else
		{
			tooltip.add(I18n.format("tooltip.ecomod.respirator.insert_filter", new Object[0]));
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		
		if(worldIn.isRemote)
			return;
		/*
		Achievement ach = EMAchievements.ACHS.get("respirator");
		
		if(ach != null)
		if(!playerIn.hasAchievement(ach))
		{
			playerIn.addStat(ach);
		}*/
	}
	
	private static final String villager_texture = EMUtils.resloc("textures/models/armor/respirator_villager_layer_1.png").toString();

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return entity instanceof EntityVillager || (entity instanceof EntityZombie && ((EntityZombie)entity).isVillager()) ? villager_texture : super.getArmorTexture(stack, entity, slot, type);
	}
}
