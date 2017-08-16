package ecomod.common.items;

import ecomod.api.client.IRenderableHeadArmor;
import ecomod.api.pollution.IRespirator;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.EMItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemRespirator extends ItemArmor implements IRespirator, IRenderableHeadArmor
{
	public ItemRespirator() {
		super(EMItems.RESPIRATOR_MATERIAL, 1, EntityEquipmentSlot.HEAD);
		
		this.setCreativeTab(CreativeTabs.COMBAT);
		
	}

	@Override
	public boolean isRespirating(EntityLivingBase entity, ItemStack stack)
	{
		if(entity instanceof EntityPlayer)
		{		
			NBTTagCompound nbt = stack.getTagCompound();
			
			EntityPlayer player = (EntityPlayer)entity;
			
			if(nbt != null)
			{
				int nf;
			
				if(nbt.hasKey("filter"))
				{
					if(nbt.getInteger("filter") > 0)
					{
						nbt.setInteger("filter", nbt.getInteger("filter")-1);
						
						return true;
					}
					else
					{
						int k = getFilterInInventory(player);
						
						if(k != -1)
						{
							ItemStack stk = player.inventory.getStackInSlot(k);
							stk.shrink(1);
							player.inventory.setInventorySlotContents(k, stk);
							
							nbt.setInteger("filter", EMConfig.filter_durability);
							
							stack.setTagCompound(nbt);
							
							return true;
						}
					}
				}
				else
				{
					int k = getFilterInInventory(player);
					
					if(k != -1)
					{
						ItemStack stk = player.inventory.getStackInSlot(k);
						stk.shrink(1);
						player.inventory.setInventorySlotContents(k, stk);
						
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
					stk.shrink(1);
					player.inventory.setInventorySlotContents(k, stk);
					
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
            
            if(!itemstack.isEmpty())
            	if(itemstack.getItem() instanceof ItemCore && itemstack.getMetadata() == 0)
            	{
            		return i;
            	}
        }
		
		return -1;
	}
}
